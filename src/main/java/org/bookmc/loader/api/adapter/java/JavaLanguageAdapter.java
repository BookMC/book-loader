package org.bookmc.loader.api.adapter.java;

import org.bookmc.loader.api.adapter.BookLanguageAdapter;

import java.lang.reflect.Field;

/**
 * The default language adapter built for Java. This language adapter also checks for instance fields
 * meaning it should have compatibility with things such as Kotlin objects with errors ocurring when
 * trying to initiate the instance.
 *
 * @author ChachyDev
 */
public class JavaLanguageAdapter implements BookLanguageAdapter {
    @Override
    public Object createInstance(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        try {
            // Let's be kind and look for instance fields :)
            Field instance = clazz.getDeclaredField("INSTANCE");
            return instance.get(null);
        } catch (NoSuchFieldException ignored) {

        }

        return clazz.newInstance();
    }
}
