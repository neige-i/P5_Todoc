package com.neige_i.todoc.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.neige_i.todoc.data.model.Project;
import com.neige_i.todoc.data.model.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM Task")
    LiveData<List<Task>> getAllTasks();

    // TIPS: 'COLLATE NOCASE' removes case sensitivity
    @Query("SELECT * FROM Task ORDER BY name COLLATE NOCASE ASC")
    LiveData<List<Task>> getTasksByNameAsc();

    @Query("SELECT * FROM Task ORDER BY name COLLATE NOCASE DESC")
    LiveData<List<Task>> getTasksByNameDesc();

    @Query("SELECT * FROM Task ORDER BY creation_timestamp ASC")
    LiveData<List<Task>> getTasksByDateAsc();

    @Query("SELECT * FROM Task ORDER BY creation_timestamp DESC")
    LiveData<List<Task>> getTasksByDateDesc();

    @Insert
    void insert(Task task);

    @Query("DELETE FROM Task WHERE id = :taskId")
    void delete(long taskId);

    @Query("DELETE FROM Task")
    void clearAllTasks();

    @Insert
    void insert(Project project);

    @Query("SELECT * FROM Project")
    LiveData<List<Project>> getAllProjects();

    // ASKME: null value if no match
    @Query("SELECT * FROM Project WHERE id = :projectId")
    LiveData<Project> getProjectById(long projectId);

    @Query("SELECT * FROM Project WHERE name = :projectName")
    LiveData<Project> getProjectByName(String projectName);
}
