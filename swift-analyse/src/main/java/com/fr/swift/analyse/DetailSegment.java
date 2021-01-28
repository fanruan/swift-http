package com.fr.swift.analyse;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.limit.Limit;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/12/15
 */
public class DetailSegment extends AbstractDetailSegment {

    public DetailSegment(int fetchSize, List<Segment> totalSegments, List<Dimension> dimensions, List<FilterInfo> filters, Limit limit, SwiftMetaData metaData, SwiftMetaData queriedMetadata) {
        super(fetchSize, new DetailQuerySegmentComponent(totalSegments), dimensions, filters, limit, metaData, queriedMetadata);
    }

    /**
     * 这里只负责拿数据，需要配合hasNext 进行使用。只使用这个函数的话最多拿一个批次的数据
     * 如果在这里进行更新的话极端情况下比较难处理
     * 保证一般流程（先判断有没有，再去获取）的正常运行。
     *
     * @return 抽取的数据
     */
    @Override
    public Row getNextRow() {
        try {
            List<Object> values = new ArrayList<>();
            Integer next = currentRowItr.next();
            Segment curSeg = currentSegments.getKey();
            for (ColumnKey columnKey : columnKeys) {
                Column<Object> column = curSeg.getColumn(columnKey);
                values.add(column.getDetailColumn().get(next));
            }
            return new ListBasedRow(values);
        } catch (Exception warn) {
            SwiftLoggers.getLogger().warn("catch Exception during get next , please check in case of things getting worse.message is {} ", warn.getMessage());
            currentSegments = filteredList.get(++segIndex);
            currentRowItr = currentSegments.getValue().intIterator();
            return getNextRow();
        }
    }

    @Override
    public List<Row> getPage() {
        if (!hasNextPage()) {
            return new ArrayList<Row>(0);
        }
        int count = fetchSize;
        List<Row> result = new ArrayList<>();
        while (hasNext() && count-- > 0) {
            result.add(getNextRow());
        }
        return result;
    }
}