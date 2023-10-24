package mx.gob.puentesfronterizos.lineaexpres;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "Firebase";
    @Override
    public void onNewToken(@NonNull String token) {
        System.out.println("FireBaseMessasingServiceToken = " + token);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        String msgTitle = Objects.requireNonNull(message.getNotification()).getTitle();
        String msgBody = message.getNotification().getBody();
        show_Notification(msgTitle, msgBody);
        System.out.println(message);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void show_Notification(String msgTitle, String msgBody){

        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        String CHANNEL_ID="MYCHANNEL";
        NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"All",NotificationManager.IMPORTANCE_HIGH);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),1,intent,PendingIntent.FLAG_IMMUTABLE);
        Notification notification=new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentText(msgBody)
                .setContentTitle(msgTitle)
                .setContentIntent(pendingIntent)
                .setChannelId(CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),                                                                                                 R.drawable.ic_stat_name))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .build();

        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(1,notification);
    }
}
