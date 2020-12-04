package com.neige_i.todoc.view;

import androidx.annotation.NonNull;
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
import java.util.List;

public class TaskViewModel extends ViewModel {

    // ------------------------------------ LIVE DATA VARIABLES ------------------------------------

    @NonNull
    private final MutableLiveData<ORDER_BY> orderBy = new MutableLiveData<>();
    @NonNull
    private final SingleLiveEvent<Void> dismissDialogEvent = new SingleLiveEvent<>();
    @NonNull
    private final SingleLiveEvent<Integer> errorMessageEvent = new SingleLiveEvent<>();

    @NonNull
    private final MediatorLiveData<Void> fakeLiveData = new MediatorLiveData<>();

    // -------------------------------------- LOCAL VARIABLES --------------------------------------

    @NonNull
    private final TaskRepository taskRepository;
    @NonNull
    private final Clock clock;

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    public TaskViewModel(@NonNull TaskRepository taskRepository, @NonNull Clock clock) {
        this.taskRepository = taskRepository;
        this.clock = clock;
        orderBy.setValue(ORDER_BY.NONE);
    }

    @NonNull
    public LiveData<List<Project>> getProjectList() {
        return taskRepository.getProjects();
    }

    @NonNull
    public LiveData<MainUiModel> getUiState() {
        return Transformations.map(Transformations.switchMap(orderBy, orderBy -> {
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
        }), MainUiModel::new);
    }

    @NonNull
    public LiveData<Void> getFakeLiveData() {
        return fakeLiveData;
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

    public void removeTask(long taskId) {
        taskRepository.deleteTask(taskId);
    }

    public void setSortType(ORDER_BY order_by) {
        orderBy.setValue(order_by);
    }

    public void checkTask(@NonNull String taskName, long projectId) {
        fakeLiveData.addSource(taskRepository.getProjectById(projectId), project -> {
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

    // ---------------------------------------- ENUM CLASS -----------------------------------------

    enum ORDER_BY {
        TASK_NAME_ASC,
        TASK_NAME_DESC,
        PROJECT_NAME_ASC,
        PROJECT_NAME_DESC,
        DATE_ASC,
        DATE_DESC,
        NONE,
    }
}
