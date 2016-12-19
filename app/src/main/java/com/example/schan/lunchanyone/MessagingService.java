package com.example.schan.lunchanyone;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by schan on 12/16/16.
 */

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "NotificationService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from DatabaseReference Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The DatabaseReference console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        if(remoteMessage.getData().size() > 0) {
            Log.d(TAG, "From: " + remoteMessage.getData().size());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        //Calling method to generate notification
        showNotification(this, remoteMessage);
//        sendNotification(this, R.mipmap.ic_launcher, remoteMessage.getNotification().getBody());
    }

    public void showNotification(Context context, RemoteMessage remoteMessage){
        new NotificationPicture(context).execute();
    }

    private class NotificationPicture extends AsyncTask<String, Void, Bitmap> {

        Context context;

        public NotificationPicture(Context context) {
            super();
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            try {
                URL url = new URL("http://www.pastificiodeicampi.it/images/blogs/linguina/lunch.jpg");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream in = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                Bitmap output;
                Rect srcRect;
                if (bitmap.getWidth() > bitmap.getHeight()) {
                    output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    srcRect = new Rect((bitmap.getWidth() - bitmap.getHeight()) / 2, 0, bitmap.getWidth() + (bitmap.getWidth() - bitmap.getHeight()) / 2, bitmap.getHeight());
                } else {
                    output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
                    srcRect = new Rect(0, (bitmap.getHeight() - bitmap.getWidth()) / 2, bitmap.getWidth(), bitmap.getHeight() + (bitmap.getHeight() - bitmap.getWidth()) / 2);
                }

                Canvas canvas = new Canvas(output);
                final int color = 0xff424242;
                final Paint paint = new Paint();
                final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

                float r;

                if (bitmap.getWidth() > bitmap.getHeight()) {
                    r = bitmap.getHeight() / 2;
                } else {
                    r = bitmap.getWidth() / 2;
                }

                paint.setAntiAlias(true);
                canvas.drawARGB(0, 0, 0, 0);
                paint.setColor(color);
                canvas.drawCircle(r, r, r, paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(bitmap, srcRect, rect, paint);
                return output;
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            super.onPostExecute(result);
            try {
                Intent intent = new Intent(MessagingService.this, GroupChatActivity.class);
                // use System.currentTimeMillis() to have a unique ID for the pending intent
                PendingIntent pIntent = PendingIntent.getActivity(MessagingService.this, (int) System.currentTimeMillis(), intent, 0);

                // build notification
                // the addAction re-use the same intent to keep the example short
                Notification n = new Notification.Builder(MessagingService.this)
                        .setContentTitle("Engineering")
                        .setContentText("Today's restaurant is set to: Sweet Basil")
                        .setSmallIcon(R.drawable.ic_lunch_anyone)
                        .setLargeIcon(result)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true).build();


                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                notificationManager.notify(0, n);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}