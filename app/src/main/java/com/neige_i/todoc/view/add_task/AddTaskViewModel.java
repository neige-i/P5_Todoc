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

/**
 * This {@link ViewModel} handles when:
 * <ul>
 *     <li>a task is added to the database</li>
 *     <li>the dialog is dismissed</li>
 *     <li>an error message is displayed</li>
 * </ul>
 */
public class AddTaskViewModel extends ViewModel {

    // ----------------------------------- INJECTED DEPENDENCIES -----------------------------------

    @NonNull
    private final TaskRepository taskRepository;
    @NonNull
    private final Clock clock;

    // -------------------------------------- EVENT LIVE DATA --------------------------------------

    @NonNull
    private final SingleLiveEvent<Void> dismissDialogEvent = new SingleLiveEvent<>();
    @NonNull
    private final SingleLiveEvent<Integer> errorMessageEvent = new SingleLiveEvent<>();

    // -------------------------------------- LOCAL VARIABLES --------------------------------------

    private Project selectedProject;
    private String taskName;

    // ----------------------------------- CONSTRUCTOR & GETTERS -----------------------------------

    public AddTaskViewModel(@NonNull TaskRepository taskRepository, @NonNull Clock clock) {
        this.taskRepository = taskRepository;
        this.clock = clock;
    }

    public LiveData<List<Project>> getProjectList() {
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

    // --------------------------------------- TASK METHODS ----------------------------------------

    public void onProjectSelected(@NonNull Project project) {
        selectedProject = project;
    }

    public void onTaskNameChanged(@NonNull String newTaskName) {
        taskName = newTaskName;
    }

    public void onPositiveButtonClicked() {
        // TIPS:
        //  taskName is set in main thread inside onTaskNameChanged() and is used in background thread inside TaskRepository.addTask()
        //  To ensure that the appropriate task name is actually used when adding it to the database, we capture it in a final variable
        //  Without it, adding an empty named task is possible
        final String capturedTaskName = taskName;

        // If no name (or an empty one) is typed, display an error message
        if (capturedTaskName == null || capturedTaskName.trim().isEmpty()) {
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

                // TIPS: postValue() instead of setValue() because this lambda is called inside a background thread
                dismissDialogEvent.postCall();
            });
        }
    }
}
