package com.fr.swift.query.aggregator;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author yee
 * @date 2019-06-24
 */
public class SingleAggregatorValueSet extends ListAggregatorValueSet {
    public SingleAggregatorValueSet(AggregatorValueRow row) {
        super(new ArrayList<AggregatorValueRow>(Collections.singleton(row)));
    }
}