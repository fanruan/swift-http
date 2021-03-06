package com.fr.swift.cloud.query.group.by2.node;

import com.fr.swift.cloud.bitmap.BitMaps;
import com.fr.swift.cloud.query.aggregator.AggregatorFactory;
import com.fr.swift.cloud.query.aggregator.AggregatorType;
import com.fr.swift.cloud.query.filter.detail.DetailFilter;
import com.fr.swift.cloud.query.group.by.CubeData;
import com.fr.swift.cloud.query.group.info.GroupByInfo;
import com.fr.swift.cloud.query.group.info.GroupByInfoImpl;
import com.fr.swift.cloud.query.group.info.IndexInfo;
import com.fr.swift.cloud.query.group.info.MetricInfo;
import com.fr.swift.cloud.query.group.info.MetricInfoImpl;
import com.fr.swift.cloud.query.sort.Sort;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.result.SwiftNodeUtils;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.structure.Pair;
import junit.framework.TestCase;
import org.easymock.EasyMock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by lyon on 2019/1/4.
 */
public class NodeGroupByUtilsTest extends TestCase {

    private Iterator<GroupPage> iterator;
    private List<Pair<Column, IndexInfo>> dimensions;
    private Column metric;
    private Map<List<String>, Double> expected;
    private int rowCount = 10000;

    @Override
    public void setUp() {
        CubeData cubeData = new CubeData(2, 1, rowCount);
        dimensions = cubeData.getDimensions();
        metric = cubeData.getMetrics().get(0);
        DetailFilter filter = EasyMock.createMock(DetailFilter.class);
        EasyMock.expect(filter.createFilterIndex()).andReturn(BitMaps.newAllShowBitMap(rowCount)).anyTimes();
        EasyMock.replay(filter);
        GroupByInfo groupByInfo = new GroupByInfoImpl(Integer.MAX_VALUE, dimensions, filter, new ArrayList<Sort>(), null);
        MetricInfo metricInfo = new MetricInfoImpl(Collections.singletonList(metric),
                Collections.singletonList(AggregatorFactory.createAggregator(AggregatorType.SUM)), 1);
        iterator = NodeGroupByUtils.groupBy(groupByInfo, metricInfo);
        prepareExpected();
    }

    private void prepareExpected() {
        expected = new HashMap<List<String>, Double>();
        for (int i = 0; i < rowCount; i++) {
            List<String> keys = new ArrayList<String>();
            for (Pair<Column, IndexInfo> dimension : dimensions) {
                String value = (String) dimension.getKey().getDetailColumn().get(i);
                keys.add(value);
            }
            Double value = expected.get(keys);
            Integer detail = (Integer) metric.getDetailColumn().get(i);
            if (detail != null) {
                value = value == null ? detail : value + detail;
            }
            expected.put(keys, value);
        }
    }

    public void test() {
        assertTrue(iterator.hasNext());
        SwiftNode root = iterator.next().getRoot();
        assertNotNull(root);
        Iterator<List<SwiftNode>> it = SwiftNodeUtils.node2RowListIterator(root);
        assertTrue(it.hasNext());
        while (it.hasNext()) {
            List<SwiftNode> row = it.next();
            List<String> key = getKey(row);
            assertTrue(expected.containsKey(key));
            assertEquals(expected.get(key), row.get(row.size() - 1).getAggregatorValue()[0].calculateValue());
        }
    }

    private List<String> getKey(List<SwiftNode> row) {
        List<String> key = new ArrayList<String>();
        for (SwiftNode swiftNode : row) {
            key.add((String) swiftNode.getData());
        }
        return key;
    }
}