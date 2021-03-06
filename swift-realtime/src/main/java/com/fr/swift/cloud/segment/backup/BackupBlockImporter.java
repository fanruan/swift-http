package com.fr.swift.cloud.segment.backup;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.cube.CubePathBuilder;
import com.fr.swift.cloud.cube.io.Types.StoreType;
import com.fr.swift.cloud.cube.io.location.ResourceLocation;
import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.segment.BackupSegment;
import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.segment.SegmentUtils;
import com.fr.swift.cloud.segment.operator.Inserter;
import com.fr.swift.cloud.segment.operator.insert.BaseBlockImporter;
import com.fr.swift.cloud.source.DataSource;
import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.source.alloter.RowInfo;
import com.fr.swift.cloud.source.alloter.SegmentInfo;
import com.fr.swift.cloud.source.alloter.SwiftSourceAlloter;
import com.fr.swift.cloud.source.alloter.impl.SwiftSegmentInfo;
import com.fr.swift.cloud.structure.Pair;

/**
 * @author anchore
 * @date 2019/3/8
 */
public class BackupBlockImporter<A extends SwiftSourceAlloter<?, RowInfo>> extends BaseBlockImporter<A, SwiftResultSet> {

    private static final SegmentInfo DEFAULT_SEG_INFO = new SwiftSegmentInfo(0, StoreType.NIO);
    private Pair<Integer, ImmutableBitMap> snapshot;
    private Segment backupSeg;

    public BackupBlockImporter(DataSource dataSource, A alloter) {
        super(dataSource, alloter);
    }

    @Override
    protected Inserting getInserting(SegmentKey segKey) {

        ResourceLocation location = new ResourceLocation(new CubePathBuilder(segKey).asBackup().build(), StoreType.NIO);
        backupSeg = new BackupSegment(location, dataSource.getMetadata());

        Inserter inserter = SwiftContext.get().getBean(Inserter.class, backupSeg, true);
        Inserting inserting = new Inserting(inserter, backupSeg, SegmentUtils.safeGetRowCount(backupSeg));
        if (backupSeg.isReadable()) {
            snapshot = snapshot(backupSeg);
        }
        return inserting;
    }

    @Override
    protected void handleFullSegment(SegmentInfo segInfo) {
        // do nothing
    }

    @Override
    protected void onSucceed() {
    }

    @Override
    protected SegmentInfo allot(int cursor, Row row) {
        return DEFAULT_SEG_INFO;
    }

    @Override
    protected void onFailed() {
        // do nothing
    }

    @Override
    public Pair<Integer, ImmutableBitMap> snapshot(Segment segment) {
        return Pair.of(segment.getRowCount(), segment.getAllShowIndex());
    }

    @Override
    public void rollback() {
        if (backupSeg != null) {
            backupSeg.putRowCount(snapshot.getKey());
            backupSeg.putAllShowIndex(snapshot.getValue());
            backupSeg.release();
        }
    }
}