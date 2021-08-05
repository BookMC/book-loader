package org.bookmc.loader.impl.obfuscation;

import org.bookmc.loader.api.launch.transform.QuiltTransformer;
import org.bookmc.loader.impl.launch.Launcher;
import org.bookmc.srg.SrgProcessor;
import org.bookmc.srg.output.MappedClass;
import org.bookmc.srg.output.MappedField;
import org.bookmc.srg.output.MappedMethod;
import org.bookmc.srg.output.SrgOutput;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

import java.io.File;

public class MinecraftObfuscationTransformer extends Remapper implements QuiltTransformer {
    public static MinecraftObfuscationTransformer INSTANCE = new MinecraftObfuscationTransformer();

    private final SrgOutput output;

    private MinecraftObfuscationTransformer() {
        File mappings = Launcher.getMappings();

        if (mappings == null) {
            throw new IllegalStateException("Failed to find mappings! Notch -> MCP");
        }

        output = new SrgProcessor(mappings).process();
    }

    @Override
    public byte[] transform(String name, byte[] clazz) {
        ClassReader reader = new ClassReader(clazz);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassRemapper remapper = new ClassRemapper(writer, this);
        reader.accept(remapper, ClassReader.EXPAND_FRAMES);
        return writer.toByteArray();
    }

    @Override
    public String mapFieldName(String owner, String name, String descriptor) {
        for (MappedField field : output.getFields()) {
            if (field.getObfuscatedName().equals(name) && field.getObfuscatedOwner().equals(owner)) {
                return field.getDeobfuscatedName();
            }
        }

        return super.mapFieldName(owner, name, descriptor);
    }

    @Override
    public String map(String internalName) {
        for (MappedClass mappedClass : output.getClasses()) {
            if (mappedClass.getObfuscatedName().equals(internalName)) {
                return mappedClass.getDeobfuscatedName();
            }
        }

        int dollar = internalName.lastIndexOf('$');
        if (dollar > -1) {
            return map(internalName.substring(0, dollar)) + "$" + internalName.substring(dollar + 1);
        }

        return super.map(internalName);
    }

    @Override
    public String mapMethodName(String owner, String name, String descriptor) {
        for (MappedMethod method : output.getMethods()) {
            if (method.getObfuscatedName().equals(name) && method.getObfuscatedDescriptor().equals(descriptor) && method.getObfuscatedOwner().equals(owner)) {
                return method.getDeobfuscatedName();
            }
        }

        return super.mapMethodName(owner, name, descriptor);
    }

    @Override
    public String mapSignature(String signature, boolean typeSignature) {
        if (signature != null && signature.contains("!*")) {
            return null;
        }

        return super.mapSignature(signature, typeSignature);
    }
}
