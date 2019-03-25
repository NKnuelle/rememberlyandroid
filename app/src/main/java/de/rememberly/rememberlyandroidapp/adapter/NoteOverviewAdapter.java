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

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import de.rememberly.rememberlyandroidapp.R;
import de.rememberly.rememberlyandroidapp.activities.NoteOverviewActivity;
import de.rememberly.rememberlyandroidapp.apputils.PreferencesManager;
import de.rememberly.rememberlyandroidapp.model.Note;
import de.rememberly.rememberlyandroidapp.model.HttpResponse;
import de.rememberly.rememberlyandroidapp.remote.APICall;
import de.rememberly.rememberlyandroidapp.activities.NoteActivity;


public class NoteOverviewAdapter extends RecyclerView.Adapter<NoteOverviewAdapter.noteViewHolder> {
    private ArrayList<Note> noteData;
    private NoteOverviewActivity noteOverviewActivity;
    private APICall apiCall;

    static class noteViewHolder extends RecyclerView.ViewHolder {

        TextView noteView;
        TextView noteViewOptions;
        private ImageView shareIcon;
        noteViewHolder(View view) {
            super(view);
            noteView = view.findViewById(R.id.textView);
            noteViewOptions = view.findViewById(R.id.textViewOptions);
            shareIcon = view.findViewById(R.id.shareIcon);
        }
    }
    public NoteOverviewAdapter(NoteOverviewActivity noteOverviewActivity, ArrayList<Note> dataset) {
        this.noteData = dataset;
        this.noteOverviewActivity = noteOverviewActivity;
        this.apiCall = new APICall(PreferencesManager.getURL(noteOverviewActivity));
    }
    @Override
    @NonNull
    public noteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_view, parent, false);

        return new noteViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final noteViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        initNoteView(holder, position);
        initOptionsMenu(holder, position);
        initShareIcon(holder, position);
    }
    private void initShareIcon(final noteViewHolder holder, final int position) {
        if (!noteData.get(position).isShared()) {
            holder.shareIcon.setVisibility(View.GONE);
        } else {
            holder.shareIcon.setVisibility(View.VISIBLE);
        }
    }
    private void initNoteView(final noteViewHolder holder, final int position) {
        holder.noteView.setText(noteData.get(position).getNoteName());
        holder.noteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteID = noteData.get(position).getNoteID();
                String noteContent = noteData.get(position).getNoteContent();
                String noteName = noteData.get(position).getNoteName();
                Intent intent = new Intent(holder.noteView.getContext(), NoteActivity.class);
                intent.putExtra("noteID", noteID);
                intent.putExtra("noteName", noteName);
                intent.putExtra("noteContent", noteContent);
                holder.noteView.getContext().startActivity(intent);
            }
        });
    }
    private void initOptionsMenu(final noteViewHolder holder, final int position) {
        holder.noteViewOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = holder.noteViewOptions.getContext();
                final PopupMenu popupMenu = new PopupMenu(context, holder.noteViewOptions);
                popupMenu.inflate(R.menu.listoptions);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Note note = noteData.get(position);
                        switch (item.getItemId()) {
                            case R.id.optionsdelete:
                                showDeleteDialog(note, position);
                                break;
                            case R.id.optionsrename:
                                showRenameDialog(note, position);
                                break;
                            case R.id.optionsshare:
                                showShareDialog(note, position);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }
    /** Shows a Dialog to share the Note.
     The user has to enter a username to share the note with.

     @param note the Note the user clicked on

     **/
    private void showShareDialog(final Note note, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(noteOverviewActivity);
        builder.setMessage(R.string.shareNoteMessage);
        // Create Edittext to enter username
        final EditText enterUsername = new EditText(noteOverviewActivity);
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
                        String token = PreferencesManager.getUserToken(noteOverviewActivity);

                        if (enterUsername.getText().toString().isEmpty()) {
                            enterUsername.setError(noteOverviewActivity.getString(R.string.shareDialogUsernameRequired));
                        } else {
                            JsonObject sharedJSON = createShareJson(enterUsername.getText().toString(), note.getNoteID());
                            APICall apiCall = new APICall(PreferencesManager.getURL(noteOverviewActivity));
                            apiCall.shareNote(NoteOverviewAdapter.this, token, sharedJSON, position);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
    }
    private void showRenameDialog(final Note note, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(noteOverviewActivity);
        builder.setMessage(R.string.renameNoteMessage);
        // Create Edittext to enter username
        final EditText enterNewName = new EditText(noteOverviewActivity);
        enterNewName.setHint(R.string.renameNoteHint);

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
                        String token = PreferencesManager.getUserToken(noteOverviewActivity);

                        if (enterNewName.getText().toString().isEmpty()) {
                            enterNewName.setError(noteOverviewActivity.getString(R.string.renameNoteRequired));
                        } else {
                            note.setNoteName(enterNewName.getText().toString());
                            APICall apiCall = new APICall(PreferencesManager.getURL(noteOverviewActivity));
                            apiCall.updateNote(NoteOverviewAdapter.this, token, note, position);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
    }
    private JsonObject createShareJson(String username, String noteID) {
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("username", username);
            jsonObject.addProperty("noteID", noteID);
        } catch (JsonIOException e) {
            Log.e("JSON Error", e.getMessage());
        }
        return jsonObject;
    }
    private void showDeleteDialog(final Note note, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(noteOverviewActivity);
        builder.setMessage(R.string.deleteMessage);
        builder.setPositiveButton(R.string.deleteButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String token = PreferencesManager.getUserToken(noteOverviewActivity);
                apiCall.deleteNote(NoteOverviewAdapter.this, token, note, position);
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
    public void onNoteShared(int requestCode, HttpResponse httpResponse, int position) {
        if (requestCode == APICall.NOTE_SHARED) {
            noteData.get(position).setIsShared("1");
            notifyItemChanged(position);
            noteOverviewActivity.showMessage(httpResponse.getMessage());
        }
    }
    public void onNoteUpdated(int requestCode, HttpResponse httpResponse, int position) {
        if (requestCode == APICall.NOTE_UPDATED) {
            noteOverviewActivity.showMessage(httpResponse.getMessage());
            NoteOverviewAdapter.this.notifyItemChanged(position);
        }
    }
    public void onNoteDeleted(int requestCode, HttpResponse httpResponse, int position) {
        if (requestCode == APICall.NOTE_DELETED) {
            noteOverviewActivity.showMessage(httpResponse.getMessage());
            // notify adapter for deletion
            noteData.remove(position);
            notifyItemRemoved(position);
        }
    }
    public void onError(int requestCode, HttpResponse httpResponse) {
        noteOverviewActivity.showMessage(httpResponse.getMessage());
    }
    public void onFailure(int requestCode, Throwable t) {
        noteOverviewActivity.showMessage(t.getMessage());
    }
    public void clear() {
        final int size = noteData.size();
        noteData.clear();
        notifyItemRangeRemoved(0, size);
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return noteData.size();
    }
}
