package com.fr.swift.source.etl.groupsum;

import com.fr.general.ComparatorUtils;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.query.aggregator.AverageAggregate;
import com.fr.swift.query.aggregator.DistinctAggregate;
import com.fr.swift.query.aggregator.DistinctCountAggregatorValue;
import com.fr.swift.query.aggregator.DoubleAmountAggregateValue;
import com.fr.swift.query.aggregator.DoubleAverageAggregateValue;
import com.fr.swift.query.aggregator.MaxAggregate;
import com.fr.swift.query.aggregator.MinAggregate;
import com.fr.swift.query.aggregator.SumAggregate;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.etl.utils.ETLConstant;
import com.fr.swift.structure.iterator.RowTraversal;

import java.io.Serializable;

/**
 * Created by Handsome on 2017/12/8 0008 14:14
 */
public class SumByGroupTarget implements Serializable {

    private int sumType;
    private String name;
    private String nameText;
    private ColumnType columnType;
    private ClassType classType;


    public Object getSumValue(Segment[] segments, RowTraversal[] traversal) {
        switch (sumType) {
            case ETLConstant.CONF.GROUP.NUMBER.SUM: {
                DoubleAmountAggregateValue value = SumAggregate.INSTANCE.aggregate(traversal[0], segments[0].getColumn(new ColumnKey(name)));
                for (int i = 1; i < segments.length; i++) {
                    DoubleAmountAggregateValue otherValue = SumAggregate.INSTANCE.aggregate(traversal[i], segments[i].getColumn(new ColumnKey(name)));
                    SumAggregate.INSTANCE.combine(value, otherValue);
                }
                return value.calculate();
            }
            case ETLConstant.CONF.GROUP.NUMBER.AVG: {
                DoubleAverageAggregateValue averageValue = AverageAggregate.INSTANCE.aggregate(traversal[0], segments[0].getColumn(new ColumnKey(name)));
                for (int i = 1; i < segments.length; i++) {
                    DoubleAverageAggregateValue otherValue = AverageAggregate.INSTANCE.aggregate(traversal[i], segments[i].getColumn(new ColumnKey(name)));
                    AverageAggregate.INSTANCE.combine(averageValue, otherValue);
                }
                return averageValue.calculate();
            }
            case ETLConstant.CONF.GROUP.NUMBER.MAX: {
                DoubleAmountAggregateValue maxValue = MaxAggregate.INSTANCE.aggregate(traversal[0], segments[0].getColumn(new ColumnKey(name)));
                for (int i = 1; i < segments.length; i++) {
                    DoubleAmountAggregateValue otherValue = MaxAggregate.INSTANCE.aggregate(traversal[i], segments[i].getColumn(new ColumnKey(name)));
                    MaxAggregate.INSTANCE.combine(maxValue, otherValue);
                }
                return maxValue.calculate();
            }
            case ETLConstant.CONF.GROUP.NUMBER.MIN: {
                DoubleAmountAggregateValue minValue = MinAggregate.INSTANCE.aggregate(traversal[0], segments[0].getColumn(new ColumnKey(name)));
                for (int i = 1; i < segments.length; i++) {
                    DoubleAmountAggregateValue otherValue = MaxAggregate.INSTANCE.aggregate(traversal[i], segments[i].getColumn(new ColumnKey(name)));
                    MinAggregate.INSTANCE.combine(minValue, otherValue);
                }
                return minValue.calculate();
            }
            case ETLConstant.CONF.GROUP.NUMBER.COUNT: {
                DistinctCountAggregatorValue aggregatorValue = DistinctAggregate.INSTANCE.aggregate(traversal[0], segments[0].getColumn(new ColumnKey(name)));
                for (int i = 1; i < segments.length; i++) {
                    DistinctCountAggregatorValue otherValue = DistinctAggregate.INSTANCE.aggregate(traversal[i], segments[i].getColumn(new ColumnKey(name)));
                    DistinctAggregate.INSTANCE.combine(aggregatorValue, otherValue);
                }
                return aggregatorValue.calculate();
            }
            case ETLConstant.CONF.GROUP.STRING.APPEND: {
                return getAppendString(segments, traversal);
            }
            case ETLConstant.CONF.GROUP.NUMBER.RECORD_COUNT: {
                double count = 0;
                for (int i = 0; i < traversal.length; i++) {
                    count += (double) traversal[i].getCardinality();
                }
                return count;
            }
        }
        return null;
    }

    private String getAppendString(Segment[] segment, RowTraversal[] traversal) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < segment.length; i++) {
            final DictionaryEncodedColumn getter = segment[i].getColumn(new ColumnKey(name)).getDictionaryEncodedColumn();
            traversal[i].traversal(new TraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    sb.append("/");
                    sb.append(getter.getValue(getter.getIndexByRow(row)));
                }
            });
        }
        return sb.toString().substring(1);
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setNameText(String name) {
        this.nameText = name;
    }

    public String getName() {
        return this.name;
    }

    public String getNameText() {
        return nameText;
    }

    public int getSumType() {
        return sumType;
    }

    public void setSumType(int sumType) {
        this.sumType = sumType;
    }


    public void setColumnType(ColumnType columnType) {
        this.columnType = columnType;
    }

    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public ColumnType getColumnType() {
        // TODO
        return getSumType() == ETLConstant.CONF.GROUP.STRING.APPEND ?
                ColumnType.STRING : ColumnType.NUMBER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SumByGroupTarget)) {
            return false;
        }

        SumByGroupTarget that = (SumByGroupTarget) o;

        if (sumType != that.sumType) {
            return false;
        }
        return name != null ? ComparatorUtils.equals(name, that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = sumType;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
