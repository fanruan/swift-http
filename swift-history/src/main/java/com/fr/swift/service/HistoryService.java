package com.fr.swift.service;

import com.fr.swift.query.query.QueryBean;
import com.fr.swift.source.SwiftResultSet;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.Set;

/**
 * @author yee
 * @date 2018/6/5
 */
public interface HistoryService extends SwiftService {
    /**
     * 查询
     *
     * @param queryInfo 查询描述
     * @return 数据
     */
    SwiftResultSet query(QueryBean queryInfo) throws SQLException;

    /**
     * 从共享存储加载
     *
     * @param remoteUris
     * @throws IOException
     */
    void load(Set<URI> remoteUris) throws IOException;
}
