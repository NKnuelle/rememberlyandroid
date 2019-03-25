package de.rememberly.rememberlyandroidapp.adapter;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.ArrayList;

import de.rememberly.rememberlyandroidapp.R;
import de.rememberly.rememberlyandroidapp.activities.TodoActivity;
import de.rememberly.rememberlyandroidapp.model.Todo;

public class DoneTodoAdapter extends RecyclerView.Adapter<DoneTodoAdapter.TodoViewHolder> {
    private ArrayList<Todo> todos;

    static class TodoViewHolder extends RecyclerView.ViewHolder {

        CheckBox todoCheck;

        TodoViewHolder(CheckBox checkBox) {
            super(checkBox);
            todoCheck = checkBox;
        }
    }
        public DoneTodoAdapter(ArrayList<Todo> dataset) {
            todos = dataset;
        }
        @Override
        @NonNull
        public DoneTodoAdapter.TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            CheckBox v = (CheckBox) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.todo_view, parent, false);

            return new TodoViewHolder(v);
        }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final TodoViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.todoCheck.setText(todos.get(position).getTodoText());
        holder.todoCheck.setChecked(true);
        holder.todoCheck.setPaintFlags(holder.todoCheck.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.todoCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                ((TodoActivity) v.getContext()).setTodoUndone(todos.get(position), position);
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return todos.size();
    }
}
