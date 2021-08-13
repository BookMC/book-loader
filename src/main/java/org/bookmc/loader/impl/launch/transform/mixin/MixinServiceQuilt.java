package org.bookmc.loader.impl.launch.transform.mixin;

import org.bookmc.loader.impl.launch.Launcher;
import org.bookmc.loader.impl.launch.Quilt;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.platform.container.ContainerHandleURI;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.service.*;
import org.spongepowered.asm.util.ReEntranceLock;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

public class MixinServiceQuilt implements IMixinService, IClassProvider, IClassBytecodeProvider, ITransformerProvider, IClassTracker {
    private final ReEntranceLock lock = new ReEntranceLock(1);

    @Override
    public ClassNode getClassNode(String name) {
        return getClassNode(name, true);
    }

    @Override
    public ClassNode getClassNode(String name, boolean runTransformers) {
        // We use 0 for our flags to avoid re-entrant transformer errros
        return Launcher.getClassNode(name, runTransformers, 0);
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
    public void offer(IMixinInternal internal) {

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
    public ITransformerProvider getTransformerProvider() {
        return this;
    }

    @Override
    public IClassTracker getClassTracker() {
        return this;
    }

    @Override
    public IMixinAuditTrail getAuditTrail() {
        return null;
    }

    @Override
    public Collection<String> getPlatformAgents() {
        return Collections.singletonList("org.spongepowered.asm.launch.platform.MixinPlatformAgentDefault");
    }

    @Override
    public IContainerHandle getPrimaryContainer() {
        try {
            return new ContainerHandleURI(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<IContainerHandle> getMixinContainers() {
        return Collections.emptyList();
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
    public Collection<ITransformer> getDelegatedTransformers() {
        return Collections.emptyList();
    }

    @Override
    public void addTransformerExclusion(String name) {

    }

    @Override
    public String getSideName() {
        return Launcher.getEnvironment().name();
    }

    @Override
    public MixinEnvironment.CompatibilityLevel getMinCompatibilityLevel() {
        return MixinEnvironment.CompatibilityLevel.JAVA_8;
    }

    @Override
    public MixinEnvironment.CompatibilityLevel getMaxCompatibilityLevel() {
        return MixinEnvironment.CompatibilityLevel.JAVA_11;
    }
}
