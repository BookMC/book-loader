package org.bookmc.test;

import org.bookmc.loader.api.environment.GameEnvironment;
import org.bookmc.loader.api.loader.BookLoaderBase;
import org.bookmc.loader.api.mod.ModContainer;
import org.bookmc.loader.impl.config.JVMLoaderConfig;
import org.bookmc.loader.impl.loader.BookLoaderImpl;
import org.bookmc.test.runner.ClassLoaderUnitRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.OrderWith;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.Alphanumeric;

import java.nio.file.Paths;

@RunWith(ClassLoaderUnitRunner.class)
@OrderWith(Alphanumeric.class)
public class LoaderTest {
    @Test
    public void $0testPreload() {
        BookLoaderBase.INSTANCE = new BookLoaderImpl(Paths.get(System.getProperty("user.dir"), "test-data"), GameEnvironment.UNIT_TEST, ClassLoaderUnitRunner.classLoader, new JVMLoaderConfig());
        BookLoaderBase.INSTANCE.getLoaderConfig().setOption("book.candidate.disableResourceSearching", "false");
        BookLoaderBase.INSTANCE.preload();
    }

    @Test
    public void $1testLoad() {
        BookLoaderBase.INSTANCE.load();
    }

    @Test
    public void verifyLoad() {
        Assert.assertNotNull(BookLoaderBase.INSTANCE.getContainers().get("book-loader"));
        ModContainer container = BookLoaderBase.INSTANCE.getContainers().get("test");

        // Existence test
        Assert.assertNotNull(container);

        // Metadata tests
        Assert.assertEquals("test", container.getMetadata().getId());
        Assert.assertEquals("test", container.getMetadata().getName());
        Assert.assertEquals("0.0.0", container.getMetadata().getVersion().getVersion());
        Assert.assertEquals(GameEnvironment.UNIT_TEST, container.getMetadata().getEnvironment());
    }
}
