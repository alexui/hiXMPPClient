package cs.pub.ro.hixmppclient.mainActivity.hiClient.messagesFragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cs.pub.ro.hixmppclient.R;
import cs.pub.ro.hixmppclient.common.XmppUserInfo;
import cs.pub.ro.hixmppclient.general.Utils;
import cs.pub.ro.hixmppclient.mainActivity.MainActivity;
import cs.pub.ro.hixmppclient.mainActivity.hiClient.chat.XmppChatMessage;

public class MessagesListAdapter extends BaseAdapter {

    private MainActivity mainActivity;
    private ArrayList<XmppUserInfo> userInfoList;

    private ImageView userAvatarImageView;
    private TextView usernameTextView;
    private TextView messageTextView;
    private TextView messagesCountTextView;

    public MessagesListAdapter(ArrayList<XmppUserInfo> userInfoList) {
        this.userInfoList = userInfoList;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public int getCount() {
        return userInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return userInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final XmppUserInfo xmppUserInfo = (XmppUserInfo) getItem(position);
        String contactName = xmppUserInfo.getName();
        byte[] xmppUserInfoAvatar = xmppUserInfo.getAvatar();
        LayoutInflater inflater = mainActivity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.messages_list_item, null);
        }

        userAvatarImageView = (ImageView) convertView.findViewById(R.id.userPhotoImageViewMessages);
        usernameTextView = (TextView) convertView.findViewById(R.id.usernameTextViewMessages);
        messageTextView = (TextView) convertView.findViewById(R.id.messageTextViewMessages);
        messagesCountTextView = (TextView) convertView.findViewById(R.id.messagesCountTextViewMessages);

        if (xmppUserInfoAvatar != null) {
            Bitmap bitmapImage = BitmapFactory.decodeByteArray(xmppUserInfoAvatar, 0, xmppUserInfoAvatar.length);
            userAvatarImageView.setImageBitmap(bitmapImage);
        }

        if (contactName.isEmpty())
            contactName = xmppUserInfo.getJid().replaceAll("[@].*", "");
        usernameTextView.setText(contactName);

        ArrayList<XmppChatMessage> contactMessageList = xmppUserInfo.getMessages();
        messageTextView.setText(contactMessageList.get(contactMessageList.size() - 1).getBody());

        int unreadMessages = xmppUserInfo.getUnreadMessages();
        if (xmppUserInfo.isAvailable()) {

            usernameTextView.setTextColor(mainActivity.getResources().getColor(R.color.colorPrimary));
            if (unreadMessages != 0) {
                messagesCountTextView.setVisibility(View.VISIBLE);
                messagesCountTextView.setText(String.valueOf(unreadMessages));
                messagesCountTextView.setBackground(mainActivity.getResources().getDrawable(R.drawable.user_messages_online));
                messageTextView.setTypeface(null, Typeface.BOLD);
            } else {
                messagesCountTextView.setVisibility(View.INVISIBLE);
                messageTextView.setTypeface(null, Typeface.NORMAL);
            }

            userAvatarImageView.clearColorFilter();
        } else {

            userAvatarImageView.setColorFilter(Utils.getGreyScaleFilter());

            usernameTextView.setTextColor(mainActivity.getResources().getColor(R.color.colorPrimaryDark));
            if (unreadMessages != 0) {
                messagesCountTextView.setVisibility(View.VISIBLE);
                messagesCountTextView.setText(String.valueOf(unreadMessages));
                messagesCountTextView.setBackground(mainActivity.getResources().getDrawable(R.drawable.user_messages_offline));
                messageTextView.setTypeface(null, Typeface.BOLD);
            } else {
                messagesCountTextView.setVisibility(View.INVISIBLE);
                messageTextView.setTypeface(null, Typeface.NORMAL);
            }
        }

        return convertView;
    }

}
