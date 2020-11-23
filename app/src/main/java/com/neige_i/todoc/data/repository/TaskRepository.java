package com.neige_i.todoc.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.neige_i.todoc.data.database.TaskDao;
import com.neige_i.todoc.data.database.TaskDatabase;
import com.neige_i.todoc.data.model.Task;

import java.util.List;

// ASKME: manage directories
public class TaskRepository {

    private final TaskDao taskDao;

    public TaskRepository(Application application) {
        taskDao = TaskDatabase.getInstance(application).taskDao();
    }

    public LiveData<List<Task>> getTasks() {
        return taskDao.getAllTasks();
    }

    public void addTask(final Task taskToAdd) {
        TaskDatabase.databaseWriteExecutor.execute(() -> taskDao.insert(taskToAdd));
    }

    public void deleteTask(final Task taskToDelete) {
        TaskDatabase.databaseWriteExecutor.execute(() -> taskDao.delete(taskToDelete));
    }
}
