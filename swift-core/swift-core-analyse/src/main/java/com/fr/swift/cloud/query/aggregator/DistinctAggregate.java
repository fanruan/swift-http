package com.fr.swift.cloud.query.aggregator;

import com.fr.swift.cloud.bitmap.traversal.CalculatorTraversalAction;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.structure.iterator.RowTraversal;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Xiaolei.liu
 */

public class DistinctAggregate extends SingleColumnAggregator<DistinctCountAggregatorValue> implements Serializable {

    protected static final Aggregator INSTANCE = new DistinctAggregate();
    private static final long serialVersionUID = -6095081218942328474L;

    @Override
    public DistinctCountAggregatorValue aggregate(RowTraversal traversal, final Column column) {

        DistinctCountAggregatorValue distinctCount = new DistinctCountAggregatorValue();
        final Set set = new HashSet();
        traversal.traversal(new CalculatorTraversalAction() {
            @Override
            public double getCalculatorValue() {
                return 0;
            }

            @Override
            public void actionPerformed(int row) {
                set.add(column.getDictionaryEncodedColumn().getValueByRow(row));
            }
        });
        distinctCount.setValues(set);
        return distinctCount;
    }

    @Override
    public DistinctCountAggregatorValue createAggregatorValue(AggregatorValue<?> value) {
        return new DistinctCountAggregatorValue();
    }

    @Override
    public AggregatorType getAggregatorType() {
        return AggregatorType.DISTINCT;
    }

    @Override
    public void combine(DistinctCountAggregatorValue value, DistinctCountAggregatorValue other) {
        value.getValues().addAll(other.getValues());
    }
}
