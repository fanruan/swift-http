package com.fr.swift.cloud.bitmap.impl;

import com.fr.swift.cloud.bitmap.BitMapType;
import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.bitmap.MutableBitMap;
import com.fr.swift.cloud.bitmap.roaringbitmap.buffer.MutableRoaringBitmap;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.util.IoUtil;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author anchore
 * TODO: 2019/7/29 anchore 确保imut不变性，脱离imut bitmap
 */
public class RoaringMutableBitMap extends RoaringImmutableBitMap implements MutableBitMap {
    private RoaringMutableBitMap(MutableRoaringBitmap bitmap) {
        super(bitmap);
    }

    static MutableBitMap of(MutableRoaringBitmap bitmap) {
        return new RoaringMutableBitMap(bitmap);
    }

    public static MutableBitMap of() {
        return of(new MutableRoaringBitmap());
    }

    public static MutableBitMap ofBytes(byte[] bytes) {
        return ofStream(new ByteArrayInputStream(bytes));
    }

    public static MutableBitMap ofStream(InputStream input) {
        MutableRoaringBitmap another = new MutableRoaringBitmap();
        DataInputStream dis = new DataInputStream(input);
        try {
            another.deserialize(dis);
            return of(another);
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
            return of();
        } finally {
            IoUtil.close(dis);
        }
    }

    @Override
    public void or(ImmutableBitMap index) {
        if (index.isEmpty()) {
            return;
        }
        if (index instanceof RangeBitmap) {
            bitmap.add((long) ((RangeBitmap) index).start, ((RangeBitmap) index).end);
            return;
        }
        bitmap.or(extract(index));
    }

    @Override
    public void and(ImmutableBitMap index) {
        if (index.isEmpty()) {
            bitmap.clear();
            return;
        }
        if (index.isFull()) {
            return;
        }
        bitmap.and(extract(index));
    }

    @Override
    public void not(int bound) {
        bitmap.flip(0L, bound);
    }

    @Override
    public void andNot(ImmutableBitMap index) {
        if (index.isEmpty()) {
            return;
        }
        if (index.isFull()) {
            bitmap.clear();
            return;
        }
        if (index instanceof RangeBitmap) {
            FasterAggregation.andNot(this, ((RangeBitmap) index));
            return;
        }
        bitmap.andNot(extract(index));
    }

    @Override
    public void add(int index) {
        bitmap.add(index);
    }

    @Override
    public void remove(int index) {
        bitmap.remove(index);
    }

    @Override
    public ImmutableBitMap clone() {
        return of(bitmap.clone());
    }

    @Override
    public BitMapType getType() {
        return BitMapType.ROARING_MUTABLE;
    }
}