package com.fr.swift.cloud.query.aggregator.extension;

import com.fr.swift.cloud.bitmap.traversal.TraversalAction;
import com.fr.swift.cloud.query.aggregator.AbstractAggregator;
import com.fr.swift.cloud.query.aggregator.AggregatorType;
import com.fr.swift.cloud.query.aggregator.AggregatorValue;
import com.fr.swift.cloud.query.aggregator.extension.histogram.Histogram;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.DetailColumn;
import com.fr.swift.cloud.structure.iterator.RowTraversal;

import java.io.Serializable;

/**
 * @author lyon
 * @date 2019/1/23
 */
public class TopPercentileAggregator extends AbstractAggregator<TopPercentileAggregatorValue> implements Serializable {

    private static final long serialVersionUID = 3239591363978300191L;

    private double percentile;
    private int numberOfSignificantValueDigits;

    /**
     * TP即Top Percentile，Top百分数，是一个统计学里的术语，与平均数、中位数都是一类
     * TP90的意思是保证90%请求都能被响应的最小耗时
     *
     * @param percentile                     百分比, 取值范围[0, 100]
     * @param numberOfSignificantValueDigits {@link Histogram#Histogram(int)}
     */
    public TopPercentileAggregator(double percentile, int numberOfSignificantValueDigits) {
        this.percentile = percentile;
        this.numberOfSignificantValueDigits = numberOfSignificantValueDigits;
    }

    @Override
    public TopPercentileAggregatorValue aggregate(RowTraversal traversal, Column<?> column) {
        final DetailColumn detailColumn = column.getDetailColumn();
        final TopPercentileAggregatorValue value = new TopPercentileAggregatorValue(percentile, numberOfSignificantValueDigits);
        traversal = getNotNullTraversal(traversal, column);
        traversal.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                value.recordValue(detailColumn.getLong(row));
            }
        });
        return value;
    }

    @Override
    public TopPercentileAggregatorValue createAggregatorValue(AggregatorValue<?> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AggregatorType getAggregatorType() {
        return AggregatorType.TOP_PERCENTILE;
    }

    @Override
    public void combine(TopPercentileAggregatorValue current, TopPercentileAggregatorValue other) {
        if (other != null) {
            current.add(other);
        }
    }
}
