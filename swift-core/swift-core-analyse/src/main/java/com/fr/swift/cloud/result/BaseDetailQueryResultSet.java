package com.fr.swift.cloud.result;

import com.fr.swift.cloud.source.SwiftMetaData;

/**
 * @author yee
 * @date 2018-12-13
 */
public abstract class BaseDetailQueryResultSet implements DetailQueryResultSet {
    protected int fetchSize;

    public BaseDetailQueryResultSet(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    @Override
    public int getFetchSize() {
        return fetchSize;
    }

    @Override
    public SwiftResultSet convert(SwiftMetaData metaData) {
        throw new UnsupportedOperationException();
    }
}
