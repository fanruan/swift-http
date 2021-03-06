package com.fr.swift.cloud.query.info.element.metric;

import com.fr.swift.cloud.query.filter.info.FilterInfo;
import com.fr.swift.cloud.query.info.element.dimension.AbstractQueryColumn;
import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.ColumnKey;

/**
 * Created by pony on 2017/12/21.
 */
public abstract class AbstractMetric extends AbstractQueryColumn implements Metric {

    private ColumnKey columnKey;
    private FilterInfo filterInfo;

    public AbstractMetric(int index, ColumnKey columnKey, FilterInfo filterInfo) {
        super(index);
        this.columnKey = columnKey;
        this.filterInfo = filterInfo;
    }

    @Override
    public FilterInfo getFilter() {
        return filterInfo;
    }

    @Override
    public ColumnKey getColumnKey() {
        return columnKey;
    }

    @Override
    public Column getColumn(Segment segment) {
        return segment.getColumn(getColumnKey());
    }
}
