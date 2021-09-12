package org.bookmc.loader.impl.mixin;

import org.bookmc.loader.impl.launch.BookLauncher;
import org.bookmc.loader.impl.mixin.proxy.IMixinProxyManager;
import org.spongepowered.asm.mixin.transformer.BookMixinTransformer;

import java.util.ArrayList;
import java.util.List;

public class BookMixinProxyManager implements IMixinProxyManager {
    private final BookMixinTransformer transformer = new BookMixinTransformer();
    private final List<BookMixinProxy> proxies = new ArrayList<>();

    public void registerProxy(BookMixinProxy proxy) {
        BookLauncher.getQuiltClassLoader().registerTransformer(proxy);

        // Disable all current proxies
        for (BookMixinProxy bookMixinProxy : proxies) {
            bookMixinProxy.setActive(false);
        }
        // Add our new proxy
        proxies.add(proxy);
    }

    public void createProxy() {
        registerProxy(new BookMixinProxy(this));
    }

    public BookMixinTransformer getTransformer() {
        return transformer;
    }
}
