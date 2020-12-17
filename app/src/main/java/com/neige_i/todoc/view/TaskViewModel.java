package com.neige_i.todoc.view;

import android.content.res.ColorStateList;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.neige_i.todoc.R;
import com.neige_i.todoc.data.model.Project;
import com.neige_i.todoc.data.model.Task;
import com.neige_i.todoc.data.repository.TaskRepository;
import com.neige_i.todoc.view.util.SingleLiveEvent;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class TaskViewModel extends ViewModel {

    // ------------------------------------ LIVE DATA VARIABLES ------------------------------------

    @NonNull
    private final MutableLiveData<OrderBy> orderBy = new MutableLiveData<>();
    @NonNull
    private final SingleLiveEvent<Void> openDialogEvent = new SingleLiveEvent<>();
    @NonNull
    private final SingleLiveEvent<Void> dismissDialogEvent = new SingleLiveEvent<>();
    @NonNull
    private final SingleLiveEvent<Integer> errorMessageEvent = new SingleLiveEvent<>();
    @NonNull
    private final MediatorLiveData<MainUiModel> mainUiModelMediatorLiveData = new MediatorLiveData<>();

    // -------------------------------------- LOCAL VARIABLES --------------------------------------

    @NonNull
    private final TaskRepository taskRepository;
    @NonNull
    private final Clock clock;
    @NonNull
    final Handler handler;

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    public TaskViewModel(@NonNull TaskRepository taskRepository, @NonNull Clock clock, @NonNull Handler handler) {
        this.taskRepository = taskRepository;
        this.clock = clock;
        this.handler = handler;
        orderBy.setValue(OrderBy.NONE);

        final LiveData<List<Task>> tasksLiveData = Transformations.switchMap(orderBy, orderBy -> {
            switch (orderBy) {
                case TASK_NAME_ASC:
                    return taskRepository.getTasksByNameAsc();
                case TASK_NAME_DESC:
                    return taskRepository.getTasksByNameDesc();
                case PROJECT_NAME_ASC:
                    return taskRepository.getTasksByProjectNameAsc();
                case PROJECT_NAME_DESC:
                    return taskRepository.getTasksByProjectNameDesc();
                case DATE_ASC:
                    return taskRepository.getTasksByDateAsc();
                case DATE_DESC:
                    return taskRepository.getTasksByDateDesc();
                default:
                    return taskRepository.getTasks();
            }
        });

        final LiveData<List<Project>> projectsLiveData = taskRepository.getProjects();

        mainUiModelMediatorLiveData.addSource(tasksLiveData, tasks -> combine(tasks, projectsLiveData.getValue()));

        mainUiModelMediatorLiveData.addSource(projectsLiveData, projects -> combine(tasksLiveData.getValue(), projects));
    }

    public LiveData<MainUiModel> getUiState() {
        return mainUiModelMediatorLiveData;
    }

    private void combine(@Nullable List<Task> tasks, @Nullable List<Project> projects) {
        if (tasks == null || projects == null) {
            return;
        }

        final List<TaskUiModel> taskUiModels = new ArrayList<>();

        for (Task task : tasks) {
            for (Project project : projects) {
                if (task.getProjectId() == project.getId()) {
                    taskUiModels.add(
                        new TaskUiModel(
                            task.getId(),
                            task.getName(),
                            project.getName(),
                            ColorStateList.valueOf(project.getColor())
                        )
                    );

                    break;
                }
            }
        }

        mainUiModelMediatorLiveData.setValue(new MainUiModel(taskUiModels, taskUiModels.isEmpty(), projects));
    }

    @NonNull
    public LiveData<Void> getOpenDialogEvent() {
        return openDialogEvent;
    }

    @NonNull
    public LiveData<Void> getDismissDialogEvent() {
        return dismissDialogEvent;
    }

    @NonNull
    public LiveData<Integer> getErrorMessageEvent() {
        return errorMessageEvent;
    }

    // --------------------------------------- TASK METHODS ----------------------------------------

    public void openDialog() {
        openDialogEvent.call();
    }

    public void removeTask(long taskId) {
        taskRepository.deleteTask(taskId);
    }

    public void setSortType(OrderBy orderBy) {
        this.orderBy.setValue(orderBy);
    }

    public void checkTask(@NonNull String taskName, long projectId) {
        getProjectByIdAsync(projectId, project -> {
            if (taskName.trim().isEmpty()) {
                errorMessageEvent.setValue(R.string.empty_task_name);
            } else if (project != null) {
                taskRepository.addTask(new Task(
                    project.getId(),
                    taskName,
                    Instant.now(clock).toEpochMilli()
                ));

                dismissDialogEvent.call();
            }
        });
    }

    // --------------------------------------- ASYNC METHOD ----------------------------------------

    private void getProjectByIdAsync(long projectId, @NonNull Callback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Background work here: query project from database
            final Project project = taskRepository.getProjectById(projectId);

            handler.post(() -> {
                // UI thread work here: return SQL query result to main thread
                callback.getProject(project);
            });
        });
    }

    // ------------------------------------ CALLBACK INTERFACE -------------------------------------

    interface Callback {
        void getProject(@Nullable Project project);
    }

    // ---------------------------------------- ENUM CLASS -----------------------------------------

    enum OrderBy {
        TASK_NAME_ASC,
        TASK_NAME_DESC,
        PROJECT_NAME_ASC,
        PROJECT_NAME_DESC,
        DATE_ASC,
        DATE_DESC,
        NONE,
    }
}
