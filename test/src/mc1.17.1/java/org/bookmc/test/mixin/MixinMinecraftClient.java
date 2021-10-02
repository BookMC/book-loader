package org.bookmc.test.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.apache.logging.log4j.Logger;
import org.bookmc.loader.api.exception.LoaderException;
import org.bookmc.loader.api.loader.BookLoaderBase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Shadow @Final private static Logger LOGGER;
    private final String TEST_FIELD = "why_are_you_looking_for_me?";

    @Inject(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/util/Window;setPhase(Ljava/lang/String;)V",
            args = "ldc=Startup",
            shift = At.Shift.AFTER,
            ordinal = 0
        )
    )
    private void test$$ctor$$init(RunArgs runArgs, CallbackInfo ci) {
        if (BookLoaderBase.INSTANCE == null) {
            throw new IllegalStateException("Book has not been launched correctly! (Failed to find loader implementation)");
        }
        try {
            BookLoaderBase.INSTANCE.load();
            LOGGER.info("Loaded book-loader!");
        } catch (LoaderException e) {
            e.printStackTrace();
        }
    }

    private void testMethod() {
        throw new UnsupportedOperationException();
    }
}
