package de.rememberly.rememberlyandroidapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import de.rememberly.rememberlyandroidapp.R;
import de.rememberly.rememberlyandroidapp.activities.TodoActivity;
import de.rememberly.rememberlyandroidapp.activities.TodolistActivity;
import de.rememberly.rememberlyandroidapp.apputils.PreferencesManager;
import de.rememberly.rememberlyandroidapp.model.HttpResponse;
import de.rememberly.rememberlyandroidapp.model.Todolist;
import de.rememberly.rememberlyandroidapp.remote.APICall;
import de.rememberly.rememberlyandroidapp.remote.ApiUtils;
import de.rememberly.rememberlyandroidapp.service.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodolistAdapter extends RecyclerView.Adapter<TodolistAdapter.TodoViewHolder> {
    private ArrayList<Todolist> todoData;
    private TodolistActivity todolistActivity;
    private APICall apiCall;

    static class TodoViewHolder extends RecyclerView.ViewHolder {

        TextView todoView;
        TextView todoViewOptions;
        private ImageView shareIcon;
        TodoViewHolder(View view) {
            super(view);
            todoView = view.findViewById(R.id.textView);
            todoViewOptions = view.findViewById(R.id.textViewOptions);
            shareIcon = view.findViewById(R.id.shareIcon);
        }
    }
        public TodolistAdapter(TodolistActivity todolistActivity, ArrayList<Todolist> dataset) {
            this.todoData = dataset;
            this.todolistActivity = todolistActivity;
            this.apiCall = new APICall(PreferencesManager.getURL(todolistActivity));
        }
        @Override
        @NonNull
        public TodolistAdapter.TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listitem_view, parent, false);

            return new TodoViewHolder(v);
        }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final TodoViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        initTodoview(holder, position);
        initOptionsMenu(holder, position);
        initShareIcon(holder, position);
    }
    private void initTodoview(final TodoViewHolder holder, final int position) {
        holder.todoView.setText(todoData.get(position).getListName());
        holder.todoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listID = todoData.get(position).getListID();
                Intent intent = new Intent(holder.todoView.getContext(), TodoActivity.class);
                intent.putExtra("listID", listID);
                holder.todoView.getContext().startActivity(intent);
            }
        });
    }
    private void initShareIcon(final TodoViewHolder holder, final int position) {
        if (!todoData.get(position).IsShared()) {
            holder.shareIcon.setVisibility(View.GONE);
        } else {
            holder.shareIcon.setVisibility(View.VISIBLE);
        }
    }
    private void initOptionsMenu(final TodoViewHolder holder, final int position) {
        holder.todoViewOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = holder.todoViewOptions.getContext();

                final PopupMenu popupMenu = new PopupMenu(context, holder.todoViewOptions);
                popupMenu.inflate(R.menu.listoptions);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Todolist list = todoData.get(position);
                        switch (item.getItemId()) {
                            case R.id.optionsdelete:
                                showDeleteDialog(list,  position);
                                break;
                            case R.id.optionsrename:
                                showRenameDialog(list, position);
                                break;
                            case R.id.optionsshare:
                                showShareDialog(list, position);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }
    /** Shows a Dialog to share the Todolist.
    The user has to enter a username to share the todolist with.

     @param list The Todolist the user clicked on

     **/
    private void showShareDialog(final Todolist list, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(todolistActivity);
        builder.setMessage(R.string.shareMessage);
        // Create Edittext to enter username
        final EditText enterUsername = new EditText(todolistActivity);
        enterUsername.setHint(R.string.shareDialogHint);

        builder.setView(enterUsername);
        // Onlick listener is overriden later for error handling
        builder.setPositiveButton(R.string.shareButton, null);

        builder.setNegativeButton(R.string.cancelButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = (dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String token = PreferencesManager.getUserToken(todolistActivity);

                        if (enterUsername.getText().toString().isEmpty()) {
                            enterUsername.setError(todolistActivity.getString(R.string.shareDialogUsernameRequired));
                        } else {
                            JsonObject sharedJSON = createShareJson(enterUsername.getText().toString(), list.getListID());
                            apiCall.shareTodolist(TodolistAdapter.this, token, sharedJSON, position);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
    }
    private void showRenameDialog(final Todolist list, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(todolistActivity);
        builder.setMessage(R.string.renameMessage);
        // Create Edittext to enter username
        final EditText enterNewName = new EditText(todolistActivity);
        enterNewName.setHint(R.string.renameListHint);

        builder.setView(enterNewName);
        // Onlick listener is overriden later for error handling
        builder.setPositiveButton(R.string.renameButton, null);

        builder.setNegativeButton(R.string.cancelButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = (dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String token = PreferencesManager.getUserToken(todolistActivity);

                        if (enterNewName.getText().toString().isEmpty()) {
                            enterNewName.setError(todolistActivity.getString(R.string.renameListRequired));
                        } else {
                            list.setListName(enterNewName.getText().toString());
                            apiCall.updateTodolist(TodolistAdapter.this, token, list, position);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
    }
    private JsonObject createShareJson(String username, String listID) {
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("username", username);
            jsonObject.addProperty("listID", listID);
        } catch (JsonIOException e) {
            Log.e("JSON Error", e.getMessage());
        }
        return jsonObject;
    }
    private void showDeleteDialog(final Todolist list, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(todolistActivity);
        builder.setMessage(R.string.deleteMessage);
        builder.setPositiveButton(R.string.deleteButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String token = PreferencesManager.getUserToken(todolistActivity);
                apiCall.deleteTodolist(TodolistAdapter.this, token, list, position);
            }
        });
        builder.setNegativeButton(R.string.cancelButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void onError(int requestCode, HttpResponse httpResponse) {
        if (requestCode == APICall.TODOLIST_SHARED) {
            todolistActivity.showMessage(httpResponse.getMessage());
        }
    }
    public void onFailure(int requestCode, Throwable t) {
        todolistActivity.showMessage(t.getMessage());
    }
    public void onTodolistDeleted(int requestCode, HttpResponse httpResponse, int position) {
        if (requestCode == APICall.TODOLIST_DELETED) {
            todolistActivity.showMessage(httpResponse.getMessage());
            // notify adapter for deletion
            todoData.remove(position);
            notifyItemRemoved(position);
        }
    }
    public void onTodolistShared(int requestCode, HttpResponse httpResponse, int position) {
        if (requestCode == APICall.TODOLIST_SHARED) {
            todoData.get(position).setShared("1");
            notifyItemChanged(position);
            todolistActivity.showMessage(httpResponse.getMessage());
        }
    }
    public void onTodolistUpdated(int requestCode, HttpResponse httpResponse, int position) {
        if (requestCode == APICall.TODOLIST_UPDATED) {
            todolistActivity.showMessage(httpResponse.getMessage());
            notifyItemChanged(position);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return todoData.size();
    }
}
