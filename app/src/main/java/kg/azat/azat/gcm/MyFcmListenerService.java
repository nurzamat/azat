/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kg.azat.azat.gcm;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.json.JSONException;
import org.json.JSONObject;
import kg.azat.azat.AppController;
import kg.azat.azat.ChatRoomMessagesActivity;
import kg.azat.azat.ChatsActivity;
import kg.azat.azat.helpers.Constants;
import kg.azat.azat.model.Message;
import kg.azat.azat.model.User;

public class MyFcmListenerService extends FirebaseMessagingService {

    private static final String TAG = MyFcmListenerService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage message)
    {

        String from = message.getFrom();
        //Map data = message.getData();

        String title = message.getData().get("title");
        Boolean isBackground = Boolean.valueOf(message.getData().get("is_background"));
        String flag = message.getData().get("flag");
        String data = message.getData().get("data");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "title: " + title);
        Log.d(TAG, "isBackground: " + isBackground);
        Log.d(TAG, "flag: " + flag);
        Log.d(TAG, "data: " + data);

        if(message.getNotification()!=null){
            Log.d("FCM", "Notification Message Body: " + message.getNotification().getBody());
        }

        if (flag == null)
            return;

        if(AppController.getInstance().getUser() == null){
            // user is not logged in, skipping push notification
            Log.e(TAG, "user is not logged in, skipping push notification");
            return;
        }

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        switch (Integer.parseInt(flag)) {
            case Constants.PUSH_TYPE_CHATROOM:
                // push notification belongs to a chat room
                processChatRoomPush(title, isBackground, data);
                break;
            case Constants.PUSH_TYPE_USER:
                // push notification is specific to user
                processUserMessage(title, isBackground, data);
                break;
        }
    }

    /**
     * Processing chat room push message
     * this message will be broadcasts to all the activities registered
     * */
    private void processChatRoomPush(String title, boolean isBackground, String data) {
        if (!isBackground) {

            try {
                JSONObject datObj = new JSONObject(data);

                String chatRoomId = datObj.getString("chat_room_id");

                JSONObject mObj = datObj.getJSONObject("message");
                Message message = new Message();
                message.setMessage(mObj.getString("message"));
                message.setId(mObj.getString("message_id"));
                message.setCreatedAt(mObj.getString("created_at"));

                JSONObject uObj = datObj.getJSONObject("user");

                // skip the message if the message belongs to same user as
                // the user would be having the same message when he was sending
                // but it might differs in your scenario
                if (uObj.getString("user_id").equals(AppController.getInstance().getUser().getId())) {
                    Log.e(TAG, "Skipping the push message as it belongs to same user");
                    return;
                }

                User user = new User();
                user.setId(uObj.getString("user_id"));
                user.setEmail(uObj.getString("email"));
                user.setName(uObj.getString("name"));
                message.setUser(user);

                // verifying whether the app is in background or foreground
                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

                    // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
                    pushNotification.putExtra("type", Constants.PUSH_TYPE_CHATROOM);
                    pushNotification.putExtra("message", message);
                    pushNotification.putExtra("chat_room_id", chatRoomId);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    // play notification sound
                    NotificationUtils notificationUtils = new NotificationUtils();
                    notificationUtils.playNotificationSound();
                } else {

                    // app is in background. show the message in notification try
                    Intent resultIntent = new Intent(getApplicationContext(), ChatRoomMessagesActivity.class);
                    resultIntent.putExtra("chat_room_id", chatRoomId);
                    showNotificationMessage(getApplicationContext(), title, user.getName() + " : " + message.getMessage(), message.getCreatedAt(), resultIntent);
                }

            } catch (JSONException e) {
                Log.e(TAG, "json parsing error: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        } else {
            // the push notification is silent, may be other operations needed
            // like inserting it in to SQLite
        }
    }

    /**
     * Processing user specific push message
     * It will be displayed with / without image in push notification tray
     * */
    private void processUserMessage(String title, boolean isBackground, String data) {
        if (!isBackground) {

            try {
                JSONObject datObj = new JSONObject(data);

                String imageUrl = datObj.getString("image");

                JSONObject mObj = datObj.getJSONObject("message");
                Message message = new Message();
                message.setMessage(mObj.getString("message"));
                message.setId(mObj.getString("message_id"));
                message.setCreatedAt(mObj.getString("created_at"));

                JSONObject uObj = datObj.getJSONObject("user");
                User user = new User();
                user.setId(uObj.getString("user_id"));
                user.setEmail(uObj.getString("email"));
                user.setName(uObj.getString("name"));
                message.setUser(user);

                // verifying whether the app is in background or foreground
                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

                    // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
                    pushNotification.putExtra("type", Constants.PUSH_TYPE_USER);
                    pushNotification.putExtra("message", message);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    // play notification sound
                    NotificationUtils notificationUtils = new NotificationUtils();
                    notificationUtils.playNotificationSound();
                } else {

                    // app is in background. show the message in notification try
                    Intent resultIntent = new Intent(getApplicationContext(), ChatsActivity.class);

                    // check for push notification image attachment
                    if (TextUtils.isEmpty(imageUrl)) {
                        showNotificationMessage(getApplicationContext(), title, user.getName() + " : " + message.getMessage(), message.getCreatedAt(), resultIntent);
                    } else {
                        // push notification contains image
                        // show it with the image
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message.getMessage(), message.getCreatedAt(), resultIntent, imageUrl);
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, "json parsing error: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        } else {
            // the push notification is silent, may be other operations needed
            // like inserting it in to SQLite
        }
    }

    /**
     * Showing notification with text only
     * */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     * */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
