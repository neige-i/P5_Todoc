package com.neige_i.todoc.view.list_task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.neige_i.todoc.data.model.Project;
import com.neige_i.todoc.data.model.Task;
import com.neige_i.todoc.data.repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * This {@link ViewModel} handles:
 * <ul>
 *     <li>when the 'empty state' text is displayed</li>
 *     <li>which task list to query according to the selected sorting</li>
 * </ul>
 */
public class ListViewModel extends ViewModel {

    // ----------------------------------- INJECTED DEPENDENCIES -----------------------------------

    @NonNull
    private final TaskRepository taskRepository;

    // -------------------------------------- STATE LIVE DATA --------------------------------------

    @NonNull
    private final MediatorLiveData<ListUiModel> uiState = new MediatorLiveData<>();

    // -------------------------------------- LOCAL VARIABLES --------------------------------------

    @NonNull
    private final MutableLiveData<OrderBy> orderBy = new MutableLiveData<>();

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    public ListViewModel(@NonNull TaskRepository taskRepository) {
        // Init ViewModel: set repository and default order
        this.taskRepository = taskRepository;
        orderBy.setValue(OrderBy.NONE);

        // Retrieve tasks and projects from the repository
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

        // Update UI model when tasks and projects are updated
        uiState.addSource(tasksLiveData, tasks -> combine(tasks, projectsLiveData.getValue()));
        uiState.addSource(projectsLiveData, projects -> combine(tasksLiveData.getValue(), projects));
    }

    public LiveData<ListUiModel> getUiState() {
        return uiState;
    }

    // ------------------------------------- UI MODEL METHODS --------------------------------------

    private void combine(@Nullable List<Task> tasks, @Nullable List<Project> projects) {
        // Update UI model only if both tasks and projects are not null
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
                            project.getColor()
                        )
                    );

                    break;
                }
            }
        }

        uiState.setValue(new ListUiModel(taskUiModels, taskUiModels.isEmpty()));
    }

    // --------------------------------------- TASK METHODS ----------------------------------------

    public void onTaskRemoved(long taskId) {
        taskRepository.deleteTask(taskId);
    }

    public void onSortingSelected(@NonNull OrderBy orderBy) {
        this.orderBy.setValue(orderBy);
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
