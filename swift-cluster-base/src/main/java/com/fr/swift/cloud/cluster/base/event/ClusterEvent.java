package com.fr.swift.cloud.cluster.base.event;

import com.fr.swift.cloud.event.SwiftEvent;

/**
 * This class created on 2020/4/26
 *
 * @author Kuifang.Liu
 */
public enum ClusterEvent implements SwiftEvent {
    /**
     * 成为主节点
     */
    BECOME_MASTER,
    /**
     * 加入集群
     */
    JOIN,
    /**
     * 离开集群
     */
    LEFT
}
