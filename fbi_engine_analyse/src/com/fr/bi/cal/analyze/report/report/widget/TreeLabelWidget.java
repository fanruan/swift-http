package com.fr.bi.cal.analyze.report.report.widget;

import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.relation.BITableRelationHelper;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.paging.PagingFactory;
import com.fr.bi.cal.analyze.report.report.widget.treelabel.GetTreeLabelExecutor;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.dimension.filter.DimensionFilter;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.field.dimension.BIDimensionFactory;
import com.fr.bi.field.dimension.filter.DimensionFilterFactory;
import com.fr.bi.stable.constant.BIExcutorConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.utils.BITravalUtils;
import com.fr.bi.util.BIConfUtils;
import com.fr.general.NameObject;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.report.poly.PolyECBlock;
import com.fr.report.poly.TemplateBlock;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
 * Created by fay on 2016/10/14.
 */
public class TreeLabelWidget extends AbstractBIWidget {
    private int page = -1;
    private int floors;
    private String parentValues;
    private String selectedValues;
    private String[] viewData;
    private BIDimension[] dimensions;
    private BusinessTable target;
    protected NameObject targetSort;
    protected Map<String, DimensionFilter> targetFilterMap = new HashMap<String, DimensionFilter>();
    protected Map<BIDimension, ArrayList<BITableRelation>> dimensionMap = new HashMap<BIDimension, ArrayList<BITableRelation>>();


    @Override
    public BIDimension[] getViewDimensions() {
        String[] array = viewData;
        List<BIDimension> usedDimensions = new ArrayList<BIDimension>();
        for (int i = 0; i < array.length; i++) {
            BIDimension dimension = BITravalUtils.getTargetByName(array[i], dimensions);
            if (dimension.isUsed()) {
                usedDimensions.add(dimension);
            }

        }
        return usedDimensions.toArray(new BIDimension[usedDimensions.size()]);
    }

    @Override
    public BIDimension[] getViewTargets() {
        return new BIDimension[0];
    }

    @Override
    public <T extends BITargetAndDimension> T[] getDimensions() {
        return (T[]) new BITargetAndDimension[0];
    }

    @Override
    public <T extends BITargetAndDimension> T[] getTargets() {
        return (T[]) new BITargetAndDimension[0];
    }

    @Override
    public List<BusinessTable> getUsedTableDefine() {
        return null;
    }

    @Override
    public List<BusinessField> getUsedFieldDefine() {
        return null;
    }

    @Override
    public int isOrder() {
        return 0;
    }

    @Override
    public JSONObject createDataJSON(BISessionProvider session, HttpServletRequest req) throws Exception {
        Paging paging = PagingFactory.createPaging(BIExcutorConstant.PAGINGTYPE.NONE);
        paging.setCurrentPage(page);
        return getInitDataJSON((BISession) session);
    }


    @Override
    protected TemplateBlock createBIBlock(BISession session) {
        return new PolyECBlock();
    }

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        parseDimensions(jo, userId);
        setTargetTable();
        parseSortFilter(jo, userId);

        if (jo.has("treeOptions")) {
            JSONObject treeJo = jo.getJSONObject("treeOptions");
            if (treeJo.has("floors")) {
                floors = treeJo.getInt("floors");
            }
            if (treeJo.has("parentValues")) {
                parentValues = treeJo.getString("parentValues");
            }
            if (treeJo.has("selectedValues")) {
                selectedValues = treeJo.getString("selectedValues");
            }
        }


    }


    private JSONObject getInitDataJSON(BISession session) throws JSONException {
        Paging paging = PagingFactory.createPaging(BIExcutorConstant.PAGINGTYPE.NONE);
        paging.setCurrentPage(page);
        GetTreeLabelExecutor executor = new GetTreeLabelExecutor(this, paging, session);
        JSONObject jo = new JSONObject();
        jo.put("floors", floors);
        jo.put("parentValues", parentValues);
        jo.put("selectedValues", selectedValues);
        executor.parseJSON(jo);
        return executor.getResultJSON();
    }

    private void parseSortFilter(JSONObject jo, long userId) throws Exception {
        if (jo.has("sort")) {
            JSONObject targetSort = (JSONObject) jo.get("sort");
            this.targetSort = new NameObject(targetSort.getString("sortTarget"), targetSort.getInt("type"));
        }

        if (jo.has("filterValue")) {
            JSONObject targetFilter = (JSONObject) jo.get("filterValue");
            Iterator it = targetFilter.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                JSONObject filter = targetFilter.getJSONObject(key);
                filter.put("targetId", key);
                this.targetFilterMap.put(key, DimensionFilterFactory.parseFilter(filter, userId));
            }
        }
    }

    private void setTargetTable() {
        if (dimensions.length > 0) {
            BITableID targetTableID = dimensions[0].createTableKey().getID();
            target = BIModuleUtils.getAnalysisBusinessTableById(targetTableID);
            for (int i = 0; i < dimensions.length; i++) {
                List<BITableRelation> relations = this.getRelationList(dimensions[i]);
                if (!relations.isEmpty()) {
                    target = relations.get(relations.size() - 1).getForeignTable();
                    break;
                }
            }
        }

    }

    private void parseDimensions(JSONObject jo, long userId) throws Exception {
        JSONObject dims = jo.optJSONObject("dimensions");
        JSONObject viewJo = jo.optJSONObject("view");
        if (viewJo == null) {
            viewJo = new JSONObject();
        }
        JSONArray viewJa = viewJo.optJSONArray(BIReportConstant.REGION.DIMENSION1);
        if (viewJa == null) {
            viewJa = new JSONArray();
        }
        viewData = new String[viewJa.length()];
        for (int i = 0; i < viewJa.length(); i++) {
            viewData[i] = viewJa.getString(i);
        }

        this.dimensions = new BIDimension[viewJa.length()];
        for (int i = 0; i < viewJa.length(); i++) {
            JSONObject dimObject = dims.getJSONObject(viewJa.getString(i));
            dimObject.put("did", viewJa.getString(i));
            this.dimensions[i] = BIDimensionFactory.parseDimension(dimObject, userId);
            JSONObject dimensionMap = dimObject.getJSONObject("dimensionMap");
            Iterator it = dimensionMap.keys();
            JSONArray relationJa = dimensionMap.optJSONObject(it.next().toString()).getJSONArray("targetRelation");
            ArrayList<BITableRelation> relationMap = new ArrayList<BITableRelation>();
            for (int j = 0; j < relationJa.length(); j++) {
                BITableRelation tableRelation = BITableRelationHelper.getRelation(relationJa.getJSONObject(j));
                relationMap.add(tableRelation);
            }
            this.dimensionMap.put(this.dimensions[i], relationMap);
        }
    }

    public BusinessTable getTargetTable() {
        return this.target;
    }

    public List<BITableRelation> getRelationList(BIDimension dimension) {
        return this.dimensionMap.get(dimension);
    }

    public List<BITableSourceRelation> getTableSourceRelationList(BIDimension dimension, long userId) {
        ArrayList<BITableRelation> tableRelationList = this.dimensionMap.get(dimension);
        List<BITableSourceRelation> tableSourceRelationList = new ArrayList<BITableSourceRelation>();
        for (BITableRelation relation : tableRelationList) {
            BITableSourceRelation tableSourceRelation = BIConfUtils.convert2TableSourceRelation(relation);
            tableSourceRelationList.add(tableSourceRelation);
        }
        return tableSourceRelationList;
    }

    @Override
    public WidgetType getType() {
        return WidgetType.TREE_LABEL;
    }

    @Override
    public void reSetDetailTarget() {

    }
}
