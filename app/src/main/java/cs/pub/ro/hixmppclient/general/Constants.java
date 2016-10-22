package cs.pub.ro.hixmppclient.general;

import cs.pub.ro.hixmppclient.R;

public class Constants {

    //common
    public static final String ACTION_LOG_IN = "cs.pub.ro.hixmppclient.action_log_in_";
    public static final String ACTION_REGISTER = "cs.pub.ro.hixmppclient.action_register_";
    public static final String ACTION_CONTACT_LIST = "cs.pub.ro.hixmppclient.action_contacts_list";
    public static final String ACTION_CONTACT_MESSAGE = "cs.pub.ro.hixmppclient.action_contact_message";
    public static final String ACTION_CONTACT_OTR_MESSAGE = "cs.pub.ro.hixmppclient.action_contact_otr_message";
    public static final String ACTION_CONTACT_PRESENCE_CHANGED = "cs.pub.ro.hixmppclient.action_contact_presence_changed";
    public static final String ACTION_CONTACT_CHAT_STATUS = "cs.pub.ro.hixmppclient.action_contact_chat_status";

    public static final String TAG_CLIENT_JID = "cs.pub.ro.hixmppclient.action_data.client_jid";
    public static final String TAG_CONTACT_JID = "cs.pub.ro.hixmppclient.action_data.contact_jid";
    public static final String TAG_BROADCAST_DATA = "cs.pub.ro.hixmppclient.action_data";
    public static final String TAG_CLIENT_INFO = "cs.pub.ro.hixmppclient.action_client_info";

    //mainActivity
    public static final String OPENFIRE = "Openfire";

    public static final String HOT_CHILLI = "Hot Chilli";
    public static final int OPENFIRE_ICON = R.drawable.openfire;

    public static final int HOT_CHILLI_ICON = R.drawable.hot_chilli;
    public static final int LOGIN_WAIT_BACKGROUND_ACTION = 0;

    public static final int REGISTER_WAIT_BACKGROUND_ACTION = 1;
    public static final long OPENFIRE_ID = 0;
    public static final long HOT_CHILLI_ID = 1;

    public static final String FRAGMENT_WELCOME_TAG = "WELCOME_FRAGMENT";
    public static final String FRAGMENT_LOG_IN_TAG = "LOG_IN_FRAGMENT";
    public static final String FRAGMENT_REGISTER_TAG = "REGISTER_FRAGMENT";
    public static final String FRAGMENT_WAIT_TAG = "REGISTER_FRAGMENT";
    public static final String FRAGMENT_CHAT_TAG = "CHAT_FRAGMENT";

    public static final String SAVE_STATE_CONTACTS_FRAGMENT_DATA = "contactsFragmentData";
    public static final int MESSAGE_MAX_ID = 1000;

    public static final String LOG_TAG = "hixmpp";

    //smack
    public static final String OPENFIRE_SERVER_NAME = "openfire";
    public static final String HOT_CHILLI_SERVER_NAME = "hotchilli";

    public static final int NOTIFICATION_IDENTIFIER = 1337;

    public static final String DEFAULT_HOST_ADDRESS = "192.168.1.100";
    public static final String DEFAULT_PORT = "5222";
    public static final String DEFAULT_SERVER_NAME = "chester-pc";

    public static final String DEFAULT_PHONE_NUMBER_TYPE = "VOICE";

    public static final String DEFAULT_USERNAME = "alex.budau";
    public static final String DEFAULT_PASSWORD = "casablanca";

    public static final String DEFAULT_XMPP_SERVICE_NAME = "chester-pc";
    public static final String DEFAULT_RESOURCE = "Android";

    public static String NO_SUPPORT_FOR_ACCOUNT_CREATION_EXCEPTION = "NO_SUPPORT_FOR_ACCOUNT_CREATION";
    public static final String EXISTING_USERNAME_EXCEPTION = "EXISTING_USERNAME";

    public static final String LOG_TAG_SMACK = "hixmpp_smack";

    public static final int DEFAULT_PACKAGE_REPLY_TIMEOUT = 30000;

    public static final int NOTIFICATION_PENDING_INTENT_REQUEST_CODE = 0;
    public static final int MAX_NOTIFICATION_MESSAGE_LENGTH = 10;

    public static final String TAG_CLIENT_JID_EXTRA = "cs.pub.ro.hixmppclient.extra_data.client_jid";
    public static final String TAG_USER_JID_EXTRA = "cs.pub.ro.hixmppclient.extra_data.user_jid";

    public static final int VIBRATOR_NOTIFICATION_DURATION = 500;
    public static final String CHAT_STATUS_MESSAGE = " is typing.";

    public static final int AVAILABLE_ICON = R.drawable.online_icon;
    public static final int AWAY_ICON = R.drawable.away_icon;
    public static final int DND_ICON = R.drawable.dnd_icon;
    public static final int CHAT_ICON = R.drawable.chat_icon;

    public static final int MENU_ITEM_OTR_ID = 0;
    public static final String MESSAGE_SUBJECT_NORMAL = "normal";
    public static final String MESSAGE_SUBJECT_OTR_DH_REQUEST = "otrRequest";
    public static final String MESSAGE_SUBJECT_OTR_DH_RESPONSE = "otrResponse";
    public static final String MESSAGE_SUBJECT_OTR_DH_ACK = "otrAck";
    public static final String MESSAGE_SUBJECT_DH_KEY_ACK = "dhKeyAck";
    public static final int KEY_LENGTH = 512;

    public static final String TOAST_START_OTR_SESSION = "Please wait for the OTR session to be established.";
    public static final String TOAST_OTR_SESSION_SUCCESS = "OTR session established successfully.";
    public static final String TOAST_OTR_SESSION_FAILURE = "Failed to establish OTR session.";
    public static final String TOAST_CHECK_INTERNET_CONNECTION = "Check your internet connection first.";
    public static final int OTR_SESSION_MIN_WAIT_INTERVAL = 5000;

    public static final String VCARD_FIELD_KEY = "key";
    public static final int MAX_RSA_LENGTH = 32;
}
