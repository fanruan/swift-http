package com.fr.swift.cloud.source.alloter.impl;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.config.service.SwiftSegmentService;
import com.fr.swift.cloud.segment.SegmentService;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.source.alloter.AllotRule;
import com.fr.swift.cloud.source.alloter.RowInfo;
import com.fr.swift.cloud.source.alloter.SegmentInfo;
import com.fr.swift.cloud.source.alloter.SwiftSourceAlloter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/12/21
 */
public abstract class BaseSourceAlloter<A extends AllotRule, R extends RowInfo> implements SwiftSourceAlloter<A, R>, Cloneable {

    protected final SwiftSegmentService swiftSegmentService = SwiftContext.get().getBean(SwiftSegmentService.class);
    protected final SegmentService segmentService = SwiftContext.get().getBean(SegmentService.class);

    protected SourceKey tableKey;

    protected A rule;

    /**
     * 逻辑order到实际order的映射
     * 处理order竞争的
     */
    private Map<Integer, SegmentState> logicToReal = new HashMap<>();

    protected BaseSourceAlloter(SourceKey tableKey, A rule) {
        this.tableKey = tableKey;
        this.rule = rule;
    }

    @Override
    public SegmentInfo allot(R rowInfo) {
        int logicOrder = getLogicOrder(rowInfo);

        SegmentState segState;
        if (logicToReal.containsKey(logicOrder)) {
            // 已分配
            segState = logicToReal.get(logicOrder);
        } else {
            // 新分配
            segState = getInsertableSeg(logicOrder);
            logicToReal.put(logicOrder, segState);
        }
        if (segState.incrementAndGet() < rule.getCapacity()) {
            // 未满
            return segState.getSegInfo();
        }
        // 已满，再分配
        segState = getInsertableSeg(logicOrder);
        logicToReal.put(logicOrder, segState);
        segState.incrementAndGet();
        return segState.getSegInfo();
    }

    protected abstract SegmentState getInsertableSeg(int logicOrder);

    /**
     * 计算逻辑seg order
     *
     * @param rowInfo 行
     * @return logic order
     */
    protected abstract int getLogicOrder(R rowInfo);

    protected boolean isSegInserting(SegmentInfo segInfo) {
        for (SegmentState segState : logicToReal.values()) {
            if (segInfo.equals(segState.getSegInfo())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public A getAllotRule() {
        return rule;
    }


    protected static class SegmentState {

        private SegmentInfo segInfo;

        private int cursor = -1;

        public SegmentState(SegmentInfo segInfo) {
            this.segInfo = segInfo;
        }

        public SegmentState(SegmentInfo segInfo, int cursor) {
            this.segInfo = segInfo;
            this.cursor = cursor;
        }

        int incrementAndGet() {
            return ++cursor;
        }

        SegmentInfo getSegInfo() {
            return segInfo;
        }
    }

    public void setSourceKey(SourceKey sourceKey) {
        this.tableKey = sourceKey;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        BaseSourceAlloter clonedAlloter = (BaseSourceAlloter) super.clone();
        clonedAlloter.logicToReal = new HashMap<>();
        return clonedAlloter;
    }
}