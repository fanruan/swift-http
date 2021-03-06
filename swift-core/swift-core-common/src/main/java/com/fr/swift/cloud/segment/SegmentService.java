package com.fr.swift.cloud.segment;

import com.fr.swift.cloud.annotation.service.InnerService;
import com.fr.swift.cloud.config.entity.SwiftSegmentBucket;
import com.fr.swift.cloud.source.SourceKey;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * @author lucifer
 * @date 2020/3/17
 * @description
 * @since swift 1.1
 */
@InnerService
public interface SegmentService {

    void addSegment(SegmentKey segmentKey);

    /**
     * 旧块转移至新块，如memio->fineio等
     *
     * @param oldSeg
     * @param newSeg
     * @return 被转移的块
     */
    SegmentKey transferSegment(SegmentKey oldSeg, SegmentKey newSeg);

    /**
     * mutableImport、slimImport、collate、historyBLock
     *
     * @param segmentKeys
     */
    void addSegments(List<SegmentKey> segmentKeys);

    Segment getSegment(SegmentKey key);

    List<Segment> getSegments(List<SegmentKey> keys);

    List<Segment> getSegments(SourceKey tableKey);

    List<Segment> getSegments(Set<String> segKeys);

    List<SegmentKey> getSegmentKeys(SourceKey tableKey);

    List<SegmentKey> getSegmentKeysByIds(SourceKey tableKey, Collection<String> segmentIds);

    boolean exist(SegmentKey segmentKey);

    boolean existAll(Collection<String> segmentIds);

    SegmentKey removeSegment(SegmentKey segmentKey);

    List<SegmentKey> removeSegments(List<SegmentKey> segmentKeys);

    SwiftSegmentBucket getBucketByTable(SourceKey sourceKey);

    <S extends SegmentIndex> SegmentIndex computeSegIndexIfAbsent(SourceKey sourceKey, Function<SourceKey, S> function);

    void addSegmentIndex(SourceKey sourceKey, SegmentIndex segmentIndex);

    void flushCache();
}