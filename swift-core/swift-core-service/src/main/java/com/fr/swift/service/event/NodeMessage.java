package com.fr.swift.service.event;

import java.io.Serializable;

/**
 * @author Heng.J
 * @date 2020/11/3
 * @description
 * @since swift-1.2.0
 */
public class NodeMessage implements Serializable {

    private static final long serialVersionUID = 6312237404167456647L;

    private String clusterId;

    private String messageInfo;

    public NodeMessage(String clusterId, String messageInfo) {
        this.clusterId = clusterId;
        this.messageInfo = messageInfo;
    }

    public static NodeMessage of(String clusterId, String messageInfo) {
        return new NodeMessage(clusterId, messageInfo);
    }

    public String getClusterId() {
        return clusterId;
    }

    public String getMessageInfo() {
        return messageInfo;
    }
}
