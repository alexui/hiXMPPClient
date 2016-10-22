package cs.pub.ro.hixmppclient.mainActivity.hiClient.chat;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cs.pub.ro.hixmppclient.R;
import cs.pub.ro.hixmppclient.mainActivity.MainActivity;

public class XmppChatAdapter extends BaseAdapter {

    private RelativeLayout chatListItemLayout;
    private MainActivity mainActivity;
    private ArrayList<XmppChatMessage> chatMessagesList;

    private TextView chatMessageTextView;
    private ImageView chatMessageImageView;
    private TextView timestampTextView;

    public XmppChatAdapter(MainActivity mainActivity, ArrayList<XmppChatMessage> chatMessagesList) {
        this.mainActivity = mainActivity;
        this.chatMessagesList = chatMessagesList;
    }

    @Override
    public int getCount() {
        if (chatMessagesList == null)
            return 0;
        else
            return chatMessagesList.size();
    }

    @Override
    public Object getItem(int position) {
        return chatMessagesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        XmppChatMessage chatMessage;
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.chat_list_item, null);
        }

        chatMessage = (XmppChatMessage) getItem(position);

        chatListItemLayout = (RelativeLayout) convertView.findViewById(R.id.chatListItemLayout);
        chatMessageTextView = (TextView) convertView.findViewById(R.id.chatMessageTextView);
        chatMessageImageView = (ImageView) convertView.findViewById(R.id.chatMessageImageView);
        timestampTextView = (TextView) convertView.findViewById(R.id.timestampTextView);

        String messageBody = chatMessage.getBody();
        if (chatMessage != null && messageBody != null)
            chatMessageTextView.setText(messageBody);

        if (chatMessage.isClientComposed()) {
            chatListItemLayout.setPadding(0, 0, 30, 0);
            chatListItemLayout.setGravity(Gravity.LEFT);
            chatMessageTextView.setBackground(mainActivity.getResources().getDrawable(R.drawable.chat_message_bubble_sent));
            chatMessageImageView.setVisibility(View.VISIBLE);
        } else {
            chatListItemLayout.setPadding(30, 0, 0, 0);
            chatListItemLayout.setGravity(Gravity.RIGHT);
            chatMessageTextView.setBackground(mainActivity.getResources().getDrawable(R.drawable.chat_message_bubble_received));
            chatMessageImageView.setVisibility(View.GONE);
        }

        return convertView;
    }
}
