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

    @Insert
    void insert(Task task);

    @Query("DELETE FROM Task WHERE id = :taskId")
    void delete(long taskId);

    @Insert
    void insert(Project project);
}
