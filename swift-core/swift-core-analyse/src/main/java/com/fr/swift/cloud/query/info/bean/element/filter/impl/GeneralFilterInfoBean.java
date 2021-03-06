package com.fr.swift.cloud.query.info.bean.element.filter.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fr.swift.cloud.query.filter.SwiftDetailFilterType;
import com.fr.swift.cloud.query.info.bean.element.filter.FilterInfoBean;

/**
 * @author yee
 * @date 2018/6/22
 */
abstract class GeneralFilterInfoBean<T> implements FilterInfoBean<T> {

    @JsonProperty
    protected T filterValue;
    @JsonProperty
    protected SwiftDetailFilterType type;

    @Override
    public SwiftDetailFilterType getType() {
        return type;
    }

    @Override
    public void setType(SwiftDetailFilterType type) {
        this.type = type;
    }
}
