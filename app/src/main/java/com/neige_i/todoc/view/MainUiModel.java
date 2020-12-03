package com.neige_i.todoc.view;

import android.view.View;

import androidx.annotation.NonNull;

import com.neige_i.todoc.data.model.Task;

import java.util.ArrayList;
import java.util.List;

public class MainUiModel {

    // ------------------------------------ INSTANCE VARIABLES -------------------------------------

    private final List<TaskUiModel> taskUiModels;
    private final int noTaskVisibility;

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    public MainUiModel(@NonNull List<Task> taskList) {
        taskUiModels = new ArrayList<>();
        for (Task task : taskList)
            taskUiModels.add(new TaskUiModel(task));
        noTaskVisibility = taskList.isEmpty() ? View.VISIBLE : View.GONE;
    }

    public List<TaskUiModel> getTaskUiModels() {
        return taskUiModels;
    }

    public int getNoTaskVisibility() {
        return noTaskVisibility;
    }
}
