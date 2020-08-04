package com.fr.swift.segment;

import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.source.SourceKey;

import java.util.Date;

/**
 * @author anchore
 * @date 2018/5/23
 */
public interface SegmentKey {
    SourceKey getTable();

    Integer getOrder();

    StoreType getStoreType();

    SwiftDatabase getSwiftSchema();

    String getId();

    Date getCreateTime();

    String getLocation();

    Date getVisitedTime();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}