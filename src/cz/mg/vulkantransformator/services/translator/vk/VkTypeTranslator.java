package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.*;
import cz.mg.vulkantransformator.services.translator.Configuration;
import cz.mg.vulkantransformator.services.translator.Index;

public @Service class VkTypeTranslator implements VkTranslator<VkType> {
    private static @Optional VkTypeTranslator instance;

    public static @Mandatory VkTypeTranslator getInstance() {
        if (instance == null) {
            instance = new VkTypeTranslator();
            instance.vkComponentTranslator = VkComponentTranslator.getInstance();
        }
        return instance;
    }

    private VkComponentTranslator vkComponentTranslator;

    private VkTypeTranslator() {
    }

    @Override
    public @Mandatory Class<? extends VkComponent> targetClass() {
        return VkType.class;
    }

    @Override
    public @Mandatory List<String> translateJava(@Mandatory Index index, @Mandatory VkType type) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            vkComponentTranslator.getCommonJavaHeader(type)
        );

        if (type.getName().equals("VkBool32"))
        {
            String vkTrue = ((VkConstant)index.getComponent("VK_TRUE")).getValue();
            String vkFalse = ((VkConstant)index.getComponent("VK_FALSE")).getValue();

            lines.addCollectionLast(
                new List<>(
                    "    public boolean get() {",
                    "        return _get(address) != " + vkFalse + ";",
                    "    }",
                    "",
                    "    private static native int _get(long address);",
                    "",
                    "    public void set(boolean value) {",
                    "        _set2(address, value ? " + vkTrue + " : " + vkFalse + ");",
                    "    }",
                    "",
                    "    private static native void _set2(long address, int value);"
                )
            );
        }

        lines.addCollectionLast(
            vkComponentTranslator.getCommonJavaFooter(type)
        );

        return lines;
    }

    @Override
    public @Mandatory List<String> translateNative(@Mandatory Index index, @Mandatory VkType type) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            vkComponentTranslator.getCommonNativeHeader(type)
        );

        if (type.getName().equals("VkBool32"))
        {
            String path = Configuration.VULKAN_FUNCTION + "_VkBool32_";
            lines.addCollectionLast(
                new List<>(
                    "JNIEXPORT jint JNICALL Java_" + path + "_get(JNIEnv* env, jclass clazz, jlong address) {",
                    "    VkBool32* a = (VkBool32*) l2a(address);",
                    "    return *a;",
                    "}",
                    "",
                    "JNIEXPORT void JNICALL Java_" + path + "_set2(JNIEnv* env, jclass clazz, jlong address, jint value) {",
                    "    VkBool32* a = (VkBool32*) l2a(address);",
                    "    *a = value;",
                    "}"
                )
            );
        }

        lines.addCollectionLast(
            vkComponentTranslator.getCommonNativeFooter(type)
        );

        return lines;
    }
}
