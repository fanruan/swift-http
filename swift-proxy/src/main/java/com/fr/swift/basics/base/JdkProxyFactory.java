package com.fr.swift.basics.base;


import com.fr.swift.basic.URL;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.InvokerHandler;
import com.fr.swift.service.listener.SwiftServiceListenerManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018/10/24
 */
public class JdkProxyFactory extends BaseProxyFactory {

    private InvocationHandler handler;

    public JdkProxyFactory(InvokerCreator invokerCreator) {
        super(invokerCreator);
        this.handler = createInvokerHandler();
    }

    @Override
    protected InvokerHandler createInvokerHandler() {
        return new JdkProxyInvokeHandler(invokerCreator);
    }

    @Override
    public <T> T getProxy(Class<T> proxyIface) {
        return proxyIface.cast(Proxy.newProxyInstance(proxyIface.getClassLoader(), new Class[]{proxyIface}, handler));
    }

    @Override
    public <S, T> Map<URL, T> getPeerProxies(Class<S> serviceClass, Class<T> proxyIface) {
        Map<URL, T> urlToProxy = new HashMap<URL, T>();
        Set<URL> urls = SwiftServiceListenerManager.getInstance().getNodeUrls(serviceClass);
        for (URL url : urls) {
            T proxy = proxyIface.cast(Proxy.newProxyInstance(proxyIface.getClassLoader(), new Class[]{proxyIface}, new PeerInvocationHandler(proxyIface, url, invokerCreator)));
            urlToProxy.put(url, proxy);
        }
        return urlToProxy;
    }
}
