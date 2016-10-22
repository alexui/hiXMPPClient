package cs.pub.ro.hixmppclient.smack.xmppClient;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xevent.MessageEventManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import cs.pub.ro.hixmppclient.common.ClientInfo;
import cs.pub.ro.hixmppclient.common.XmppUserInfo;
import cs.pub.ro.hixmppclient.general.Constants;
import cs.pub.ro.hixmppclient.general.Utils;
import cs.pub.ro.hixmppclient.mainActivity.hiClient.chat.XmppChatMessage;
import cs.pub.ro.hixmppclient.otr.OTRUtils;

public class XmppClient {

    private Context context;
    private ClientInfo clientInfo;
    private HashMap<String, ArrayList<XmppChatMessage>> offlineMessagesMap;

    private XMPPTCPConnectionConfiguration xmppConnectionConfig;
    private XMPPTCPConnection xmppConnection;
    private Presence presence;

    private ChatManager xmppChatManager;
    private ChatStateManager xmppChatStateManager;
    private OfflineMessageManager offlineMessageManager;
    private Roster xmppRoster;
    private VCardManager xmppVCardManager;

    private XmppConnectionListener xmppConnectionListener;
    private XmppRosterListener xmppRosterListener;
    private XmppChatMessageListener xmppChatMessageListener;
    private XmppChatManagerListener xmppChatManagerListener;
    private MessageEventManager xmppMessageEventManager;
    private AccountManager accountManager;

    private boolean userPresenceUpdatesEnabled;

    private VCard clientVCard;

    private HashMap<String, ArrayList<XmppChatMessage>> xmppUserMessages;

    public XmppClient(Context context, ClientInfo info) {
        this.context = context;
        this.clientInfo = info;
        xmppConnectionListener = new XmppConnectionListener();
        initXmppConnection();

        xmppChatMessageListener = new XmppChatMessageListener();
        xmppChatManagerListener = new XmppChatManagerListener();
        presence = new Presence(Presence.Type.available);

        xmppUserMessages = new HashMap<>();
    }

    private void initXmppConnection() {
        xmppConnectionConfig = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword(clientInfo.getUsername(), clientInfo.getPassword())
                .setResource(clientInfo.getResource())
                .setHost(clientInfo.getHostAddress())
                .setPort(Integer.valueOf(clientInfo.getPort()))
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setDebuggerEnabled(true)
                .setServiceName(clientInfo.getServiceName())
                .build();

        xmppConnection = new XMPPTCPConnection(xmppConnectionConfig);
        xmppConnection.addConnectionListener(xmppConnectionListener);

    }

    public void establishConnectionAndLogIn() throws IOException, XMPPException, SmackException {

        if (!xmppConnection.isConnected()) {

            xmppConnection.setPacketReplyTimeout(Constants.DEFAULT_PACKAGE_REPLY_TIMEOUT);
            xmppConnection.connect();
            userPresenceUpdatesEnabled = false;
        }

        if (!xmppConnection.isAuthenticated()) {
            xmppConnection.login();
        }
    }

    public void establishConnectionAndCreateAccount() throws Exception {

        if (!xmppConnection.isConnected()) {
            try {
                xmppConnection.connect();

                accountManager = AccountManager.getInstance(xmppConnection);
                accountManager.sensitiveOperationOverInsecureConnection(true);
                if (accountManager.supportsAccountCreation()) {
                    accountManager.createAccount(clientInfo.getUsername(), clientInfo.getPassword());
                } else {
                    throw new Exception(Constants.NO_SUPPORT_FOR_ACCOUNT_CREATION_EXCEPTION);
                }
                xmppConnection.disconnect();
            } catch (XMPPException.XMPPErrorException e) {
                if (e.getXMPPError().getCondition() == XMPPError.Condition.conflict) {
                    throw new Exception(Constants.EXISTING_USERNAME_EXCEPTION);
                }
            }
        }
    }

    public void setUpChatListener() {
        xmppChatManager = ChatManager.getInstanceFor(xmppConnection);
        xmppChatStateManager = ChatStateManager.getInstance(xmppConnection);
        xmppChatManager.addChatListener(xmppChatManagerListener);

        xmppMessageEventManager = MessageEventManager.getInstanceFor(xmppConnection);
    }

    public void updatePresence(Presence.Type type) {
        presence.setType(type);
        try {
            xmppConnection.sendStanza(presence);
        } catch (SmackException.NotConnectedException e) {
            Log.d(Constants.LOG_TAG_SMACK, e.getMessage());
            Log.e(Constants.LOG_TAG_SMACK, e.toString());
        }
    }

    public void updatePresence(String statusMode, String statusMessage) {
        presence.setMode(Presence.Mode.fromString(statusMode));
        presence.setStatus(statusMessage);
        try {
            xmppConnection.sendStanza(presence);
        } catch (SmackException.NotConnectedException e) {
            Log.d(Constants.LOG_TAG_SMACK, e.getMessage());
            Log.e(Constants.LOG_TAG_SMACK, e.toString());
        }

    }

    public XmppUserInfo getXmppClientInfo() {
        String jid = clientInfo.getJid();
        XmppUserInfo xmppClientUserInfo = new XmppUserInfo();

        xmppClientUserInfo.setJid(jid);
        xmppClientUserInfo.setAvailable(true);

        try {
            xmppClientUserInfo.setName(accountManager.getAccountAttribute("name"));
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
            Log.d(Constants.LOG_TAG_SMACK, e.getMessage());
            Log.e(Constants.LOG_TAG_SMACK, e.toString());
        }

        xmppClientUserInfo.setAvatar(clientVCard.getAvatar());
        xmppClientUserInfo.setPublicKeyRSA(clientInfo.getKeyPairRSA().getPublic());
        xmppClientUserInfo.setPrivateKeyRSA(clientInfo.getKeyPairRSA().getPrivate());

        if (presence.isAvailable()) {
            xmppClientUserInfo.setStatusMode(presence.getMode().name());
            xmppClientUserInfo.setStatusMessage(presence.getStatus());
        }

        return xmppClientUserInfo;
    }

    public ArrayList<XmppUserInfo> getXmppUsersInfo() {

        Presence presence;
        ArrayList<XmppUserInfo> userInfoList = new ArrayList<>();
        Collection<RosterEntry> rosterEntries = xmppRoster.getEntries();

        for (RosterEntry rosterEntry : rosterEntries) {
            ArrayList<String> groupList = new ArrayList<>();

            String jid = rosterEntry.getUser();
            XmppUserInfo userInfo = new XmppUserInfo();
            presence = xmppRoster.getPresence(rosterEntry.getUser());

            userInfo.setJid(jid);
            userInfo.setName(rosterEntry.getName());
            userInfo.setAvailable(presence.isAvailable());

            for (RosterGroup rosterGroup : rosterEntry.getGroups())
                groupList.add(rosterGroup.getName());
            userInfo.setGroups(groupList);

            try {
                VCard xmppVCard = xmppVCardManager.loadVCard(rosterEntry.getUser());
                userInfo.setAvatar(xmppVCard.getAvatar());
                String publicKeyField = xmppVCard.getField(Constants.VCARD_FIELD_KEY);
                if (publicKeyField != null && !publicKeyField.isEmpty())
                    userInfo.setPublicKeyRSA(publicKeyField);
                Log.d(Constants.LOG_TAG, "user public key: " + publicKeyField);
            } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
                Log.d(Constants.LOG_TAG_SMACK, e.getMessage());
                Log.e(Constants.LOG_TAG_SMACK, e.toString());
            }

            if (presence.isAvailable()) {
                userInfo.setStatusMode(presence.getMode().name());
                userInfo.setStatusMessage(presence.getStatus());
            }

            ArrayList<XmppChatMessage> offlineMessagesList = offlineMessagesMap.get(jid);
            if (offlineMessagesList != null) {
                userInfo.setMessages(offlineMessagesList);
                userInfo.setUnreadMessages(offlineMessagesList.size());
            }

            userInfoList.add(userInfo);
        }

        Collections.sort(userInfoList);

        return userInfoList;
    }

    public XmppUserInfo getXmppUserAdditionalInfo(XmppUserInfo userInfo) {

        try {
            VCard xmppVCard = xmppVCardManager.loadVCard(userInfo.getJid());
            userInfo.setLastName(xmppVCard.getLastName());
            userInfo.setMiddleName(xmppVCard.getMiddleName());
            userInfo.setNickName(xmppVCard.getNickName());
            userInfo.setOrganization(xmppVCard.getOrganization());
            userInfo.setEmailHome(xmppVCard.getEmailHome());
            userInfo.setEmailWork(xmppVCard.getEmailWork());
            userInfo.setPhoneHome(xmppVCard.getPhoneHome(Constants.DEFAULT_PHONE_NUMBER_TYPE));
            userInfo.setPhoneWork(xmppVCard.getPhoneWork(Constants.DEFAULT_PHONE_NUMBER_TYPE));

            Log.d(Constants.LOG_TAG_SMACK, "Card :\n" + Utils.prettyFormat(xmppVCard.toString()));
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
            Log.d(Constants.LOG_TAG_SMACK, e.getMessage());
            Log.e(Constants.LOG_TAG_SMACK, e.toString());
        }

        return userInfo;
    }

    public void sendMessageToUser(String clientJid, String userJid, XmppChatMessage xmppChatMessage) {
        Chat chat = xmppChatManager.createChat(userJid, xmppChatMessageListener);
        final Message message = new Message();
        message.setStanzaId(String.valueOf(xmppChatMessage.getId()));
        message.setSubject(xmppChatMessage.getSubject());
        message.setType(Message.Type.chat);
        message.setBody(xmppChatMessage.getBody());
        message.setFrom(clientJid);
        message.setTo(userJid);
        try {
            chat.sendMessage(message);
        } catch (SmackException.NotConnectedException e) {
            Log.d(Constants.LOG_TAG_SMACK, e.getMessage());
            Log.e(Constants.LOG_TAG_SMACK, e.toString());
        }
    }

    public void sendChatStateComposing(String clientJid, String userJid) {

        Chat chat = xmppChatManager.createChat(userJid, xmppChatMessageListener);
        final Message message = new Message();
        message.setStanzaId(String.valueOf(new Random().nextInt(Constants.MESSAGE_MAX_ID)));
        message.setType(Message.Type.chat);
        message.setFrom(clientJid);
        message.setTo(userJid);
        try {
            xmppChatStateManager.setCurrentState(ChatState.composing, chat);
        } catch (SmackException.NotConnectedException e) {
            Log.d(Constants.LOG_TAG, e.getMessage());
            Log.e(Constants.LOG_TAG, e.toString());
        }
    }

    public void sendChatStateActive(String clientJid, String userJid) {

        Chat chat = xmppChatManager.createChat(userJid, xmppChatMessageListener);
        final Message message = new Message();
        message.setStanzaId(String.valueOf(new Random().nextInt(Constants.MESSAGE_MAX_ID)));
        message.setType(Message.Type.chat);
        message.setFrom(clientJid);
        message.setTo(userJid);
        try {
            xmppChatStateManager.setCurrentState(ChatState.active, chat);
        } catch (SmackException.NotConnectedException e) {
            Log.d(Constants.LOG_TAG, e.getMessage());
            Log.e(Constants.LOG_TAG, e.toString());
        }
    }

    public void closeConnection() {

        clientVCard.setField(Constants.VCARD_FIELD_KEY, null);
        try {
            xmppVCardManager.saveVCard(clientVCard);
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
            Log.d(Constants.LOG_TAG_SMACK, e.getMessage());
            Log.e(Constants.LOG_TAG_SMACK, e.toString());
        }

        if (xmppConnection != null && xmppConnection.isConnected()) {
            xmppConnection.disconnect();
            clientInfo.setAuthencticated(false);
            clientInfo.setConnected(false);
            Log.d(Constants.LOG_TAG_SMACK, "Client Disconnected");
        }
    }

    private HashMap<String, ArrayList<XmppChatMessage>> getOfflineMessages() {

        HashMap<String, ArrayList<XmppChatMessage>> offlineMessagesMap = new HashMap<>();

        try {
            if (offlineMessageManager.getMessageCount() == 0) {
                Log.d(Constants.LOG_TAG_SMACK, "Offline messages not found on server.");
            } else {
                List<Message> messageList = offlineMessageManager.getMessages();
                for (Message message : messageList) {
                    String userJid = message.getFrom().replaceAll("[/].*", "");
                    String subject = message.getSubject();
                    String messageBody = message.getBody();
                    Date currentDate = new Date();
                    if (messageBody != null && (subject == null || subject.equals(Constants.MESSAGE_SUBJECT_NORMAL))) {
                        XmppChatMessage xmppChatMessage = new XmppChatMessage(subject, messageBody, userJid, clientInfo.getJid(), false, currentDate);
                        ArrayList<XmppChatMessage> offlineMessages = offlineMessagesMap.get(userJid);
                        if (offlineMessages == null) {
                            offlineMessages = new ArrayList<>();
                            offlineMessagesMap.put(userJid, offlineMessages);
                        }
                        offlineMessages.add(xmppChatMessage);
                        Log.d(Constants.LOG_TAG_SMACK, "Retrieved offline message from " + userJid + " with content: " + messageBody);
                    }
                }
                offlineMessageManager.deleteMessages();
            }
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
            Log.e(Constants.LOG_TAG_SMACK, "Exception Offline Message Manager");
        }

        return offlineMessagesMap;
    }

    private class XmppChatManagerListener implements ChatManagerListener {

        @Override
        public void chatCreated(Chat chat, boolean createdLocally) {
            if (!createdLocally)
                chat.addMessageListener(xmppChatMessageListener);
        }
    }

    private class XmppChatMessageListener implements ChatMessageListener, ChatStateListener {

        @Override
        public void processMessage(Chat chat, Message message) {
            String xmlFormatedMessage = Utils.prettyFormat(message.toString());
            String user = chat.getParticipant().replaceAll("[/].*", "");
            if (message != null) {
                String subject = message.getSubject();
                String messageBody = message.getBody();
                Log.d(Constants.LOG_TAG_SMACK, "Received message body from: " + chat.getParticipant() + "\n" + xmlFormatedMessage);

                if (subject == null || subject.equals(Constants.MESSAGE_SUBJECT_NORMAL)) {
                    if (messageBody != null && !messageBody.isEmpty()) {

                        ArrayList<XmppChatMessage> userMessagesList = xmppUserMessages.get(user);
                        Date currentDate = new Date();
                        XmppChatMessage xmppChatMessage = new XmppChatMessage(subject, messageBody, user, clientInfo.getJid(), false, currentDate);
                        if (userMessagesList == null) {
                            userMessagesList = new ArrayList<>();
                            userMessagesList.add(xmppChatMessage);
                            xmppUserMessages.put(user, userMessagesList);

                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_CONTACT_MESSAGE);
                            intent.putExtra(Constants.TAG_CLIENT_JID, clientInfo.getJid());
                            intent.putExtra(Constants.TAG_BROADCAST_DATA, xmppChatMessage);
                            context.sendBroadcast(intent);

                            xmppUserMessages.remove(user);
                        } else
                            userMessagesList.add(xmppChatMessage);
                    }
                }
                else {
                    if (messageBody != null && !messageBody.isEmpty()) {

                        Date currentDate = new Date();
                        XmppChatMessage xmppChatMessage = new XmppChatMessage(subject, messageBody, user, clientInfo.getJid(), false, currentDate);

                        Intent intent = new Intent();
                        intent.setAction(Constants.ACTION_CONTACT_OTR_MESSAGE);
                        intent.putExtra(Constants.TAG_CLIENT_JID, clientInfo.getJid());
                        intent.putExtra(Constants.TAG_BROADCAST_DATA, xmppChatMessage);
                        context.sendBroadcast(intent);
                    }
                }
            }
        }

        @Override
        public void stateChanged(Chat chat, ChatState state) {
            Log.d(Constants.LOG_TAG_SMACK, "Chat state changed: " + state);

            if (ChatState.composing.equals(state) || ChatState.active.equals(state)) {
                Intent intent = new Intent();
                intent.setAction(Constants.ACTION_CONTACT_CHAT_STATUS);
                intent.putExtra(Constants.TAG_CLIENT_JID, clientInfo.getJid());
                intent.putExtra(Constants.TAG_CONTACT_JID, chat.getParticipant().replaceAll("[/].*", ""));
                intent.putExtra(Constants.TAG_BROADCAST_DATA, state.name().toString());
                context.sendBroadcast(intent);
            }
        }
    }

    private class XmppRosterListener implements RosterListener {

        @Override
        public void entriesAdded(Collection<String> addresses) {
            Log.d(Constants.LOG_TAG_SMACK, "Presence added: " + addresses);
        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {
            Log.d(Constants.LOG_TAG_SMACK, "Presence updated: " + addresses);
        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {
            Log.d(Constants.LOG_TAG_SMACK, "Presence deleted: " + addresses);
        }

        @Override
        public void presenceChanged(Presence presence) {

            if (userPresenceUpdatesEnabled) {
                XmppUserInfo userInfo = new XmppUserInfo();
                userInfo.setJid(presence.getFrom().replaceAll("[/].*", ""));
                userInfo.setAvailable(presence.isAvailable());
                userInfo.setStatusMode(presence.getMode().name());
                userInfo.setStatusMessage(presence.getStatus());

                try {
                    VCard xmppVCard = xmppVCardManager.loadVCard(userInfo.getJid());
                    userInfo.setAvatar(xmppVCard.getAvatar());
                    String publicKeyField = xmppVCard.getField(Constants.VCARD_FIELD_KEY);
                    if (publicKeyField != null && !publicKeyField.isEmpty())
                        userInfo.setPublicKeyRSA(publicKeyField);
                    Log.d(Constants.LOG_TAG, "user public key: " + publicKeyField);
                } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
                    Log.d(Constants.LOG_TAG_SMACK, e.getMessage());
                    Log.e(Constants.LOG_TAG_SMACK, e.toString());
                }

                Intent intent = new Intent();
                intent.setAction(cs.pub.ro.hixmppclient.general.Constants.ACTION_CONTACT_PRESENCE_CHANGED);
                intent.putExtra(cs.pub.ro.hixmppclient.general.Constants.TAG_CLIENT_JID, clientInfo.getJid());
                intent.putExtra(cs.pub.ro.hixmppclient.general.Constants.TAG_BROADCAST_DATA, userInfo);
                context.sendBroadcast(intent);
            }

            Log.d(Constants.LOG_TAG_SMACK, "Presence changed: " + presence.getFrom() + " " + Utils.prettyFormat(presence.toString()));
        }
    }

    private class XmppConnectionListener implements ConnectionListener {

        @Override
        public void connected(final XMPPConnection connection) {
            Log.d(Constants.LOG_TAG_SMACK, "Client Connected.");
            clientInfo.setConnected(true);
        }

        @Override
        public void connectionClosed() {
            Log.d(Constants.LOG_TAG_SMACK, "Client Connection Closed.");
            clientInfo.setConnected(false);

            xmppRoster.removeRosterListener(xmppRosterListener);
            xmppChatStateManager = null;
            xmppChatManager.removeChatListener(xmppChatManagerListener);

            xmppRoster = null;
            xmppVCardManager = null;
            xmppRosterListener = null;
            offlineMessageManager = null;
            accountManager = null;

            userPresenceUpdatesEnabled = false;
        }

        @Override
        public void connectionClosedOnError(Exception arg0) {
            Log.d(Constants.LOG_TAG_SMACK, "Client Connection Closed OnError : " + arg0.getMessage());
            Log.e(Constants.LOG_TAG_SMACK, arg0.toString());
            clientInfo.setConnected(false);
        }

        @Override
        public void reconnectingIn(int arg0) {
            Log.d(Constants.LOG_TAG_SMACK, "Client Reconnecting In " + arg0);
        }

        @Override
        public void reconnectionFailed(Exception arg0) {
            Log.d(Constants.LOG_TAG_SMACK, "Client ReconnectionFailed");
            Log.e(Constants.LOG_TAG_SMACK, arg0.toString());
        }

        @Override
        public void reconnectionSuccessful() {
            Log.d(Constants.LOG_TAG_SMACK, "Client ReconnectionSuccessful");
            clientInfo.setConnected(true);
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {
            Log.d(Constants.LOG_TAG_SMACK, "Client Authenticated");
            clientInfo.setAuthencticated(true);

            xmppRoster = Roster.getInstanceFor(xmppConnection);
            xmppVCardManager = VCardManager.getInstanceFor(xmppConnection);
            xmppRosterListener = new XmppRosterListener();
            xmppRoster.addRosterListener(xmppRosterListener);
            xmppRoster.setSubscriptionMode(Roster.SubscriptionMode.manual);

            offlineMessageManager = new OfflineMessageManager(xmppConnection);
            offlineMessagesMap = getOfflineMessages();

            updatePresence(Presence.Type.available);

            if (!xmppRoster.isLoaded()) {
                try {
                    xmppRoster.reloadAndWait();
                } catch (SmackException.NotLoggedInException | SmackException.NotConnectedException | InterruptedException e) {
                    Log.d(Constants.LOG_TAG_SMACK, e.getMessage());
                    Log.e(Constants.LOG_TAG_SMACK, e.toString());
                }
            }

            if (accountManager == null)
                accountManager = AccountManager.getInstance(xmppConnection);

            try {
                clientInfo.setKeyPairRSA(OTRUtils.getRSAKeyPair());
                clientVCard = xmppVCardManager.loadVCard();
                String base64EncodedPublicKey = Base64.encodeToString(clientInfo.getKeyPairRSA().getPublic().getEncoded(), Base64.DEFAULT);
                Log.d(Constants.LOG_TAG, "Generated public key : " + base64EncodedPublicKey);
                clientVCard.setField(Constants.VCARD_FIELD_KEY, base64EncodedPublicKey);
                xmppVCardManager.saveVCard(clientVCard);
            } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
                Log.d(Constants.LOG_TAG_SMACK, e.getMessage());
                Log.e(Constants.LOG_TAG_SMACK, e.toString());
            }

            Intent intent = new Intent();
            intent.setAction(Constants.ACTION_CONTACT_LIST);
            intent.putExtra(Constants.TAG_CLIENT_JID, clientInfo.getJid());
            intent.putExtra(Constants.TAG_CLIENT_INFO, getXmppClientInfo());
            intent.putExtra(Constants.TAG_BROADCAST_DATA, getXmppUsersInfo());
            context.sendBroadcast(intent);
            userPresenceUpdatesEnabled = true;

            Log.d(Constants.LOG_TAG_SMACK, "action_contact_list broadcast");
        }
    }
}
