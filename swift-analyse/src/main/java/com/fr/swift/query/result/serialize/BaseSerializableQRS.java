package com.fr.swift.query.result.serialize;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;
import com.fr.swift.source.SwiftMetaData;

import java.io.Serializable;

/**
 *
 * @author lyon
 * @date 2018/12/29
 */
public abstract class BaseSerializableQRS<T> implements QueryResultSet<T>, Serializable {
    private static final long serialVersionUID = 3284100787389755050L;

    private int fetchSize;
    private QueryResultSetMerger<? extends QueryResultSet<T>> merger;
    private T page;
    private boolean originHasNextPage;
    private transient SyncInvoker invoker;

    BaseSerializableQRS(int fetchSize, QueryResultSetMerger<? extends QueryResultSet<T>> merger, T page, boolean originHasNextPage) {
        this.fetchSize = fetchSize;
        this.merger = merger;
        this.page = page;
        this.originHasNextPage = originHasNextPage;
    }

    public void setInvoker(SyncInvoker invoker) {
        this.invoker = invoker;
    }

    @Override
    public int getFetchSize() {
        return fetchSize;
    }

    @Override
    public <Q extends QueryResultSet<T>> QueryResultSetMerger<Q> getMerger() {
        return (QueryResultSetMerger<Q>) merger;
    }

    @Override
    public T getPage() {
        T ret = page;
        page = null;
        if (hasNextPage() && invoker != null) {
            BaseSerializableQRS<T> qrs = invoker.invoke();
            page = qrs.page;
            originHasNextPage = qrs.originHasNextPage;
        } else {
            originHasNextPage = false;
        }
        return ret;
    }

    @Override
    public SwiftResultSet convert(SwiftMetaData metaData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {

    }

    @Override
    public boolean hasNextPage() {
        return page != null || originHasNextPage;
    }

    public interface SyncInvoker {

        <D> BaseSerializableQRS<D> invoke();
    }
}
