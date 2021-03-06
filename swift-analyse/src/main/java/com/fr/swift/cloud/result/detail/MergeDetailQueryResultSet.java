package com.fr.swift.cloud.result.detail;

import com.fr.swift.cloud.query.limit.Limit;
import com.fr.swift.cloud.result.BaseDetailQueryResultSet;
import com.fr.swift.cloud.result.DetailQueryResultSet;
import com.fr.swift.cloud.result.DetailResultSet;
import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.result.SwiftRowIteratorImpl;
import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.source.SwiftMetaData;
import com.fr.swift.cloud.util.IoUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Xiaolei.Liu on 2018/1/23
 *
 * @author yee
 */

public class MergeDetailQueryResultSet extends BaseDetailQueryResultSet {

    private int rowCount;
    private List<DetailQueryResultSet> resultSets;
    /**
     * mergeIterator和rowIterator看似相同，其实不然，前者可以理解为内部实现(处理翻页缓存等)，后者为外部实现(对应SwiftResult)
     */
    private Iterator<Row> mergeIterator;

    public MergeDetailQueryResultSet(int fetchSize, List<DetailQueryResultSet> queryResultSets, Limit limit) {
        this(fetchSize, queryResultSets);
        if (limit != null) {
            rowCount = limit.end() > rowCount ? rowCount : limit.end();
        }
    }

    public MergeDetailQueryResultSet(int fetchSize, List<DetailQueryResultSet> queryResultSets) {
        super(fetchSize);
        resultSets = queryResultSets;
        rowCount = getRowCount(queryResultSets);
        mergeIterator = new DetailRowIterator();
    }

    private static int getRowCount(List<DetailQueryResultSet> queryResultSets) {
        int rowCount = 0;
        for (DetailQueryResultSet queryResultSet : queryResultSets) {
            rowCount += queryResultSet.getRowCount();
        }
        return rowCount;
    }

    @Override
    public List<Row> getPage() {
        List<Row> rows = new ArrayList<Row>();
        int count = fetchSize;
        while (mergeIterator.hasNext() && count-- > 0 && rowCount-- > 0) {
            rows.add(mergeIterator.next());
        }
        return rows;
    }

    @Override
    public boolean hasNextPage() {
        return mergeIterator.hasNext();
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public SwiftResultSet convert(SwiftMetaData metaData) {
        return new DetailResultSetImpl(this, metaData);
    }

    @Override
    public void close() {
        IoUtil.close(resultSets);
    }

    class DetailRowIterator implements Iterator<Row> {

        private int index = 0;
        private Iterator<Row> iterator = new ArrayList<Row>().iterator();

        @Override
        public boolean hasNext() {
            if (iterator.hasNext()) {
                return true;
            }
            while (index < resultSets.size()) {
                DetailQueryResultSet resultSet = resultSets.get(index);
                if (resultSet.hasNextPage()) {
                    List<Row> page = resultSet.getPage();
                    if (page != null && !page.isEmpty()) {
                        iterator = page.iterator();
                        break;
                    }
                }
                index++;
            }
            return iterator.hasNext();
        }

        @Override
        public Row next() {
            return iterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    static class DetailResultSetImpl implements DetailResultSet {
        DetailQueryResultSet detailQueryResultSet;
        SwiftMetaData metaData;
        Iterator<Row> iterator;

        DetailResultSetImpl(DetailQueryResultSet detailQueryResultSet, SwiftMetaData metaData) {
            this.detailQueryResultSet = detailQueryResultSet;
            this.metaData = metaData;
            iterator = new SwiftRowIteratorImpl<DetailQueryResultSet>(detailQueryResultSet);
        }

        @Override
        public List<Row> getPage() {
            return null;
        }

        @Override
        public boolean hasNextPage() {
            return false;
        }

        @Override
        public int getRowCount() {
            return detailQueryResultSet.getRowCount();
        }

        @Override
        public int getFetchSize() {
            return detailQueryResultSet.getFetchSize();
        }

        @Override
        public SwiftMetaData getMetaData() {
            return metaData;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Row getNextRow() {
            return iterator.next();
        }

        @Override
        public void close() {
            IoUtil.close(detailQueryResultSet);
        }
    }
}
