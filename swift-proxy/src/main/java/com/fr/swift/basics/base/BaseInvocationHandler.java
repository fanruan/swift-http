package com.fr.swift.basics.base;

import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreater;
import com.fr.swift.basics.InvokerHandler;
import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.annotation.InvokeMethod;
import com.fr.swift.local.LocalInvoker;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.util.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author yee
 * @date 2018/10/23
 */
public class BaseInvocationHandler implements InvokerHandler {

    private InvokerCreater invokerCreater;

    public BaseInvocationHandler(InvokerCreater invokerCreater) {
        this.invokerCreater = invokerCreater;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Assert.isTrue(method.isAnnotationPresent(InvokeMethod.class), "Method must be wrapped by " + InvokeMethod.class);

        //单机直接invoke本地，不调用远程
        if (!SwiftProperty.getProperty().isCluster()) {
            Invoker invoker = new LocalInvoker(ProxyServiceRegistry.INSTANCE.getService(method.getDeclaringClass()), method.getDeclaringClass(), null);
            return invoker.invoke(new SwiftInvocation(method, args)).recreate();
        }
        InvokeMethod invokeMethod = method.getAnnotation(InvokeMethod.class);
        Class<? extends ProcessHandler> handlerInterface = invokeMethod.value();
        Class<? extends ProcessHandler> handlerClass = ProxyProcessHandlerRegistry.INSTANCE.getHandler(handlerInterface);
        Constructor<? extends ProcessHandler> cons = handlerClass.getDeclaredConstructor(InvokerCreater.class);
        ProcessHandler handler = cons.newInstance(invokerCreater);
        return handler.processResult(method, invokeMethod.target(), args);
    }

}
