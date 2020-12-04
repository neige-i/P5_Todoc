package com.neige_i.todoc.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

/**
 * <p>Model for the tasks of the application.</p>
 *
 * @author Gaëtan HERFRAY
 */
@Entity(foreignKeys = @ForeignKey(entity = Project.class, parentColumns = "id", childColumns = "project_id"))
public class Task {

    // ------------------------------------ INSTANCE VARIABLES -------------------------------------

    /**
     * The unique identifier of the task
     */
    @PrimaryKey(autoGenerate = true)
    private long id;
    /**
     * The unique identifier of the project associated to the task
     */
    @ColumnInfo(name = "project_id", index = true)
    private final long projectId;
    /**
     * The name of the task
     */
    @NonNull
    private final String name;
    /**
     * The timestamp when the task has been created
     */
    @ColumnInfo(name = "creation_timestamp")
    private final long creationTimestamp;

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    /**
     * Instantiates a new Task.
     *
     * @param id                the unique identifier of the task to set
     * @param projectId         the unique identifier of the project associated to the task to set
     * @param name              the name of the task to set
     * @param creationTimestamp the timestamp when the task has been created to set
     */
    public Task(long id, long projectId, @NonNull String name, long creationTimestamp) {
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.creationTimestamp = creationTimestamp;
    }

    @Ignore
    public Task(long projectId, @NonNull String name, long creationTimestamp) {
        this.projectId = projectId;
        this.name = name;
        this.creationTimestamp = creationTimestamp;
    }

    /**
     * Returns the unique identifier of the task.
     *
     * @return the unique identifier of the task
     */
    public long getId() {
        return id;
    }

    public long getProjectId() {
        return projectId;
    }

    /**
     * Returns the name of the task.
     *
     * @return the name of the task
     */
    @NonNull
    public String getName() {
        return name;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    // -------------------------------------- OBJECT METHODS ---------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
            projectId == task.projectId &&
            creationTimestamp == task.creationTimestamp &&
            name.equals(task.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, projectId, name, creationTimestamp);
    }
}
