package com.fr.swift.cloud.segment.column;

import com.fr.swift.cloud.cube.io.Releasable;
import com.fr.swift.cloud.cube.io.impl.mem.IntMemIo;
import com.fr.swift.cloud.util.IoUtil;

/**
 * @author anchore
 * @date 2018/8/15
 */
class IndexAndId implements Releasable {
    private IntMemIo indexToId;

    private IntMemIo idToIndex;

    private int lastPos = -1;

    IndexAndId() {
        indexToId = new IntMemIo();
        idToIndex = new IntMemIo();
    }

    IndexAndId(int cap) {
        indexToId = new IntMemIo(cap);
        idToIndex = new IntMemIo(cap);
    }

    void putIndexAndId(int id, int index) {
        idToIndex.put(id, index);
        indexToId.put(index, id);

        int max = Math.max(id, index);
        if (lastPos < max) {
            lastPos = max;
        }
    }

    int getId(int index) {
        return indexToId.get(index);
    }

    int getIndex(int id) {
        return idToIndex.get(id);
    }

    int size() {
        return lastPos + 1;
    }

    @Override
    public void release() {
        IoUtil.release(indexToId, idToIndex);
    }
}