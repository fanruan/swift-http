package com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.operation;

import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.setting.ICellFormatSetting;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.utils.BITableCellFormatHelper;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.stable.StableUtils;

/**
 * Created by Kary on 2017/4/10.
 */
public class BITableCellNumberFormatOperation extends BITableCellFormatOperation {

    public BITableCellNumberFormatOperation(ICellFormatSetting ICellFormatSetting) {
        this.iCellFormatSetting = ICellFormatSetting;
    }

    @Override
    public String formatItemTextValues(String text) throws Exception {
        if (BIStringUtils.isEmptyString(text) || !StableUtils.isNumber(text)) {
            return text;
        }
        return BITableCellFormatHelper.targetValueFormat(iCellFormatSetting.createJSON(), text);
    }

    @Override
    public String formatHeaderText(String headerText) throws Exception {
        return BITableCellFormatHelper.headerTextFormat(iCellFormatSetting.createJSON(), headerText);
    }

    protected String getTextAlign() {
        return "right";
    }
}
