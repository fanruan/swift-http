package com.fr.swift.cloud.local;

import com.fr.swift.cloud.basic.URL;
import com.fr.swift.cloud.basics.Invoker;
import com.fr.swift.cloud.basics.InvokerCreator;
import com.fr.swift.cloud.basics.annotation.Target;
import com.fr.swift.cloud.basics.base.handler.AbstractProcessHandler;

import java.lang.reflect.Method;

/**
 * This class created on 2018/11/14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class LocalProcessHandler extends AbstractProcessHandler<URL> {

    public LocalProcessHandler(InvokerCreator invokerCreator) {
        super(invokerCreator);
    }

    @Override
    public Object processResult(Method method, Target[] targets, Object... args) throws Throwable {
        Class proxyClass = method.getDeclaringClass();
        Class<?>[] parameterTypes = method.getParameterTypes();
        String methodName = method.getName();
        Invoker invoker = invokerCreator.createSyncInvoker(proxyClass, null);
        return invoke(invoker, proxyClass, method, methodName, parameterTypes, args);
    }

    @Override
    protected URL processUrl(Target[] targets, Object... args) {
        return null;
    }
}
