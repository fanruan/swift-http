package com.fr.swift.query.builder;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.info.bean.parser.QueryInfoParser;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.result.detail.DetailResultQuery;
import com.fr.swift.query.segment.detail.DetailSegmentQuery;
import com.fr.swift.result.DetailQueryResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SourceKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;


/**
 * Create by lifan on 2019-07-03 11:51
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftContext.class, QueryInfoParser.class, FilterBuilder.class, SwiftDatabase.class, DetailQueryBuilder.class, ColumnTypeUtils.class, BaseQueryBuilder.class})
public class DetailQueryBuilderTest {

    private static final SwiftSegmentManager swiftSegmentManager = mock(SwiftSegmentManager.class);

    @Before
    public void setUp() {
        //mock SwiftContext
        mockStatic(SwiftContext.class);
        BeanFactory beanFactory = mock(BeanFactory.class);
        when(SwiftContext.get()).thenReturn(beanFactory);

        //mock SwiftSegmentManager
        when(beanFactory.getBean("localSegmentProvider", SwiftSegmentManager.class)).thenReturn(swiftSegmentManager);
    }

    @Test
    public void of() throws Exception {
        DetailQueryInfoBean detailQueryInfoBean = mock(DetailQueryInfoBean.class);
        mockStatic(QueryInfoParser.class);
        DetailQueryInfo detailQueryInfo = mock(DetailQueryInfo.class);
        when(QueryInfoParser.parse(detailQueryInfoBean)).thenReturn(detailQueryInfo);

        //info.hashSort()==true   new DetailQueryBuilder(queryInfo)
        when(detailQueryInfo.hasSort()).thenReturn(true);
        DetailQueryBuilder detailQueryBuilder = mock(DetailQueryBuilder.class);
        whenNew(DetailQueryBuilder.class).withArguments(detailQueryInfo).thenReturn(detailQueryBuilder);
        assertThat(DetailQueryBuilder.of(detailQueryInfoBean)).isEqualTo(detailQueryBuilder);

        //info.hasSort()==false   new SortedDetailQueryBuilder(queryInfo)
        when(detailQueryInfo.hasSort()).thenReturn(false);
        SortedDetailQueryBuilder sortedDetailQueryBuilder = mock(SortedDetailQueryBuilder.class);
        whenNew(SortedDetailQueryBuilder.class).withArguments(detailQueryInfo).thenReturn(sortedDetailQueryBuilder);
        assertThat(DetailQueryBuilder.of(detailQueryInfoBean)).isEqualTo(sortedDetailQueryBuilder);
    }

    @Test
    public void buildQuery() {
        DetailQueryInfo detailQueryInfo = mock(DetailQueryInfo.class);
        List<Query<DetailQueryResultSet>> queries = new ArrayList<Query<DetailQueryResultSet>>();
        Segment segment = mock(Segment.class);
        List<Segment> segments = Arrays.asList(segment);

        //mock SourceKey
        SourceKey table = mock(SourceKey.class);
        when(detailQueryInfo.getTable()).thenReturn(table);

        //mock QuerySegment
        Set queryTarget = mock(Set.class);
        when(detailQueryInfo.getQuerySegment()).thenReturn(queryTarget);

        SwiftSegmentManager localSegmentProvider = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
        when(localSegmentProvider.getSegmentsByIds(table, queryTarget)).thenReturn(segments);

        //mock List<Dimentsions>
        List dimensions = new ArrayList();
        when(detailQueryInfo.getDimensions()).thenReturn(dimensions);

        //init filterInfos
        List filterInfos = new ArrayList();

        //mock columns
        List columns = mock(List.class);
        mockStatic(BaseQueryBuilder.class);
        when(BaseQueryBuilder.getDimensionSegments(segment, dimensions)).thenReturn(columns);

        //mock FilterInfo  detailQueryInfo.getFilterInfo() != null
        FilterInfo filterInfo = mock(FilterInfo.class);
        when(detailQueryInfo.getFilterInfo()).thenReturn(filterInfo);
        filterInfos.add(filterInfo);

        //mock fetchSize
        int fetchSize = 1;
        when(detailQueryInfo.getFetchSize()).thenReturn(fetchSize);

        //mock query getSegmentQuery的返回值
        Query query = mock(Query.class);
        DetailQueryBuilder detailQueryBuilder = spy(new DetailQueryBuilder(detailQueryInfo));
        doReturn(query).when(detailQueryBuilder).getSegmentQuery(segment, columns, filterInfos);
        queries.add(query);

        //mock query getResultQuery的返回值
        Query queryResult = mock(Query.class);
        doReturn(queryResult).when(detailQueryBuilder).getResultQuery(queries);

        assertThat(detailQueryBuilder.buildQuery()).isEqualTo(queryResult);
    }

    @Test
    public void getSegmentQuery() throws Exception {
        Segment seg = mock(Segment.class);
        List columns = mock(List.class);
        List filterInfos = mock(List.class);

        //mock detailQueryInfo.getFetchSize()
        DetailQueryInfo detailQueryInfo = mock(DetailQueryInfo.class);
        int fetchSize = 1;
        when(detailQueryInfo.getFetchSize()).thenReturn(fetchSize);

        //mock new GeneralFilterInfo()
        GeneralFilterInfo generalFilterInfo = mock(GeneralFilterInfo.class);
        whenNew(GeneralFilterInfo.class).withArguments(filterInfos, GeneralFilterInfo.AND).thenReturn(generalFilterInfo);

        //mock FilterBuilder.buildDetailFilter
        mockStatic(FilterBuilder.class);
        DetailFilter detailFilter = mock(DetailFilter.class);
        when(FilterBuilder.buildDetailFilter(seg, generalFilterInfo)).thenReturn(detailFilter);

        //mock new DetailSegmentQuery
        DetailSegmentQuery detailSegmentQuery = mock(DetailSegmentQuery.class);
        whenNew(DetailSegmentQuery.class).withArguments(fetchSize, columns, detailFilter).thenReturn(detailSegmentQuery);

        assertThat(new DetailQueryBuilder(detailQueryInfo).getSegmentQuery(seg, columns, filterInfos)).isEqualTo(detailSegmentQuery);
    }

    @Test
    public void getResultQuery() throws Exception {
        List queris = mock(List.class);

        //mock detailQueryInfo.getFetchSize()
        DetailQueryInfo detailQueryInfo = mock(DetailQueryInfo.class);
        int fetchSize = 1;
        when(detailQueryInfo.getFetchSize()).thenReturn(fetchSize);

        DetailResultQuery detailResultQuery = mock(DetailResultQuery.class);
        whenNew(DetailResultQuery.class).withArguments(fetchSize, queris).thenReturn(detailResultQuery);

        assertThat(new DetailQueryBuilder(detailQueryInfo).getResultQuery(queris)).isEqualTo(detailResultQuery);

    }


}