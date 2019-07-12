package com.fr.swift.query.aggregator;

import com.fr.swift.query.aggregator.funnel.FunnelNodeAggregatorValue;
import com.fr.swift.query.group.FunnelGroupKey;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.util.function.Function;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2019-07-10
 */
public class FunnelAggregatorValue implements IterableAggregatorValue<SwiftNodeAggregatorValue> {
    private Map<FunnelGroupKey, FunnelAggValue> valueMap;

    public FunnelAggregatorValue(Map<FunnelGroupKey, FunnelAggValue> valueMap) {
        this.valueMap = valueMap;
    }

    @Override
    public double calculate() {
        return 0;
    }

    @Override
    public SwiftNodeAggregatorValue calculateValue() {
        return iterator().next();
    }

    @Override
    public Object clone() {
        return new FunnelAggregatorValue(new HashMap<FunnelGroupKey, FunnelAggValue>(valueMap));
    }


    @Override
    public Iterator<SwiftNodeAggregatorValue> iterator() {
        Iterator<Map.Entry<FunnelGroupKey, FunnelAggValue>> iterator = valueMap.entrySet().iterator();
        return new MapperIterator<Map.Entry<FunnelGroupKey, FunnelAggValue>, SwiftNodeAggregatorValue>(iterator, new Function<Map.Entry<FunnelGroupKey, FunnelAggValue>, SwiftNodeAggregatorValue>() {
            @Override
            public SwiftNodeAggregatorValue apply(Map.Entry<FunnelGroupKey, FunnelAggValue> p) {
                return new FunnelNodeAggregatorValue(p.getKey(), p.getValue());
            }
        });
    }

    public Map<FunnelGroupKey, FunnelAggValue> getValueMap() {
        return valueMap;
    }

    public void combine(FunnelAggregatorValue value) {
        for (Map.Entry<FunnelGroupKey, FunnelAggValue> entry : value.getValueMap().entrySet()) {
            FunnelAggValue contestAggValue = valueMap.get(entry.getKey());
            if (contestAggValue == null) {
                valueMap.put(entry.getKey(), new FunnelAggValue(contestAggValue.getCount(), contestAggValue.getPeriods()));
                continue;
            }
            int[] values = entry.getValue().getCount();
            int[] counters = contestAggValue.getCount();
            for (int i = 0; i < values.length; i++) {
                counters[i] += values[i];
            }
            List<List<Long>> valuePeriods = entry.getValue().getPeriods();
            List<List<Long>> lists = contestAggValue.getPeriods();
            // TODO: 2018/9/25 可以做好一部分排序，以及使用基本类型
            for (int i = 0; i < valuePeriods.size(); i++) {
                for (int j = 0; j < valuePeriods.get(i).size(); j++) {
                    lists.get(i).add(valuePeriods.get(i).get(j));
                }
            }
        }
    }
}