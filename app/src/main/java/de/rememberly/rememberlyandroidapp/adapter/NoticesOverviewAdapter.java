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
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import de.rememberly.rememberlyandroidapp.R;
import de.rememberly.rememberlyandroidapp.activities.NoticeActivity;
import de.rememberly.rememberlyandroidapp.model.Notice;
import de.rememberly.rememberlyandroidapp.model.ReturnMessage;
import de.rememberly.rememberlyandroidapp.remote.ApiUtils;
import de.rememberly.rememberlyandroidapp.service.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticesOverviewAdapter extends RecyclerView.Adapter<NoticesOverviewAdapter.NoticeViewHolder> {
    private ArrayList<Notice> noticeData;

    public static class NoticeViewHolder extends RecyclerView.ViewHolder {

        public TextView noticeView;
        public TextView noticeViewOptions;
        public NoticeViewHolder(View view) {
            super(view);
            noticeView = view.findViewById(R.id.textView);
            noticeViewOptions = view.findViewById(R.id.textViewOptions);
        }
    }
    public NoticesOverviewAdapter(ArrayList<Notice> dataset) {
        noticeData = dataset;
    }
    @Override
    public NoticeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todolist_text_view, parent, false);

        NoticeViewHolder vh = new NoticeViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final NoticeViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        initNoticeView(holder, position);
        initOptionsMenu(holder, position);

    }
    private void initNoticeView(final NoticeViewHolder holder, final int position) {
        holder.noticeView.setText(noticeData.get(position).getNoticeName());
        holder.noticeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noticeID = noticeData.get(position).getNoticeID();
                Intent intent = new Intent(holder.noticeView.getContext(), NoticeActivity.class);
                intent.putExtra("noticeID", noticeID);
                holder.noticeView.getContext().startActivity(intent);
            }
        });
    }
    private void initOptionsMenu(final NoticeViewHolder holder, final int position) {
        holder.noticeViewOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = holder.noticeViewOptions.getContext();
                final PopupMenu popupMenu = new PopupMenu(context, holder.noticeViewOptions);
                popupMenu.inflate(R.menu.listoptions);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Notice notice = noticeData.get(position);
                        switch (item.getItemId()) {
                            case R.id.optionsdelete:
                                showDeleteDialog(notice, context, position);
                                break;
                            case R.id.optionsrename:
                                showRenameDialog(notice, context, position);
                                break;
                            case R.id.optionsshare:
                                showShareDialog(notice, context);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }
    /** Shows a Dialog to share the Notice.
     The user has to enter a username to share the notice with.

     @param notice the Notice the user clicked on
     @param context The context (activity) used for the dialog

     **/
    private void showShareDialog(final Notice notice, final Context context) {
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
                            JsonObject sharedJSON = createShareJson(enterUsername.getText().toString(), notice.getNoticeID());
                            Call<ReturnMessage> call = userService.shareNotice(token, sharedJSON);
                            call.enqueue(new Callback<ReturnMessage>() {
                                @Override
                                public void onResponse(Call<ReturnMessage> call, Response<ReturnMessage> response) {
                                    ReturnMessage returnMessage = response.body();
                                    Toast.makeText(context, returnMessage.getMessage(), Toast.LENGTH_LONG).show();

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
    private void showRenameDialog(final Notice notice, final Context context, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.renameMessage);
        // Create Edittext to enter username
        final EditText enterNewName = new EditText(context);
        enterNewName.setHint(R.string.renameDialogHint);

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
                            enterNewName.setError(context.getString(R.string.shareDialogUsernameRequired));
                        } else {
                            notice.setNoticeName(enterNewName.getText().toString());
                            Call<ReturnMessage> call = userService.updateNotice(token, notice);
                            call.enqueue(new Callback<ReturnMessage>() {
                                @Override
                                public void onResponse(Call<ReturnMessage> call, Response<ReturnMessage> response) {
                                    ReturnMessage returnMessage = response.body();
                                    Toast.makeText(context, returnMessage.getMessage(), Toast.LENGTH_LONG).show();
                                    NoticesOverviewAdapter.this.notifyItemChanged(position);

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
    private JsonObject createShareJson(String username, String noticeID) {
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("username", username);
            jsonObject.addProperty("noticeID", noticeID);
        } catch (JsonIOException e) {
            Log.e("JSON Error", e.getMessage());
        }
        return jsonObject;
    }
    private void showDeleteDialog(final Notice notice, final Context context, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.deleteMessage);
        builder.setPositiveButton(R.string.deleteButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String token = "Bearer " + ApiUtils.getUserToken(context);
                UserService userService = ApiUtils.getUserService();

                Call<ReturnMessage> call = userService.deleteNotice(token, notice.getNoticeID());
                Log.i("List ID: ", notice.getNoticeID());
                call.enqueue(new Callback<ReturnMessage>() {
                    @Override
                    public void onResponse(Call<ReturnMessage> call, Response<ReturnMessage> response) {
                        if (response.isSuccessful()) {
                            ReturnMessage returnMessage = response.body();
                            Log.i("Operation: ", returnMessage.getMessage());
                            // notify adapter for deletion
                            noticeData.remove(position);
                            NoticesOverviewAdapter.this.notifyItemRemoved(position);
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
        return noticeData.size();
    }
}