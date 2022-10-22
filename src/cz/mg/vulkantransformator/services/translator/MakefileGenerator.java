package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.collections.services.StringJoiner;
import cz.mg.vulkantransformator.entities.filesystem.File;

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
        @Mandatory List<File> files,
        @Mandatory String name,
        @Mandatory List<String> includes,
        @Mandatory List<String> libs,
        @Mandatory List<String> linkerFlags
    ) {
        return new List<>(
            "SOURCES = " + getSources(files),
            "OBJECTS = " + getObjects(files),
            "INCLUDES = " + getIncludes(includes),
            "LIBS = " + getLibs(libs),
            "NAME = " + "lib" + name + ".so",
            "LFLAGS = " + getFlags(linkerFlags),
            "",
            "${NAME}:${OBJ}",
            "\tgcc -c -fpic ${INCLUDES} ${SOURCES}",
            "\tgcc -o ${NAME} -shared ${LFLAGS} ${LIBS} ${OBJECTS}",
            "",
            "clean:",
            "\trm -f " + getObjects(files),
            "\trm -f " + "lib" + name + ".so"
        );
    }

    private @Mandatory String getSources(@Mandatory List<File> files) {
        StringBuilder sources = new StringBuilder();
        for (File file : files) {
            if (file.getPath().toString().endsWith(".c")) {
                if (!file.getLines().isEmpty()) {
                    String fileName = file.getPath().getFileName().toString();
                    sources.append(fileName);
                    sources.append(" ");
                }
            }
        }
        return sources.toString();
    }


    private @Mandatory String getObjects(@Mandatory List<File> files) {
        StringBuilder objects = new StringBuilder();
        for (File file : files) {
            if (file.getPath().toString().endsWith(".c")) {
                if (!file.getLines().isEmpty()) {
                    String fileName = file.getPath().getFileName().toString().replace(".c", ".o");
                    objects.append(fileName);
                    objects.append(" ");
                }
            }
        }
        return objects.toString();
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
