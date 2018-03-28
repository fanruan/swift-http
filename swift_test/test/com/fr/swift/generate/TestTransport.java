package com.fr.swift.generate;

import com.fr.swift.flow.FlowRuleController;
import com.fr.swift.generate.history.transport.TableTransporter;
import com.fr.swift.generate.realtime.RealtimeDataTransporter;
import com.fr.swift.increment.Increment;
import com.fr.swift.source.DataSource;

/**
 * This class created on 2018/3/27
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class TestTransport {
    public static TableTransporter historyTransport(DataSource dataSource) throws Exception {
        TableTransporter transporter = new TableTransporter(dataSource);
        transporter.transport();
        return transporter;
    }


    public static RealtimeDataTransporter realtimeDataTransporter(DataSource dataSource, Increment increment, FlowRuleController flowRuleController) {
        RealtimeDataTransporter transporter = new RealtimeDataTransporter(dataSource, increment, flowRuleController);
        transporter.work();
        return transporter;
    }
}
