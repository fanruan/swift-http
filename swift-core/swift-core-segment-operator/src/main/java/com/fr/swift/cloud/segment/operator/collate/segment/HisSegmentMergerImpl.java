package com.fr.swift.cloud.segment.operator.collate.segment;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.config.entity.SwiftSegmentBucketElement;
import com.fr.swift.cloud.config.service.SwiftSegmentService;
import com.fr.swift.cloud.cube.CubePathBuilder;
import com.fr.swift.cloud.cube.io.Types;
import com.fr.swift.cloud.cube.io.location.ResourceLocation;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.segment.CacheColumnSegment;
import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.segment.SegmentSource;
import com.fr.swift.cloud.segment.SegmentUtils;
import com.fr.swift.cloud.source.DataSource;
import com.fr.swift.cloud.util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author lyon
 * @date 2019/2/20
 */
public class HisSegmentMergerImpl implements HisSegmentMerger {

    private static final SwiftSegmentService SEG_SVC = SwiftContext.get().getBean(SwiftSegmentService.class);
    private static final int currentDir = 0;
    private static final int LINE_VIRTUAL_INDEX = -1;

    @Override
    public List<SegmentKey> merge(DataSource dataSource, List<SegmentPartition> segmentPartitions, int index) {
        List<SegmentKey> segmentKeys = new ArrayList<>();
        List<String> fields = dataSource.getMetadata().getFieldNames();
        try {
            for (SegmentPartition item : segmentPartitions) {
                Optional<String> first = item.getSegmentKeys().stream().map(SegmentKey::getSegmentUri).collect(Collectors.toSet()).stream().findFirst();
                SegmentKey segKey = SEG_SVC.tryAppendSegment(dataSource.getSourceKey(), Types.StoreType.FINE_IO, SegmentSource.COLLATED, first.orElse(Strings.EMPTY));
                segmentKeys.add(segKey);
                ResourceLocation location = new ResourceLocation(new CubePathBuilder(segKey).setTempDir(segKey.getSegmentUri()).build(), segKey.getStoreType());
                Segment segment = new CacheColumnSegment(location, dataSource.getMetadata());
                try {
                    Builder builder = new SegmentBuilder(segment, fields, item.getSegments(), item.getAllShow());
                    builder.build();
                    SegmentUtils.releaseHisSeg(segment);
                    segKey.markFinish(segment.getRowCount());
                } catch (Throwable e) {
                    try {
                        SegmentUtils.releaseHisSeg(segment);
                        SEG_SVC.delete(segmentKeys);
                        for (SegmentKey key : segmentKeys) {
                            SegmentUtils.clearSegment(key);
                        }
                    } catch (Exception ignore) {
                        SwiftLoggers.getLogger().error("ignore exception", ignore);
                    }
                    SwiftLoggers.getLogger().error("merge", e);
                    return new ArrayList<>();
                }
            }
            if (index != LINE_VIRTUAL_INDEX) {
                for (SegmentKey segmentKey : segmentKeys) {
                    SwiftSegmentBucketElement element = new SwiftSegmentBucketElement(dataSource.getSourceKey(), index, segmentKey.getId());
                    SEG_SVC.saveBucket(element);
                }
            }
            return segmentKeys;
        } finally {
            // 释放读过的碎片块。这边直接释放碎片块，可能会导致还在session中的查询报错，刷新之后就没问题了
            for (SegmentPartition item : segmentPartitions) {
                SegmentUtils.releaseHisSeg(item.getSegments());
            }
        }
    }

}
