package org.bookmc.loader.impl.mixin;

import org.spongepowered.asm.service.IPropertyKey;

import java.util.Objects;

public record MixinStringPropertyKey(String key) implements IPropertyKey {
    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MixinStringPropertyKey that = (MixinStringPropertyKey) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
