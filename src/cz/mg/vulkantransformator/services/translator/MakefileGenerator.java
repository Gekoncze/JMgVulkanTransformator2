package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;

public @Service class MakefileGenerator {
    private static @Optional MakefileGenerator instance;

    public static @Mandatory MakefileGenerator getInstance() {
        if (instance == null) {
            instance = new MakefileGenerator();
        }
        return instance;
    }

    private MakefileGenerator() {
    }

    public @Mandatory List<String> create(
        @Mandatory List<File> files,
        @Mandatory String name,
        @Mandatory List<String> includes,
        @Mandatory List<String> libs
    ) {
        return new List<>(
            "SOURCES = " + getSources(files),
            "OBJECTS = " + getObjects(files),
            "INCLUDES = " + getIncludes(includes),
            "LIBS = " + getLibs(libs),
            "NAME = " + "lib" + name + ".so",
            "",
            "${NAME}:${OBJ}",
            "\tgcc -c -Wall -Werror -fpic ${INCLUDES} ${SOURCES}",
            "\tgcc -o ${NAME} -shared ${LIBS} ${OBJECTS}",
            "",
            "clean:",
            "\trm -f " + getObjects(files)
        );
    }

    private @Mandatory String getSources(@Mandatory List<File> files) {
        StringBuilder sources = new StringBuilder();
        for (File file : files) {
            if (file.getPath().toString().endsWith(".c")) {
                String fileName = file.getPath().getFileName().toString();
                sources.append(fileName);
                sources.append(" ");
            }
        }
        return sources.toString();
    }


    private @Mandatory String getObjects(@Mandatory List<File> files) {
        StringBuilder objects = new StringBuilder();
        for (File file : files) {
            if (file.getPath().toString().endsWith(".c")) {
                String fileName = file.getPath().getFileName().toString().replace(".c", ".o");
                objects.append(fileName);
                objects.append(" ");
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
}
