package com.fr.swift.cloud.segment.column.impl;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.compare.Comparators;
import com.fr.swift.cloud.query.group.GroupType;
import com.fr.swift.cloud.segment.column.BitmapIndexedColumn;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.DetailColumn;
import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;
import com.fr.swift.cloud.segment.column.impl.base.LongDictColumn;
import com.fr.swift.cloud.util.IoUtil;
import com.fr.swift.cloud.util.function.Function;

import java.util.Arrays;
import java.util.List;

/**
 * 日期子列，用于分组的
 *
 * @author anchore
 * @date 2017/12/4
 * @see GroupType
 */
public class SubDateColumn extends BaseColumn<Long> {
    public static final List<GroupType> TYPES_TO_GENERATE = Arrays.asList(
            GroupType.YEAR, GroupType.QUARTER, GroupType.MONTH, GroupType.WEEK, GroupType.DAY,

            GroupType.WEEK_OF_YEAR, GroupType.HOUR, GroupType.MINUTE, GroupType.SECOND,

            GroupType.Y_Q, GroupType.Y_M, GroupType.Y_W,
            GroupType.Y_M_D_H, GroupType.Y_M_D_H_M, GroupType.Y_M_D_H_M_S,

            GroupType.Y_M_D
    );

    private GroupType type;

    /**
     * 源列，子列的父列
     */
    private Column origin;

    public SubDateColumn(Column origin, GroupType type) {
        super(origin.getLocation().buildChildLocation(type.toString()));
        this.type = type;
        this.origin = origin;
    }

    @Override
    public DetailColumn<Long> getDetailColumn() throws UnsupportedOperationException {
        return detailColumn == null ? detailColumn = new SubDetailColumn() : detailColumn;
    }

    @Override
    public DictionaryEncodedColumn<Long> getDictionaryEncodedColumn() {
        return dictColumn == null ? dictColumn = new LongDictColumn(location, Comparators.<Long>asc()) : dictColumn;
    }

    @Override
    public BitmapIndexedColumn getBitmapIndex() {
        return indexColumn == null ? indexColumn = new SubBitmapIndexedColumn() : indexColumn;
    }

    private class SubDetailColumn implements DetailColumn<Long> {
        private DictionaryEncodedColumn<Long> baseDict = origin.getDictionaryEncodedColumn();

        private Function<Long, Long> deriver = DateDerivers.newDeriver(type);

        @Override
        public int getInt(int pos) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getLong(int pos) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double getDouble(int pos) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void put(int pos, Long val) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Long get(int pos) {
            Long originVal = baseDict.getValueByRow(pos);
            return originVal == null ? null : deriver.apply(originVal);
        }

        @Override
        public boolean isReadable() {
            return false;
        }

        @Override
        public void release() {
            IoUtil.release(baseDict);
        }
    }

    private class SubBitmapIndexedColumn implements BitmapIndexedColumn {
        BitmapIndexedColumn deriveBitmapColumn = SubDateColumn.super.getBitmapIndex();

        @Override
        public void putBitMapIndex(int index, ImmutableBitMap bitmap) {
            deriveBitmapColumn.putBitMapIndex(index, bitmap);
        }

        @Override
        public ImmutableBitMap getBitMapIndex(int index) {
            if (index < 1) {
                // 拿源列的nullIndex
                return getNullIndex();
            }
            return deriveBitmapColumn.getBitMapIndex(index);
        }

        @Override
        public void putNullIndex(ImmutableBitMap bitMap) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ImmutableBitMap getNullIndex() {
            return origin.getBitmapIndex().getNullIndex();
        }

        @Override
        public void release() {
            IoUtil.release(deriveBitmapColumn);
        }

        @Override
        public boolean isReadable() {
            return origin.getBitmapIndex().isReadable();
        }
    }
}