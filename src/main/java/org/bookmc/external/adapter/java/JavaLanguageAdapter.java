package org.bookmc.external.adapter.java;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bookmc.external.adapter.BookLanguageAdapter;
import org.bookmc.external.adapter.FillInstance;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * The default language adapter built for Java. This language adapter also checks for instance fields
 * meaning it should have compatibility with things such as Kotlin objects with errors occurring when
 * trying to initiate the instance.
 *
 * @author ChachyDev
 */
public class JavaLanguageAdapter implements BookLanguageAdapter {
    private final Logger LOGGER = LogManager.getLogger(this);

    @Override
    public Object createInstance(Class<?> clazz) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        try {
            // Let's be kind and look for instance fields :)
            // Since we're kind Kotlin objects should work
            // by default :)
            Field instance = clazz.getDeclaredField("INSTANCE");
            Object obj =  instance.get(null);
            if (obj == null) {
                if (instance.isAnnotationPresent(FillInstance.class)) {
                    // If we're requested to we fill in the instance
                    // In other words we just create the instance ourself
                    // while also setting the value to it
                    obj = createInstance0(clazz);
                    if (Modifier.isStatic(instance.getModifiers())) {
                        instance.set(null, obj);
                    } else {
                        LOGGER.error("Failed to set INSTANCE field for {}, this was because it is not static", clazz.getName());
                    }
                } else {
                    throw new NoSuchFieldException();
                }
            }

            return obj;
        } catch (NoSuchFieldException ignored) {
            // INSTANCE field doesn't exist
        }

        return createInstance0(clazz);
    }

    private Object createInstance0(Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return clazz.getConstructor().newInstance();
    }
}
