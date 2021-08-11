package org.bookmc.loader.impl.launch.transform.mixin;

import org.bookmc.loader.impl.launch.Launcher;
import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IPropertyKey;

public class MixinQuiltGlobalPropertyService implements IGlobalPropertyService {
    @Override
    public IPropertyKey resolveKey(String name) {
        return new QuiltStringPropertyKey(name);
    }

    private String toString(IPropertyKey key) {
        return ((QuiltStringPropertyKey) key).getKey();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(IPropertyKey key) {
        return (T) Launcher.getProperties().get(toString(key));
    }

    @Override
    public void setProperty(IPropertyKey key, Object value) {
        Launcher.getProperties().put(toString(key), value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(IPropertyKey key, T defaultValue) {
        return (T) Launcher.getProperties().getOrDefault(toString(key), defaultValue);
    }

    @Override
    public String getPropertyString(IPropertyKey key, String defaultValue) {
        return Launcher.getProperties().getOrDefault(toString(key), defaultValue).toString();
    }
}
