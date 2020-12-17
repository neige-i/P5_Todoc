package com.neige_i.todoc.view;

import com.neige_i.todoc.data.model.Project;

import java.util.List;
import java.util.Objects;

public class MainUiModel {

    // ------------------------------------ INSTANCE VARIABLES -------------------------------------

    private final List<TaskUiModel> taskUiModels;
    private final boolean isNoTaskVisible;
    private final List<Project> projectList;

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    public MainUiModel(List<TaskUiModel> taskUiModels, boolean isNoTaskVisible, List<Project> projectList) {
        this.taskUiModels = taskUiModels;
        this.isNoTaskVisible = isNoTaskVisible;
        this.projectList = projectList;
    }

    public List<TaskUiModel> getTaskUiModels() {
        return taskUiModels;
    }

    public boolean isNoTaskVisible() {
        return isNoTaskVisible;
    }

    public List<Project> getProjectList() {
        return projectList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MainUiModel that = (MainUiModel) o;
        return isNoTaskVisible == that.isNoTaskVisible &&
            Objects.equals(taskUiModels, that.taskUiModels) &&
            Objects.equals(projectList, that.projectList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskUiModels, isNoTaskVisible, projectList);
    }
}
