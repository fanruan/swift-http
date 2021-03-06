package com.fr.swift.cloud.cube.io.output;

import com.fr.swift.cloud.structure.array.LongArray;
import com.fr.swift.cloud.structure.array.LongListFactory;

/**
 * @author yee
 * @date 2018/4/9
 */
public interface LongArrayWriter extends ObjectWriter<LongArray> {
    String CONTENT = "content";
    String POSITION = "position";
    String LENGTH = "length";
    String LAST_POSITION = "last_position";

    LongArray NULL_VALUE = LongListFactory.createEmptyLongArray();
}
