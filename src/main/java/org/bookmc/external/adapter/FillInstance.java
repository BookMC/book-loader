package org.bookmc.external.adapter;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to declare by mods if they would like
 * for their instance to be automatically
 * declared by the language adapter...
 *
 * For more info: see {@link org.bookmc.external.adapter.java.JavaLanguageAdapter}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FillInstance {
}
