package org.bookmc.loader.impl.mixin;

import org.bookmc.loader.impl.launch.BookLauncher;
import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IPropertyKey;

public class MixinBookGlobalPropertyService implements IGlobalPropertyService {
    @Override
    public IPropertyKey resolveKey(String name) {
        return new MixinStringPropertyKey(name);
    }

    private String toString(IPropertyKey key) {
        return ((MixinStringPropertyKey) key).getKey();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(IPropertyKey key) {
        return (T) BookLauncher.getProperties().get(toString(key));
    }

    @Override
    public void setProperty(IPropertyKey key, Object value) {
        BookLauncher.getProperties().put(toString(key), value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(IPropertyKey key, T defaultValue) {
        return (T) BookLauncher.getProperties().getOrDefault(toString(key), defaultValue);
    }

    @Override
    public String getPropertyString(IPropertyKey key, String defaultValue) {
        return BookLauncher.getProperties().getOrDefault(toString(key), defaultValue).toString();
    }
}
