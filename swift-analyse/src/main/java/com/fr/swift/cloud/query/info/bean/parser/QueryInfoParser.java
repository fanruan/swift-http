package com.fr.swift.cloud.query.info.bean.parser;

import com.fr.swift.cloud.query.filter.info.FilterInfo;
import com.fr.swift.cloud.query.info.bean.element.SortBean;
import com.fr.swift.cloud.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.cloud.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.cloud.query.info.bean.query.QueryInfoBean;
import com.fr.swift.cloud.query.info.bean.type.PostQueryType;
import com.fr.swift.cloud.query.info.detail.DetailQueryInfo;
import com.fr.swift.cloud.query.info.element.dimension.Dimension;
import com.fr.swift.cloud.query.info.element.metric.Metric;
import com.fr.swift.cloud.query.info.group.GroupQueryInfo;
import com.fr.swift.cloud.query.info.group.GroupQueryInfoImpl;
import com.fr.swift.cloud.query.info.group.post.PostQueryInfo;
import com.fr.swift.cloud.query.limit.Limit;
import com.fr.swift.cloud.query.limit.SingleLimit;
import com.fr.swift.cloud.query.query.QueryInfo;
import com.fr.swift.cloud.query.query.QueryType;
import com.fr.swift.cloud.query.sort.AscSort;
import com.fr.swift.cloud.query.sort.DescSort;
import com.fr.swift.cloud.query.sort.NoneSort;
import com.fr.swift.cloud.query.sort.Sort;
import com.fr.swift.cloud.segment.column.ColumnKey;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.util.Crasher;
import com.fr.swift.cloud.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Lyon on 2018/6/7.
 */
public class QueryInfoParser {

    public static QueryInfo parse(QueryInfoBean queryInfoBean) {
        QueryType type = queryInfoBean.getQueryType();
        switch (type) {
            case GROUP:
                return parseGroupQueryInfo((GroupQueryInfoBean) queryInfoBean);
            case DETAIL:
                return parseDetailQueryInfo((DetailQueryInfoBean) queryInfoBean);
            default:
        }
        return Crasher.crash(new UnsupportedOperationException("Unsupported query type!"));
    }

    public static List<PostQueryInfo> parsePostQuery(QueryInfoBean queryInfoBean) {
        return queryInfoBean.getQueryType() == QueryType.GROUP ?
                ((GroupQueryInfo) parseGroupQueryInfo((GroupQueryInfoBean) queryInfoBean)).getPostQueryInfoList() :
                Collections.<PostQueryInfo>emptyList();
    }

    private static QueryInfo parseGroupQueryInfo(GroupQueryInfoBean bean) {
        String queryId = bean.getQueryId();
        SourceKey table = new SourceKey(bean.getTableName());
        FilterInfo filterInfo = FilterInfoParser.parse(table, bean.getFilter());
        List<Dimension> dimensions = DimensionParser.parse(table, bean.getDimensions(), bean.getSorts());
        List<Metric> metrics = MetricParser.parse(table, bean.getAggregations());
        List<PostQueryInfo> postQueryInfoList = PostQueryInfoParser.parse(table, bean, dimensions);
        if (!isPageable(postQueryInfoList)) {
            // 全部计算
            bean.setFetchSize(Integer.MAX_VALUE);
        }
        GroupQueryInfo groupQueryInfo = new GroupQueryInfoImpl(queryId, bean.getFetchSize(), table, filterInfo, dimensions, metrics, postQueryInfoList);
        groupQueryInfo.setQuerySegment(bean.getSegments());
        return groupQueryInfo;
    }

    private static boolean isPageable(List<PostQueryInfo> infoList) {
        for (PostQueryInfo info : infoList) {
            if (info.getType() == PostQueryType.ROW_SORT) {
                return false;
            }
        }
        return true;
    }


//    private static QueryInfo parseResultJoinQueryInfo(ResultJoinQueryInfoBean bean) {
//        String queryId = bean.getQueryId();
//        List<QueryInfoBean> queryInfoBeans = bean.getQueryInfoBeans();
//        List<QueryInfo> queryInfoList = new ArrayList<QueryInfo>();
//        for (QueryInfoBean queryInfoBean : queryInfoBeans) {
//            queryInfoList.add(parse(queryInfoBean));
//        }
//        List<Dimension> dimensions = DimensionParser.parse(bean.getJoinedFields());
//        List<PostQueryInfo> postQueryInfoList = PostQueryInfoParser.parsePostQueryInfoOfResultJoinQuery(
//                bean.getPostQueryInfoBeans(), dimensions, queryInfoBeans);
//        return new ResultJoinQueryInfoImpl(queryId, bean.getFetchSize(), queryInfoList, dimensions, postQueryInfoList);
//    }

    private static QueryInfo parseDetailQueryInfo(DetailQueryInfoBean bean) {
        String queryId = bean.getQueryId();
        SourceKey table = new SourceKey(bean.getTableName());
        FilterInfo filterInfo = FilterInfoParser.parse(table, bean.getFilter());
        List<Dimension> dimensions = DimensionParser.parse(table, bean.getDimensions(), bean.getSorts());
        List<Sort> sorts = null;
        List<SortBean> sortBeans = bean.getSorts();
        if (null != sortBeans) {
            sorts = new ArrayList<Sort>();
            for (SortBean sortBean : sortBeans) {
                ColumnKey columnKey = new ColumnKey(sortBean.getName());
                switch (sortBean.getType()) {
                    case NONE:
                        sorts.add(new NoneSort());
                        break;
                    case DESC:
                        sorts.add(new DescSort(getDimensionIndex(sortBean.getName(), dimensions), columnKey));
                        break;
                    case ASC:
                        sorts.add(new AscSort(getDimensionIndex(sortBean.getName(), dimensions), columnKey));
                        break;
                }
            }
        }
        Limit limit = null;
        if (bean.getLimit() != null) {
            limit = new SingleLimit(bean.getLimit().end());
        }
        DetailQueryInfo detailQueryInfo = new DetailQueryInfo(queryId, bean.getFetchSize(), table, filterInfo, dimensions, sorts, null, limit);
        detailQueryInfo.setQuerySegment(bean.getSegments());
        return detailQueryInfo;
    }

    private static int getDimensionIndex(String columnName, List<Dimension> dimensions) {
        for (int i = 0; i < dimensions.size(); i++) {
            if (Util.equals(columnName, dimensions.get(i).getColumnKey().getName())) {
                return i;
            }
        }
        return -1;
    }
}
