package com.fr.swift.core.rpc;

import com.fr.cluster.rpc.base.ClusterInvoker;
import com.fr.swift.structure.lru.ConcurrentCacheHashMap;

import java.util.Map;

/**
 * This class created on 2018/12/29
 *
 * @author Lucifer
 * @description
 */
public class InvokerCache {

    public static InvokerCache INSTANCE = new InvokerCache();

    private Map<Class, ClusterInvoker> invokerMap;

    private InvokerCache() {
        this.invokerMap = new ConcurrentCacheHashMap<Class, ClusterInvoker>();
    }

    public static InvokerCache getInstance() {
        return INSTANCE;
    }

    public void bindInvoker(Class clazz, ClusterInvoker invoker) {
        invokerMap.put(clazz, invoker);
    }

    public ClusterInvoker getInvoker(Class clazz) {
        return invokerMap.get(clazz);
    }
}
