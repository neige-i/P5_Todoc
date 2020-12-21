package com.neige_i.todoc.view;

import java.util.List;
import java.util.Objects;

/**
 * UI model to have only one UI state.<br />
 * This allows {@link TaskViewModel} to return a unique UI state to {@link MainActivity}.
 */
public class ListUiModel {

    // ------------------------------------ INSTANCE VARIABLES -------------------------------------

    private final List<TaskUiModel> taskUiModels;
    private final boolean isNoTaskVisible;

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    public ListUiModel(List<TaskUiModel> taskUiModels, boolean isNoTaskVisible) {
        this.taskUiModels = taskUiModels;
        this.isNoTaskVisible = isNoTaskVisible;
    }

    public List<TaskUiModel> getTaskUiModels() {
        return taskUiModels;
    }

    public boolean isNoTaskVisible() {
        return isNoTaskVisible;
    }

    // -------------------------------------- OBJECT METHODS ---------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListUiModel that = (ListUiModel) o;
        return isNoTaskVisible == that.isNoTaskVisible &&
            Objects.equals(taskUiModels, that.taskUiModels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskUiModels, isNoTaskVisible);
    }
}
