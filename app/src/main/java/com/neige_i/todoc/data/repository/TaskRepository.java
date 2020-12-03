package com.neige_i.todoc.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.neige_i.todoc.data.database.TaskDao;
import com.neige_i.todoc.data.database.TaskDatabase;
import com.neige_i.todoc.data.model.Project;
import com.neige_i.todoc.data.model.Task;

import java.util.List;

public class TaskRepository {

    // ------------------------------------ INSTANCE VARIABLES -------------------------------------

    private final TaskDao taskDao;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    public TaskRepository(Application application) {
        taskDao = TaskDatabase.getInstance(application).taskDao();
    }

    // --------------------------------------- TASK METHODS ----------------------------------------

    public LiveData<List<Task>> getTasks() {
        return taskDao.getAllTasks();
    }

    public LiveData<List<Task>> getTasksByNameAsc() {
        return taskDao.getTasksByNameAsc();
    }

    public LiveData<List<Task>> getTasksByNameDesc() {
        return taskDao.getTasksByNameDesc();
    }

    public LiveData<List<Task>> getTasksByProjectNameAsc() {
        return taskDao.getTasksByProjectNameAsc();
    }

    public LiveData<List<Task>> getTasksByProjectNameDesc() {
        return taskDao.getTasksByProjectNameDesc();
    }

    public LiveData<List<Task>> getTasksByDateAsc() {
        return taskDao.getTasksByDateAsc();
    }

    public LiveData<List<Task>> getTasksByDateDesc() {
        return taskDao.getTasksByDateDesc();
    }

    public void addTask(Task taskToAdd) {
        TaskDatabase.databaseWriteExecutor.execute(() -> taskDao.insert(taskToAdd));
    }

    public void deleteTask(long taskId) {
        TaskDatabase.databaseWriteExecutor.execute(() -> taskDao.delete(taskId));
    }

    // -------------------------------------- PROJECT METHODS --------------------------------------

    public LiveData<List<Project>> getProjects() {
        return taskDao.getAllProjects();
    }

    public LiveData<Project> getProjectById(long projectId) {
        return taskDao.getProjectById(projectId);
    }
}
