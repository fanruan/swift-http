package com.fr.swift.segment.container;

import com.fr.swift.converter.FindList;
import com.fr.swift.converter.FindListImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/10/19
 */
public enum SegmentContainer {
    /**
     * instance
     */
    NORMAL, INDEXING;
    private final Map<SourceKey, Set<SegmentKey>> tableMapSegments = new ConcurrentHashMap<SourceKey, Set<SegmentKey>>();
    private final Map<String, SegmentKey> segmentKeyMapSegment = new ConcurrentHashMap<String, SegmentKey>();


    public boolean contains(SourceKey sourceKey) {
        return tableMapSegments.containsKey(sourceKey);
    }

    public synchronized void register(SegmentKey segmentKey) {
        String segId = segmentKey.getId();
        SourceKey sourceKey = segmentKey.getTable();
        if (!tableMapSegments.containsKey(sourceKey)) {
            tableMapSegments.put(sourceKey, new HashSet<SegmentKey>());
        }
        tableMapSegments.get(sourceKey).add(segmentKey);
        segmentKeyMapSegment.put(segId, segmentKey);
    }

    public <T extends SegmentKey> void register(List<T> segmentKey) {
        for (T key : segmentKey) {
            register(key);
        }
    }

    public void register(Map<SourceKey, List<SegmentKey>> segmentKeys) {
        for (List<SegmentKey> value : segmentKeys.values()) {
            register(value);
        }
    }

    private List<SegmentKey> getSegmentAsList(SourceKey sourceKey) {
        return new ArrayList(tableMapSegments.get(sourceKey));
    }

    public List<Segment> getSegments(SourceKey sourceKey, Integer tmpPath) {
        if (contains(sourceKey)) {
            List<SegmentKey> list = getSegmentAsList(sourceKey);
            return createSegments(list, tmpPath);
        }
        return Collections.emptyList();
    }

    public List<Segment> getSegments(Collection<String> segmentKeys, List<String> notMatch, Integer tmpPath) {
        List<SegmentKey> list = new ArrayList<SegmentKey>();
        for (String segmentKey : segmentKeys) {
            SegmentKey segment = segmentKeyMapSegment.get(segmentKey);
            if (null == segment) {
                notMatch.add(segmentKey);
            } else {
                list.add(segment);
            }
        }
        return createSegments(list, tmpPath);
    }

    public List<Segment> getSegments(List<SegmentKey> segmentKeys, List<SegmentKey> notMatch, Integer tempPath) {
        List<SegmentKey> list = new ArrayList<SegmentKey>();
        for (SegmentKey segmentKey : segmentKeys) {
            SegmentKey segment = segmentKeyMapSegment.get(segmentKey.toString());
            if (null == segment) {
                notMatch.add(segmentKey);
            } else {
                list.add(segment);
            }
        }
        return createSegments(list, tempPath);
    }

    private List<Segment> createSegments(List<SegmentKey> list, final Integer tmpPath) {
        try {
            Collections.sort(list, new Comparator<SegmentKey>() {
                @Override
                public int compare(SegmentKey o1, SegmentKey o2) {
                    return o1.getOrder() - o2.getOrder();
                }
            });
            return new FindListImpl<Segment>(list).forEach(new FindList.ConvertEach<SegmentKey, Segment>() {
                @Override
                public Segment forEach(int idx, SegmentKey segmentKey) throws Exception {
                    return SegmentUtils.newSegment(segmentKey, tmpPath);
                }
            });
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }


    public void clear() {
        tableMapSegments.clear();
        segmentKeyMapSegment.clear();
    }

    public void remove(SourceKey sourceKey) {
        Set<SegmentKey> set = tableMapSegments.remove(sourceKey);
        if (null == set) {
            return;
        }
        for (SegmentKey pair : set) {
            segmentKeyMapSegment.remove(pair.getId());
        }
    }

    public <T extends SegmentKey> void remove(List<T> segmentKey) {
        for (T key : segmentKey) {
            remove(key);
        }
    }

    public synchronized void remove(SegmentKey segmentKey) {
        segmentKeyMapSegment.remove(segmentKey.toString());
        SourceKey table = segmentKey.getTable();
        if (tableMapSegments.containsKey(table)) {
            Set<SegmentKey> segmentKeys = tableMapSegments.get(table);
            if (segmentKeys != null) {
                synchronized (segmentKeys) {
                    segmentKeys.remove(segmentKey);
                }
            }
        }
    }
}
