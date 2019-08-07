package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.dao.SwiftMetaDataDao;
import com.fr.swift.config.dao.impl.SwiftMetaDataDaoImpl;
import com.fr.swift.config.oper.BaseTransactionWorker;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigSessionCreator;
import com.fr.swift.config.oper.impl.BaseConfigSessionCreator;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.converter.ObjectConverter;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

/**
 * @author yee
 * @date 2019-01-04
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest(SwiftContext.class)
public class SwiftMetaDataServiceImplTest extends BaseServiceTest {
    private SwiftMetaDataService service;

    @Before
    public void setUp() throws Exception {
        // Generate by Mock Plugin
        PowerMock.mockStatic(SwiftContext.class);
        SwiftContext mockSwiftContext = PowerMock.createMock(SwiftContext.class);
        EasyMock.expect(SwiftContext.get()).andReturn(mockSwiftContext).anyTimes();
        PowerMock.replay(SwiftContext.class);


        // Generate by Mock Plugin
        final ConfigSession mockConfigSession = mockSession(SwiftMetaDataBean.class, mockData());

        BaseConfigSessionCreator mockBaseTransactionManager = new BaseConfigSessionCreator() {
            @Override
            public ConfigSession createSession() {
                return mockConfigSession;
            }
        };
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(ConfigSessionCreator.class))).andReturn(mockBaseTransactionManager).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(SwiftMetaDataDao.class))).andReturn(new SwiftMetaDataDaoImpl()).anyTimes();
        PowerMock.replay(mockSwiftContext);
        service = new SwiftMetaDataServiceImpl();
    }

    @Test
    public void addMetaData() {
        // Generate by Mock Plugin
        SwiftMetaData mockSwiftMetaData = PowerMock.createMock(SwiftMetaData.class);
        PowerMock.replayAll();
        service.addMetaData("table", mockSwiftMetaData);

// do test
        PowerMock.verifyAll();

    }

    @Test
    public void addMetaDatas() {
        Map<String, SwiftMetaData> metaDataMap = new HashMap<String, SwiftMetaData>();
        // Generate by Mock Plugin
        SwiftMetaData tableA = PowerMock.createMock(SwiftMetaData.class);
        SwiftMetaData tableB = PowerMock.createMock(SwiftMetaData.class);
        SwiftMetaData tableC = PowerMock.createMock(SwiftMetaData.class);
        PowerMock.replayAll();

        metaDataMap.put("tableA", tableA);
        metaDataMap.put("tableB", tableB);
        metaDataMap.put("tableC", tableC);
        service.addMetaDatas(metaDataMap);
        PowerMock.verifyAll();
    }

    @Test
    public void removeMetaDatas() {
        service.removeMetaDatas(new SourceKey("table"));
        PowerMock.verifyAll();
    }

    @Test
    public void updateMetaData() {
        // Generate by Mock Plugin
        SwiftMetaData mockSwiftMetaData = PowerMock.createMock(SwiftMetaData.class);
        PowerMock.replayAll();
        service.updateMetaData("table", mockSwiftMetaData);

// do test
        PowerMock.verifyAll();
    }

    @Test
    public void getAllMetaData() {
        Map<String, SwiftMetaData> all = service.getAllMetaData();
        assertFalse(all.isEmpty());
        PowerMock.verifyAll();
    }

    @Test
    public void getMetaDataByKey() {
        assertNull(service.getMetaDataByKey("a"));
        PowerMock.verifyAll();
    }

    @Test
    public void containsMeta() {
        assertFalse(service.containsMeta(new SourceKey("tableA")));
        PowerMock.verifyAll();
    }

    @Test
    public void cleanCache() throws NoSuchFieldException, IllegalAccessException {
        getAllMetaData();
        Field mapField = service.getClass().getDeclaredField("metaDataCache");
        mapField.setAccessible(true);
        assertFalse(((Map) mapField.get(service)).isEmpty());
        service.cleanCache(new String[]{"tableA", "tableB", "tableC"});
        assertTrue(((Map) mapField.get(service)).isEmpty());
        PowerMock.verifyAll();
    }

    @Test
    public void find() {
        assertFalse(service.find().isEmpty());
        PowerMock.verifyAll();
    }

    @Test
    public void saveOrUpdate() {
        // Generate by Mock Plugin
        SwiftMetaData mockSwiftMetaData = PowerMock.createMock(SwiftMetaData.class);
        EasyMock.expect(mockSwiftMetaData.getId()).andReturn("tableA").anyTimes();
        PowerMock.replayAll();
        service.saveOrUpdate(mockSwiftMetaData);

        PowerMock.verifyAll();

    }

    @Test
    public void testGetFuzzyMetaData() throws SQLException {
        PowerMockito.mockStatic(SwiftContext.class);
        BeanFactory beanFactory = Mockito.mock(BeanFactory.class);
        Mockito.when(SwiftContext.get()).thenReturn(beanFactory);
        ConfigSessionCreator configSessionCreator = Mockito.mock(ConfigSessionCreator.class);
        SwiftMetaDataDao swiftMetaDataDao = Mockito.mock(SwiftMetaDataDao.class);
        Mockito.when(beanFactory.getBean(ConfigSessionCreator.class)).thenReturn(configSessionCreator);
        Mockito.when(beanFactory.getBean(SwiftMetaDataDao.class)).thenReturn(swiftMetaDataDao);
        final ConfigSession configSession = Mockito.mock(ConfigSession.class);

        Mockito.when(configSessionCreator.doTransactionIfNeed(Mockito.any(BaseTransactionWorker.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                ConfigSessionCreator.TransactionWorker worker = invocationOnMock.getArgument(0);
                return worker.work(configSession);
            }
        });

        Mockito.when(swiftMetaDataDao.fuzzyFind(configSession, "a")).thenReturn(Collections.<SwiftMetaDataBean>emptyList());
        new SwiftMetaDataServiceImpl().getFuzzyMetaData("a");
        Mockito.verify(swiftMetaDataDao).fuzzyFind(configSession, "a");

    }

    private ObjectConverter[] mockData() {
        // Generate by Mock Plugin
        ObjectConverter converterA = PowerMock.createMock(ObjectConverter.class);
        SwiftMetaDataBean tableA = PowerMock.createMock(SwiftMetaDataBean.class);
        EasyMock.expect(tableA.getId()).andReturn("tableA").anyTimes();
        EasyMock.expect(converterA.convert()).andReturn(tableA).anyTimes();
        ObjectConverter converterB = PowerMock.createMock(ObjectConverter.class);
        SwiftMetaDataBean tableB = PowerMock.createMock(SwiftMetaDataBean.class);
        EasyMock.expect(tableB.getId()).andReturn("tableB").anyTimes();
        EasyMock.expect(converterB.convert()).andReturn(tableB).anyTimes();
        ObjectConverter converterC = PowerMock.createMock(ObjectConverter.class);
        SwiftMetaDataBean tableC = PowerMock.createMock(SwiftMetaDataBean.class);
        EasyMock.expect(tableC.getId()).andReturn("tableC").anyTimes();
        EasyMock.expect(converterC.convert()).andReturn(tableC).anyTimes();

        PowerMock.replay(converterA, converterB, converterC, tableA, tableB, tableC);

        return new ObjectConverter[]{
                converterA, converterB, converterC
        };
    }
}