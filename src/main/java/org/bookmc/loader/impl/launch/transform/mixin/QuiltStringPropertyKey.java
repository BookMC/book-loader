package org.bookmc.loader.impl.launch.transform.mixin;

import org.spongepowered.asm.service.IPropertyKey;

import java.util.Objects;

public class QuiltStringPropertyKey implements IPropertyKey {
    private final String key;

    public QuiltStringPropertyKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuiltStringPropertyKey that = (QuiltStringPropertyKey) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
