package kg.azat.azat.helpers;

/**
 * Created by nurzamat on 5/22/15.
 */
public final class Constants {

    public static final String AZAT = "azat";
    public static final String USER = "user";
    // Number of columns of Grid View
    public static final int NUM_OF_COLUMNS = 3;

    // Gridview image padding
    public static final int GRID_PADDING = 4; // in dp

    public static final int MAX_IMAGES = 5;

    public static final String ADD_POST_MODE = "add_post";
    public static final String POSTS_MODE = "posts";
    public static final String HOME_MODE = "home";
    public static final String ORDER_ID = "order_id";

    //CHAT
    public static final String GCM_TOKEN = "gcm_token";

    // flag to identify whether to show single line
    // or multi line test push notification tray
    public static boolean appendNotificationMessages = true;

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // type of push messages
    public static final int PUSH_TYPE_CHATROOM = 1;
    public static final int PUSH_TYPE_USER = 2;

    // id to handle the notification in the notification try
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

}
