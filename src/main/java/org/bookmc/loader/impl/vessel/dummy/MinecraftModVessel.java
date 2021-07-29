package org.bookmc.loader.impl.vessel.dummy;

import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.api.vessel.dependency.ModDependency;

import java.io.File;
import java.net.URL;

public class MinecraftModVessel implements ModVessel {
    private final String version;

    public MinecraftModVessel(String version) {
        this.version = version;
    }

    @Override
    public String getName() {
        return "Minecraft";
    }

    @Override
    public String getId() {
        return "minecraft";
    }

    @Override
    public String getDescription() {
        return "Minecraft is a sandbox video game developed by the Swedish video game developer Mojang Studios. " +
            "The game was created by Markus \"Notch\" Persson in the Java programming language.";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Override
    public String[] getAuthors() {
        return new String[]{"Oracle Corporation", "Sun Microsystems", "James Gosling"};
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getEntrypoint() {
        return "org.bookmc.loader.impl.vessel.dummy.entrypoint.FakeEntrypoint::fakeItToMakeIt";
    }

    @Override
    public File getFile() {
        return new File("there_is_no_mod_to_see");
    }

    @Override
    public String getMixinEntrypoint() {
        return null;
    }

    @Override
    public ModDependency[] getDependsOn() {
        return new ModDependency[0];
    }

    @Override
    public ModDependency[] getSuggestions() {
        return new ModDependency[0];
    }

    @Override
    public URL[] getExternalDependencies() {
        return new URL[0];
    }

    @Override
    public String getIcon() {
        return null;
    }

    @Override
    public boolean isInternallyEnabled() {
        return false;
    }

    @Override
    public void setInternallyEnabled(boolean enabled) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCompatibilityLayer() {
        return false;
    }
}
