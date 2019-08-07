package com.fr.swift.config.command.impl;

import com.fr.swift.config.command.SwiftConfigCommand;
import com.fr.swift.config.command.SwiftConfigCommandBus;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigTransaction;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2019-07-30
 */
public abstract class BaseSwiftConfigCommandBus<T> implements SwiftConfigCommandBus<T> {

    @Override
    public <R> R transaction(SwiftConfigCommand<R> fn) throws SQLException {
        try (ConfigSession session = createSession()) {
            ConfigTransaction tx = session.beginTransaction();
            R apply = fn.apply(session);
            tx.commit();
            return apply;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    /**
     * 创建session
     *
     * @return
     * @throws SQLException
     */
    protected abstract ConfigSession createSession() throws SQLException;
}
