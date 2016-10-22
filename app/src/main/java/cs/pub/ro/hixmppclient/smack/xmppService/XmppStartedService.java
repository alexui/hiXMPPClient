package cs.pub.ro.hixmppclient.smack.xmppService;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.sasl.SASLErrorException;
import org.jivesoftware.smackx.chatstates.ChatState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cs.pub.ro.hixmppclient.common.BackgroundTaskResult;
import cs.pub.ro.hixmppclient.common.ClientInfo;
import cs.pub.ro.hixmppclient.general.Constants;
import cs.pub.ro.hixmppclient.mainActivity.MainActivity;
import cs.pub.ro.hixmppclient.mainActivity.hiClient.chat.XmppChatMessage;
import cs.pub.ro.hixmppclient.smack.xmppClient.XmppClient;

public class XmppStartedService extends Service {

    public boolean running;

    private LocalBinder localBinder;
    private Map<ClientInfo, XmppClient> xmppClientMap;

    public XmppStartedService() {
        xmppClientMap = new HashMap<>();
    }

    public XmppClient getXmppClient(ClientInfo xmppClientInfo) {
        XmppClient xmppClient = xmppClientMap.get(xmppClientInfo);
        if (xmppClient == null) {
            xmppClient = new XmppClient(this, xmppClientInfo);
            xmppClientMap.put(xmppClientInfo, xmppClient);
        }
        return xmppClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        localBinder = new LocalBinder();
        Log.d(Constants.LOG_TAG_SMACK, "onCreate() method was invoked");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        running = true;

        sendToForeground();
        Log.d(Constants.LOG_TAG_SMACK, "onStartCommand() method was invoked");
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(Constants.LOG_TAG_SMACK, "onBind() method was invoked");
        return localBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(Constants.LOG_TAG_SMACK, "onUnbind() method was invoked");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Constants.LOG_TAG_SMACK, "startedService onDestroy() method was invoked");
    }

    public ArrayList<String> getAuthenticatedClients() {
        ArrayList<String> xmppClientList = new ArrayList<>();
        for (ClientInfo clientInfo : xmppClientMap.keySet()) {
            if (clientInfo.isAuthencticated())
                xmppClientList.add(clientInfo.getJid());
        }
        return xmppClientList;
    }

    private ClientInfo getAuthenticatedClientByJid(String jid) {
        ClientInfo clientInfo = null;
        for (ClientInfo ci : xmppClientMap.keySet()) {
            if (ci.isAuthencticated())
                if (ci.getJid().equals(jid)) {
                    clientInfo = ci;
                }
        }
        return clientInfo;
    }

    public void logIn(ClientInfo clientInfo) {
        Log.d(Constants.LOG_TAG_SMACK, "logIn");
        new LogInBackgroundTask(clientInfo).execute();
    }

    public void register(ClientInfo clientInfo) {
        Log.d(Constants.LOG_TAG_SMACK, "register");
        new RegisterBackgroundTask(clientInfo).execute();
    }

    public void setUpChatListener(String clientJid) {
        Log.d(Constants.LOG_TAG_SMACK, "setUpChatListener");
        XmppClient xmppClient = getXmppClient(getAuthenticatedClientByJid(clientJid));
        xmppClient.setUpChatListener();
    }

    public void sendMessage(String clientJid, String userJid, XmppChatMessage xmppChatMessage) {
        XmppClient xmppClient = getXmppClient(getAuthenticatedClientByJid(clientJid));
        xmppClient.sendMessageToUser(clientJid, userJid, xmppChatMessage);
    }

    public void sendChatStatus(String clientJid, String userJid, ChatState chatState) {
        XmppClient xmppClient = getXmppClient(getAuthenticatedClientByJid(clientJid));
        if (ChatState.composing.equals(chatState)) {
            xmppClient.sendChatStateComposing(clientJid, userJid);
        } else if (ChatState.active.equals(chatState)) {
            xmppClient.sendChatStateActive(clientJid, userJid);
        }
    }

    public void sendPresenceStatus(String clientJid, String statusMode, String statusMessage) {
        XmppClient xmppClient = getXmppClient(getAuthenticatedClientByJid(clientJid));
        xmppClient.updatePresence(statusMode, statusMessage);
    }

    public void logOut(String clientJid) {
        XmppClient xmppClient = getXmppClient(getAuthenticatedClientByJid(clientJid));
        xmppClient.closeConnection();
    }

    private void sendToForeground() {

        Intent intent;
        Notification notification;

        intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        notification = new Notification.Builder(this).build();

        startForeground(Constants.NOTIFICATION_IDENTIFIER, notification);
    }

    private class LogInBackgroundTask extends AsyncTask<Void, Void, BackgroundTaskResult> {

        private ClientInfo clientInfo;

        public LogInBackgroundTask(ClientInfo clientInfo) {
            this.clientInfo = clientInfo;
        }

        @Override
        protected BackgroundTaskResult doInBackground(Void... arg0) {

            BackgroundTaskResult backgroundTaskResult = new BackgroundTaskResult();

            XmppClient xmppClient = getXmppClient(clientInfo);
            try {
                xmppClient.establishConnectionAndLogIn();
                backgroundTaskResult.setSuccess(true);
                backgroundTaskResult.setMessage("Successfully logged in");
            } catch (SmackException.ConnectionException e) {
                backgroundTaskResult.setSuccess(false);
                backgroundTaskResult.setMessage("Log In failed. Unreachable server.");
                logException(e);
                backgroundTaskResult.setSuccess(false);
            } catch (SASLErrorException e) {
                backgroundTaskResult.setSuccess(false);
                backgroundTaskResult.setMessage("Log In failed. Invalid username or password.");
                logException(e);
            } catch (IOException| SmackException | XMPPException e) {
                backgroundTaskResult.setSuccess(false);
                logException(e);
            }
            return backgroundTaskResult;
        }

        @Override
        protected void onPostExecute(BackgroundTaskResult backgroundTaskStatus) {
            Intent intent = new Intent();
            String actionLogIn = cs.pub.ro.hixmppclient.general.Constants.ACTION_LOG_IN + clientInfo.getJid();
            intent.setAction(actionLogIn);
            intent.putExtra(cs.pub.ro.hixmppclient.general.Constants.TAG_BROADCAST_DATA, backgroundTaskStatus);
            sendBroadcast(intent);
        }
    }

    private class RegisterBackgroundTask extends AsyncTask<Void, Void, BackgroundTaskResult> {

        private ClientInfo clientInfo;

        public RegisterBackgroundTask(ClientInfo clientInfo) {
            this.clientInfo = clientInfo;
        }

        @Override
        protected BackgroundTaskResult doInBackground(Void... params) {

            BackgroundTaskResult backgroundTaskResult = new BackgroundTaskResult();

            XmppClient xmppClient = getXmppClient(clientInfo);
            try {
                xmppClient.establishConnectionAndCreateAccount();
                backgroundTaskResult.setSuccess(true);
                backgroundTaskResult.setMessage("Successfully created account");
            } catch (Exception exception) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                backgroundTaskResult.setSuccess(false);
                if (cm.getActiveNetworkInfo() == null)
                    backgroundTaskResult.setMessage("Check your internet connection first.");
                else if (exception.getMessage().equals(Constants.NO_SUPPORT_FOR_ACCOUNT_CREATION_EXCEPTION))
                    backgroundTaskResult.setMessage("No support for account creation.");
                else if (exception.getMessage().equals(Constants.EXISTING_USERNAME_EXCEPTION))
                    backgroundTaskResult.setMessage("The username exists already.");
                else
                    backgroundTaskResult.setMessage("Register failed. Connection problems or inactive server.");
            }

            return backgroundTaskResult;
        }

        @Override
        protected void onPostExecute(BackgroundTaskResult backgroundTaskStatus) {
            Intent intent = new Intent();
            String actionRegister = cs.pub.ro.hixmppclient.general.Constants.ACTION_REGISTER + clientInfo.getJid();
            intent.setAction(actionRegister);
            intent.putExtra(cs.pub.ro.hixmppclient.general.Constants.TAG_BROADCAST_DATA, backgroundTaskStatus);
            XmppStartedService.this.sendBroadcast(intent);
        }
    }

    public class LocalBinder extends Binder {
        public XmppStartedService getService() {
            return XmppStartedService.this;
        }
    }

    private void logException(Exception e) {
        Log.d(Constants.LOG_TAG_SMACK, e.getMessage());
        Log.e(Constants.LOG_TAG_SMACK, e.toString());
    }
}
