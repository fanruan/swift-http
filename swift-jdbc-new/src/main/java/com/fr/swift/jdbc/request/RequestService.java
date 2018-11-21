package com.fr.swift.jdbc.request;

import com.fr.swift.jdbc.info.SqlInfo;
import com.fr.swift.jdbc.response.JdbcResponse;
import com.fr.swift.jdbc.rpc.JdbcExecutor;

/**
 * @author yee
 * @date 2018/11/16
 */
public interface RequestService {
    /**
     * execute auth
     *
     * @param sender
     * @param user
     * @param password
     * @param <T>
     * @return
     */
    <T extends JdbcResponse> T apply(JdbcExecutor sender, String user, String password);

    /**
     * execute query
     *
     * @param sender
     * @param sql
     * @param <T>
     * @return
     */
    <T extends JdbcResponse> T apply(JdbcExecutor sender, SqlInfo sql);

    <T extends JdbcResponse> T apply(JdbcExecutor sender, String requestJson);

    /**
     * execute auth with retry
     *
     * @param sender
     * @param user
     * @param password
     * @param retryTime
     * @param <T>
     * @return
     */
    <T extends JdbcResponse> T applyWithRetry(JdbcExecutor sender, String user, String password, int retryTime);

    /**
     * execute query with retry
     *
     * @param sender
     * @param sql
     * @param retryTime
     * @param <T>
     * @return
     */
    <T extends JdbcResponse> T applyWithRetry(JdbcExecutor sender, SqlInfo sql, int retryTime);

    <T extends JdbcResponse> T applyWithRetry(JdbcExecutor sender, String requestJson, int retryTime);
}
