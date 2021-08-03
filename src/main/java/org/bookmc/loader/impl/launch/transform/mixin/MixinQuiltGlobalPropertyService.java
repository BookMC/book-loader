package org.bookmc.loader.impl.launch.transform.mixin;

import org.bookmc.loader.impl.launch.Launcher;
import org.spongepowered.asm.service.IGlobalPropertyService;

public class MixinQuiltGlobalPropertyService implements IGlobalPropertyService {
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(String key) {
        return (T) Launcher.getProperties().get(key);
    }

    @Override
    public void setProperty(String key, Object value) {
        Launcher.getProperties().put(key, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(String key, T defaultValue) {
        return (T) Launcher.getProperties().getOrDefault(key, defaultValue);
    }

    @Override
    public String getPropertyString(String key, String defaultValue) {
        return Launcher.getProperties().getOrDefault(key, defaultValue).toString();
    }
}
