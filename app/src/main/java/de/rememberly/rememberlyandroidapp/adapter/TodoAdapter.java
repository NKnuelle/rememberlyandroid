package de.rememberly.rememberlyandroidapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.ArrayList;

import de.rememberly.rememberlyandroidapp.R;
import de.rememberly.rememberlyandroidapp.activities.TodoActivity;
import de.rememberly.rememberlyandroidapp.model.Todo;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    private ArrayList<Todo> todos;

    public static class TodoViewHolder extends RecyclerView.ViewHolder {

        public CheckBox todoCheck;

        public TodoViewHolder(CheckBox checkBox) {
            super(checkBox);
            todoCheck = checkBox;
        }
    }
        public TodoAdapter(ArrayList<Todo> dataset) {
            todos = dataset;
        }
        @Override
        public TodoAdapter.TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            CheckBox v = (CheckBox) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.todo_view, parent, false);

            TodoViewHolder vh = new TodoViewHolder(v);
            return vh;
        }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final TodoViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.todoCheck.setText(todos.get(position).getTodo_text());
        holder.todoCheck.setChecked(false);
        holder.todoCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                ((TodoActivity) v.getContext()).setTodoDone(todos.get(position), position);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return todos.size();
    }
}
