package com.neige_i.todoc.view.add_task;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.neige_i.todoc.R;
import com.neige_i.todoc.data.model.Project;
import com.neige_i.todoc.data.model.Task;
import com.neige_i.todoc.data.repository.TaskRepository;
import com.neige_i.todoc.view.util.SingleLiveEvent;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

public class AddTaskViewModel extends ViewModel {

    @NonNull
    private final TaskRepository taskRepository;
    @NonNull
    private final Clock clock;

    @NonNull
    private final SingleLiveEvent<Void> dismissDialogEvent = new SingleLiveEvent<>();
    @NonNull
    private final SingleLiveEvent<Integer> errorMessageEvent = new SingleLiveEvent<>();

    private Project selectedProject;
    private String taskName;

    public AddTaskViewModel(@NonNull TaskRepository taskRepository, @NonNull Clock clock) {
        this.taskRepository = taskRepository;
        this.clock = clock;
    }

    public LiveData<List<Project>> getUiModelLiveData() {
        return taskRepository.getProjects();
    }

    @NonNull
    public SingleLiveEvent<Void> getDismissDialogEvent() {
        return dismissDialogEvent;
    }

    @NonNull
    public SingleLiveEvent<Integer> getErrorMessageEvent() {
        return errorMessageEvent;
    }

    public void onProjectSelected(Project project) {
        selectedProject = project;
    }

    public void onTaskNameChanged(String newTaskName) {
        taskName = newTaskName;
    }

    public void onPositiveButtonClicked() {
        String capturedTaskName = taskName;

        if (capturedTaskName.trim().isEmpty()) {
            errorMessageEvent.setValue(R.string.empty_task_name);
        } else {
            taskRepository.getProjectById(selectedProject.getId(), project -> {
                taskRepository.addTask(
                    new Task(
                        project.getId(),
                        capturedTaskName,
                        Instant.now(clock).toEpochMilli()
                    )
                );

                dismissDialogEvent.postCall();
            });
        }
    }
}
