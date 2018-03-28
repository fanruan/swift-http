package com.fr.swift.segment.operator.insert;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.RealTimeSegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

import java.util.List;
import java.util.Map;

/**
 * This class created on 2018/3/27
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class RealtimeBlockSwiftInserter extends AbstractBlockInserter {

    public RealtimeBlockSwiftInserter(List<Segment> segments, SourceKey sourceKey, String cubeSourceKey, SwiftMetaData swiftMetaData) {
        super(segments, sourceKey, cubeSourceKey, swiftMetaData);
    }

    public RealtimeBlockSwiftInserter(List<Segment> segments, SourceKey sourceKey, String cubeSourceKey, SwiftMetaData swiftMetaData, List<String> fields) {
        super(segments, sourceKey, cubeSourceKey, swiftMetaData, fields);
    }

    @Override
    protected Segment createNewSegment(IResourceLocation location, SwiftMetaData swiftMetaData) {
        return new RealTimeSegmentImpl(location, swiftMetaData);
    }

    @Override
    protected Segment createSegment(int order) {
        return createSegment(order, Types.StoreType.MEMORY);
    }

    @Override
    public void release() {
        super.release();
        for (Map.Entry<Integer, Segment> entry : segmentIndexCache.getNewSegMap().entrySet()) {
            Segment segment = entry.getValue();
            segment.putAllShowIndex(BitMaps.newAllShowBitMap(segmentIndexCache.getSegRowByIndex(entry.getKey())));
            segment.putRowCount(segmentIndexCache.getSegRowByIndex(entry.getKey()));
            if (segment.isHistory()) {
                for (String field : fields) {
                    segment.getColumn(new ColumnKey(field)).getBitmapIndex().putNullIndex(segmentIndexCache.getNullBySegAndField(entry.getKey(), field));
                    segment.getColumn(new ColumnKey(field)).getBitmapIndex().release();
                    segment.getColumn(new ColumnKey(field)).getDetailColumn().release();
                }
                segment.release();
            } else {
                for (String field : fields) {
                    segment.getColumn(new ColumnKey(field)).getBitmapIndex().putNullIndex(segmentIndexCache.getNullBySegAndField(entry.getKey(), field));
                }
            }
        }
    }
}
