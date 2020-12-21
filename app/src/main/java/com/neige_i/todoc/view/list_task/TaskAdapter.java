package com.neige_i.todoc.view.list_task;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.neige_i.todoc.R;

public class TaskAdapter extends ListAdapter<TaskUiModel, TaskAdapter.TaskViewHolder> {

    // ------------------------------------  CALLBACK VARIABLES ------------------------------------

    @NonNull
    private final OnTaskDeletedCallback onTaskDeletedCallback;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

    protected TaskAdapter(@NonNull OnTaskDeletedCallback onTaskDeletedCallback) {
        super(new TaskDiffCallback());
        this.onTaskDeletedCallback = onTaskDeletedCallback;
    }

    // -------------------------------------- ADAPTER METHODS --------------------------------------

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskViewHolder(LayoutInflater.from(parent.getContext()).inflate(
            R.layout.item_task,
            parent,
            false
        ), onTaskDeletedCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        final TaskUiModel taskUiModel = getItem(position);
        holder.imgDelete.setTag(taskUiModel.getTaskId());
        holder.lblTaskName.setText(taskUiModel.getTaskName());
        holder.lblProjectName.setText(taskUiModel.getProjectName());
        holder.imgProject.setImageTintList(ColorStateList.valueOf(taskUiModel.getProjectColor()));
    }

    // ------------------------------------- VIEW HOLDER CLASS -------------------------------------

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        /**
         * The circle icon showing the color of the project
         */
        private final AppCompatImageView imgProject;
        /**
         * The TextView displaying the name of the task
         */
        private final TextView lblTaskName;
        /**
         * The TextView displaying the name of the project
         */
        private final TextView lblProjectName;
        /**
         * The delete icon
         */
        private final AppCompatImageView imgDelete;

        public TaskViewHolder(@NonNull View itemView, @NonNull OnTaskDeletedCallback onTaskDeletedCallback) {
            super(itemView);

            imgProject = itemView.findViewById(R.id.img_project);
            lblTaskName = itemView.findViewById(R.id.lbl_task_name);
            lblProjectName = itemView.findViewById(R.id.lbl_project_name);
            imgDelete = itemView.findViewById(R.id.img_delete);

            imgDelete.setOnClickListener(view -> onTaskDeletedCallback.onTaskDeleted((long) view.getTag()));
        }
    }

    // -------------------------------------- DIFF UTIL CLASS --------------------------------------

    static class TaskDiffCallback extends DiffUtil.ItemCallback<TaskUiModel> {

        @Override
        public boolean areItemsTheSame(@NonNull TaskUiModel oldItem, @NonNull TaskUiModel newItem) {
            return oldItem.getTaskId() == newItem.getTaskId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull TaskUiModel oldItem, @NonNull TaskUiModel newItem) {
            return oldItem.equals(newItem);
        }
    }

    // ------------------------------------ CALLBACK INTERFACE -------------------------------------

    /**
     * Listener for deleting tasks.
     */
    interface OnTaskDeletedCallback {
        /**
         * Called when a task needs to be deleted.
         */
        void onTaskDeleted(long taskId);
    }
}
