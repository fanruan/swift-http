package com.fr.swift.source.etl.datamining;

import com.finebi.conf.algorithm.*;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.AlgorithmBean;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonas on 2018/3/12 9:00
 */
public class DataMiningOperator extends AbstractOperator {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(DataMiningOperator.class);

    private AlgorithmBean algorithmBean = null;

    private DMAbstractAlgorithm algorithm;

    public DataMiningOperator(AlgorithmBean algorithmBean) {
        this.algorithmBean = algorithmBean;
        this.algorithm = DMAlgorithmFactory.create(algorithmBean.getAlgorithmName());
    }

    public AlgorithmBean getAlgorithmBean() {
        return this.algorithmBean;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] tables) {
        SwiftMetaData table = tables[0];
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        try {
            DMRowMetaData inputData = new DMRowMetaData();
            for (int i = 0; i < table.getColumnCount(); i++) {
                SwiftMetaDataColumn column = table.getColumn(i + 1);
                inputData.addColMeta(new DMColMetaData(column.getName(), DMType.fromSwiftInt(column.getType())));
            }

            algorithm.init(algorithmBean, new DMDataModel(null, inputData));
            DMRowMetaData outputMetaData = algorithm.getOutputMetaData();
            for (DMColMetaData colMetaData : outputMetaData.getColMetas()) {
                columnList.add(new MetaDataColumn(colMetaData.getColName(), colMetaData.getColType().toSwiftInt()));
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        if (algorithm.isAddColumns()) {
            return OperatorType.EXTRA_TRUE;
        } else {
            return OperatorType.EXTRA_FALSE;
        }
    }

    @Override
    public List<String> getNewAddedName() {
        DMRowMetaData outputMetaData;
        List<String> list = new ArrayList<String>();
        try {
            outputMetaData = algorithm.getOutputMetaData();
            for (DMColMetaData colMetaData : outputMetaData.getColMetas()) {
                list.add(colMetaData.getColName());
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return list;
    }
}
