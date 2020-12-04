package com.neige_i.todoc.view;

import java.util.List;

public class MainUiModel {

    // ------------------------------------ INSTANCE VARIABLES -------------------------------------

    private final List<TaskUiModel> taskUiModels;
    private final boolean isNoTaskVisible;

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    public MainUiModel(List<TaskUiModel> taskUiModels, boolean isNoTaskVisible) {
        this.taskUiModels = taskUiModels;
        this.isNoTaskVisible = isNoTaskVisible;
    }

    public List<TaskUiModel> getTaskUiModels() {
        return taskUiModels;
    }

    public boolean isNoTaskVisible() {
        return isNoTaskVisible;
    }
}
