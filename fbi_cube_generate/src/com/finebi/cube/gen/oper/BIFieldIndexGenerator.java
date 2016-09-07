package com.finebi.cube.gen.oper;

import com.finebi.cube.engine.map.map2.ExternalIntArrayMapFactory;
import com.finebi.cube.engine.map.map2.IntArrayListExternalMap;
import com.finebi.cube.impl.pubsub.BIProcessor;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.BITableKey;
import com.finebi.cube.structure.Cube;
import com.finebi.cube.structure.CubeTableEntityGetterService;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.ICubeColumnEntityService;
import com.fr.base.FRContext;
import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.conf.log.BILogManager;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.manager.PerformancePlugManager;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.fs.control.UserControl;
import com.fr.stable.bridge.StableFactory;
import com.fr.stable.collections.array.IntArray;
import com.fr.stable.project.ProjectConstants;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class created on 2016/4/7.
 *
 * @author Connery
 * @since 4.0
 */
public class BIFieldIndexGenerator<T> extends BIProcessor {
    protected CubeTableSource tableSource;
    protected ICubeFieldSource hostBICubeFieldSource;
    /**
     * 当前需要生产的ColumnKey，不能通过hostDBField转换。
     * 因为子类型是无法通过DBFiled转换得到的
     */
    protected BIColumnKey targetColumnKey;
    protected ICubeColumnEntityService<T> columnEntityService;
    protected Cube cube;
    protected long rowCount;
    private final String CACHE ="caches";
    private final String BASEPATH = File.separator + ProjectConstants.RESOURCES_NAME + File.separator + CACHE;
    public BIFieldIndexGenerator(Cube cube, CubeTableSource tableSource, ICubeFieldSource hostBICubeFieldSource, BIColumnKey targetColumnKey) {
        this.tableSource = tableSource;
        this.hostBICubeFieldSource = hostBICubeFieldSource;
        this.cube = cube;
        this.targetColumnKey = targetColumnKey;
    }

    private void initial() {
        try {
            CubeTableEntityGetterService tableEntityService = cube.getCubeTable(new BITableKey(tableSource.getSourceID()));
            columnEntityService = (ICubeColumnEntityService<T>) tableEntityService.getColumnDataGetter(targetColumnKey);
            rowCount = tableEntityService.getRowCount();
            tableEntityService.clear();
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    @Override
    public Object mainTask(IMessage lastReceiveMessage) {
        BILogManager biLogManager = StableFactory.getMarkedObject(BILogManagerProvider.XML_TAG, BILogManager.class);
        long t = System.currentTimeMillis();
        biLogManager.logIndexStart(UserControl.getInstance().getSuperManagerID());
        try {
            initial();
            if (PerformancePlugManager.getInstance().isDiskSort()) {
                buildTableIndexExternal();
            }
            else {
                buildTableIndex();
            }
            long costTime = System.currentTimeMillis() - t;
            try {
                biLogManager.infoColumn(tableSource.getPersistentTable(), hostBICubeFieldSource.getFieldName(), costTime, Long.valueOf(UserControl.getInstance().getSuperManagerID()));
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        } catch (Exception e) {
            try {
                biLogManager.errorTable(tableSource.getPersistentTable(), e.getMessage(), UserControl.getInstance().getSuperManagerID());
            } catch (Exception e1) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
            BILogger.getLogger().error(e.getMessage(), e);
        } finally {
            return null;
        }
    }

    @Override
    public void release() {
        columnEntityService.clear();
    }

    public void buildTableIndex() {
        IntArray nullRowNumbers = new IntArray();
        Map<T, IntArray> group2rowNumber = createTreeMap(nullRowNumbers);
        Iterator<Map.Entry<T, IntArray>> group2rowNumberIt = group2rowNumber.entrySet().iterator();
        int[] positionOfGroup =  doBuildTableIndex(group2rowNumberIt);
        group2rowNumber.clear();
        GroupValueIndex nullIndex = buildGroupValueIndex(nullRowNumbers);
        buildPositionOfGroup(positionOfGroup);
        columnEntityService.addNULLIndex(0, nullIndex);
//        group2rowNumber.clear();
    }

    public void buildTableIndexExternal() {
        IntArray nullRowNumbers = new IntArray();
        IntArrayListExternalMap group2rowNumber = createExternalMap(nullRowNumbers);
        Iterator<Map.Entry<T, IntArray>> group2rowNumberIt=  group2rowNumber.getIterator();
        int[] positionOfGroup = doBuildTableIndex(group2rowNumberIt);
        group2rowNumber.clear();
        GroupValueIndex nullIndex = buildGroupValueIndex(nullRowNumbers);
        buildPositionOfGroup(positionOfGroup);
        columnEntityService.addNULLIndex(0, nullIndex);
//        group2rowNumber.release();
    }
    private int[] doBuildTableIndex(Iterator<Map.Entry<T, IntArray>> group2rowNumberIt){
        int groupPosition = 0;
        int[] positionOfGroup = new int[(int)rowCount];
        while (group2rowNumberIt.hasNext()) {
            Map.Entry<T, IntArray> entry = group2rowNumberIt.next();
            T groupValue = entry.getKey();
            IntArray groupRowNumbers = entry.getValue();
            columnEntityService.addGroupValue(groupPosition, groupValue);
            GroupValueIndex groupValueIndex = buildGroupValueIndex(groupRowNumbers);
            columnEntityService.addGroupIndex(groupPosition, groupValueIndex);
            initPositionOfGroup(positionOfGroup, groupPosition, groupValueIndex);
            groupPosition++;
        }
        columnEntityService.recordSizeOfGroup(groupPosition);
        return positionOfGroup;
    }

    private void initPositionOfGroup(final int[] position, final int groupPosition, GroupValueIndex groupValueIndex) {
        groupValueIndex.Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                position[row] = groupPosition;
            }
        });
    }

    private void buildPositionOfGroup(int[] position) {
        for (int i = 0; i < position.length; i++) {
            columnEntityService.addPositionOfGroup(i, position[i]);
        }
    }

    private GroupValueIndex buildGroupValueIndex(IntArray groupRowNumbers) {
        return GVIFactory.createGroupValueIndexBySimpleIndex(groupRowNumbers);
    }
    private void constructMap(Map<T, IntArray> map,IntArray nullRowNumbers){
        for (int i = 0; i < rowCount; i++) {
            T originalValue = columnEntityService.getOriginalObjectValueByRow(i);
            if (originalValue != null) {
                IntArray list = map.get(originalValue);
                if (list == null) {
                    list = new IntArray();
                    map.put(originalValue, list);
                }
                list.add(i);
            } else {
                nullRowNumbers.add(i);
            }
        }
    }
    private Map<T, IntArray> createTreeMap(IntArray nullRowNumbers) {
        Map<T, IntArray> group2rowNumber = new TreeMap<T, IntArray>(columnEntityService.getGroupComparator());
        constructMap(group2rowNumber,nullRowNumbers);
        return group2rowNumber;
    }
    private IntArrayListExternalMap<T> createExternalMap(IntArray nullRowNumbers) {
        String dataFloder = FRContext.getCurrentEnv().getPath() + BASEPATH + File.separator +((DBTableSource)tableSource).getDbName() + File.separator + tableSource.getTableName() + File.separator + targetColumnKey.getColumnName();
        IntArrayListExternalMap<T> group2rowNumber = ExternalIntArrayMapFactory.getIntListExternalMap(columnEntityService.getClassType(),columnEntityService.getGroupComparator(),dataFloder);
        constructMap(group2rowNumber,nullRowNumbers);
        return group2rowNumber;
    }
}
