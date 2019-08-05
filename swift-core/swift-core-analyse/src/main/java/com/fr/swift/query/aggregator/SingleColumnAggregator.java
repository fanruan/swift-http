package com.fr.swift.query.aggregator;

import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.Map;

/**
 * @author anchore
 * @date 2019/7/9
 * <p>
 * 聚合单列的aggregator，大部分聚合只聚合单列
 */
public abstract class SingleColumnAggregator<T extends AggregatorValue<?>> implements Aggregator<T> {
    @Override
    public T aggregate(RowTraversal traversal, Map<ColumnKey, Column<?>> columns) {
        return aggregate(traversal, columns.values().iterator().next());
    }
}