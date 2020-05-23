/**
 * Created by Chatura Dilan Perera on 7/1/2017.
 */
package lk.mobilevisions.kiki.modules.notifications;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import androidx.core.app.NotificationManagerCompat;
import lk.mobilevisions.kiki.R;
import lk.mobilevisions.kiki.ui.auth.LoginActivity;
import lk.mobilevisions.kiki.ui.splash.SplashActivity;

public class NotificationReceiver extends FirebaseMessagingService {

    public static final int NOTIFICATION_ID = 1;
    String CHANNEL_ID = "my_channel_01";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)  {
        super.onMessageReceived(remoteMessage);

        remoteMessage.getData();
        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("body");
        String imageUrl = remoteMessage.getData().get("image_url");
        String type = remoteMessage.getData().get("type");
        String contentType = remoteMessage.getData().get("content_type");
        String contentId = remoteMessage.getData().get("content_id");
        String dateTime = remoteMessage.getData().get("date_time");

        sendNotification(title, message, imageUrl, type, contentType, contentId, dateTime);

    }

    private void sendNotification(String title, String body, String imageUrl, String type, String contentType, String contentId, String dateTime) {

        Intent intent = new Intent(this, SplashActivity.class);
//        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("type", type);
        intent.putExtra("body", body);
        intent.putExtra("image_url", imageUrl);
        intent.putExtra("content_id", contentId);
        intent.putExtra("content_type", contentType);
        intent.putExtra("date_time", dateTime);
        intent.setAction(Long.toString(System.currentTimeMillis()));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        if (imageUrl != null) {
            Bitmap bitmap = getBitmapfromUrl(imageUrl);
            notificationBuilder.setStyle(
                    new NotificationCompat.BigPictureStyle()
                            .bigPicture(bitmap)
                            .bigLargeIcon(null)
            ).setLargeIcon(bitmap);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, notificationBuilder.build());

    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            Log.e("awesome", "Error in getting notification image: " + e.getLocalizedMessage());
            return null;
        }
    }

}
