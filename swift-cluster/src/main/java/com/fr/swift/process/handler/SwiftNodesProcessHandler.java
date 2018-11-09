package com.fr.swift.process.handler;

import com.fr.swift.basics.AsyncRpcCallback;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreater;
import com.fr.swift.basics.RpcFuture;
import com.fr.swift.basics.URL;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.handler.AbstractProcessHandler;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.event.base.EventResult;
import com.fr.swift.heart.NodeState;
import com.fr.swift.heart.NodeType;
import com.fr.swift.util.MonitorUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * This class created on 2018/11/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftNodesProcessHandler extends AbstractProcessHandler implements NodesProcessHandler {

    public SwiftNodesProcessHandler(InvokerCreater invokerCreater) {
        super(invokerCreater);
    }

    /**
     * 同步所有NodeState信息
     *
     * @param method
     * @param target
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object processResult(Method method, Target target, Object... args) throws Throwable {
        Class proxyClass = method.getDeclaringClass();
        Class<?>[] parameterTypes = method.getParameterTypes();
        String methodName = method.getName();
        try {
            MonitorUtil.start();
            List<URL> urlList = processUrl(target, args);

            final List<EventResult> resultList = new ArrayList<EventResult>();
            final CountDownLatch latch = new CountDownLatch(urlList.size());
            for (final URL url : urlList) {
                Invoker invoker = invokerCreater.createAsyncInvoker(proxyClass, url);
                RpcFuture rpcFuture = (RpcFuture) invoke(invoker, proxyClass, method, methodName, parameterTypes, args);
                rpcFuture.addCallback(new AsyncRpcCallback() {
                    @Override
                    public void success(Object result) {
                        EventResult eventResult = new EventResult(url.getDestination().getId(), true);
                        resultList.add(eventResult);
                        latch.countDown();
                    }

                    @Override
                    public void fail(Exception e) {
                        EventResult eventResult = new EventResult(url.getDestination().getId(), false);
                        eventResult.setError(e.getMessage());
                        resultList.add(eventResult);
                        latch.countDown();
                    }
                });
            }
            latch.await();
            return resultList;
        } finally {
            MonitorUtil.finish(methodName);
        }
    }

    /**
     * 根据nodestate算出所有online节点的url
     *
     * @param target
     * @param args   List<NodeState>
     * @return
     */
    @Override
    public List<URL> processUrl(Target target, Object... args) {
        List<NodeState> nodeStateList = (List<NodeState>) args[0];
        List<URL> urlList = new ArrayList<URL>();
        for (NodeState nodeState : nodeStateList) {
            if (nodeState.getNodeType() == NodeType.ONLINE) {
                urlList.add(UrlSelector.getInstance().getFactory().getURL(nodeState.getHeartBeatInfo().getNodeId()));
            }
        }
        return urlList;
    }
}
