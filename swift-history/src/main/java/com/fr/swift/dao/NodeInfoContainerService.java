package com.fr.swift.dao;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.entity.SwiftNodeInfo;
import com.fr.swift.db.NodeType;
import com.fr.swift.service.info.TaskInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Heng.J
 * @date 2020/10/27
 * @description
 * @since swift-1.2.0
 */
@SwiftBean(name = "NodeInfoService")
public class NodeInfoContainerService implements NodeInfoService {

    @Override
    public void clearCache() {
        NodeInfoContainer.MASTER.clearCache();
    }

    @Override
    public void flushCache() {
        NodeInfoContainer.MASTER.flushCache();
    }

    @Override
    public Map<String, String> getIdBlockIndexMap() {
        return NodeInfoContainer.MASTER.getIdBlockIndexMap();
    }

    @Override
    public SwiftNodeInfo getNodeInfoById(String clusterId) {
        return NodeInfoContainer.MASTER.getNodeInfoById(clusterId);
    }

    @Override
    public Set<String> getNodeInfosByType(NodeType nodeType) {
        return NodeInfoContainer.MASTER.getNodeInfosByType(nodeType);
    }

    @Override
    public List<TaskInfo> getMigrateInfosById(String clusterId) {
        return NodeInfoContainer.MASTER.getMigrateInfosById(clusterId);
    }

    @Override
    public Set<String> getMigrateNodeIds() {
        return NodeInfoContainer.MASTER.getMigrateNodeIds();
    }

    @Override
    public Set<String> getTaskTargets(String migIndex) {
        return NodeInfoContainer.MASTER.getTaskTargets(migIndex);
    }

    @Override
    public void activateNodeMigIndex(String clusterId, String migrateIndex) {
        NodeInfoContainer.MASTER.activateNodeMigIndex(clusterId, migrateIndex);
    }

    @Override
    public void blockNodeMigIndex(String clusterId, String migrateIndex) {
        NodeInfoContainer.MASTER.blockNodeMigIndex(clusterId, migrateIndex);
    }

    @Override
    public void updateBlockMigIndex(String clusterId) {
        NodeInfoContainer.MASTER.updateBlockMigIndex(clusterId);
    }
}