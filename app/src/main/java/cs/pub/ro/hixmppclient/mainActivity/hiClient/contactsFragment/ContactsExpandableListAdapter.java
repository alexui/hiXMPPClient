package cs.pub.ro.hixmppclient.mainActivity.hiClient.contactsFragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.TreeMap;

import cs.pub.ro.hixmppclient.R;
import cs.pub.ro.hixmppclient.common.XmppUserInfo;
import cs.pub.ro.hixmppclient.general.Constants;
import cs.pub.ro.hixmppclient.general.Utils;
import cs.pub.ro.hixmppclient.mainActivity.MainActivity;

public class ContactsExpandableListAdapter extends BaseExpandableListAdapter {

    private MainActivity mainActivity;
    private TreeMap<String, ArrayList<XmppUserInfo>> contactsMap;
    private ArrayList<String> contactsGroupTitleList;

    private ImageView userAvatarImageView;
    private TextView usersGroupItemTextView;
    private TextView statusModeTextView;
    private TextView statusMessageTextView;
    private TextView statusSeparatorTextView;
    private ImageView onlineImageView;
    private TextView messagesCountTextView;

    public ContactsExpandableListAdapter(ArrayList<String> contactsGroupTitleList, TreeMap<String, ArrayList<XmppUserInfo>> contactsMap) {
        this.contactsGroupTitleList = contactsGroupTitleList;
        this.contactsMap = contactsMap;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return contactsMap.get(contactsGroupTitleList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {

        final XmppUserInfo xmppUserInfo = (XmppUserInfo) getChild(groupPosition, childPosition);
        String contactName = xmppUserInfo.getName();
        String statusMode = xmppUserInfo.getStatusMode();
        String statusMessage = xmppUserInfo.getStatusMessage();
        byte[] xmppUserInfoAvatar = xmppUserInfo.getAvatar();
        LayoutInflater inflater = mainActivity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.contacts_expandable_list_group_item, null);
        }

        userAvatarImageView = (ImageView) convertView.findViewById(R.id.userPhotoImageViewContacts);
        usersGroupItemTextView = (TextView) convertView.findViewById(R.id.usernameTextViewContacts);
        onlineImageView = (ImageView) convertView.findViewById(R.id.onlineImageViewContacts);
        statusModeTextView = (TextView) convertView.findViewById(R.id.statusModeTextViewContacts);
        statusMessageTextView = (TextView) convertView.findViewById(R.id.statusMessageTextViewContacts);
        statusSeparatorTextView = (TextView) convertView.findViewById(R.id.statusSeparatorContacts);
        messagesCountTextView = (TextView) convertView.findViewById(R.id.messagesCountTextViewContacts);

        if (xmppUserInfoAvatar != null) {
            Bitmap bitmapImage = BitmapFactory.decodeByteArray(xmppUserInfoAvatar, 0, xmppUserInfoAvatar.length);
            userAvatarImageView.setImageBitmap(bitmapImage);
        }

        if (contactName.isEmpty())
            contactName = xmppUserInfo.getJid().replaceAll("[@].*", "");
        usersGroupItemTextView.setText(contactName);

        int unreadMessages = xmppUserInfo.getUnreadMessages();
        if (xmppUserInfo.isAvailable()) {

            onlineImageView.setVisibility(View.VISIBLE);
            statusModeTextView.setVisibility(View.VISIBLE);

            usersGroupItemTextView.setTypeface(null, Typeface.BOLD);
            statusModeTextView.setText(statusMode);
            if (Presence.Mode.available.name().equals(statusMode)) {
                onlineImageView.setImageDrawable(mainActivity.getResources().getDrawable(Constants.AVAILABLE_ICON));
            } else if (Presence.Mode.away.name().equals(statusMode)) {
                onlineImageView.setImageDrawable(mainActivity.getResources().getDrawable(Constants.AWAY_ICON));
            } else if (Presence.Mode.dnd.name().equals(statusMode)) {
                onlineImageView.setImageDrawable(mainActivity.getResources().getDrawable(Constants.DND_ICON));
            } else if (Presence.Mode.chat.name().equals(statusMode)) {
                onlineImageView.setImageDrawable(mainActivity.getResources().getDrawable(Constants.CHAT_ICON));
            }

            if (statusMessage != null && !statusMessage.isEmpty()){
                statusSeparatorTextView.setVisibility(View.VISIBLE);
                statusMessageTextView.setVisibility(View.VISIBLE);
                statusMessageTextView.setText(statusMessage);
            }
            if (unreadMessages != 0) {
                onlineImageView.setVisibility(View.INVISIBLE);
                messagesCountTextView.setVisibility(View.VISIBLE);
                messagesCountTextView.setText(String.valueOf(unreadMessages));
                messagesCountTextView.setBackground(mainActivity.getResources().getDrawable(R.drawable.user_messages_online));
            } else {
                onlineImageView.setVisibility(View.VISIBLE);
                messagesCountTextView.setVisibility(View.INVISIBLE);
            }

            userAvatarImageView.clearColorFilter();
        } else {
            userAvatarImageView.setColorFilter(Utils.getGreyScaleFilter());

            onlineImageView.setVisibility(View.INVISIBLE);

            statusModeTextView.setVisibility(View.GONE);
            statusSeparatorTextView.setVisibility(View.GONE);

            if (unreadMessages != 0) {
                messagesCountTextView.setVisibility(View.VISIBLE);
                messagesCountTextView.setText(String.valueOf(unreadMessages));
                messagesCountTextView.setBackground(mainActivity.getResources().getDrawable(R.drawable.user_messages_offline));
            } else {
                messagesCountTextView.setVisibility(View.INVISIBLE);
            }
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return contactsMap.get(contactsGroupTitleList.get(groupPosition)).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return contactsGroupTitleList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return contactsGroupTitleList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String usersGroupTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.contacts_expandable_list_group_header, null);
        }
        TextView usersGroupTitleTextView = (TextView) convertView.findViewById(R.id.usersGroupTitleTextView);
        usersGroupTitleTextView.setText(usersGroupTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
