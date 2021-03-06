package com.fr.swift.cloud.executor.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Heng.J
 * @date 2020/10/29
 * @description
 * @since swift-1.2.0
 */
@Entity
@Table(name = "fine_swift_block_tasks")
public class SwiftBlockTaskEntity implements Serializable {

    private static final long serialVersionUID = -7999955851694196509L;

    @Id
    private String id;

    @Column(name = "blockingIndex")
    private String blockingIndex;

    @Column(name = "taskContent", length = 4000)
    private String taskContent;

    @Column(name = "createTime")
    private long createTime;

    public SwiftBlockTaskEntity() {
    }

    public SwiftBlockTaskEntity(String id, String blockingIndex, String taskContent) {
        this.id = id;
        this.blockingIndex = blockingIndex;
        this.taskContent = taskContent;
        this.createTime = System.currentTimeMillis();
    }
}
