package com.neige_i.todoc.view;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.neige_i.todoc.R;
import com.neige_i.todoc.data.model.Project;
import com.neige_i.todoc.data.model.Task;
import com.neige_i.todoc.data.repository.TaskRepository;
import com.neige_i.todoc.view.util.SingleLiveEvent;

import java.time.Instant;
import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private final MutableLiveData<ORDER_BY> orderBy = new MutableLiveData<>();
    private final SingleLiveEvent<Void> dismissDialogEvent = new SingleLiveEvent<>();
    private final SingleLiveEvent<Integer> errorMessageEvent = new SingleLiveEvent<>();

    private final MediatorLiveData<Void> fakeLiveData = new MediatorLiveData<>();

    private final TaskRepository taskRepository;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepository(application);
        orderBy.setValue(ORDER_BY.NONE);
    }

    public LiveData<List<Project>> getProjectList() {
        return taskRepository.getProjects();
    }

    public LiveData<MainUiModel> getUiState() {
        return Transformations.map(Transformations.switchMap(orderBy, orderBy -> {
            switch (orderBy) {
                case NAME_ASC:
                    return taskRepository.getTasksByNameAsc();
                case NAME_DESC:
                    return taskRepository.getTasksByNameDesc();
                case DATE_ASC:
                    return taskRepository.getTasksByDateAsc();
                case DATE_DESC:
                    return taskRepository.getTasksByDateDesc();
                default:
                    return taskRepository.getTasks();
            }
        }), MainUiModel::new);
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

    public void removeTask(long taskId) {
        taskRepository.deleteTask(taskId);
    }

    public void setSortType(ORDER_BY order_by) {
        orderBy.setValue(order_by);
    }

    public void checkTask(@NonNull String taskName, @NonNull String projectName) {
        // ASKME: asynchronous call
        fakeLiveData.addSource(taskRepository.getProjectByName(projectName), project -> {
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

    enum ORDER_BY {
        NAME_ASC,
        NAME_DESC,
        DATE_ASC,
        DATE_DESC,
        NONE,
    }
}
