package com.neige_i.todoc.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.List;

import static com.neige_i.todoc.view.TaskViewModel.ORDER_BY.DATE_ASC;
import static com.neige_i.todoc.view.TaskViewModel.ORDER_BY.DATE_DESC;
import static com.neige_i.todoc.view.TaskViewModel.ORDER_BY.PROJECT_NAME_ASC;
import static com.neige_i.todoc.view.TaskViewModel.ORDER_BY.PROJECT_NAME_DESC;
import static com.neige_i.todoc.view.TaskViewModel.ORDER_BY.TASK_NAME_ASC;
import static com.neige_i.todoc.view.TaskViewModel.ORDER_BY.TASK_NAME_DESC;

public class MainActivity extends AppCompatActivity {

    private TaskViewModel viewModel;

    private List<Project> allProjects;

    private TaskAdapter taskAdapter;
    private TextView noTaskLbl;
    private AlertDialog dialog;
    private TextInputLayout taskNameLayout;
    private TextInputEditText taskNameInput;
    private AutoCompleteTextView projectNameInput;

    // ------------------------------------- ACTIVITY METHODS --------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Config layout and toolbar
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        // Init ViewModel
        viewModel = new ViewModelProvider(
            this,
            TaskViewModelFactory.getInstance(getApplication())// new ViewModelProvider.AndroidViewModelFactory(getApplication())
        ).get(TaskViewModel.class);

        // Init data
        viewModel.getProjectList().observe(this, projects -> allProjects = projects);

        // Init UI
        initUi();

        // Update Ui
        updateUi();
        viewModel.getFakeLiveData().observe(this, aVoid -> {});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.filter_alphabetical_task) {
            viewModel.setSortType(TASK_NAME_ASC);
            return true;
        } else if (id == R.id.filter_alphabetical_inverted_task) {
            viewModel.setSortType(TASK_NAME_DESC);
            return true;
        } else if (id == R.id.filter_alphabetical_project) {
            viewModel.setSortType(PROJECT_NAME_ASC);
            return true;
        } else if (id == R.id.filter_alphabetical_inverted_project) {
            viewModel.setSortType(PROJECT_NAME_DESC);
            return true;
        } else if (id == R.id.filter_oldest_first) {
            viewModel.setSortType(DATE_ASC);
            return true;
        } else if (id == R.id.filter_recent_first) {
            viewModel.setSortType(DATE_DESC);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    // ---------------------------------------- UI METHODS -----------------------------------------

    private void initUi() {
        taskAdapter = new TaskAdapter(viewModel::removeTask);
        ((RecyclerView) findViewById(R.id.list_task)).setAdapter(taskAdapter);
        noTaskLbl = findViewById(R.id.lbl_no_task);

        dialog = new AlertDialog.Builder(this)
            .setTitle(R.string.add_task)
            .setView(R.layout.dialog_add_task)
            .setPositiveButton(R.string.add, null) // TIPS: null listener to avoid automatic dismiss
            .create();
        findViewById(R.id.fab_add_task).setOnClickListener(v -> configDialog());
    }

    private void configDialog() {
        // Show dialog
        dialog.show();

//        final Project[] allProjects = Project.getAllProjects();

        // Init views
        final int[] adapterPosition = {0};
        taskNameLayout = dialog.findViewById(R.id.layout_task_name);
        taskNameInput = dialog.findViewById(R.id.input_task_name);
        projectNameInput = dialog.findViewById(R.id.input_project_name);
        projectNameInput.setAdapter(new ArrayAdapter<>(
            this,
            android.R.layout.simple_list_item_1,
            allProjects
        ));
        taskNameInput.setText("");
        projectNameInput.setText(allProjects.get(0).toString(), false);
        projectNameInput.setOnItemClickListener((parent, view, position, id) -> adapterPosition[0] = position);

        // Config button listener
        // TIPS: call getButton() AFTER showing the dialog (otherwise getButton() returns null)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
            v -> viewModel.checkTask(taskNameInput.getText().toString(), allProjects.get(adapterPosition[0]).getId())
        );
    }

    private void updateUi() {
        // Update UI when state is changed
        viewModel.getUiState().observe(this, mainUiModel -> {
            taskAdapter.submitList(mainUiModel.getTaskUiModels());
            noTaskLbl.setVisibility(mainUiModel.getNoTaskVisibility());
        });

        // Update UI when events are triggered
        viewModel.getDismissDialogEvent().observe(this, aVoid -> dialog.dismiss());
        viewModel.getErrorMessageEvent().observe(this, errorMessageId -> taskNameLayout.setError(getString(errorMessageId)));
    }
}