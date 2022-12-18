package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.collections.services.StringJoiner;

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
        @Mandatory List<String> linkerFlags
    ) {
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
            "\tjavac *.java\n" +
            "\tjar -cf ${JAR} *.class\n" +
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
}
