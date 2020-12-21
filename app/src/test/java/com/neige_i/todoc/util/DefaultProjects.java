package com.neige_i.todoc.util;

import com.neige_i.todoc.data.model.Project;

import java.util.Arrays;
import java.util.List;

public class DefaultProjects {

    public static List<Project> getList() {
        return Arrays.asList(
            new Project(1L, "Projet Tartampion", 0xFFEADAD1),
            new Project(2L, "Projet Lucidia", 0xFFB4CDBA),
            new Project(3L, "Projet Circus", 0xFFA3CED2)
        );
    }
}
