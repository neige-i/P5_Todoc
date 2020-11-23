package com.neige_i.todoc.view;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.neige_i.todoc.data.model.Task;
import com.neige_i.todoc.data.repository.TaskRepository;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private final TaskRepository taskRepository;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepository(application);
    }

    public LiveData<List<Task>> getTaskList() {
        return taskRepository.getTasks();
    }

    public void addTask(Task taskToAdd) {
        taskRepository.addTask(taskToAdd);
    }

    public void removeTask(Task taskToRemove) {
        taskRepository.deleteTask(taskToRemove);
    }
}
