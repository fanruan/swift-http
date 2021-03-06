package com.fr.swift.cloud.query.filter.detail.impl;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.query.filter.match.MatchConverter;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;
import com.fr.swift.cloud.structure.iterator.RowTraversal;

/**
 * Created by Lyon on 2017/12/5.
 */
public class NullFilter extends AbstractDetailFilter {

    public NullFilter(Column column) {
        this.column = column;
    }

    @Override
    protected RowTraversal getIntIterator(DictionaryEncodedColumn dict) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImmutableBitMap createFilterIndex() {
        return column.getBitmapIndex().getNullIndex();
    }

    @Override
    public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
        if (targetIndex == -1) {
            return node.getData() == null;
        }
        return node.getAggregatorValue(targetIndex).calculateValue() == null;
    }
}
