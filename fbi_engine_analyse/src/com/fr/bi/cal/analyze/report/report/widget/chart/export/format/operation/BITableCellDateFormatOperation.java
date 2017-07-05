package com.fr.bi.cal.analyze.report.report.widget.chart.export.format.operation;

import com.fr.bi.cal.analyze.report.report.widget.chart.export.format.setting.ICellFormatSetting;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.format.utils.BITableCellFormatHelper;
import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/4/10.
 */
public class BITableCellDateFormatOperation extends BITableCellFormatOperation {
    private int typeGroup;

    public BITableCellDateFormatOperation(int groupType, ICellFormatSetting ICellFormatSetting) {
        this.typeGroup = groupType;
        iCellFormatSetting = ICellFormatSetting;
    }

    @Override
    public String formatItemTextValues(String text) throws Exception {
         JSONObject format = null != iCellFormatSetting ? iCellFormatSetting.createJSON() : new JSONObject();
            return BITableCellFormatHelper.dateFormat(format, typeGroup, text);
    }

    @Override
    public String formatHeaderText(String headerText) throws Exception {
        return headerText;
    }
}
