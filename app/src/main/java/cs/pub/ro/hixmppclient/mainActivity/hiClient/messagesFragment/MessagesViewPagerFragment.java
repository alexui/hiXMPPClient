package cs.pub.ro.hixmppclient.mainActivity.hiClient.messagesFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import cs.pub.ro.hixmppclient.R;
import cs.pub.ro.hixmppclient.common.XmppUserInfo;
import cs.pub.ro.hixmppclient.general.Constants;
import cs.pub.ro.hixmppclient.mainActivity.MainActivity;
import cs.pub.ro.hixmppclient.mainActivity.hiClient.chat.XmppChatFragment;

public class MessagesViewPagerFragment extends Fragment {

    private MainActivity mainActivity;

    private XmppUserInfo clientInfo;
    private ArrayList<XmppUserInfo> userInfoList;
    private MessagesFragment messagesFragment;

    private ListView usersMessagesListView;
    private MessagesListAdapter messagesListAdapter;

    private MessagesListChildClickListener messagesListChildClickListener;

    public MessagesViewPagerFragment() {
    }

    public void initContactsViewPagerFragment(XmppUserInfo clientInfo, ArrayList<XmppUserInfo> userInfoList, MessagesFragment messagesFragment) {
        this.clientInfo = clientInfo;
        this.userInfoList = userInfoList;
        this.messagesFragment = messagesFragment;
        messagesListAdapter = new MessagesListAdapter(userInfoList);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        messagesListAdapter.setMainActivity(mainActivity);

        messagesListChildClickListener = new MessagesListChildClickListener();

        View layoutView = inflater.inflate(R.layout.fragment_messages_view_pager, container, false);

        usersMessagesListView = (ListView) layoutView.findViewById(R.id.messagesListView);
        usersMessagesListView.setAdapter(messagesListAdapter);

        usersMessagesListView.setOnItemClickListener(messagesListChildClickListener);

        Log.d(Constants.LOG_TAG, "Messages Fragment Pager created View");

        return layoutView;
    }

    public void updateContactsMap() {
        messagesListAdapter.notifyDataSetChanged();
    }

    private class MessagesListChildClickListener implements ListView.OnItemClickListener {

        private FragmentManager mainActivityFragmentManager;
        private FragmentTransaction fragmentTransaction;

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            mainActivityFragmentManager = mainActivity.getSupportFragmentManager();

            XmppChatFragment xmppChatFragment = new XmppChatFragment();
            XmppUserInfo xmppUserInfo = userInfoList.get(position);

            xmppUserInfo.setUnreadMessages(0);
            messagesListAdapter.notifyDataSetChanged();

            xmppChatFragment.initChatFragment(clientInfo, xmppUserInfo);

            fragmentTransaction = mainActivityFragmentManager.beginTransaction();
            fragmentTransaction.remove(messagesFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.add(R.id.hi_body, xmppChatFragment, Constants.FRAGMENT_CHAT_TAG);
            fragmentTransaction.commit();
        }
    }
}

