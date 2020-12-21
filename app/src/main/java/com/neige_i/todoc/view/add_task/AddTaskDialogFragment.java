package com.neige_i.todoc.view.add_task;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.neige_i.todoc.R;
import com.neige_i.todoc.data.model.Project;
import com.neige_i.todoc.view.ViewModelFactory;

/**
 * This {@link DialogFragment} allows user to add a task. It displays:
 * <ul>
 *     <li>an input: to type the task name</li>
 *     <li>a dropdown menu: to choose the name of the project the task belong to</li>
 * </ul>
 * If the task to add is empty, the dialog is not dismissed and an error message is shown.
 */
public class AddTaskDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // Init ViewModel
        final AddTaskViewModel viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(AddTaskViewModel.class);

        // Init UI components
        final View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        final AutoCompleteTextView projectNameInput = view.findViewById(R.id.input_project_name);
        final TextInputLayout taskNameLayout = view.findViewById(R.id.layout_task_name);
        final TextInputEditText taskNameInput = view.findViewById(R.id.input_task_name);

        taskNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.onTaskNameChanged(s.toString());
            }
        });

        // Update project name (UI state)
        viewModel.getProjectList().observe(this, projects -> {
            // Config AutoCompleteTextView's dropdown
            projectNameInput.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                projects
            ));

            final Project firstProjectOfTheList = projects.isEmpty() ? null : projects.get(0);

            // Config dropdown's selected item if project list is not empty
            if (firstProjectOfTheList != null) {
                projectNameInput.setText(firstProjectOfTheList.getName(), false); // TIPS: disable filtering to keep dropdown unchanged
                viewModel.onProjectSelected(firstProjectOfTheList);

                projectNameInput.setOnItemClickListener((parent, spinnerView, position, id) ->
                    viewModel.onProjectSelected(projects.get(position))
                );
            }
        });

        // Update UI when events are triggered
        viewModel.getErrorMessageEvent().observe(this, errorStringRes -> taskNameLayout.setError(getString(errorStringRes)));
        viewModel.getDismissDialogEvent().observe(this, aVoid -> dismiss());

        // Create dialog
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
            .setTitle(R.string.add_task)
            .setView(view)
            .setPositiveButton(R.string.add, null) // TIPS: null listener to avoid automatic dismiss
            .create();

        // Set positive button listener here
        alertDialog.setOnShowListener(dialog ->
            ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v ->
                viewModel.onPositiveButtonClicked()
            )
        );

        return alertDialog;
    }
}
