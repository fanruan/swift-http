package com.fr.swift.beans.annotation.handle;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.handler.AnnotationHandlerContext;
import com.fr.swift.beans.annotation.handler.SwiftAutowiredHandler;
import com.fr.swift.beans.annotation.handler.SwiftInitHandler;
import com.fr.swift.beans.factory.BeanFactory;
import junit.framework.TestCase;

import java.lang.reflect.InvocationTargetException;

/**
 * @author anner
 * @this class created on date 19-8-14
 * @description
 */
public class SwiftInitHandlerTest extends TestCase {
    @Override
    protected void setUp() throws Exception {
        BeanFactory beanFactory= SwiftContext.get();
        beanFactory.registerPackages("com.fr.swift.beans.annotation.handle");
        beanFactory.init();
        super.setUp();
    }
    public void testProcess() throws InvocationTargetException, IllegalAccessException {
        AnnotationHandlerContext annotationHandlerContext=AnnotationHandlerContext.getInstance();
        BeanWithMethod beanWithMethod= SwiftContext.get().getBean(BeanWithMethod.class);
        annotationHandlerContext.process(beanWithMethod,BeanWithMethod.class);
        assertEquals(1,beanWithMethod.number);
    }
}
