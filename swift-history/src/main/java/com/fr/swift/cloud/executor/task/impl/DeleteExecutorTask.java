package com.fr.swift.cloud.executor.task.impl;

import com.fr.swift.cloud.base.json.JsonBuilder;
import com.fr.swift.cloud.db.Where;
import com.fr.swift.cloud.db.impl.SwiftWhere;
import com.fr.swift.cloud.executor.task.AbstractExecutorTask;
import com.fr.swift.cloud.executor.task.ExecutorTask;
import com.fr.swift.cloud.executor.task.job.Job;
import com.fr.swift.cloud.executor.task.job.impl.DeleteJob;
import com.fr.swift.cloud.executor.type.DBStatusType;
import com.fr.swift.cloud.executor.type.ExecutorTaskType;
import com.fr.swift.cloud.executor.type.LockType;
import com.fr.swift.cloud.executor.type.SwiftTaskType;
import com.fr.swift.cloud.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.cloud.query.query.FilterBean;
import com.fr.swift.cloud.source.SourceKey;

/**
 * This class created on 2019/2/14
 *
 * @author Lucifer
 * @description
 */
public class DeleteExecutorTask extends AbstractExecutorTask<Job> {

    public DeleteExecutorTask(SourceKey sourceKey, Where where) throws Exception {
        super(sourceKey,
                false,
                SwiftTaskType.DELETE,
                LockType.TABLE,
                sourceKey.getId(),
                DBStatusType.ACTIVE,
                new DeleteJob(sourceKey, where), 10);
    }

    public DeleteExecutorTask(SourceKey sourceKey, Where where, String clusterId) throws Exception {
        super(sourceKey,
                false,
                SwiftTaskType.DELETE,
                LockType.TABLE,
                sourceKey.getId(),
                DBStatusType.ACTIVE,
                new DeleteJob(sourceKey, where), 10);
        this.clusterId = clusterId;
    }

    public static ExecutorTask of(SourceKey sourceKey, Where where) throws Exception {
        return new DeleteExecutorTask(sourceKey, where);
    }

    public static ExecutorTask ofByCluster(SourceKey sourceKey, Where where, String clusterId) throws Exception {
        return new DeleteExecutorTask(sourceKey, where, clusterId);
    }

    public DeleteExecutorTask(SourceKey sourceKey, boolean persistent, ExecutorTaskType executorTaskType, LockType lockType,
                              String lockKey, DBStatusType dbStatusType, String taskId, long createTime, String taskContent,
                              int priority) throws Exception {
        super(sourceKey, persistent, executorTaskType, lockType, lockKey, dbStatusType, taskId, createTime, taskContent, priority);

        FilterBean filterBean = JsonBuilder.readValue(taskContent, FilterInfoBean.class);
        Where where = new SwiftWhere(filterBean);
        this.job = new DeleteJob(sourceKey, where);
    }
}
