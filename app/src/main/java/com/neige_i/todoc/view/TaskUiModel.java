package com.neige_i.todoc.view;

import android.content.res.ColorStateList;

import androidx.annotation.NonNull;

public class TaskUiModel {

    // ------------------------------------ INSTANCE VARIABLES -------------------------------------

    private final long taskId;
    @NonNull
    private final String taskName;
    @NonNull
    private final String projectName;
    @NonNull
    private final ColorStateList projectColor;

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    public TaskUiModel(long taskId, @NonNull String taskName, @NonNull String projectName, @NonNull ColorStateList projectColor) {
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

    @NonNull
    public ColorStateList getProjectColor() {
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
            projectColor.equals(that.projectColor);
    }
}
