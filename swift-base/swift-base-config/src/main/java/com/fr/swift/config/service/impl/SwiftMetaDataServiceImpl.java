package com.fr.swift.config.service.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.SwiftDao;
import com.fr.swift.config.dao.SwiftDaoImpl;
import com.fr.swift.config.entity.SwiftMetaDataEntity;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/6
 */
@SwiftBean(name = "swiftMetaDataService")
public class SwiftMetaDataServiceImpl implements SwiftMetaDataService {
    private SwiftDao<SwiftMetaDataEntity> dao = new SwiftDaoImpl<>(SwiftMetaDataEntity.class);

    @Override
    public void saveMeta(SwiftMetaData meta) {
        dao.insert((SwiftMetaDataEntity) meta);
    }

    @Override
    public void updateMeta(SwiftMetaData newMeta) {
        dao.update((SwiftMetaDataEntity) newMeta);
    }

    @Override
    public List<SwiftMetaData> getAllMetas() {
        final List<SwiftMetaData> selectAll = (List<SwiftMetaData>) dao.selectAll();
        return selectAll;
    }

    @Override
    public List<SwiftMetaData> getMetasBySchema(SwiftDatabase schema) {
        final List<?> metas = dao.selectQuery((query, builder, from) ->
                query.select(from).where(builder.equal(from.get("swiftDatabase"), schema)));
        if (metas.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return (List<SwiftMetaData>) metas;
    }

    @Override
    public SwiftMetaData getMeta(final SourceKey tableKey) {
        final List<?> metas = dao.selectQuery((query, builder, from) ->
                query.select(from).where(builder.equal(from.get("id"), tableKey.getId())));
        if (metas.isEmpty()) {
            return null;
        }
        SwiftMetaDataEntity entity = (SwiftMetaDataEntity) metas.get(0);
        return entity;
    }

    @Override
    public boolean existsMeta(SourceKey tableKey) {
        return getMeta(tableKey) != null;
    }

    @Override
    public void deleteMeta(final SourceKey tableKey) {
        dao.deleteQuery((query, builder, from) ->
                query.select(from)
                        .where(builder.equal(from.get("id"), tableKey.getId())));
    }

    @Override
    public List<SwiftMetaData> getFuzzyMetaData(SourceKey tableKey) {
        List<?> metas = dao.selectQuery((query, builder, from) ->
                query.select(from).where(builder.like(from.get("id"), "%" + tableKey.getId() + "%")));
        return (List<SwiftMetaData>) metas;
    }
}
