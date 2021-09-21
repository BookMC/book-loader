package org.bookmc.test.entrypoint;

import net.minecraft.client.MinecraftClient;

public class EntrypointTest {
    @SuppressWarnings({"ConstantConditions", "JavaReflectionMemberAccess"})
    public static void main() {
        Class<?> clazz = MinecraftClient.class;
        try {
            if (clazz.getDeclaredField("TEST_FIELD") == null) {
                throw new IllegalStateException("Mixin failed! (TEST_FIELD) is missing");
            }
            if (clazz.getDeclaredMethod("testMethod") == null) {
                throw new IllegalStateException("Mixin failed! (testMethod) is missing");
            }
            System.out.println("Entrypoints work! (and passed Mixin appliance tests)");
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
