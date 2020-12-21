package com.neige_i.todoc.view.list_task;

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
import com.neige_i.todoc.view.ViewModelFactory;
import com.neige_i.todoc.view.add_task.AddTaskDialogFragment;

import static com.neige_i.todoc.view.list_task.ListViewModel.OrderBy.DATE_ASC;
import static com.neige_i.todoc.view.list_task.ListViewModel.OrderBy.DATE_DESC;
import static com.neige_i.todoc.view.list_task.ListViewModel.OrderBy.PROJECT_NAME_ASC;
import static com.neige_i.todoc.view.list_task.ListViewModel.OrderBy.PROJECT_NAME_DESC;
import static com.neige_i.todoc.view.list_task.ListViewModel.OrderBy.TASK_NAME_ASC;
import static com.neige_i.todoc.view.list_task.ListViewModel.OrderBy.TASK_NAME_DESC;

/**
 * This {@link AppCompatActivity Activity} displays:
 * <ul>
 *     <li>a list of tasks (or an 'empty state' text if no task is stored in the database)</li>
 *     <li>a button to add a new task</li>
 *     <li>a menu item to sort the tasks</li>
 * </ul>
 *
 */
public class ListActivity extends AppCompatActivity {

    // ---------------------------------------- VIEW MODEL -----------------------------------------

    private ListViewModel viewModel;

    // ------------------------------------- ACTIVITY METHODS --------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Config layout and toolbar
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        // Init ViewModel
        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ListViewModel.class);

        // Init UI components
        final TaskAdapter taskAdapter = new TaskAdapter(viewModel::onTaskRemoved);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.filter_alphabetical_task) {
            viewModel.onSortingSelected(TASK_NAME_ASC);
            return true;
        } else if (id == R.id.filter_alphabetical_inverted_task) {
            viewModel.onSortingSelected(TASK_NAME_DESC);
            return true;
        } else if (id == R.id.filter_alphabetical_project) {
            viewModel.onSortingSelected(PROJECT_NAME_ASC);
            return true;
        } else if (id == R.id.filter_alphabetical_inverted_project) {
            viewModel.onSortingSelected(PROJECT_NAME_DESC);
            return true;
        } else if (id == R.id.filter_oldest_first) {
            viewModel.onSortingSelected(DATE_ASC);
            return true;
        } else if (id == R.id.filter_recent_first) {
            viewModel.onSortingSelected(DATE_DESC);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}