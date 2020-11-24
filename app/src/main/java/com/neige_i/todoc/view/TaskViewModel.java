package com.neige_i.todoc.view;

import android.app.Application;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.neige_i.todoc.R;
import com.neige_i.todoc.data.model.Project;
import com.neige_i.todoc.data.model.Task;
import com.neige_i.todoc.data.repository.TaskRepository;
import com.neige_i.todoc.view.util.SingleLiveEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private final TaskRepository taskRepository;
    private final MutableLiveData<MainUiModel> uiState = new MutableLiveData<>();
    private final SingleLiveEvent<Void> dismissDialogEvent = new SingleLiveEvent<>();
    private final SingleLiveEvent<Integer> errorMessageEvent = new SingleLiveEvent<>();
    private final List<Task> roomTaskList = new ArrayList<>();
    private int sortType;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepository(application);
    }

    public LiveData<MainUiModel> getUiState() {
        return Transformations.switchMap(taskRepository.getTasks(), taskList -> {
            roomTaskList.clear();
            roomTaskList.addAll(taskList);
            sortTasks();
            return uiState;
        });
    }

    public LiveData<Void> getDismissDialogEvent() {
        return dismissDialogEvent;
    }

    public LiveData<Integer> getErrorMessageEvent() {
        return errorMessageEvent;
    }

    //    public void addTask(Task taskToAdd) {
//        taskRepository.addTask(taskToAdd);
//    }

    public void removeTask(long taskId) {
        taskRepository.deleteTask(taskId);
    }
    
    public void setSortType(@IdRes int sortType) {
        this.sortType = sortType;
        sortTasks();
    }

    private void sortTasks() {
        if (!roomTaskList.isEmpty()) {
            int what = Task.SORT_NOT_SET;
            int order = Task.SORT_NOT_SET;
            if (sortType == R.id.filter_alphabetical) {
                what = Task.SORT_BY_NAME;
                order = Task.SORT_ASCENDING;
//                Collections.sort(tasks, new Task.TaskAZComparator());
            } else if (sortType == R.id.filter_alphabetical_inverted) {
                what = Task.SORT_BY_NAME;
                order = Task.SORT_DESCENDING;
//                Collections.sort(tasks, new Task.TaskZAComparator());
            } else if (sortType == R.id.filter_oldest_first) {
                what = Task.SORT_BY_DATE;
                order = Task.SORT_ASCENDING;
//                Collections.sort(tasks, new Task.TaskOldComparator());
            } else if (sortType == R.id.filter_recent_first) {
                what = Task.SORT_BY_DATE;
                order = Task.SORT_DESCENDING;
//                Collections.sort(tasks, new Task.TaskRecentComparator());
            }
            Collections.sort(roomTaskList, Task.compare(what, order));
        }
        uiState.setValue(new MainUiModel(roomTaskList));
    }

    public void checkTask(@NonNull String taskName, @NonNull String projectName) {
        final Project selectedProject = Project.getProjectByName(projectName);

        if (taskName.trim().isEmpty()) {
            errorMessageEvent.setValue(R.string.empty_task_name);
//            taskNameLayout.setError(getString(R.string.empty_task_name));
        } else if (selectedProject != null) {
            taskRepository.addTask(new Task(
                selectedProject.getId(),
                taskName,
                Instant.now().toEpochMilli()
            ));

            dismissDialogEvent.call();
        }
    }
}
