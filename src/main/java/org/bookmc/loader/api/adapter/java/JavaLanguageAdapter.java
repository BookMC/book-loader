package org.bookmc.loader.api.adapter.java;

import org.bookmc.loader.api.adapter.BookLanguageAdapter;

import java.lang.reflect.Field;

public class JavaLanguageAdapter implements BookLanguageAdapter {
    @Override
    public Object createInstance(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        try {
            Field instance = clazz.getDeclaredField("INSTANCE");
            return instance.get(null);
        } catch (NoSuchFieldException ignored) {

        }

        return clazz.newInstance();
    }
}
