package com.fr.swift.cloud.result.node.cal;

import com.fr.swift.cloud.query.aggregator.AggregatorValue;
import com.fr.swift.cloud.query.info.bean.type.cal.CalTargetType;
import com.fr.swift.cloud.query.info.element.target.GroupTarget;
import com.fr.swift.cloud.query.info.element.target.cal.BrotherGroupTarget;
import com.fr.swift.cloud.query.info.element.target.cal.GroupFormulaTarget;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.result.node.iterator.CurrentDimensionIterator;
import com.fr.swift.cloud.result.node.iterator.LeafNodeIterator;
import com.fr.swift.cloud.structure.iterator.MapperIterator;
import com.fr.swift.cloud.util.function.Function;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/8.
 */
public class TargetCalculatorFactory {

    public static TargetCalculator create(GroupTarget target, SwiftNode groupNode, List<Map<Integer, Object>> dic) {
        CalTargetType type = target.type();
        Iterator<Iterator<List<AggregatorValue[]>>> iterator = createIterator(type, groupNode);
        switch (type) {
            case ALL_SUM_OF_ALL:
            case GROUP_SUM_OF_ALL: {
                Double[] values = new Double[]{null};
                return new SumOfAllCalculator(target.paramIndexes()[0], target.resultIndex(), iterator, values);
            }
            case ALL_AVG:
            case GROUP_AVG:
                return new AverageCalculator(target.paramIndexes()[0], target.resultIndex(), iterator);
            case ALL_SUM_OF_ABOVE:
            case GROUP_SUM_OF_ABOVE:
                return new SumOfAboveCalculator(target.paramIndexes()[0], target.resultIndex(), iterator);
            case ALL_MAX:
            case GROUP_MAX:
                return new MaxOrMinCalculator(target.paramIndexes()[0], target.resultIndex(), iterator, true);
            case ALL_MIN:
            case GROUP_MIN:
                return new MaxOrMinCalculator(target.paramIndexes()[0], target.resultIndex(), iterator, false);
            case ALL_RANK_ASC:
            case GROUP_RANK_ASC:
                return new RankCalculator(target.paramIndexes()[0], target.resultIndex(), iterator, true);
            case ALL_RANK_DEC:
            case GROUP_RANK_DEC:
                return new RankCalculator(target.paramIndexes()[0], target.resultIndex(), iterator, false);
            case DIMENSION_PERCENT:
                return new DimensionPercentCalculator(target.paramIndexes()[0], target.resultIndex(), iterator);
            case TARGET_PERCENT:
                return new TargetPercentCalculator(target.paramIndexes()[0], target.resultIndex(), iterator);
            case BROTHER_VALUE:
                return new BrotherValueTargetCalculator(target.paramIndexes(), target.resultIndex(), groupNode, dic, groupNodeMapper(), ((BrotherGroupTarget) target).getBrotherGroupIndex());
            case BROTHER_RATE:
                return new BrotherRateTargetCalculator(target.paramIndexes(), target.resultIndex(), groupNode, dic, groupNodeMapper(), ((BrotherGroupTarget) target).getBrotherGroupIndex());
            case COUSIN_VALUE:
                return new CousinValueTargetCalculator(target.paramIndexes(), target.resultIndex(), groupNode, dic, groupNodeMapper(), ((BrotherGroupTarget) target).getBrotherGroupIndex());
            case COUSIN_RATE:
                return new CousinRateTargetCalculator(target.paramIndexes(), target.resultIndex(), groupNode, dic, groupNodeMapper(), ((BrotherGroupTarget) target).getBrotherGroupIndex());
            case FORMULA:
                return new GroupFormulaCalculator(target.paramIndexes(), target.resultIndex(),
                        ((GroupFormulaTarget) target).getFormula(), iterator.next());
            case ARITHMETIC_ADD:
            case ARITHMETIC_DIV:
            case ARITHMETIC_MUL:
            case ARITHMETIC_SUB:
                return new ArithmeticTargetCalculator(type, target.paramIndexes(), target.resultIndex(), new MapperIterator<SwiftNode, AggregatorValue[]>(new LeafNodeIterator(groupNode), new Function<SwiftNode, AggregatorValue[]>() {
                    @Override
                    public AggregatorValue[] apply(SwiftNode p) {
                        return p.getAggregatorValue();
                    }
                }));
            default:
        }
        return null;
    }

    private static Iterator<Iterator<List<AggregatorValue[]>>> createIterator(CalTargetType type, SwiftNode root) {
        switch (type) {
            case ALL_SUM_OF_ALL:
            case ALL_AVG:
            case ALL_SUM_OF_ABOVE:
            case ALL_MAX:
            case ALL_MIN:
            case ALL_RANK_ASC:
            case ALL_RANK_DEC:
            case TARGET_PERCENT:
            case DIMENSION_PERCENT:
                return new RootIterator(root, groupNodeMapper());
            case GROUP_SUM_OF_ALL:
            case GROUP_AVG:
            case GROUP_SUM_OF_ABOVE:
            case GROUP_MAX:
            case GROUP_MIN:
            case GROUP_RANK_ASC:
            case GROUP_RANK_DEC:
                return new GroupIterator(root, groupNodeMapper());
            default:
        }
        return new RootIterator(root, groupNodeMapper());
    }

    private static Function<SwiftNode, List<AggregatorValue[]>> groupNodeMapper() {
        return new Function<SwiftNode, List<AggregatorValue[]>>() {
            @Override
            public List<AggregatorValue[]> apply(final SwiftNode p) {
                return Collections.singletonList(p.getAggregatorValue());
            }
        };
    }

    private static class RootIterator implements Iterator<Iterator<List<AggregatorValue[]>>> {
        private Function<SwiftNode, List<AggregatorValue[]>> function;
        private SwiftNode root;
        private boolean hasNext = true;

        private RootIterator(SwiftNode root, Function<SwiftNode, List<AggregatorValue[]>> function) {
            this.root = root;
            this.function = function;
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public Iterator<List<AggregatorValue[]>> next() {
            hasNext = false;
            return new MapperIterator<SwiftNode, List<AggregatorValue[]>>(new LeafNodeIterator(root), function);
        }

        @Override
        public void remove() {

        }
    }

    private static class GroupIterator implements Iterator<Iterator<List<AggregatorValue[]>>> {
        private SwiftNode current;
        private Function<SwiftNode, List<AggregatorValue[]>> function;

        private GroupIterator(SwiftNode root, Function<SwiftNode, List<AggregatorValue[]>> function) {
            initCurrent(root);
            this.function = function;
        }

        private void initCurrent(SwiftNode root) {
            current = root;
            while (current.getChildrenSize() != 0) {
                current = current.getChild(0);
            }
            current = current.getParent();
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Iterator<List<AggregatorValue[]>> next() {
            SwiftNode node = current;
            current = current.getSibling();
            return new CurrentDimensionIterator(node, function);
        }

        @Override
        public void remove() {

        }
    }
}
