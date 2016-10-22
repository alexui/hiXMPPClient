package cs.pub.ro.hixmppclient.mainActivity.hiClient.chat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.jivesoftware.smackx.chatstates.ChatState;

import java.util.ArrayList;
import java.util.Date;

import cs.pub.ro.hixmppclient.R;
import cs.pub.ro.hixmppclient.common.XmppUserInfo;
import cs.pub.ro.hixmppclient.general.Constants;
import cs.pub.ro.hixmppclient.general.Utils;
import cs.pub.ro.hixmppclient.mainActivity.MainActivity;
import cs.pub.ro.hixmppclient.otr.OTRMessageBody;
import cs.pub.ro.hixmppclient.otr.OTRSession;
import cs.pub.ro.hixmppclient.smack.xmppService.XmppStartedService;

public class XmppChatFragment extends Fragment {

    private MainActivity mainActivity;
    private XmppStartedService startedService;

    private EditText chatMessageEditText;
    private ImageView chatSendImageView;

    private ListView chatListView;
    private XmppChatAdapter xmppChatAdapter;
    private TextView chatMessageComposingTextView;
    private ImageView userPhotoImageView;
    private TextView usernameTextView;

    private XmppUserInfo xmppClientInfo;
    private XmppUserInfo xmppUserInfo;
    private ArrayList<XmppChatMessage> userMessages;

    private SendImageViewTouchListener sendImageViewTouchListener;
    private ChatMessageEditTextTextWatcher chatMessageEditTextTextWatcher;

    private Gson gson;

    public void initChatFragment(XmppUserInfo xmppClientInfo, XmppUserInfo xmppUserInfo) {
        this.xmppClientInfo = xmppClientInfo;
        this.xmppUserInfo = xmppUserInfo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String contactName = xmppUserInfo.getName();

        mainActivity = (MainActivity) getActivity();
        startedService = mainActivity.getStartedService();
        userMessages = xmppUserInfo.getMessages();

        xmppChatAdapter = new XmppChatAdapter(mainActivity, userMessages);
        sendImageViewTouchListener = new SendImageViewTouchListener();
        chatMessageEditTextTextWatcher = new ChatMessageEditTextTextWatcher();

        View layoutView = inflater.inflate(R.layout.fragment_chat, container, false);

        chatMessageEditText = (EditText) layoutView.findViewById(R.id.chatMessageEditText);
        chatSendImageView = (ImageView) layoutView.findViewById(R.id.chatSendImageView);
        chatListView = (ListView) layoutView.findViewById(R.id.chatListView);
        chatMessageComposingTextView = (TextView) layoutView.findViewById(R.id.chatMessageComposingTextView);
        userPhotoImageView = (ImageView) layoutView.findViewById(R.id.userPhotoImageViewChat);
        usernameTextView = (TextView) layoutView.findViewById(R.id.usernameTextViewChat);

        if (contactName.isEmpty())
            contactName = xmppUserInfo.getJid().replaceAll("[@].*", "");
        usernameTextView.setText(contactName);

        byte[] xmppUserInfoAvatar = xmppUserInfo.getAvatar();
        if (xmppUserInfoAvatar != null) {
            Bitmap bitmapImage = BitmapFactory.decodeByteArray(xmppUserInfoAvatar, 0, xmppUserInfoAvatar.length);
            userPhotoImageView.setImageBitmap(bitmapImage);
        }

        if (!xmppUserInfo.isAvailable()) {
            userPhotoImageView.setColorFilter(Utils.getGreyScaleFilter());
        }

        chatListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatListView.setStackFromBottom(true);
        chatListView.setAdapter(xmppChatAdapter);

        chatSendImageView.setOnTouchListener(sendImageViewTouchListener);
        chatMessageEditText.addTextChangedListener(chatMessageEditTextTextWatcher);

        mainActivity.setCurrentXmppChatFragment(this);

        gson = new Gson();

        Log.d(Constants.LOG_TAG, "Chat Fragment created View");

        return layoutView;
    }

    public XmppUserInfo getClientInfo() {
        return xmppClientInfo;
    }

    public XmppUserInfo getUserInfo() {
        return xmppUserInfo;
    }

    public void updateChatFragment() {
        xmppChatAdapter.notifyDataSetChanged();
    }

    public void updateChatFragmentUserAvailable() {
        if (xmppUserInfo.isAvailable()) {
            userPhotoImageView.clearColorFilter();
        } else {
            userPhotoImageView.setColorFilter(Utils.getGreyScaleFilter());
        }
    }

    @Override
    public void onDestroyView() {

        mainActivity.destroyCurrentXmppChatFragment();
        mainActivity.getOtrEnableMenuItem().setVisible(false);

        Log.d(Constants.LOG_TAG, "Chat Fragment created View");
        super.onDestroyView();
    }

    public boolean checkChatParticipants(XmppChatMessage xmppChatMessage) {
        return (xmppClientInfo.getJid().equals(xmppChatMessage.getDestinationJID())) && (xmppUserInfo.getJid().equals(xmppChatMessage.getSourceJID()));
    }

    public boolean checkChatParticipants(String clientJid, String contactJid) {
        return (xmppClientInfo.getJid().equals(clientJid)) && (xmppUserInfo.getJid().equals(contactJid));
    }

    public void updateChatStatus(String chatStatus) {
        if (ChatState.composing.name().equals(chatStatus)) {
            chatMessageComposingTextView.setVisibility(View.VISIBLE);
            chatMessageComposingTextView.setText(xmppUserInfo.getJid().replaceAll("[@].*", "") + Constants.CHAT_STATUS_MESSAGE);
        }
        if (ChatState.active.name().equals(chatStatus))
            chatMessageComposingTextView.setVisibility(View.GONE);
    }

    public void sendOTRSessionRequest(OTRSession otrSession) {

        String clientJid = xmppClientInfo.getJid();
        String userJid = xmppUserInfo.getJid();
        Date currentDate = new Date();

        byte[] dhPublicKey = otrSession.createClientEncodedDHPublicKey();
        OTRMessageBody otrMessageBody = new OTRMessageBody(dhPublicKey, xmppClientInfo.getPrivateKeyRSA());

        XmppChatMessage xmppChatMessage = new XmppChatMessage(Constants.MESSAGE_SUBJECT_OTR_DH_REQUEST, gson.toJson(otrMessageBody),
                clientJid, userJid, true, currentDate);

        startedService.sendMessage(xmppClientInfo.getJid(), userJid, xmppChatMessage);

        Log.d(Constants.LOG_TAG, "OTR Sent Request dhPublicKey: " + dhPublicKey);
    }

    public void disableSendingMessages() {
        chatSendImageView.setEnabled(false);
        chatSendImageView.setColorFilter(Utils.getGreyScaleFilter());
    }

    public void enableSendingMessages() {
        chatSendImageView.setEnabled(true);
        chatSendImageView.clearColorFilter();
    }

    private class SendImageViewTouchListener implements View.OnTouchListener {

        private XmppChatMessage xmppChatMessage;
        private String clientJid = xmppClientInfo.getJid();
        private String userJid = xmppUserInfo.getJid();
        private Date currentDate;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ImageView handImageView = (ImageView) v;
            OTRSession otrSession;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    String messageBody = chatMessageEditText.getText().toString();
                    Utils.handleImageViewActionDown(handImageView, 4);

                    otrSession = mainActivity.getOTRSession(xmppClientInfo.getJid(), xmppUserInfo.getJid());

                    if (!messageBody.isEmpty()) {
                        if (otrSession != null)
                            sendEncryptedMessage(otrSession, messageBody);
                        else
                            sendPlainTextMessage(messageBody);
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    Utils.handleImageViewActionUp(handImageView, 4);
                    break;
                }
            }
            return true;
        }

        private void sendPlainTextMessage(String messageBody) {
            currentDate = new Date();
            xmppChatMessage = new XmppChatMessage(Constants.MESSAGE_SUBJECT_NORMAL, messageBody, clientJid, userJid, true, currentDate);
            chatMessageEditText.setText("");
            mainActivity.updateClientContactsMapSentMessages(clientJid, xmppChatMessage);
            mainActivity.notifyFragmentsDataChanged(clientJid);
            startedService.sendMessage(clientJid, userJid, xmppChatMessage);
        }

        private void sendEncryptedMessage(OTRSession otrSession, String messageBody) {
            currentDate = new Date();

            xmppChatMessage = new XmppChatMessage(Constants.MESSAGE_SUBJECT_NORMAL, messageBody,
                    clientJid, userJid, true, currentDate);
            chatMessageEditText.setText("");
            mainActivity.updateClientContactsMapSentMessages(clientJid, xmppChatMessage);
            mainActivity.notifyFragmentsDataChanged(clientJid);

            byte[] encryptedByteArray = otrSession.getAesEncryptedByteArray(messageBody.getBytes());
            OTRMessageBody otrMessageBody = new OTRMessageBody(encryptedByteArray, otrSession.createClientEncodedDHPublicKey(), otrSession);
            xmppChatMessage = new XmppChatMessage(Constants.MESSAGE_SUBJECT_NORMAL, gson.toJson(otrMessageBody),
                    clientJid, userJid, true, currentDate);

            startedService.sendMessage(clientJid, userJid, xmppChatMessage);
            Log.d(Constants.LOG_TAG, "sent encrypted message");
        }
    }

    private class ChatMessageEditTextTextWatcher implements TextWatcher {

        private ChatState chatState = ChatState.active;
        private String clientJid = xmppClientInfo.getJid();
        private String userJid = xmppUserInfo.getJid();

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            return;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().isEmpty() && ChatState.composing.equals(chatState)) {
                startedService.sendChatStatus(clientJid, userJid, ChatState.active);
                chatState = ChatState.active;
            }
            if (!s.toString().isEmpty() && ChatState.active.equals(chatState)) {
                startedService.sendChatStatus(clientJid, userJid, ChatState.composing);
                chatState = ChatState.composing;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            return;
        }
    }
}
