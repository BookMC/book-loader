package org.bookmc.loader.impl.launch.transform.mixin;

import org.bookmc.loader.impl.launch.Launcher;
import org.bookmc.loader.impl.launch.Quilt;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.service.IClassBytecodeProvider;
import org.spongepowered.asm.service.IClassProvider;
import org.spongepowered.asm.service.IMixinService;
import org.spongepowered.asm.service.ITransformer;
import org.spongepowered.asm.util.ReEntranceLock;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

public class MixinServiceQuilt implements IMixinService, IClassProvider, IClassBytecodeProvider {
    private final ReEntranceLock lock = new ReEntranceLock(1);

    @Override
    public byte[] getClassBytes(String name, String transformedName) {
        return getClassBytes(name, true);
    }

    @Override
    public byte[] getClassBytes(String name, boolean runTransformers) {
        return Launcher.getClassBytes(name, runTransformers);
    }

    @Override
    public ClassNode getClassNode(String name) {
        return Launcher.getMixinClassNode(name);
    }

    @Override
    public URL[] getClassPath() {
        return Launcher.getURLs();
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return Launcher.loadClass(name, true);
    }

    @Override
    public Class<?> findClass(String name, boolean initialize) throws ClassNotFoundException {
        return Launcher.findClass(name, initialize);
    }

    @Override
    public Class<?> findAgentClass(String name, boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, Quilt.class.getClassLoader());
    }

    @Override
    public String getName() {
        return "Quilt/BookMC";
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void prepare() {

    }

    @Override
    public MixinEnvironment.Phase getInitialPhase() {
        return MixinEnvironment.Phase.PREINIT;
    }

    @Override
    public void init() {
    }

    @Override
    public void beginPhase() {
        Launcher.getQuiltClassLoader().registerTransformer(new QuiltMixinProxy());
    }

    @Override
    public void checkEnv(Object bootSource) {

    }

    @Override
    public ReEntranceLock getReEntranceLock() {
        return lock;
    }

    @Override
    public IClassProvider getClassProvider() {
        return this;
    }

    @Override
    public IClassBytecodeProvider getBytecodeProvider() {
        return this;
    }

    @Override
    public Collection<String> getPlatformAgents() {
        return Collections.singletonList("org.spongepowered.asm.launch.platform.MixinPlatformAgentDefault");
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return Launcher.getResourceAsStream(name);
    }

    @Override
    public void registerInvalidClass(String className) {

    }

    @Override
    public boolean isClassLoaded(String className) {
        return Launcher.isClassLoaded(className);
    }

    @Override
    public String getClassRestrictions(String className) {
        return "";
    }

    @Override
    public Collection<ITransformer> getTransformers() {
        return Collections.emptyList();
    }

    @Override
    public String getSideName() {
        return Launcher.getEnvironment().name();
    }
}
