package com.fr.swift.source;

import java.util.List;

/**
 * Create by lifan on 2019-06-14 14:55
 */
public interface MutableRow extends Row {
    Object getValue(int index);

    void addElement(Object object);

    void addAllRowElement(MutableRow mutableRow);

    void setRow(List<Object> values);

    void removeElement(int index);
}
