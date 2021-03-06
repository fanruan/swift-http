package com.fr.swift.cloud.config.service;

import com.fr.swift.cloud.annotation.service.DbService;

/**
 * @author yee
 * @date 2018/6/6
 */
@DbService
public interface SwiftCubePathService {
    /**
     * 设置cube更新路径
     *
     * @param path
     */
    boolean setSwiftPath(String path);

    /**
     * 获取cube更新路径
     *
     * @return
     */
    String getSwiftPath();

    void registerPathChangeListener(PathChangeListener listener);

    interface PathChangeListener {
        void changed(String path);
    }
}
