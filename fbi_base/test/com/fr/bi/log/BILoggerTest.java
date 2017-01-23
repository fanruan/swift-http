package com.fr.bi.log;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.general.ComparatorUtils;
import junit.framework.TestCase;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by roy on 2017/1/13.
 */
public class BILoggerTest extends TestCase {
    public void testLoggerCache() {
        try {
            long cacheContent = System.currentTimeMillis();
            BILoggerFactory.cacheLoggerInfo("BILoggerTest", "logCacheStartTime", cacheContent);
            assertTrue(ComparatorUtils.equals(cacheContent, BILoggerFactory.getLoggerCacheValue("BILoggerTest", "logCacheStartTime")));
            BILoggerFactory.clearLoggerCacheValue("BILoggerTest");
            assertTrue(ComparatorUtils.equals(null, BILoggerFactory.getLoggerCacheValue("BILoggerTest", "logCacheStartTime")));
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
    }


    public void testMapClearGC() {
        int counter = 0;
        while (true) {
            counter++;
            Map<String, Map<String, String>> outMap = new ConcurrentHashMap<String, Map<String, String>>();
            Map<String, String> innerMap = new ConcurrentHashMap<String, String>();
            innerMap.put("innerKey", "value");
            outMap.put("outerKey", innerMap);
            outMap.clear();
            if (counter % 100 == 0) {
                System.out.println("Free memory after count " + counter + "is" + getFreeMemory() + "M");
            }
        }
    }

    private long getFreeMemory() {
        return Runtime.getRuntime().freeMemory() / (1024 * 1024);
    }


}