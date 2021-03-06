package com.fr.swift.cloud.query.builder;

import com.fr.swift.cloud.db.impl.SwiftDatabase;
import com.fr.swift.cloud.exception.meta.SwiftMetaDataException;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.query.aggregator.Aggregator;
import com.fr.swift.cloud.query.filter.FilterBuilder;
import com.fr.swift.cloud.query.filter.detail.DetailFilter;
import com.fr.swift.cloud.query.group.by2.node.GroupPage;
import com.fr.swift.cloud.query.group.info.GroupByInfo;
import com.fr.swift.cloud.query.group.info.GroupByInfoImpl;
import com.fr.swift.cloud.query.group.info.IndexInfo;
import com.fr.swift.cloud.query.group.info.MetricInfo;
import com.fr.swift.cloud.query.group.info.MetricInfoImpl;
import com.fr.swift.cloud.query.info.bean.parser.QueryInfoParser;
import com.fr.swift.cloud.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.cloud.query.info.bean.type.PostQueryType;
import com.fr.swift.cloud.query.info.element.dimension.Dimension;
import com.fr.swift.cloud.query.info.element.metric.Metric;
import com.fr.swift.cloud.query.info.group.GroupQueryInfo;
import com.fr.swift.cloud.query.info.group.post.PostQueryInfo;
import com.fr.swift.cloud.query.query.Query;
import com.fr.swift.cloud.query.result.group.GroupResultQuery;
import com.fr.swift.cloud.query.segment.group.GroupSegmentQuery;
import com.fr.swift.cloud.query.sort.Sort;
import com.fr.swift.cloud.query.sort.SortType;
import com.fr.swift.cloud.result.qrs.QueryResultSet;
import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.source.ColumnTypeConstants;
import com.fr.swift.cloud.source.ColumnTypeUtils;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.source.SwiftMetaData;
import com.fr.swift.cloud.source.SwiftMetaDataColumn;
import com.fr.swift.cloud.structure.Pair;
import com.fr.swift.cloud.util.Crasher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pony
 * @date 2017/12/15
 */
public class GroupQueryBuilder extends BaseQueryBuilder {

    private static boolean isPagingQuery(GroupQueryInfo info) {
        // TODO: 2018/6/14 暂时只要有postQuery都不分页，后面再细分
        return info.getPostQueryInfoList().isEmpty();
    }

    private static List<Aggregator> getFilterAggregators(List<Metric> metrics, Segment segment) {
        List<Aggregator> aggregators = new ArrayList<Aggregator>();
        for (Metric metric : metrics) {
            Aggregator aggregator = FunnelAggregatorUtil.handleFunnelAggregator(metric, segment);
            if (metric.getFilter() != null) {
                aggregators.add(new MetricFilterAggregator(aggregator, FilterBuilder.buildDetailFilter(segment, metric.getFilter())));
            } else {
                aggregators.add(aggregator);
            }
        }
        return aggregators;
    }

    private static int countCalFields(List<PostQueryInfo> postQueryInfoList) {
        int count = 0;
        for (PostQueryInfo postQueryInfo : postQueryInfoList) {
            if (postQueryInfo.getType() == PostQueryType.CAL_FIELD) {
                count += 1;
            }
        }
        return count;
    }

    private static List<Pair<SortType, ColumnTypeConstants.ClassType>> getComparatorsForMerging(SourceKey table, List<Dimension> dimensions, List<Metric> metrics) {
        SwiftMetaData metaData = SwiftDatabase.getInstance().getTable(table).getMetadata();
        List<Pair<SortType, ColumnTypeConstants.ClassType>> comparators = new ArrayList<Pair<SortType, ColumnTypeConstants.ClassType>>();
        for (Dimension dimension : dimensions) {
            Sort sort = dimension.getSort();
            SortType type = sort == null || sort.getSortType() == SortType.ASC ? SortType.ASC : SortType.DESC;
            if (dimension.getIndexInfo().isGlobalIndexed()) {
                comparators.add(Pair.of(type, ColumnTypeConstants.ClassType.INTEGER));
            } else {
                comparators.add(Pair.of(type, getComparatorByColumn(metaData, dimension.getColumnKey().getName())));
            }
        }
        comparators.addAll(FunnelAggregatorUtil.createMetricComparatorsForMerging(metrics));
        return comparators;
    }


    /**
     * 维度的明细排序，按照维度值的字典排序
     */
    private static List<Sort> getSegmentIndexSorts(List<Dimension> dimensions) {
        List<Sort> indexSorts = new ArrayList<Sort>();
        for (Dimension dimension : dimensions) {
            Sort sort = dimension.getSort();
            if (sort != null && sort.getTargetIndex() == dimension.getIndex()) {
                indexSorts.add(sort);
            }
        }
        return indexSorts;
    }

    /**
     * 给最外层查询节点（查询服务节点）条用并构建query，根据segment分布信息区分本地query和远程query
     *
     * @param bean group query bean
     * @return query
     */
    public Query<QueryResultSet<GroupPage>> buildQuery(GroupQueryInfoBean bean) throws SwiftMetaDataException {
        GroupQueryInfo info = (GroupQueryInfo) QueryInfoParser.parse(bean);
        List<Query<QueryResultSet<GroupPage>>> queries = new ArrayList<>();
        List<Metric> metrics = info.getMetrics();
        List<Dimension> dimensions = info.getDimensions();
        boolean pagingQuery = isPagingQuery(info);
        List<Segment> segments = filterQuerySegs(info);
        // List<Segment> segments = localSegmentProvider.getSegmentsByIds(info.getTable(), info.getQuerySegment());
        for (Segment segment : segments) {
            try {
                List<Pair<Column, IndexInfo>> dimensionColumns = getDimensionSegments(segment, dimensions);
                List<Column> metricColumns = getMetricSegments(segment, metrics);
                List<Aggregator> aggregators = getFilterAggregators(metrics, segment);
                List<Sort> rowIndexSorts = getSegmentIndexSorts(dimensions);
                DetailFilter rowDetailFilter = FilterBuilder.buildDetailFilter(segment, info.getFilterInfo());
                // todo 这里难道不应是由fetch size决定是否分页吗？-1表示不分页之类
                GroupByInfo rowGroupByInfo = new GroupByInfoImpl(pagingQuery ? info.getFetchSize() : Integer.MAX_VALUE,
                        dimensionColumns, rowDetailFilter, rowIndexSorts, null);
                MetricInfo metricInfo = new MetricInfoImpl(metricColumns, aggregators,
                        metrics.size() + countCalFields(info.getPostQueryInfoList()));
                queries.add(new GroupSegmentQuery(rowGroupByInfo, metricInfo));
            } catch (Exception ignore) {
                SwiftLoggers.getLogger().error(ignore);
            }
        }
        return new GroupResultQuery(
                info.getFetchSize(), queries,
                getAggregators(metrics),
                getComparatorsForMerging(info.getTable(), dimensions, metrics),
                isGlobalIndexed(dimensions, metrics));
    }

    private static ColumnTypeConstants.ClassType getComparatorByColumn(SwiftMetaData metaData, String columnName) {
        SwiftMetaDataColumn column = null;
        try {
            column = metaData.getColumn(columnName);
        } catch (SQLException e) {
            Crasher.crash("failed to read metadata of table: " + metaData.toString());
        }
        return ColumnTypeUtils.getClassType(column);
    }

    private GroupQueryBuilder() {
    }

    private static class GroupQueryBuilderHolder {
        static final GroupQueryBuilder INSTANCE = new GroupQueryBuilder();
    }

    public static GroupQueryBuilder get() {
        return GroupQueryBuilderHolder.INSTANCE;
    }
}