package de.rememberly.rememberlyandroidapp.activities;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.rememberly.rememberlyandroidapp.R;
import de.rememberly.rememberlyandroidapp.adapter.NoticesOverviewAdapter;
import de.rememberly.rememberlyandroidapp.model.Notice;
import de.rememberly.rememberlyandroidapp.model.Token;
import de.rememberly.rememberlyandroidapp.remote.ApiUtils;
import de.rememberly.rememberlyandroidapp.service.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticesOverviewActivity extends AppCompatActivity {
    private RecyclerView listRecyclerView;
    private RecyclerView.Adapter noticesOverviewAdapter;
    private RecyclerView.LayoutManager listManager;
    private UserService userService;
    private ArrayList<Notice> noticeData = new ArrayList<Notice>();;
    private ImageButton addButton;
    private EditText listAddEdittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_overview);
        // set background animation:
        LinearLayout linearLayout = findViewById(R.id.AnimationRootLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();


        listRecyclerView = findViewById(R.id.listRecyclerView);
        listManager = new LinearLayoutManager(this);
        listAddEdittext = findViewById(R.id.newListItemInput);
        listAddEdittext.setHint(getResources().getString(R.string.inputNewNoticeName));
        listRecyclerView.setLayoutManager(listManager);
        addButton = findViewById(R.id.imageButton);
        userService = ApiUtils.getUserService();
        noticesOverviewAdapter = new NoticesOverviewAdapter(noticeData);
        listRecyclerView.setAdapter(noticesOverviewAdapter);
        initImagebutton();
        initNotices();
    }
    private void initImagebutton() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newNoticeText = listAddEdittext.getText().toString();
                if (!newNoticeText.isEmpty()) {
                    Notice newNotice = new Notice(newNoticeText);
                    Call<Notice> call = userService.newNotice("Bearer " + ApiUtils.getUserToken(NoticesOverviewActivity.this), newNotice);
                    call.enqueue(new Callback<Notice>() {
                        @Override
                        public void onResponse(Call<Notice> call, Response<Notice> response) {
                            if (response.isSuccessful()) {
                                Notice responseNotice = response.body();
                                noticeData.add(responseNotice);
                                noticesOverviewAdapter.notifyItemInserted(noticeData.size() - 1);
                                getAndStoreNewToken();

                            } else {
                                Log.e("Error: ", "Code " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<Notice> call, Throwable t) {
                            Toast.makeText(NoticesOverviewActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    private void initNotices() {
        String token = ApiUtils.getUserToken(this);
        Call<List<Notice>> call = userService.getNotices("Bearer " + token);
        call.enqueue(new Callback<List<Notice>>() {
            @Override
            public void onResponse(Call<List<Notice>> call, Response<List<Notice>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Notice> noticeArray = (ArrayList<Notice>) response.body();
                    for (Notice notice : noticeArray) {
                        noticeData.add(notice);
                        noticesOverviewAdapter.notifyItemInserted(noticeData.size() - 1);
                    }
                } else {
                    Log.e("Error: ", "No notices found");
                }
            }

            @Override
            public void onFailure(Call<List<Notice>> call, Throwable t) {

            }
        });
    }
    private void getAndStoreNewToken() {
        Call<Token> call = userService.newToken("Bearer " + ApiUtils.getUserToken(this));
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    Token newToken = (Token) response.body();
                    ApiUtils.storeUserToken(newToken.getToken(), NoticesOverviewActivity.this);
                } else {
                    Log.e("Errorcode: ",response.message());
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.e("Errormessage: ",t.getMessage());
            }
        });
    }
}
