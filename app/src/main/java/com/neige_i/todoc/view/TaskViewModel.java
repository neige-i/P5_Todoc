package com.neige_i.todoc.view;

import android.content.res.ColorStateList;

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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TaskViewModel extends ViewModel {

    // ------------------------------------ LIVE DATA VARIABLES ------------------------------------

    private final MutableLiveData<OrderBy> orderBy = new MutableLiveData<>();
    private final SingleLiveEvent<Void> dismissDialogEvent = new SingleLiveEvent<>();
    private final SingleLiveEvent<Integer> errorMessageEvent = new SingleLiveEvent<>();

    // TODO Plus besoin
    private final MediatorLiveData<Void> fakeLiveData = new MediatorLiveData<>();

    private final MediatorLiveData<MainUiModel> mainUiModelMediatorLiveData = new MediatorLiveData<>();

    // -------------------------------------- LOCAL VARIABLES --------------------------------------

    private final TaskRepository taskRepository;

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    public TaskViewModel(@NonNull TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
        orderBy.setValue(OrderBy.NONE);

        LiveData<List<Task>> tasksLiveData = Transformations.switchMap(orderBy, orderBy -> {
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

        LiveData<List<Project>> projectsLiveData = taskRepository.getProjects();

        mainUiModelMediatorLiveData.addSource(tasksLiveData, tasks -> combine(tasks, projectsLiveData.getValue()));

        mainUiModelMediatorLiveData.addSource(projectsLiveData, projects -> combine(tasksLiveData.getValue(), projects));
    }

    // TODO Don't expose multiple LiveData<State>
    public LiveData<List<Project>> getProjectList() {
        return taskRepository.getProjects();
    }

    public LiveData<MainUiModel> getUiState() {
        return mainUiModelMediatorLiveData;
    }

    private void combine(@Nullable List<Task> tasks, @Nullable List<Project> projects) {
        if (tasks == null || projects == null) {
            return;
        }

        List<TaskUiModel> taskUiModels = new ArrayList<>();

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

        mainUiModelMediatorLiveData.setValue(
            new MainUiModel(
                taskUiModels,
                taskUiModels.isEmpty()
            )
        );
    }

    public LiveData<Void> getFakeLiveData() {
        return fakeLiveData;
    }

    public LiveData<Void> getDismissDialogEvent() {
        return dismissDialogEvent;
    }

    public LiveData<Integer> getErrorMessageEvent() {
        return errorMessageEvent;
    }

    // --------------------------------------- TASK METHODS ----------------------------------------

    public void removeTask(long taskId) {
        taskRepository.deleteTask(taskId);
    }

    public void setSortType(OrderBy orderBy) {
        this.orderBy.setValue(orderBy);
    }

    public void checkTask(@NonNull String taskName, long projectId) {
        fakeLiveData.addSource(taskRepository.getProjectById(projectId), project -> {
            if (taskName.trim().isEmpty()) {
                errorMessageEvent.setValue(R.string.empty_task_name);
            } else if (project != null) {
                taskRepository.addTask(new Task(
                    project.getId(),
                    taskName,
                    Instant.now().toEpochMilli()
                ));

                dismissDialogEvent.call();
            }
        });
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
