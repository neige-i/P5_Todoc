package com.neige_i.todoc.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.neige_i.todoc.R;
import com.neige_i.todoc.view.add_task.AddTaskDialogFragment;

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
        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(TaskViewModel.class);

        initUi();
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
        final TaskAdapter taskAdapter = new TaskAdapter(viewModel::removeTask);
        ((RecyclerView) findViewById(R.id.list_task)).setAdapter(taskAdapter);
        final TextView noTaskLbl = findViewById(R.id.lbl_no_task);

        findViewById(R.id.fab_add_task).setOnClickListener(v ->
            new AddTaskDialogFragment().show(getSupportFragmentManager(), null)
        );

        // Update UI when state is changed
        viewModel.getUiState().observe(this, mainUiModel -> {
            taskAdapter.submitList(mainUiModel.getTaskUiModels());
            noTaskLbl.setVisibility(mainUiModel.isNoTaskVisible() ? View.VISIBLE : View.GONE);
        });
    }
}