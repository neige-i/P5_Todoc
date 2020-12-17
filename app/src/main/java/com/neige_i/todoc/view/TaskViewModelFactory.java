package com.neige_i.todoc.view;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.neige_i.todoc.data.repository.TaskRepository;

import java.time.Clock;

public class TaskViewModelFactory implements ViewModelProvider.Factory {

    // -------------------------------------  CLASS VARIABLES --------------------------------------

    @Nullable
    private static TaskViewModelFactory factory;

    private final TaskRepository taskRepository;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    private TaskViewModelFactory(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // -------------------------------------- FACTORY METHODS --------------------------------------

    @NonNull
    public static TaskViewModelFactory getInstance(@NonNull Application application) {
        if (factory == null) {
            synchronized (TaskViewModelFactory.class) {
                if (factory == null) {
                    factory = new TaskViewModelFactory(new TaskRepository(application));
                }
            }
        }
        return factory;
    }

    // -------------------------------- VIEW MODEL FACTORY METHODS ---------------------------------

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TaskViewModel.class)) {
            return (T) new TaskViewModel(taskRepository, Clock.systemDefaultZone(), new Handler(Looper.getMainLooper()));
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
