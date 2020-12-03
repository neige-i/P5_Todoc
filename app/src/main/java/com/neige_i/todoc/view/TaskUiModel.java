package com.neige_i.todoc.view;

import android.content.res.ColorStateList;
import android.graphics.Color;

import androidx.annotation.NonNull;

import com.neige_i.todoc.data.model.Project;
import com.neige_i.todoc.data.model.Task;

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

    public TaskUiModel(@NonNull Task task) {
        taskId = task.getId();
        taskName = task.getName();

        final Project project = task.getProject();
        if (project != null) {
            projectName = project.getName();
            projectColor = ColorStateList.valueOf(project.getColor());
        } else {
            projectName = "";
            projectColor = ColorStateList.valueOf(Color.TRANSPARENT);
        }
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
