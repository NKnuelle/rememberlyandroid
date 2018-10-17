package de.rememberly.rememberlyandroidapp.service;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class RememberlyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage message) {
        RemoteMessage.Notification notificationBody = message.getNotification();
        Log.i("Body Text: ", notificationBody.getBody());

    }
}
