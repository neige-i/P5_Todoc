package com.neige_i.todoc.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.neige_i.todoc.R;
import com.neige_i.todoc.data.model.Project;
import com.neige_i.todoc.data.model.Task;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TaskAdapter taskAdapter;
    private final List<Task> tasks = new ArrayList<>();
    private TextView noTaskLbl;
    private int sortId;

    // ------------------------------------- ACTIVITY METHODS --------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Config layout and toolbar
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        // Init ViewModel
        final TaskViewModel viewModel = new ViewModelProvider(
            this,
            new ViewModelProvider.AndroidViewModelFactory(getApplication())
        ).get(TaskViewModel.class);

        // Init views
        noTaskLbl = findViewById(R.id.lbl_no_task);
        initRecyclerView(viewModel);
        findViewById(R.id.fab_add_task).setOnClickListener(v -> initDialog(viewModel));

        // Observe LiveData updates
        viewModel.getTaskList().observe(this, newTasks -> {
            tasks.clear();
            tasks.addAll(newTasks);
            updateTasks();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.filter_alphabetical ||
            id == R.id.filter_alphabetical_inverted ||
            id == R.id.filter_oldest_first ||
            id == R.id.filter_recent_first
        ) {
            sortId = id;
            updateTasks();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ------------------------------------- INIT VIEW METHODS -------------------------------------

    private void initRecyclerView(@NonNull TaskViewModel viewModel) {
        taskAdapter = new TaskAdapter(tasks, viewModel::removeTask);
        ((RecyclerView) findViewById(R.id.list_task)).setAdapter(taskAdapter);
    }

    private void initDialog(@NonNull TaskViewModel viewModel) {
        // Init and show dialog
        final AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle(R.string.add_task)
            .setView(R.layout.dialog_add_task)
            .setPositiveButton(R.string.add, null) // TIPS: null listener to avoid automatic dismiss
            .create();
        dialog.show();

        // Init views
        final TextInputLayout taskNameLayout = dialog.findViewById(R.id.layout_task_name);
        final TextInputEditText taskNameInput = dialog.findViewById(R.id.input_task_name);
        final AutoCompleteTextView projectNameInput = dialog.findViewById(R.id.input_project_name);
        final Project[] allProjects = Project.getAllProjects();
        projectNameInput.setAdapter(new ArrayAdapter<>(
            this,
            android.R.layout.simple_list_item_1,
            allProjects
        ));
        projectNameInput.setText(allProjects[0].toString(), false);

        // Config button listener
        // TIPS: call getButton() AFTER showing the dialog (otherwise getButton() returns null)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
            positiveButton -> onPositiveButtonClicked(dialog, taskNameInput, taskNameLayout, projectNameInput, viewModel)
        );
    }

    private void onPositiveButtonClicked(AlertDialog dialog, TextInputEditText taskNameInput, TextInputLayout taskNameLayout,
                                         AutoCompleteTextView projectNameInput, @NonNull TaskViewModel viewModel) {
        final String taskName = taskNameInput.getText().toString();
        final Project selectedProject = Project.getProjectByName(projectNameInput.getText().toString());
        if (taskName.trim().isEmpty()) {
            taskNameLayout.setError(getString(R.string.empty_task_name));
        } else if (selectedProject != null) {
            final Task taskToAdd = new Task(
                selectedProject.getId(),
                taskName,
                Instant.now().toEpochMilli()
            );
            viewModel.addTask(taskToAdd);

            dialog.dismiss();
        }
    }

    // --------------------------------------- DATA METHODS ----------------------------------------

    /**
     * Updates the list of tasks in the UI
     */
    private void updateTasks() {
        if (tasks.size() == 0) {
            noTaskLbl.setVisibility(View.VISIBLE);
        } else {
            noTaskLbl.setVisibility(View.GONE);
            int what = Task.SORT_NOT_SET;
            int order = Task.SORT_NOT_SET;
            if (sortId == R.id.filter_alphabetical) {
                what = Task.SORT_BY_NAME;
                order = Task.SORT_ASCENDING;
//                Collections.sort(tasks, new Task.TaskAZComparator());
            } else if (sortId == R.id.filter_alphabetical_inverted) {
                what = Task.SORT_BY_NAME;
                order = Task.SORT_DESCENDING;
//                Collections.sort(tasks, new Task.TaskZAComparator());
            } else if (sortId == R.id.filter_oldest_first) {
                what = Task.SORT_BY_DATE;
                order = Task.SORT_ASCENDING;
//                Collections.sort(tasks, new Task.TaskOldComparator());
            } else if (sortId == R.id.filter_recent_first) {
                what = Task.SORT_BY_DATE;
                order = Task.SORT_DESCENDING;
//                Collections.sort(tasks, new Task.TaskRecentComparator());
            }
            Collections.sort(tasks, Task.compare(what, order));
            taskAdapter.setNewTaskList(tasks);
        }
    }
}