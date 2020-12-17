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

import java.util.ArrayList;
import java.util.List;

import static com.neige_i.todoc.view.TaskViewModel.OrderBy.DATE_ASC;
import static com.neige_i.todoc.view.TaskViewModel.OrderBy.DATE_DESC;
import static com.neige_i.todoc.view.TaskViewModel.OrderBy.PROJECT_NAME_ASC;
import static com.neige_i.todoc.view.TaskViewModel.OrderBy.PROJECT_NAME_DESC;
import static com.neige_i.todoc.view.TaskViewModel.OrderBy.TASK_NAME_ASC;
import static com.neige_i.todoc.view.TaskViewModel.OrderBy.TASK_NAME_DESC;

public class MainActivity extends AppCompatActivity {

    // ---------------------------------------- VIEW MODEL -----------------------------------------

    private TaskViewModel viewModel; // ASKME: passing parameters between methods, retrieve project list

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
            TaskViewModelFactory.getInstance(getApplication())
        ).get(TaskViewModel.class);

        // Init UI
        final Object[] uiComponents = initUi();

        // Update Ui
        updateUi(
            (TaskAdapter) uiComponents[0],
            (TextView) uiComponents[1],
            (AlertDialog) uiComponents[2]
        );
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

    private Object[] initUi() {
        final TaskAdapter taskAdapter = new TaskAdapter(viewModel::removeTask);
        ((RecyclerView) findViewById(R.id.list_task)).setAdapter(taskAdapter);
        final TextView noTaskLbl = findViewById(R.id.lbl_no_task);

        final AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle(R.string.add_task)
            .setView(R.layout.dialog_add_task)
            .setPositiveButton(R.string.add, null) // TIPS: null listener to avoid automatic dismiss
            .create();
        findViewById(R.id.fab_add_task).setOnClickListener(v -> viewModel.openDialog());

        // ASKME: use array or POJO to return multiple values
        return new Object[]{taskAdapter, noTaskLbl, dialog};
    }

    private TextInputLayout configDialog(AlertDialog dialog, List<Project> allProjects) {
        // Show dialog
        dialog.show();

        // Find dialog's views
        final TextInputLayout taskNameLayout = dialog.findViewById(R.id.layout_task_name);
        final TextInputEditText taskNameInput = dialog.findViewById(R.id.input_task_name);
        final AutoCompleteTextView projectNameInput = dialog.findViewById(R.id.input_project_name);

        // Init dialog's views
        final int[] adapterPosition = {0};
        projectNameInput.setAdapter(new ArrayAdapter<>(
            this,
            android.R.layout.simple_list_item_1,
            allProjects
        ));
        projectNameInput.setText(allProjects.get(0).toString(), false);
        // Update the adapterPosition variable when a dropdown item is clicked
        projectNameInput.setOnItemClickListener((parent, view, position, id) -> adapterPosition[0] = position);
        taskNameInput.setText("");

        // Config button listener
        // TIPS: call getButton() AFTER showing the dialog (otherwise getButton() returns null)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            // Call the ViewModel method with the task name's input text and the selected project as arguments
            if (taskNameInput.getText() != null)
                viewModel.checkTask(taskNameInput.getText().toString(), allProjects.get(adapterPosition[0]).getId());
        });

        return taskNameLayout;
    }

    private void updateUi(TaskAdapter taskAdapter, TextView noTaskLbl, AlertDialog dialog) {
        // Update UI when state is changed
        final List<Project> projectList = new ArrayList<>();
        viewModel.getUiState().observe(this, mainUiModel -> {
            taskAdapter.submitList(mainUiModel.getTaskUiModels());
            noTaskLbl.setVisibility(mainUiModel.isNoTaskVisible() ? View.VISIBLE : View.GONE);
            projectList.clear();
            projectList.addAll(mainUiModel.getProjectList());
        });

        // Update UI when events are triggered
        final TextInputLayout[] taskNameLayout = new TextInputLayout[1];
        viewModel.getOpenDialogEvent().observe(this, aVoid -> taskNameLayout[0] = configDialog(dialog, projectList));
        viewModel.getDismissDialogEvent().observe(this, aVoid -> dialog.dismiss());
        viewModel.getErrorMessageEvent().observe(this, errorMessageId -> taskNameLayout[0].setError(getString(errorMessageId)));
    }
}