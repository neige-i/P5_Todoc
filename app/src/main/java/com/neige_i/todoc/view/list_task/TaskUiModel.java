package com.neige_i.todoc.view.list_task;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

public class TaskUiModel {

    // ------------------------------------ INSTANCE VARIABLES -------------------------------------

    private final long taskId;
    @NonNull
    private final String taskName;
    @NonNull
    private final String projectName;
    @ColorInt
    private final int projectColor;

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    public TaskUiModel(long taskId, @NonNull String taskName, @NonNull String projectName, @ColorInt int projectColor) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.projectName = projectName;
        this.projectColor = projectColor;
    }

    public long getTaskId() {
        return taskId;
    }

    @NonNull
    public String getTaskName() {
        return taskName;
    }

    @NonNull
    public String getProjectName() {
        return projectName;
    }

    @ColorInt
    public int getProjectColor() {
        return projectColor;
    }

    // -------------------------------------- OBJECT METHODS ---------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskUiModel that = (TaskUiModel) o;
        return taskId == that.taskId &&
            taskName.equals(that.taskName) &&
            projectName.equals(that.projectName) &&
            projectColor == that.projectColor;
    }
}
