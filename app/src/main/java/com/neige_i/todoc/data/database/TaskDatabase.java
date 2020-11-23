package com.neige_i.todoc.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.neige_i.todoc.data.model.Project;
import com.neige_i.todoc.data.model.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Task.class, Project.class}, version = 1, exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();

    private static volatile TaskDatabase taskDatabase;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static TaskDatabase getInstance(@NonNull Context context) {
        if (taskDatabase == null) {
            synchronized (TaskDatabase.class) {
                if (taskDatabase == null)
                    taskDatabase = Room.databaseBuilder(
                        context.getApplicationContext(),
                        TaskDatabase.class,
                        "task_database.db"
                    ).addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            databaseWriteExecutor.execute(() -> {
                                taskDatabase.taskDao().insert(new Project(1L, "Projet Tartampion", 0xFFEADAD1));
                                taskDatabase.taskDao().insert(new Project(2L, "Projet Lucidia", 0xFFB4CDBA));
                                taskDatabase.taskDao().insert(new Project(3L, "Projet Circus", 0xFFA3CED2));
                            });
                        }
                    }).build();
            }
        }
        return taskDatabase;
    }
}
