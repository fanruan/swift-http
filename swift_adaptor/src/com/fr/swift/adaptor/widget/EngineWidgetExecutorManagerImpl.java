package com.fr.swift.adaptor.widget;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.analysis.bean.operator.add.group.custom.number.NumberMaxAndMinValue;
import com.finebi.conf.internalimp.dashboard.widget.detail.DetailWidget;
import com.finebi.conf.internalimp.dashboard.widget.table.StringControlWidget;
import com.finebi.conf.internalimp.dashboard.widget.table.TableWidget;
import com.finebi.conf.service.engine.excutor.EngineWidgetExecutorManager;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.finebi.conf.structure.result.BIStringDetailResult;
import com.finebi.conf.structure.result.table.BIGroupNode;

/**
 * @author anchore
 * @date 2018/2/26
 */
public class EngineWidgetExecutorManagerImpl implements EngineWidgetExecutorManager {
    @Override
    public BIGroupNode visit(TableWidget tableWidget) {
        return null;
    }

    @Override
    public BIDetailTableResult visit(DetailWidget detailWidget) {
        return null;
    }

    @Override
    public BIStringDetailResult visit(StringControlWidget detailWidget) {
        return null;
    }

    @Override
    public NumberMaxAndMinValue getFieldMaxAndMinValueByFieldId(String fieldId) {
        return null;
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }
}