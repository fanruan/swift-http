package com.fr.bi.cal.analyze.executor.detail;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.analyze.executor.BIAbstractExecutor;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.BIDetailWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBBoxElement;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.style.BITableStyle;
import com.fr.bi.conf.report.style.TargetStyle;
import com.fr.bi.conf.report.widget.field.target.detailtarget.BIDetailTarget;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.field.BIStyleTarget;
import com.fr.bi.field.dimension.calculator.NoneDimensionCalculator;
import com.fr.bi.stable.constant.CellConstant;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.relation.BISimpleRelation;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.utils.algorithem.BIComparatorUtils;
import com.fr.bi.util.BIConfUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by GUY on 2015/4/16.
 */
public abstract class AbstractDetailExecutor extends BIAbstractExecutor<JSONObject> {


    protected transient Table target;
    protected transient BIDetailTarget[] viewDimension;
    protected transient String[] sortTargets;
    protected transient long userId;
    protected BIDetailWidget widget;

    public AbstractDetailExecutor(BIDetailWidget widget, Paging paging, BISession session) {
        super(widget, paging, session);
        this.target = widget.getTargetDimension();
        this.widget = widget;
        this.session = session;

        this.viewDimension = widget.getViewDimensions();
        this.sortTargets = widget.getSortTargets();
        this.userId = session.getUserId();
    }

    protected GroupValueIndex createDetailViewGvi() {
        ICubeTableService ti = getLoader().getTableIndex(target);
        GroupValueIndex gvi = ti.getAllShowIndex();
        for(int i = 0; i < this.viewDimension.length; i++) {
            BIDetailTarget target = this.viewDimension[i];
            TargetFilter filterValue = target.getFilter();
            if(filterValue != null) {
                BIField dataColumn = target.createColumnKey();
                List <BISimpleRelation> simpleRelations = target.getRelationList(this.target, this.userId);
                gvi = GVIUtils.AND(gvi, filterValue.createFilterIndex(new NoneDimensionCalculator(dataColumn, BIConfUtils.convertToMD5RelationFromSimpleRelation(simpleRelations, new BIUser(this.userId))), this.target, getLoader(), this.userId));
            }
        }
        Map<String, TargetFilter> filterMap = widget.getTargetFilterMap();
        for(Map.Entry<String, TargetFilter> entry : filterMap.entrySet()) {
            String targetId = entry.getKey();
            BIDetailTarget target = getTargetById(targetId);
            if(target != null) {
                BIField dataColumn = target.createColumnKey();
                List <BISimpleRelation> simpleRelations = target.getRelationList(this.target, this.userId);
                gvi = GVIUtils.AND(gvi, entry.getValue().createFilterIndex(new NoneDimensionCalculator(dataColumn, BIConfUtils.convertToMD5RelationFromSimpleRelation(simpleRelations, new BIUser(this.userId))), this.target, getLoader(), this.userId));
            }
        }
        gvi = GVIUtils.AND(gvi,
                widget.createFilterGVI(new DimensionCalculator[]{new NoneDimensionCalculator(new BIField(this.target, StringUtils.EMPTY),
                        new ArrayList<BITableSourceRelation>())}, this.target, getLoader(), this.userId));
        return gvi;
    }

    private BIDetailTarget getTargetById(String id) {
        BIDetailTarget target = null;
        for(int i = 0; i < viewDimension.length; i++) {
            if(BIComparatorUtils.isExactlyEquals(viewDimension[i].getValue(), id)) {
                target = viewDimension[i];
            }
        }
        return  target;
    }

    protected CBCell[][] createCells(GroupValueIndex gvi) {
        if (gvi == null) {
            return null;
        }
        BIDetailTarget[] viewDimension = widget.getViewDimensions();
        if (widget.getViewDimensions().length == 0) {
            return null;
        }
        long count = gvi.getRowsCountWithData();
        paging.setTotalSize(count);

        if (paging.getCurrentPage() > paging.getPages()) {
            return null;
        }

        int maxRow = paging.getCurrentSize();
        CBCell[][] cbcells = new CBCell[viewDimension.length + widget.isOrder()][maxRow + 1];
        createCellTitle(cbcells, CellConstant.CBCELL.TARGETTITLE_Y);
        return cbcells;
    }

    protected void fillOneLine(CBCell[][] cells, int row, Object[] ob) {

        for (int i = 0; i < viewDimension.length; i++) {
            BIDetailTarget t = viewDimension[i];
            Object v = ob[i];
            CBCell cell = new CBCell(v == null ? NONEVALUE : v);

            cell.setRow(row);
            cell.setColumn(i + widget.isOrder());
            cell.setRowSpan(1);
            cell.setColumnSpan(1);
            cell.setCellGUIAttr(BITableStyle.getInstance().getCellAttr());
            List cellList = new ArrayList();
            cellList.add(cell);
            //TODO CBBoxElement需要整合减少内存
            CBBoxElement cbox = new CBBoxElement(cellList);

            if (t.useHyperLink()) {
                cell.setNameHyperlinkGroup(t.createHyperLinkNameJavaScriptGroup(v));
            }
            if (t instanceof BIStyleTarget) {
                cell.setStyle(BITableStyle.getInstance().getNumberCellStyle(v, cell.getRow() % 2 == 1, t.useHyperLink()));
                BIStyleTarget sumCol = (BIStyleTarget) t;
                cbox.setName(sumCol.getValue());
                TargetStyle style = sumCol.getStyle();
                if (style != null) {
                    style.changeCellStyle(cell);
                }
            } else {
                cell.setStyle(BITableStyle.getInstance().getDimensionCellStyle(cell.getValue() instanceof Number, cell.getRow() % 2 == 1, t.useHyperLink()));
            }
            cbox.setType(CellConstant.CBCELL.ROWFIELD);
            cell.setBoxElement(cbox);
            cells[cell.getColumn()][cell.getRow()] = cell;
        }
    }

}