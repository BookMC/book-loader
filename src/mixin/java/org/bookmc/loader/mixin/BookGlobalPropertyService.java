package org.bookmc.loader.mixin;

import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IPropertyKey;

import java.util.Objects;

public class BookGlobalPropertyService implements IGlobalPropertyService {
    @Override
    public IPropertyKey resolveKey(String name) {
        return new StringPropertyKey(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(IPropertyKey key) {
        return (T) System.getProperties().get(toString(key));
    }

    @Override
    public void setProperty(IPropertyKey key, Object value) {
        System.getProperties().put(toString(key), value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(IPropertyKey key, T defaultValue) {
        return (T) System.getProperties().getOrDefault(toString(key), defaultValue);
    }

    @Override
    public String getPropertyString(IPropertyKey key, String defaultValue) {
        return System.getProperty(toString(key), defaultValue);
    }

    private String toString(IPropertyKey key) {
        return ((StringPropertyKey) key).key;
    }

    record StringPropertyKey(String key) implements IPropertyKey {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StringPropertyKey that = (StringPropertyKey) o;
            return Objects.equals(key, that.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }
}
