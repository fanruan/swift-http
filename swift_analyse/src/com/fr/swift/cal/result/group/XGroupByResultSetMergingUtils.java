package com.fr.swift.cal.result.group;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.Combiner;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.KeyValue;
import com.fr.swift.result.row.RowIndexKey;
import com.fr.swift.result.row.XGroupByResultSet;
import com.fr.swift.result.row.XGroupByResultSetImpl;
import com.fr.swift.structure.queue.SortedListMergingUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/2.
 */
public class XGroupByResultSetMergingUtils {

    @SuppressWarnings("unchecked")
    public static XGroupByResultSet merge(List<XGroupByResultSet<int[]>> xGroupByResultSets, List<Aggregator> aggregators,
                                          List<Sort> rowSorts, List<Sort> colSorts) {
        List<Map<Integer, Object>> rowGlobalDictionaries = new ArrayList<Map<Integer, Object>>();
        List<Map<Integer, Object>> colGlobalDictionaries = new ArrayList<Map<Integer, Object>>();
        List<List<KeyValue<RowIndexKey<int[]>, List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>>>>> lists = new ArrayList<List<KeyValue<RowIndexKey<int[]>, List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>>>>>();
        for (XGroupByResultSet<int[]> resultSet : xGroupByResultSets) {
            lists.add(resultSet.getXResultList());
            GroupByResultSetMergingUtils.addDictionaries(resultSet.getRowGlobalDictionaries(), rowGlobalDictionaries);
            GroupByResultSetMergingUtils.addDictionaries(resultSet.getColGlobalDictionaries(), colGlobalDictionaries);
        }
        // 合并行表头对应的聚合值，列表头聚合值(list)在KVListCombiner里面合并
        List<KeyValue<RowIndexKey<int[]>, List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>>>> result =
                SortedListMergingUtils.merge(lists, new GroupByResultSetMergingUtils.IndexKeyComparator(rowSorts),
                        new KVListCombiner(colSorts, aggregators));
        XGroupByResultSet set = xGroupByResultSets.isEmpty() ? null : xGroupByResultSets.get(0);
        int rowDimensionSize = set == null ? 0 : set.rowDimensionSize();
        int colDimensionSize = set == null ? 0 : set.colDimensionSize();
        return new XGroupByResultSetImpl(result, rowGlobalDictionaries, colGlobalDictionaries,
                rowDimensionSize, colDimensionSize);
    }

    private static class KVListCombiner implements
            Combiner<KeyValue<RowIndexKey<int[]>, List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>>>> {

        private List<Sort> sorts;
        List<Aggregator> aggregators;

        public KVListCombiner(List<Sort> sorts, List<Aggregator> aggregators) {
            this.sorts = sorts;
            this.aggregators = aggregators;
        }

        @Override
        public void combine(KeyValue<RowIndexKey<int[]>, List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>>> current,
                            KeyValue<RowIndexKey<int[]>, List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>>> other) {
            List<List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>>> lists = new ArrayList<List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>>>();
            lists.add(current.getValue());
            lists.add(other.getValue());
            // 合并列表头对应的聚合值
            List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>> mergedResult = SortedListMergingUtils.merge(lists,
                    new GroupByResultSetMergingUtils.IndexKeyComparator<AggregatorValue[]>(sorts),
                    new GroupByResultSetMergingUtils.KVCombiner<AggregatorValue>(
                            GroupByResultSetMergingUtils.convertType(aggregators)));
            // 清空current，然后把mergedResult加到current
            current.getValue().clear();
            current.getValue().addAll(mergedResult);
        }
    }
}
