package com.fr.swift.cloud.query.filter.info;

import com.fr.swift.cloud.source.core.Core;
import com.fr.swift.cloud.source.core.CoreGenerator;

/**
 * Created by pony on 2018/4/10.
 */
public abstract class AbstractFilterInfo implements FilterInfo {
    @Override
    public Core fetchObjectCore() {
        try {
            return new CoreGenerator(this).fetchObjectCore();
        } catch (Exception ignore) {

        }
        return Core.EMPTY_CORE;
    }
}
