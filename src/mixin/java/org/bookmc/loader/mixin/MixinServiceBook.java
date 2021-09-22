package org.bookmc.loader.mixin;

import org.bookmc.loader.api.loader.BookLoaderBase;
import org.bookmc.loader.mixin.internal.LoaderInternal;
import org.bookmc.loader.mixin.state.MixinStateManager;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.platform.container.ContainerHandleURI;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.IMixinTransformerFactory;
import org.spongepowered.asm.service.*;
import org.spongepowered.asm.service.modlauncher.LoggerAdapterLog4j2;
import org.spongepowered.asm.util.ReEntranceLock;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

public class MixinServiceBook implements IMixinService, IClassProvider, IClassBytecodeProvider, ITransformerProvider, IClassTracker {
    private final ReEntranceLock lock = new ReEntranceLock(1);

    @Override
    public ClassNode getClassNode(String name) {
        return getClassNode(name, true);
    }

    @Override
    public ClassNode getClassNode(String name, boolean runTransformers) {
        return LoaderInternal.getClassNode(name, runTransformers);
    }

    @Override
    public URL[] getClassPath() {
        return LoaderInternal.getURLs();
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    @Override
    public Class<?> findClass(String name, boolean initialize) throws ClassNotFoundException {
        return LoaderInternal.findClass(name, initialize);
    }

    @Override
    public Class<?> findAgentClass(String name, boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, BookLoaderBase.class.getClassLoader());
    }

    @Override
    public void registerInvalidClass(String className) {

    }

    @Override
    public boolean isClassLoaded(String className) {
        return LoaderInternal.isClassLoaded(className);
    }

    @Override
    public String getClassRestrictions(String className) {
        return "";
    }

    @Override
    public String getName() {
        return "Book";
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
        if (internal instanceof IMixinTransformerFactory) {
            MixinStateManager.setTransformer(((IMixinTransformerFactory) internal).createTransformer());
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void beginPhase() {
        MixinProxyManager.INSTANCE.createProxy();
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
        return LoaderInternal.getResourceAsStream(name);
    }

    @Override
    public String getSideName() {
        return BookLoaderBase.INSTANCE.getGlobalEnvironment().name();
    }

    @Override
    public MixinEnvironment.CompatibilityLevel getMinCompatibilityLevel() {
        return MixinEnvironment.CompatibilityLevel.JAVA_16;
    }

    @Override
    public MixinEnvironment.CompatibilityLevel getMaxCompatibilityLevel() {
        return MixinEnvironment.CompatibilityLevel.JAVA_18;
    }

    @Override
    public ILogger getLogger(String name) {
        // Ignore the name given and get a good one
        name = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass().getName();
        return new LoggerAdapterLog4j2(name);
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
}
