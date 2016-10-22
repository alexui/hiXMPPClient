package cs.pub.ro.hixmppclient.mainActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jivesoftware.smackx.chatstates.ChatState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

import cs.pub.ro.hixmppclient.R;
import cs.pub.ro.hixmppclient.common.XmppUserInfo;
import cs.pub.ro.hixmppclient.general.Constants;
import cs.pub.ro.hixmppclient.general.Utils;
import cs.pub.ro.hixmppclient.mainActivity.hiClient.LeftMenuFragment;
import cs.pub.ro.hixmppclient.mainActivity.hiClient.chat.XmppChatFragment;
import cs.pub.ro.hixmppclient.mainActivity.hiClient.chat.XmppChatMessage;
import cs.pub.ro.hixmppclient.mainActivity.hiClient.contactsFragment.ContactsFragment;
import cs.pub.ro.hixmppclient.mainActivity.hiClient.messagesFragment.MessagesFragment;
import cs.pub.ro.hixmppclient.mainActivity.hiClient.statusFragment.StatusFragment;
import cs.pub.ro.hixmppclient.mainActivity.welcome.WelcomeFragment;
import cs.pub.ro.hixmppclient.otr.OTRMessageBody;
import cs.pub.ro.hixmppclient.otr.OTRSession;
import cs.pub.ro.hixmppclient.smack.xmppService.XmppStartedService;

public class MainActivity extends AppCompatActivity implements LeftMenuFragment.LeftFragmentMenuListener {

    private XmppServiceConnection serviceConnection;
    private XmppStartedService startedService;

    private boolean welcomeLayoutCreated;
    private boolean hiLayoutCreated;

    private RelativeLayout mainLayout;
    private View welcomeLayout;
    private View hiLayout;
    private LayoutInflater layoutInflater;

    private WelcomeFragment welcomeFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private Fragment hiFragment;
    private LeftMenuFragment leftMenuFragment;
    private Toolbar toolbar;

    private MainActivityBroadcastReceiver mainActivityBroadcastReceiver;
    private IntentFilter intentFilter;

    private ContactsFragment contactsFragment;
    private MessagesFragment messagesFragment;
    private StatusFragment statusFragment;

    private XmppChatFragment currentXmppChatFragment;

    private Notification contactMessageNotification;
    private NotificationManager notificationManager;
    private Intent notificationIntent;
    private PendingIntent notificationPendingIntent;
    private Vibrator vibrator;

    private HashMap<String, XmppUserInfo> clientsList;
    private HashMap<String, HashMap<String, XmppUserInfo>> clientContactList;
    private HashMap<String, ArrayList<XmppUserInfo>> clientMessagesList;
    private HashMap<String, HashMap<String, OTRSession>> clientContactsOTRSessions;

    private MenuItem otrEnableMenuItem;
    private MenuItem otrDisableMenuItem;
    private OtrSessionStartRunnable otrSessionStartRunnable;
    private OtrSessionEstablishedRunnable otrSessionEstablishedRunnable;
    private Handler mainHandler;

    private Gson gson;

    public void setCurrentXmppChatFragment(XmppChatFragment currentXmppChatFragment) {

        OTRSession otrSession = clientContactsOTRSessions.get(currentXmppChatFragment.getClientInfo().getJid())
                .get(currentXmppChatFragment.getUserInfo().getJid());

        if (otrSession != null && otrSession.isAlive()) {
            Toast.makeText(this, Constants.TOAST_OTR_SESSION_SUCCESS, Toast.LENGTH_SHORT).show();
            otrEnableMenuItem.setVisible(false);
            otrDisableMenuItem.setVisible(true);
        } else {
            otrEnableMenuItem.setVisible(true);
            otrDisableMenuItem.setVisible(false);
        }

        this.currentXmppChatFragment = currentXmppChatFragment;
    }

    public void destroyCurrentXmppChatFragment() {
        this.currentXmppChatFragment = null;
    }

    public XmppStartedService getStartedService() {
        return startedService;
    }

    public MenuItem getOtrEnableMenuItem() {
        return otrEnableMenuItem;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, XmppStartedService.class);
        serviceConnection = new XmppServiceConnection();
        if (!Utils.isServiceRunning(this, XmppStartedService.class)) {
            startService(intent);
            Log.d(Constants.LOG_TAG, "MainActivity: Starting Service.");
        } else
            Log.d(Constants.LOG_TAG, "MainActivity: Service already started.");
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        mainActivityBroadcastReceiver = new MainActivityBroadcastReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_CONTACT_LIST);
        intentFilter.addAction(Constants.ACTION_CONTACT_PRESENCE_CHANGED);
        intentFilter.addAction(Constants.ACTION_CONTACT_MESSAGE);
        intentFilter.addAction(Constants.ACTION_CONTACT_CHAT_STATUS);
        intentFilter.addAction(Constants.ACTION_CONTACT_OTR_MESSAGE);

        welcomeFragment = new WelcomeFragment();

        contactsFragment = new ContactsFragment();
        messagesFragment = new MessagesFragment();
        statusFragment = new StatusFragment();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        clientsList = new HashMap<>();
        clientContactList = new HashMap<>();
        clientMessagesList = new HashMap<>();
        clientContactsOTRSessions = new HashMap<>();

        fragmentManager = getSupportFragmentManager();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mainHandler = new Handler();
        gson = new Gson();

        if (savedInstanceState != null)
            Log.d(Constants.LOG_TAG, "saved Instance State.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Constants.LOG_TAG, "MainActivity onResume() method invoked.");
        registerReceiver(mainActivityBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(Constants.LOG_TAG, "MainActivity onPause() method invoked.");
        unregisterReceiver(mainActivityBroadcastReceiver);
    }

    public void createWelcomeLayout() {

        if (welcomeLayoutCreated) {
            hiLayout.setVisibility(View.GONE);
            welcomeLayout.setVisibility(View.VISIBLE);
        } else {
            welcomeLayout = layoutInflater.inflate(R.layout.activity_welcome_layout, (ViewGroup) findViewById(R.id.welcomeLayout), false);
            mainLayout.addView(welcomeLayout);

            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.mainFrame, welcomeFragment, Constants.FRAGMENT_WELCOME_TAG);
            fragmentTransaction.commit();
            welcomeLayoutCreated = true;
        }
    }

    public void createHiLayout() {

        if (hiLayoutCreated) {
            welcomeLayout.setVisibility(View.GONE);
            hiLayout.setVisibility(View.VISIBLE);
        } else {
            hiLayout = layoutInflater.inflate(R.layout.activity_hi_layout, (ViewGroup) findViewById(R.id.welcomeLayout), false);
            mainLayout.addView(hiLayout);

            toolbar = (Toolbar) findViewById(R.id.hi_layout_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            leftMenuFragment = (LeftMenuFragment) fragmentManager.findFragmentById(R.id.left_menu_fragment);
            leftMenuFragment.setLeftFragmentMenuListener(this);
            leftMenuFragment.setUpFragmentMenu(toolbar);
            hiLayoutCreated = true;
        }

        itemOptionFragmentDisplay(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);

        otrEnableMenuItem = menu.findItem(R.id.app_otr_enable);
        otrEnableMenuItem.setVisible(false);
        otrDisableMenuItem = menu.findItem(R.id.app_otr_disable);
        otrDisableMenuItem.setVisible(false);
        return true;
    }

    @Override
    public void onBackPressed() {

        if (!hiLayoutCreated) {
            Log.d(Constants.LOG_TAG, "Back Button pressed.");
            int count = fragmentManager.getBackStackEntryCount();

            if (count == 0) {
                super.onBackPressed();
            } else {
                fragmentManager.popBackStack();
            }
        } else {
            if (currentXmppChatFragment != null) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(currentXmppChatFragment);
                fragmentTransaction.commit();
            }
            super.onBackPressed();
        }
    }

    @Override
    public void onMenuItemSelected(View view, int position) {
        itemOptionFragmentDisplay(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        XmppUserInfo xmppClientInfo = currentXmppChatFragment.getClientInfo();
        XmppUserInfo xmppUserInfo = currentXmppChatFragment.getUserInfo();
        OTRSession otrSession = new OTRSession(xmppClientInfo, xmppUserInfo);
        clientContactsOTRSessions.get(xmppClientInfo.getJid()).put(xmppUserInfo.getJid(), otrSession);

        if (id == R.id.app_otr_enable) {
            if (xmppUserInfo.isAvailable()) {
                Toast.makeText(this, Constants.TOAST_START_OTR_SESSION, Toast.LENGTH_SHORT).show();
                currentXmppChatFragment.disableSendingMessages();
                currentXmppChatFragment.sendOTRSessionRequest(otrSession);
                otrSessionStartRunnable = new OtrSessionStartRunnable(xmppClientInfo, xmppUserInfo);
                new Thread(otrSessionStartRunnable).start();
            }
        }

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String clientJid;
        String userJid;
        XmppUserInfo currentClientInfo;
        XmppUserInfo currentUserInfo;
        XmppChatFragment xmppChatFragment;

        clientJid = intent.getStringExtra(Constants.TAG_CLIENT_JID_EXTRA);
        userJid = intent.getStringExtra(Constants.TAG_USER_JID_EXTRA);

        if (clientJid != null && userJid != null) {
            currentClientInfo = clientsList.get(clientJid);
            currentUserInfo = clientContactList.get(clientJid).get(userJid);

            currentUserInfo.setUnreadMessages(0);
            notifyFragmentsDataChanged(clientJid);

            xmppChatFragment = new XmppChatFragment();
            xmppChatFragment.initChatFragment(currentClientInfo, currentUserInfo);

            fragmentTransaction = fragmentManager.beginTransaction();
            if (currentXmppChatFragment != null) {
                fragmentTransaction.remove(currentXmppChatFragment);
                fragmentTransaction.add(R.id.hi_body, xmppChatFragment, Constants.FRAGMENT_CHAT_TAG);
            } else {
                if (hiFragment != null) {
                    fragmentTransaction.remove(hiFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.add(R.id.hi_body, xmppChatFragment, Constants.FRAGMENT_CHAT_TAG);
                }
            }
            fragmentTransaction.commit();
        }
        Log.d(Constants.LOG_TAG, "on New intent: ");

    }

    private void processIntentActionContactMessage(String clientJid, XmppChatMessage xmppChatMessage) {

        String messageBody;
        String userJid;
        OTRSession otrSession;
        byte[] receivedPublicKey = new byte[0];

        userJid = xmppChatMessage.getSourceJID().replace("[@].*", "");
        messageBody = xmppChatMessage.getBody();
        otrSession = clientContactsOTRSessions.get(clientJid).get(userJid);

        if (otrSession != null) {
            receivedPublicKey = xmppChatMessage.decryptMessageBody(otrSession);
            Log.d(Constants.LOG_TAG, "received encrypted message");
        }

        if (currentXmppChatFragment == null || !currentXmppChatFragment.checkChatParticipants(xmppChatMessage)) {

            updateClientContactsMapUnreadMessages(clientJid, xmppChatMessage);
            notifyFragmentsDataChanged(clientJid);

            notificationIntent = new Intent(MainActivity.this, MainActivity.class);
            notificationIntent.putExtra(Constants.TAG_CLIENT_JID_EXTRA, clientJid);
            notificationIntent.putExtra(Constants.TAG_USER_JID_EXTRA, userJid);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            notificationPendingIntent = PendingIntent.getActivity(
                    MainActivity.this,
                    Constants.NOTIFICATION_PENDING_INTENT_REQUEST_CODE,
                    notificationIntent,
                    0);

            String contentText = (messageBody.length() > 10) ? ((messageBody.substring(0, Constants.MAX_NOTIFICATION_MESSAGE_LENGTH)) + "...") : messageBody;
            contactMessageNotification = new Notification.Builder(MainActivity.this)
                    .setContentTitle("New message from " + userJid)
                    .setContentText(contentText)
                    .setContentIntent(notificationPendingIntent)
                    .setLargeIcon(getContactPhoto(clientJid, userJid))
                    .setSmallIcon(R.drawable.hand)
                    .build();

            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            contactMessageNotification.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(xmppChatMessage.getId(), contactMessageNotification);
            vibrator.vibrate(Constants.VIBRATOR_NOTIFICATION_DURATION);

            Log.d(Constants.LOG_TAG, "notification Sent with message: " + contentText);

        } else {
            if (currentXmppChatFragment.checkChatParticipants(xmppChatMessage)) {
                updateClientContactsMapReadMessages(clientJid, xmppChatMessage);
                currentXmppChatFragment.updateChatStatus(ChatState.active.name());
                currentXmppChatFragment.updateChatFragment();
            } else {
                updateClientContactsMapUnreadMessages(clientJid, xmppChatMessage);
                notifyFragmentsDataChanged(clientJid);
            }
        }

        if (otrSession != null && receivedPublicKey.length != 0) {
            byte[] dhPublicKey = otrSession.createClientEncodedDHPublicKey(receivedPublicKey);
            Date currentDate = new Date();
            OTRMessageBody otrMessageBody = new OTRMessageBody(dhPublicKey, otrSession);
            otrSession.initDHChannel();

            xmppChatMessage = new XmppChatMessage(
                    Constants.MESSAGE_SUBJECT_DH_KEY_ACK,
                    gson.toJson(otrMessageBody),
                    clientJid, userJid, true, currentDate);

            startedService.sendMessage(clientJid, userJid, xmppChatMessage);

            Log.d(Constants.LOG_TAG, "OTR DH Key Updated " + receivedPublicKey);
            Log.d(Constants.LOG_TAG, "OTR Sent Ack message dhPublicKey: " + otrMessageBody.getContent());
        }
    }

    private void processIntentActionContactOtrMessage(String clientJid, XmppChatMessage xmppOtrMessage) {

        String subject;
        String userJid;
        String messageBody;
        OTRSession otrSession;
        XmppUserInfo xmppClientInfo;
        XmppUserInfo xmppUserInfo;
        Date currentDate;

        subject = xmppOtrMessage.getSubject();
        userJid = xmppOtrMessage.getSourceJID().replace("[@].*", "");
        messageBody = xmppOtrMessage.getBody();
        otrSession = clientContactsOTRSessions.get(clientJid).get(userJid);
        xmppClientInfo = clientsList.get(clientJid);
        xmppUserInfo = clientContactList.get(clientJid).get(userJid);

        OTRMessageBody otrMessageBody = gson.fromJson(messageBody, OTRMessageBody.class);
        Log.d(Constants.LOG_TAG, "OTR Received dhPublicKey: " + otrMessageBody.getContent() + " --- signed: " + otrMessageBody.getKey());


        if (Constants.MESSAGE_SUBJECT_OTR_DH_REQUEST.equals(subject)) {

            byte[] receivedPublicKey = otrMessageBody.getContent();

            currentDate = new Date();
            otrSession = new OTRSession(xmppClientInfo, xmppUserInfo);
            clientContactsOTRSessions.get(xmppClientInfo.getJid()).put(xmppUserInfo.getJid(), otrSession);

            byte[] dhPublicKey = otrSession.createClientEncodedDHPublicKey(receivedPublicKey);
            otrSession.initDHChannel();
            otrMessageBody = new OTRMessageBody(dhPublicKey, xmppClientInfo.getPrivateKeyRSA());

            XmppChatMessage xmppChatMessage = new XmppChatMessage(Constants.MESSAGE_SUBJECT_OTR_DH_RESPONSE, gson.toJson(otrMessageBody),
                    clientJid, userJid, true, currentDate);

            startedService.sendMessage(clientJid, userJid, xmppChatMessage);

            if (currentXmppChatFragment != null)
                Toast.makeText(this, Constants.TOAST_OTR_SESSION_SUCCESS, Toast.LENGTH_SHORT).show();

            Log.d(Constants.LOG_TAG, "OTR Sent Response dhPublicKey: " + dhPublicKey);
        }

        if (Constants.MESSAGE_SUBJECT_OTR_DH_RESPONSE.equals(subject)) {
            byte[] receivedPublicKey = otrMessageBody.getContent();

            currentDate = new Date();
            otrSession.initDHChannel(receivedPublicKey);

            otrMessageBody = new OTRMessageBody(receivedPublicKey, xmppClientInfo.getPrivateKeyRSA());

            XmppChatMessage xmppChatMessage = new XmppChatMessage(Constants.MESSAGE_SUBJECT_OTR_DH_ACK, gson.toJson(otrMessageBody),
                    clientJid, userJid, true, currentDate);

            startedService.sendMessage(clientJid, userJid, xmppChatMessage);
            otrSession.setAlive(true);
            Log.d(Constants.LOG_TAG, "OTR Sent Ack dhPublicKey: " + receivedPublicKey);
        }

        if (Constants.MESSAGE_SUBJECT_OTR_DH_ACK.equals(subject)) {
            byte[] receivedPublicKey = otrMessageBody.getContent();

            otrSession.setAlive(true);
            Log.d(Constants.LOG_TAG, "OTR Ok dhPublicKey: " + receivedPublicKey);
        }

        if (Constants.MESSAGE_SUBJECT_DH_KEY_ACK.equals(subject)) {
            if (otrMessageBody.checkMessageAuthenticationCode(otrSession)) {
                byte[] receivedPublicKey = otrMessageBody.getContent();
                otrSession.initDHChannel(receivedPublicKey);
                Log.d(Constants.LOG_TAG, "OTR DH Key Updated " + receivedPublicKey);
            }
        }
    }

    public OTRSession getOTRSession(String clientJid, String userJid) {
        return clientContactsOTRSessions.get(clientJid).get(userJid);
    }

    private Bitmap getContactPhoto(String clientJid, String userJid) {
        Bitmap photo;
        XmppUserInfo xmppUserInfo = clientContactList.get(clientJid).get(userJid);
        byte[] xmppUserInfoAvatar = xmppUserInfo.getAvatar();
        if (xmppUserInfoAvatar != null) {
            photo = BitmapFactory.decodeByteArray(xmppUserInfoAvatar, 0, xmppUserInfoAvatar.length);
        } else {
            BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.user_icon);
            photo = drawable.getBitmap();
        }

        return photo;
    }

    public void updateClientContactsMapUnreadMessages(String clientJid, XmppChatMessage xmppChatMessage) {
        XmppUserInfo xmppUserInfo = clientContactList.get(clientJid).get(xmppChatMessage.getSourceJID());
        ArrayList<XmppUserInfo> userMessagesList = clientMessagesList.get(clientJid);
        xmppUserInfo.addChatUnreadMessage(xmppChatMessage);
        if (!userMessagesList.contains(xmppUserInfo)) {
            userMessagesList.add(xmppUserInfo);
        }
    }

    public void updateClientContactsMapReadMessages(String clientJid, XmppChatMessage xmppChatMessage) {
        XmppUserInfo xmppUserInfo = clientContactList.get(clientJid).get(xmppChatMessage.getSourceJID());
        ArrayList<XmppUserInfo> userMessagesList = clientMessagesList.get(clientJid);
        xmppUserInfo.addChatMessage(xmppChatMessage);
        if (!userMessagesList.contains(xmppUserInfo)) {
            userMessagesList.add(xmppUserInfo);
        }
    }

    public void updateClientContactsMapSentMessages(String clientJid, XmppChatMessage xmppChatMessage) {
        XmppUserInfo xmppUserInfo = clientContactList.get(clientJid).get(xmppChatMessage.getDestinationJID());
        ArrayList<XmppUserInfo> userMessagesList = clientMessagesList.get(clientJid);
        xmppUserInfo.addChatMessage(xmppChatMessage);
        if (!userMessagesList.contains(xmppUserInfo)) {
            userMessagesList.add(xmppUserInfo);
        }
    }

    public void notifyFragmentsDataChanged(String clientJid) {
        contactsFragment.updateFragmentInTabLayout(clientJid);
        messagesFragment.updateFragmentInTabLayout(clientJid);
    }

    public void disconnectClient(String clientJid) {
        startedService.logOut(clientJid);

        clientsList.remove(clientJid);
        clientContactList.remove(clientJid);
        clientMessagesList.remove(clientJid);

        if (startedService.getAuthenticatedClients().isEmpty()) {
            createWelcomeLayout();
        }

        contactsFragment.removeFragmentFromTabLayout(clientJid);
        messagesFragment.removeFragmentFromTabLayout(clientJid);
        statusFragment.removeFragmentFromTabLayout(clientJid);
    }

    private void updateClientContactsMapUserPresence(String clientJid, XmppUserInfo userInfo) {
        XmppUserInfo xmppUserInfo = clientContactList.get(clientJid).get(userInfo.getJid());
        xmppUserInfo.updatePresence(userInfo);
    }

    private void itemOptionFragmentDisplay(int position) {
        hiFragment = null;
        String title = null;
        switch (position) {
            case 0:
                hiFragment = messagesFragment;
                title = getString(R.string.title_messages);
                break;
            case 1:
                hiFragment = contactsFragment;
                title = getString(R.string.title_contacts);
                break;
            case 2:
                hiFragment = statusFragment;
                title = getString(R.string.title_status);
                break;
            default:
                break;
        }

        if (hiFragment != null) {
            fragmentTransaction = fragmentManager.beginTransaction();
            int count = fragmentManager.getBackStackEntryCount();
            while (count > 0) {
                fragmentManager.popBackStack();
                count--;
            }
            fragmentTransaction.replace(R.id.hi_body, hiFragment);
            fragmentTransaction.commit();

            getSupportActionBar().setTitle(title);
        }
    }

    private class XmppServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            startedService = ((XmppStartedService.LocalBinder) service).getService();

            if (startedService.getAuthenticatedClients().isEmpty()) {
                createWelcomeLayout();
            } else {
                createHiLayout();
            }

            Log.d(Constants.LOG_TAG, "MainActivity: started service Instance is here. Service running:" + startedService.running);
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            startedService = null;
        }

    }

    private class MainActivityBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            String clientJid;

            if (action.equals(Constants.ACTION_CONTACT_LIST)) {
                XmppUserInfo clientUserInfo;
                ArrayList<XmppUserInfo> userInfoList;
                ArrayList<XmppUserInfo> userMessagesList = new ArrayList<>();
                TreeMap<String, ArrayList<XmppUserInfo>> contactsGroupMap = new TreeMap<>(new StringIgnoreCaseComparator());
                HashMap<String, XmppUserInfo> userInfoHashMap = new HashMap<>();

                clientJid = intent.getStringExtra(Constants.TAG_CLIENT_JID);
                clientUserInfo = intent.getParcelableExtra(Constants.TAG_CLIENT_INFO);
                userInfoList = intent.getParcelableArrayListExtra(Constants.TAG_BROADCAST_DATA);

                for (XmppUserInfo userInfo : userInfoList) {
                    userInfoHashMap.put(userInfo.getJid(), userInfo);

                    ArrayList<String> groupList = userInfo.getGroups();
                    for (String groupName : groupList) {
                        ArrayList<XmppUserInfo> usersGroupList = contactsGroupMap.get(groupName);
                        if (usersGroupList == null) {
                            usersGroupList = new ArrayList<>();
                            usersGroupList.add(userInfo);
                            contactsGroupMap.put(groupName, usersGroupList);
                        } else
                            usersGroupList.add(userInfo);
                    }
                }

                for (XmppUserInfo xmppUserInfo : userInfoList) {
                    if (!xmppUserInfo.getMessages().isEmpty())
                        userMessagesList.add(xmppUserInfo);
                }

                clientsList.put(clientJid, clientUserInfo);
                clientContactList.put(clientJid, userInfoHashMap);
                clientMessagesList.put(clientJid, userMessagesList);
                clientContactsOTRSessions.put(clientJid, new HashMap<String, OTRSession>());
                contactsFragment.addFragmentToTabLayout(clientUserInfo, contactsGroupMap);
                messagesFragment.addFragmentToTabLayout(clientUserInfo, userMessagesList);
                statusFragment.addFragmentToTabLayout(clientJid, clientUserInfo);

                startedService.setUpChatListener(clientJid);
            }

            if (action.equals(Constants.ACTION_CONTACT_PRESENCE_CHANGED)) {
                XmppUserInfo userInfo;

                clientJid = intent.getStringExtra(Constants.TAG_CLIENT_JID);
                userInfo = intent.getParcelableExtra(Constants.TAG_BROADCAST_DATA);

                if (startedService.getAuthenticatedClients().contains(clientJid)) {
                    updateClientContactsMapUserPresence(clientJid, userInfo);
                    notifyFragmentsDataChanged(clientJid);
                    if (currentXmppChatFragment != null && currentXmppChatFragment.checkChatParticipants(clientJid, userInfo.getJid())) {
                        currentXmppChatFragment.updateChatFragmentUserAvailable();
                    }
                }
            }

            if (action.equals(Constants.ACTION_CONTACT_MESSAGE)) {
                XmppChatMessage chatMessage;

                clientJid = intent.getStringExtra(Constants.TAG_CLIENT_JID);
                chatMessage = intent.getParcelableExtra(Constants.TAG_BROADCAST_DATA);

                processIntentActionContactMessage(clientJid, chatMessage);
            }

            if (action.equals(Constants.ACTION_CONTACT_OTR_MESSAGE)) {
                XmppChatMessage otrMessage;

                clientJid = intent.getStringExtra(Constants.TAG_CLIENT_JID);
                otrMessage = intent.getParcelableExtra(Constants.TAG_BROADCAST_DATA);

                processIntentActionContactOtrMessage(clientJid, otrMessage);
            }

            if (action.equals(Constants.ACTION_CONTACT_CHAT_STATUS)) {
                String contactJid;
                String chatStatus;
                clientJid = intent.getStringExtra(Constants.TAG_CLIENT_JID);
                contactJid = intent.getStringExtra(Constants.TAG_CONTACT_JID);
                chatStatus = intent.getStringExtra(Constants.TAG_BROADCAST_DATA);

                if (currentXmppChatFragment != null && currentXmppChatFragment.checkChatParticipants(clientJid, contactJid)) {
                    currentXmppChatFragment.updateChatStatus(chatStatus);
                }
            }
        }

        private class StringIgnoreCaseComparator implements Comparator<String> {

            public int compare(String s1, String s2) {
                return s1.toLowerCase().compareTo(s2.toLowerCase());
            }
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        Log.d(Constants.LOG_TAG, "MainActivity onDestroy() method invoked.");
        super.onDestroy();
    }

    private class OtrSessionStartRunnable implements Runnable {

        public OtrSessionStartRunnable(XmppUserInfo xmppClientInfo, XmppUserInfo xmppUserInfo) {
            otrSessionEstablishedRunnable = new OtrSessionEstablishedRunnable(xmppClientInfo, xmppUserInfo);
        }

        @Override
        public void run() {
            try {
                Thread.sleep(Constants.OTR_SESSION_MIN_WAIT_INTERVAL);
            } catch (InterruptedException e) {
                Log.d(Constants.LOG_TAG, e.getMessage());
                Log.e(Constants.LOG_TAG, e.toString());
            }

            Log.d(Constants.LOG_TAG, "After sleep");
            mainHandler.post(otrSessionEstablishedRunnable);
        }
    }

    private class OtrSessionEstablishedRunnable implements Runnable {

        private XmppUserInfo xmppClientInfo;
        private XmppUserInfo xmppUserInfo;

        public OtrSessionEstablishedRunnable(XmppUserInfo xmppClientInfo, XmppUserInfo xmppUserInfo) {
            this.xmppClientInfo = xmppClientInfo;
            this.xmppUserInfo = xmppUserInfo;
        }

        @Override
        public void run() {
            OTRSession otrSession = clientContactsOTRSessions.get(xmppClientInfo.getJid()).get(xmppUserInfo.getJid());
            currentXmppChatFragment.enableSendingMessages();
            if (otrSession.isAlive()) {
                Toast.makeText(MainActivity.this, Constants.TOAST_OTR_SESSION_SUCCESS, Toast.LENGTH_SHORT).show();
                otrEnableMenuItem.setVisible(false);
                otrDisableMenuItem.setVisible(true);
            } else {
                Toast.makeText(MainActivity.this, Constants.TOAST_OTR_SESSION_FAILURE, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
