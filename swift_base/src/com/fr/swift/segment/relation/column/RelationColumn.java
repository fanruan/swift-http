package com.fr.swift.segment.relation.column;

import com.fr.swift.cube.nio.NIOConstant;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.relation.RelationIndex;

/**
 * @author yee
 * @date 2018/4/3
 */
public class RelationColumn {
    private RelationIndex relationIndex;
    private Segment[] segments;
    private ColumnKey columnKey;
    private DictionaryEncodedColumn[] columns;

    public RelationColumn(RelationIndex relationIndex, Segment[] segments, ColumnKey columnKey) {
        this.relationIndex = relationIndex;
        this.segments = segments;
        this.columnKey = columnKey;
        this.columns = new DictionaryEncodedColumn[segments.length];
    }

    public Object getValue(int row) {
        long reverse = relationIndex.getReverseIndex(row);
        if (reverse != NIOConstant.LONG.NULL_VALUE) {
            int[] result = reverse2SegAndRow(reverse);
            if (columns[result[0]] == null) {
                columns[result[0]] = segments[result[0]].getColumn(columnKey).getDictionaryEncodedColumn();
            }
            return columns[result[0]].getValue(columns[result[0]].getIndexByRow(result[1]));
        }
        return null;
    }

    private int[] reverse2SegAndRow(long reverse) {
        int[] result = new int[2];
        result[0] = (int) ((reverse & 0xFFFFFFFF00000000L) >> 32);
        result[1] = (int) (0xFFFFFFFFL & reverse);
        return result;
    }
}
