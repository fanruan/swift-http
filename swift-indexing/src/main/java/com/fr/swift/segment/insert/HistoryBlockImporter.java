package com.fr.swift.segment.insert;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.CacheColumnSegment;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.event.SyncSegmentLocationEvent;
import com.fr.swift.segment.operator.insert.BaseBlockImporter;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.util.SegmentInfoUtils;

import java.util.Collections;
import java.util.HashSet;

/**
 * @author anchore
 * @date 2018/12/21
 */
@SwiftBean(name = "historyBlockImporter")
@SwiftScope("prototype")
public class HistoryBlockImporter<A extends SwiftSourceAlloter<?, RowInfo>> extends BaseBlockImporter<A, SwiftResultSet> {

    public HistoryBlockImporter(DataSource dataSource, A alloter) {
        super(dataSource, alloter);
    }

    @Override
    protected Inserting getInserting(SegmentKey segKey) {
        // FIXME 2019/3/13 anchore 有临时路径的坑 tempDir应为当前导入的路径
        ResourceLocation location = new ResourceLocation(new CubePathBuilder(segKey).setTempDir(SegmentInfoUtils.getTemDir(segKey.getSegmentUri())).build(), segKey.getStoreType());
        Segment seg = new CacheColumnSegment(location, dataSource.getMetadata());

        return new Inserting(SwiftInserter.ofOverwriteMode(seg), seg, 0);
    }

    @Override
    protected void handleFullSegment(SegmentInfo segInfo) {
        // 上传历史块
        try {
            SegmentKey segKey = newSegmentKey(segInfo);
            SwiftEventDispatcher.syncFire(SegmentEvent.UPLOAD_HISTORY, segKey);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    @Override
    protected void indexIfNeed(SegmentInfo segInfo) throws Exception {
        // FIXME 2019/3/13 anchore 有临时路径的坑 tempDir应为当前导入的路径
        // todo 考虑异步，提升性能
        SegmentUtils.indexSegmentIfNeed(Collections.singletonList(insertings.get(segInfo).getSegment()));
    }

    @Override
    protected void onSucceed() {
        segLocationSvc.saveOnNode(SwiftProperty.get().getMachineId(), new HashSet<>(importSegKeys));
        segmentService.addSegments(importSegKeys);
        SwiftLoggers.getLogger().debug("import over, save seg location {}", importSegKeys);
        SwiftEventDispatcher.asyncFire(SyncSegmentLocationEvent.PUSH_SEG, importSegKeys);
    }

    @Override
    protected void onFailed() {
        // todo 报错全给清掉？
    }
}