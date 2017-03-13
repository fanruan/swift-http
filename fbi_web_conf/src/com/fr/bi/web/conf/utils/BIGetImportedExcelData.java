package com.fr.bi.web.conf.utils;

import com.fr.base.FRContext;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.data.db.excel.BIExcelDataModel;
import com.fr.bi.stable.data.db.excel.BIExcelTableData;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.ColumnRow;

import java.io.File;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Young's on 2016/3/15.
 */
public class BIGetImportedExcelData {
    private String fullFileName;
    private File file;
    private static String DATA_PATH = FRContext.getCurrentEnv().getPath() + BIBaseConstant.EXCELDATA.EXCEL_DATA_PATH;

    public File getFile() {
        return file;
    }

    public BIGetImportedExcelData(String fullFileName) {
        this.fullFileName = fullFileName;
        File parentFile = new File(DATA_PATH);
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        file = new File(parentFile, fullFileName);
    }

    public BIExcelTableData getExcelTableData() {
        return new BIExcelTableData(file.getAbsolutePath());
    }

    public JSONObject getFieldsAndPreviewData() throws Exception {
        JSONObject jo = new JSONObject();
        BIExcelDataModel excelDataModel = getExcelTableData().createDataModel();

        String[] columnNames = excelDataModel.onlyGetColumnNames();
        int[] columnTypes = excelDataModel.onlyGetColumnTypes();
        if (columnNames.length == 0) {
            return jo;
        }
        //字段名和类型 统一成其他地方的按类型分组
        JSONArray fieldsJa = new JSONArray();
        JSONArray stringJA = new JSONArray();
        JSONArray numberJA = new JSONArray();
        JSONArray dateJA = new JSONArray();
        for (int i = 0; i < columnNames.length; i++) {
            JSONObject oneJo = new JSONObject();
            oneJo.put("field_name", columnNames[i]);
            oneJo.put("field_type", columnTypes[i]);
            oneJo.put("is_usable", true);
            oneJo.put("is_enable", true);
            stringJA.put(oneJo);
        }
        fieldsJa.put(stringJA);
        fieldsJa.put(numberJA);
        fieldsJa.put(dateJA);

        //数据
        int rowCount = Math.min(excelDataModel.getDataList().size(), BIBaseConstant.PREVIEW_COUNT);
        JSONArray dataJa = new JSONArray();
        try {
            for (int i = 0; i < rowCount; i++) {
                JSONArray oneJa = new JSONArray();
                for (int j = 0; j < columnNames.length; j++) {
                    oneJa.put(excelDataModel.getValueAt4Preview(i, j));
                }
                dataJa.put(oneJa);
            }
<<<<<<< HEAD
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage());
=======
        }catch (Exception e){
            BILoggerFactory.getLogger().error(e.getMessage(),e);
>>>>>>> 67b55d486e769f445942f15883303ca839ffd092
            return jo;
        }

        //合并规则
        Map<ColumnRow, ColumnRow> mergeInfo = excelDataModel.getMergeInfos();
        JSONArray mergeInfoJa = new JSONArray();
        if (mergeInfo != null) {
            for (Map.Entry<ColumnRow, ColumnRow> m : mergeInfo.entrySet()) {
                JSONArray tempJa = new JSONArray();

                ColumnRow s = m.getKey();
                JSONArray startJa = new JSONArray();
                startJa.put(s.getColumn());
                startJa.put(s.getRow());

                ColumnRow e = m.getValue();
                JSONArray endJa = new JSONArray();
                endJa.put(e.getColumn());
                endJa.put(e.getRow());

                tempJa.put(startJa);
                tempJa.put(endJa);
                mergeInfoJa.put(tempJa);
            }
        }

        //文件名
        String fileName = this.fullFileName.substring(UUID.randomUUID().toString().length());
        jo.put("fileName", fileName);
        jo.put("fields", fieldsJa);
        jo.put("data", dataJa);
        jo.put("mergeInfos", mergeInfoJa);
        return jo;
    }

}
