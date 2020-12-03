package com.neige_i.todoc.view;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.neige_i.todoc.data.repository.TaskRepository;

public class TaskViewModelFactory implements ViewModelProvider.Factory {

    // -------------------------------------  CLASS VARIABLES --------------------------------------

    @Nullable
    private static TaskViewModelFactory factory;
    private static Application application;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    private TaskViewModelFactory() {
    }

    // -------------------------------------- FACTORY METHODS --------------------------------------

    @NonNull
    public static TaskViewModelFactory getInstance(@NonNull Application application) {
        if (factory == null) {
            synchronized (TaskViewModelFactory.class) {
                if (factory == null) {
                    factory = new TaskViewModelFactory();
                    TaskViewModelFactory.application = application;
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
            return (T) new TaskViewModel(new TaskRepository(application)); // ASKME: no use of DI, use interface and implementation class
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
