package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkFlags;
import cz.mg.vulkantransformator.services.translator.Configuration;
import cz.mg.vulkantransformator.services.translator.Index;

public @Service class VkFlagsTranslator implements VkTranslator<VkFlags> {
    private static @Optional VkFlagsTranslator instance;

    public static @Mandatory VkFlagsTranslator getInstance() {
        if (instance == null) {
            instance = new VkFlagsTranslator();
            instance.componentTranslator = VkComponentTranslator.getInstance();
        }
        return instance;
    }

    private VkComponentTranslator componentTranslator;

    private VkFlagsTranslator() {
    }

    @Override
    public @Mandatory Class<? extends VkComponent> targetClass() {
        return VkFlags.class;
    }

    @Override
    public @Mandatory List<String> translateJava(@Mandatory Index index, @Mandatory VkFlags flags) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            componentTranslator.getCommonJavaHeader(flags)
        );

        String flagBitsName = getFlagBitsName(flags);
        lines.addCollectionLast(new List<>(
            "    public int get() {",
            "        return _get2(address);",
            "    }",
            "",
            "    private native int _get2(long address);",
            "",
            "    public void set(int flag) {",
            "        _set2(address, flag);",
            "    }",
            "",
            "    private native void _set2(long address, int value);",
            "",
            "    public void add(" + flagBitsName + " flag) {",
            "        _add(address, flag.getAddress());",
            "    }",
            "",
            "    private static native void _add(long address, long flagAddress);",
            "",
            "    public void add(int flag) {",
            "        _add2(address, flag);",
            "    }",
            "",
            "    private static native void _add2(long address, int flag);",
            "",
            "    public void remove(" + flagBitsName + " flag) {",
            "        _remove(address, flag.getAddress());",
            "    }",
            "",
            "    private static native void _remove(long address, long flagAddress);",
            "",
            "    public void remove(int flag) {",
            "        _remove2(address, flag);",
            "    }",
            "",
            "    private static native void _remove2(long address, int flag);"
        ));

        lines.addCollectionLast(
            componentTranslator.getCommonJavaFooter(flags)
        );

        return lines;
    }

    @Override
    public @Mandatory List<String> translateNative(@Mandatory Index index, @Mandatory VkFlags flags) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            componentTranslator.getCommonNativeHeader(flags)
        );

        String path = Configuration.VULKAN_FUNCTION + "_" + flags.getName() + "_";
        String name = flags.getName();
        String flagBitsName = getFlagBitsName(flags);
        lines.addCollectionLast(new List<>(
            "JNIEXPORT jint JNICALL Java_" + path + "_get2(JNIEnv* env, jclass clazz, jlong address) {",
            "    " + name + "* a = (" + name + "*) l2a(address);",
            "    return *a;",
            "}",
            "",
            "JNIEXPORT void JNICALL Java_" + path + "_set2(JNIEnv* env, jclass clazz, jlong address, jint value) {",
            "    " + name + "* a = (" + name + "*) l2a(address);",
            "    *a = value;",
            "}",
            "",
            "JNIEXPORT void JNICALL Java_" + path + "_add(JNIEnv* env, jclass clazz, jlong address, jlong flagAddress) {",
            "    " + name + "* a = (" + name + "*) l2a(address);",
            "    " + flagBitsName + "* b = (" + flagBitsName + "*) l2a(flagAddress);",
            "    *a |= *b;",
            "}",
            "",
            "JNIEXPORT void JNICALL Java_" + path + "_add2(JNIEnv* env, jclass clazz, jlong address, jint value) {",
            "    " + name + "* a = (" + name + "*) l2a(address);",
            "    *a |= value;",
            "}",
            "",
            "JNIEXPORT void JNICALL Java_" + path + "_remove(JNIEnv* env, jclass clazz, jlong address, jlong flagAddress) {",
            "    " + name + "* a = (" + name + "*) l2a(address);",
            "    " + flagBitsName + "* b = (" + flagBitsName + "*) l2a(flagAddress);",
            "    *a &= ~(*b)",
            "}",
            "",
            "JNIEXPORT void JNICALL Java_" + path + "_remove2(JNIEnv* env, jclass clazz, jlong address, jint value) {",
            "    " + name + "* a = (" + name + "*) l2a(address);",
            "    *a &= ~value;",
            "}",
            ""
        ));

        lines.addCollectionLast(
            componentTranslator.getCommonNativeFooter(flags)
        );

        return lines;
    }

    private @Mandatory String getFlagBitsName(@Mandatory VkFlags flags)
    {
        return flags.getName().replace("Flags", "FlagBits");
    }
}
