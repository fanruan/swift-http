package com.fr.swift.cloud.segment.column.impl.base;

import com.fr.swift.cloud.cube.io.input.Reader;
import com.fr.swift.cloud.cube.io.location.IResourceLocation;
import com.fr.swift.cloud.cube.io.output.Writer;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.segment.column.DetailColumn;
import com.fr.swift.cloud.util.Crasher;
import com.fr.swift.cloud.util.IoUtil;

/**
 * @author anchore
 * @date 2017/11/7
 */
abstract class BaseDetailColumn<T, W extends Writer, R extends Reader> implements DetailColumn<T> {
    private static final String DETAIL = "detail";

    static final IResourceDiscovery DISCOVERY = ResourceDiscovery.getInstance();

    IResourceLocation location;

    W detailWriter;
    R detailReader;

    BaseDetailColumn(IResourceLocation parent) {
        location = parent.buildChildLocation(DETAIL);
    }

    @Override
    public int getInt(int pos) {
        return Crasher.crash("not allowed");
    }

    @Override
    public long getLong(int pos) {
        return Crasher.crash("not allowed");
    }

    @Override
    public double getDouble(int pos) {
        return Crasher.crash("not allowed");
    }

    protected abstract void initDetailReader();

    @Override
    public boolean isReadable() {
        initDetailReader();
        boolean readable = detailReader.isReadable();
        if (location.getStoreType().isPersistent()) {
            IoUtil.release(detailReader);
        }
        detailReader = null;
        return readable;
    }

    @Override
    public void release() {
        IoUtil.release(detailWriter, detailReader);
        detailWriter = null;
        detailReader = null;
        SwiftLoggers.getLogger().debug("swift detail released at {}", location.getPath());
    }
}