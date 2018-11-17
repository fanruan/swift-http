package com.fr.swift.task.service;


import com.fr.swift.source.SourceKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class created on 2018/11/16
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftServiceBlockingQueue extends LinkedBlockingQueue<ServiceCallable> implements ServiceBlockingQueue {

    private Map<SourceKey, Integer> sourceKeyCountMap;

    public SwiftServiceBlockingQueue(int capacity) {
        super(capacity);
        sourceKeyCountMap = new ConcurrentHashMap<SourceKey, Integer>();
    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public void put(ServiceCallable serviceCallable) throws InterruptedException {
        super.put(serviceCallable);
        synchronized (this) {
            if (!sourceKeyCountMap.containsKey(serviceCallable.getKey())) {
                sourceKeyCountMap.put(serviceCallable.getKey(), 0);
            }
            int num = sourceKeyCountMap.get(serviceCallable.getKey());
            sourceKeyCountMap.put(serviceCallable.getKey(), ++num);
        }
    }

    @Override
    public ServiceCallable take() throws InterruptedException {
        ServiceCallable serviceCallable = super.take();
        synchronized (this) {
            int num = sourceKeyCountMap.get(serviceCallable.getKey());
            sourceKeyCountMap.put(serviceCallable.getKey(), --num);
        }
        return serviceCallable;
    }

    @Override
    public ServiceCallable poll() {
        ServiceCallable serviceCallable = super.poll();
        synchronized (this) {
            int num = sourceKeyCountMap.get(serviceCallable.getKey());
            sourceKeyCountMap.put(serviceCallable.getKey(), --num);
        }
        return serviceCallable;
    }

    @Override
    public Integer getNumBySourceKey(SourceKey sourceKey) {
        synchronized (this) {
            return sourceKeyCountMap.get(sourceKey);
        }
    }
}
