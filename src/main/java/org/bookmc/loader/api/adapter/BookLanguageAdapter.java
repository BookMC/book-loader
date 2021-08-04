package org.bookmc.loader.api.adapter;

public interface BookLanguageAdapter {
    Object createInstance(Class<?> clazz) throws InstantiationException, IllegalAccessException;
}
