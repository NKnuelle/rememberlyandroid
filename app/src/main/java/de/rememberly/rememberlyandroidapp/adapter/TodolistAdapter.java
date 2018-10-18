package de.rememberly.rememberlyandroidapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import de.rememberly.rememberlyandroidapp.model.ReturnMessage;
import de.rememberly.rememberlyandroidapp.model.Todolist;
import de.rememberly.rememberlyandroidapp.remote.ApiUtils;
import de.rememberly.rememberlyandroidapp.service.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodolistAdapter extends RecyclerView.Adapter<TodolistAdapter.TodoViewHolder> {
    private ArrayList<Todolist> todoData;

    public static class TodoViewHolder extends RecyclerView.ViewHolder {

        public TextView todoView;
        public TextView todoViewOptions;
        private ImageView shareIcon;
        public TodoViewHolder(View view) {
            super(view);
            todoView = view.findViewById(R.id.textView);
            todoViewOptions = view.findViewById(R.id.textViewOptions);
            shareIcon = view.findViewById(R.id.shareIcon);
        }
    }
        public TodolistAdapter(ArrayList<Todolist> dataset) {
            todoData = dataset;
        }
        @Override
        public TodolistAdapter.TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listitem_view, parent, false);

            TodoViewHolder vh = new TodoViewHolder(v);
            return vh;
        }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final TodoViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        initTodoview(holder, position);
        initOptionsMenu(holder, position);
        initShareIcon(holder, position);
    }
    private void initTodoview(final TodoViewHolder holder, final int position) {
        holder.todoView.setText(todoData.get(position).getList_name());
        holder.todoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listID = todoData.get(position).getList_id();
                Intent intent = new Intent(holder.todoView.getContext(), TodoActivity.class);
                intent.putExtra("list_id", listID);
                holder.todoView.getContext().startActivity(intent);
            }
        });
    }
    private void initShareIcon(final TodoViewHolder holder, final int position) {
        if (!todoData.get(position).IsShared()) {
            holder.shareIcon.setVisibility(View.GONE);
        }
    }
    private void initOptionsMenu(final TodoViewHolder holder, final int position) {
        holder.todoViewOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = holder.todoViewOptions.getContext();

                final PopupMenu popupMenu = new PopupMenu(context, holder.todoViewOptions);
                popupMenu.inflate(R.menu.listoptions);
                // hide menu if list is already shared
                    if (todoData.get(position).IsShared()) {
                    popupMenu.getMenu().findItem(R.id.optionsshare).setVisible(false);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Todolist list = todoData.get(position);
                        switch (item.getItemId()) {
                            case R.id.optionsdelete:
                                showDeleteDialog(list, context, position);
                                break;
                            case R.id.optionsrename:
                                showRenameDialog(list, context, position);
                                break;
                            case R.id.optionsshare:
                                showShareDialog(list, context, position);
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
     @param context The context (activity) used for the dialog

     **/
    private void showShareDialog(final Todolist list, final Context context, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.shareMessage);
        // Create Edittext to enter username
        final EditText enterUsername = new EditText(context);
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
                        String token = "Bearer " + ApiUtils.getUserToken(context);
                        UserService userService = ApiUtils.getUserService();

                        if (enterUsername.getText().toString().isEmpty()) {
                            enterUsername.setError(context.getString(R.string.shareDialogUsernameRequired));
                        } else {
                            JsonObject sharedJSON = createShareJson(enterUsername.getText().toString(), list.getList_id());
                            Call<ReturnMessage> call = userService.shareTodolist(token, sharedJSON);
                            call.enqueue(new Callback<ReturnMessage>() {
                                @Override
                                public void onResponse(Call<ReturnMessage> call, Response<ReturnMessage> response) {
                                    if (response.isSuccessful()) {
                                        todoData.get(position).setShared("1");
                                        notifyItemChanged(position);
                                        ReturnMessage returnMessage = response.body();
                                        Toast.makeText(context, returnMessage.getMessage(), Toast.LENGTH_LONG).show();
                                    }

                                }
                                @Override
                                public void onFailure(Call<ReturnMessage> call, Throwable t) {
                                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
    }
    private void showRenameDialog(final Todolist list, final Context context, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.renameMessage);
        // Create Edittext to enter username
        final EditText enterNewName = new EditText(context);
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
                        String token = "Bearer " + ApiUtils.getUserToken(context);
                        UserService userService = ApiUtils.getUserService();

                        if (enterNewName.getText().toString().isEmpty()) {
                            enterNewName.setError(context.getString(R.string.renameListRequired));
                        } else {
                            list.setList_name(enterNewName.getText().toString());
                            Call<ReturnMessage> call = userService.updateTodolist(token, list);
                            call.enqueue(new Callback<ReturnMessage>() {
                                @Override
                                public void onResponse(Call<ReturnMessage> call, Response<ReturnMessage> response) {
                                    ReturnMessage returnMessage = response.body();
                                    Toast.makeText(context, returnMessage.getMessage(), Toast.LENGTH_LONG).show();
                                    notifyItemChanged(position);

                                }
                                @Override
                                public void onFailure(Call<ReturnMessage> call, Throwable t) {
                                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
    }
    private JsonObject createShareJson(String username, String list_id) {
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("username", username);
            jsonObject.addProperty("list_id", list_id);
        } catch (JsonIOException e) {
            Log.e("JSON Error", e.getMessage());
        }
        return jsonObject;
    }
    private void showDeleteDialog(final Todolist list, final Context context, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.deleteMessage);
        builder.setPositiveButton(R.string.deleteButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String token = "Bearer " + ApiUtils.getUserToken(context);
                UserService userService = ApiUtils.getUserService();

                Call<ReturnMessage> call = userService.deleteTodolist(token, list.getList_id());
                Log.i("List ID: ", list.getList_id());
                call.enqueue(new Callback<ReturnMessage>() {
                    @Override
                    public void onResponse(Call<ReturnMessage> call, Response<ReturnMessage> response) {
                        if (response.isSuccessful()) {
                            ReturnMessage returnMessage = response.body();
                            Log.i("Operation: ", returnMessage.getMessage());
                            // notify adapter for deletion
                            todoData.remove(position);
                            notifyItemRemoved(position);
                        } else {
                            Log.e("Operation failed: ", response.errorBody().toString());
                        }
                    }


                    @Override
                    public void onFailure(Call<ReturnMessage> call, Throwable t) {
                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return todoData.size();
    }
}
