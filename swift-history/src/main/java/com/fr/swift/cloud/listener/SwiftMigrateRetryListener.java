package com.fr.swift.cloud.listener;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.dao.NodeInfoService;
import com.fr.swift.cloud.event.SwiftEventDispatcher;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.service.TaskService;
import com.fr.swift.cloud.service.event.NodeEvent;
import com.fr.swift.cloud.service.event.NodeMessage;

/**
 * @author Heng.J
 * @date 2020/11/13
 * @description
 * @since swift-1.2.0
 */
public class SwiftMigrateRetryListener implements MigStrategyListener {

    private static final SwiftMigrateRetryListener LISTENER = new SwiftMigrateRetryListener();

    private final NodeInfoService nodeInfoService = SwiftContext.get().getBean(NodeInfoService.class);

    private final TaskService taskService = SwiftContext.get().getBean(TaskService.class);

    public static void listen() {
        SwiftEventDispatcher.listen(NodeEvent.RETRY_DISTRIBUTE, LISTENER);
    }

    public static void remove() {
        SwiftEventDispatcher.remove(LISTENER);
    }

    @Override
    public void on(NodeMessage nodeMessage) {
        String clusterId = nodeMessage.getClusterId();
        SwiftLoggers.getLogger().info("Start to update or retry distribute migrateTask to {} ", clusterId);
        nodeInfoService.getMigrateInfosById(clusterId).stream().findFirst().ifPresent(taskInfo -> taskService.distributeTask(taskInfo, clusterId));
    }
}