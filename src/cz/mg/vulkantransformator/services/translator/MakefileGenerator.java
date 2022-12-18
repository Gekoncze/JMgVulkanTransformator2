package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.collections.services.StringJoiner;

import java.util.StringTokenizer;

public @Service class MakefileGenerator {
    private static @Optional MakefileGenerator instance;

    public static @Mandatory MakefileGenerator getInstance() {
        if (instance == null) {
            instance = new MakefileGenerator();
            instance.stringJoiner = StringJoiner.getInstance();
        }
        return instance;
    }

    private StringJoiner stringJoiner;

    private MakefileGenerator() {
    }

    public @Mandatory List<String> create(
        @Mandatory String javaLibraryName,
        @Mandatory String nativeLibraryName,
        @Mandatory List<String> includes,
        @Mandatory List<String> libs,
        @Mandatory List<String> linkerFlags,
        @Mandatory String javaPackage,
        @Mandatory List<String> classPaths
    ) {
        String jd = javaPackageToDirectory(javaPackage) + "/";
        String cd = javaPackageToChangeDirectory(javaPackage);
        return new List<>(
            "JAR = " + javaLibraryName + ".jar",
            "SO = " + "lib" + nativeLibraryName + ".so",
            "INCLUDES = " + getIncludes(includes),
            "LIBS = " + getLibs(libs),
            "LFLAGS = " + getFlags(linkerFlags),
            "\n",
            "help:\n" +
            "\techo \"Please select target: java, c, clean.\"\n" +
            "\n" +
            "java:\n" +
            "\tjavac -cp \"" + getClassPath(classPaths) + "\" *.java\n" +
            "\tcd " + cd + ";\\\n" +
            "\tjar -cf " + jd + "${JAR} " + jd + "*.class\n" +
            "\n" +
            "c:\n" +
            "\tgcc -c -fpic ${INCLUDES} *.c\n" +
            "\tgcc -o ${SO} -shared ${LFLAGS} ${LIBS} *.o",
            "\n" +
            "clean:\n" +
            "\trm -f *.class\n" +
            "\trm -f *.o\n" +
            "\trm -f *.so\n" +
            "\trm -f *.jar"
        );
    }

    private @Mandatory String getIncludes(@Mandatory List<String> includeList) {
        StringBuilder includes = new StringBuilder();
        for (String include : includeList) {
            String parameter = "-I" + '"' + include + '"';
            includes.append(parameter);
            includes.append(" ");
        }
        return includes.toString();
    }

    private @Mandatory String getLibs(@Mandatory List<String> libList) {
        StringBuilder libs = new StringBuilder();
        for (String lib : libList) {
            String parameter = "-l" + lib;
            libs.append(parameter);
            libs.append(" ");
        }
        return libs.toString();
    }

    private @Mandatory String getFlags(@Mandatory List<String> flags) {
        return stringJoiner.join(flags, " ");
    }

    private @Mandatory String javaPackageToDirectory(@Mandatory String javaPackage) {
        return javaPackage.replace('.', '/');
    }

    private @Mandatory String javaPackageToChangeDirectory(@Mandatory String javaPackage) {
        int count = new StringTokenizer(javaPackage, ".").countTokens();
        List<String> parts = new List<>();
        for (int i = 0; i < count; i++) {
            parts.addLast("..");
        }
        return stringJoiner.join(parts, "/");
    }

    private @Mandatory String getClassPath(@Mandatory List<String> classPaths) {
        if (classPaths.isEmpty()) {
            return ".";
        } else {
            return ".:" + stringJoiner.join(classPaths, ":");
        }
    }
}
