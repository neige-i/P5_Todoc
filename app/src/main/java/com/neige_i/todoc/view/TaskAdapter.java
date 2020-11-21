package com.neige_i.todoc.view;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.neige_i.todoc.R;
import com.neige_i.todoc.data.model.Project;
import com.neige_i.todoc.data.model.Task;

import java.util.List;

//public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    // ------------------------------------  CALLBACK VARIABLES ------------------------------------

    @NonNull
    private final DeleteTaskListener deleteTaskListener;
    private List<Task> tasks;

    // ---------------------------------------- CONSTRUCTOR ----------------------------------------

//    protected TaskAdapter(@NonNull DeleteTaskListener deleteTaskListener) {
//        super(new TaskDiffCallback());
//        this.deleteTaskListener = deleteTaskListener;
//    }

    public TaskAdapter(List<Task> tasks, @NonNull DeleteTaskListener deleteTaskListener) {
        this.deleteTaskListener = deleteTaskListener;
        this.tasks = tasks;
    }

    public void setNewTaskList(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    // -------------------------------------- ADAPTER METHODS --------------------------------------

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskViewHolder(LayoutInflater.from(parent.getContext()).inflate(
            R.layout.item_task,
            parent,
            false
        ), deleteTaskListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
//        final Task task = getItem(position);
        final Task task = tasks.get(position);
        holder.lblTaskName.setText(task.getName());
        holder.imgDelete.setTag(task);

        final Project taskProject = task.getProject();
        if (taskProject != null) {
            holder.imgProject.setImageTintList(ColorStateList.valueOf(taskProject.getColor()));
            holder.lblProjectName.setText(taskProject.getName());
        } else {
            holder.imgProject.setVisibility(View.INVISIBLE);
            holder.lblProjectName.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
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

        /**
         * The listener for when a task needs to be deleted
         */
        private final DeleteTaskListener deleteTaskListener;

        public TaskViewHolder(@NonNull View itemView, @NonNull DeleteTaskListener deleteTaskListener) {
            super(itemView);

            this.deleteTaskListener = deleteTaskListener;

            imgProject = itemView.findViewById(R.id.img_project);
            lblTaskName = itemView.findViewById(R.id.lbl_task_name);
            lblProjectName = itemView.findViewById(R.id.lbl_project_name);
            imgDelete = itemView.findViewById(R.id.img_delete);

            imgDelete.setOnClickListener(view -> {
                final Object tag = view.getTag();
                if (tag instanceof Task) {
                    TaskViewHolder.this.deleteTaskListener.onDeleteTask((Task) tag);
                }
            });
        }
    }

    // -------------------------------------- DIFF UTIL CLASS --------------------------------------

//    static class TaskDiffCallback extends DiffUtil.ItemCallback<Task> {
//
//        @Override
//        public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
//            return oldItem.getId() == newItem.getId();
//        }
//
//        @Override
//        public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
//            return oldItem.equals(newItem);
//        }
//    }

    // ------------------------------------ CALLBACK INTERFACE -------------------------------------

    /**
     * Listener for deleting tasks.
     */
    interface DeleteTaskListener {
        /**
         * Called when a task needs to be deleted.
         */
        void onDeleteTask(@NonNull Task task);
    }
}
