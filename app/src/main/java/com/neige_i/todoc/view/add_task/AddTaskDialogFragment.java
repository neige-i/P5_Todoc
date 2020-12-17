package com.neige_i.todoc.view.add_task;

import android.annotation.SuppressLint;
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

public class AddTaskDialogFragment extends DialogFragment {

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_add_task, null);

        AddTaskViewModel viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(AddTaskViewModel.class);

        final TextInputLayout taskNameLayout = view.findViewById(R.id.layout_task_name);
        final TextInputEditText taskNameInput = view.findViewById(R.id.input_task_name);
        final AutoCompleteTextView projectNameInput = view.findViewById(R.id.input_project_name);

        viewModel.getUiModelLiveData().observe(this, projects -> {
            projectNameInput.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                projects
            ));

            Project firstProjectOfTheList = projects.isEmpty() ? null : projects.get(0);

            if (firstProjectOfTheList != null) {
                projectNameInput.setText(firstProjectOfTheList.getName(), false);
                viewModel.onProjectSelected(firstProjectOfTheList);

                projectNameInput.setOnItemClickListener((parent, spinnerView, position, id) ->
                    viewModel.onProjectSelected(projects.get(position))
                );
            }
        });

        viewModel.getErrorMessageEvent().observe(this, errorStringRes -> taskNameLayout.setError(getString(errorStringRes)));

        viewModel.getDismissDialogEvent().observe(this, aVoid -> dismiss());

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

        return new AlertDialog.Builder(getContext())
            .setTitle(R.string.add_task)
            .setView(view)
            .setPositiveButton(R.string.add, (dialog, which) -> viewModel.onPositiveButtonClicked())
            .create();
    }
}
