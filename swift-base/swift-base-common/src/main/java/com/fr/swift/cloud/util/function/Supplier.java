package com.fr.swift.cloud.util.function;

/**
 * @author anchore
 * @date 2018/4/9
 * <p>
 * 生产者
 */
public interface Supplier<E> {
    E get();
}