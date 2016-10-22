package cs.pub.ro.hixmppclient.mainActivity.hiClient.contactsFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.TreeMap;

import cs.pub.ro.hixmppclient.R;
import cs.pub.ro.hixmppclient.common.XmppUserInfo;
import cs.pub.ro.hixmppclient.general.Constants;
import cs.pub.ro.hixmppclient.mainActivity.MainActivity;
import cs.pub.ro.hixmppclient.mainActivity.hiClient.chat.XmppChatFragment;

public class ContactsViewPagerFragment extends Fragment {

    private MainActivity mainActivity;

    private XmppUserInfo clientInfo;
    private TreeMap<String, ArrayList<XmppUserInfo>> contactsGroupMap;
    private ArrayList<String> contactsGroupTitleList;
    private ContactsFragment contactsFragment;

    private ExpandableListView usersGroupExpandableListView;
    private ContactsExpandableListAdapter contactsExpandableListAdapter;

    private ContactsExpandableListChildClickListener contactsExpandableListChildClickListener;

    public ContactsViewPagerFragment() {
    }

    public void initContactsViewPagerFragment(XmppUserInfo clientInfo, TreeMap<String, ArrayList<XmppUserInfo>> contactsGroupMap, ContactsFragment contactsFragment) {
        this.clientInfo = clientInfo;
        this.contactsGroupMap = contactsGroupMap;
        this.contactsFragment = contactsFragment;

        contactsGroupTitleList = new ArrayList<>();
        contactsGroupTitleList.addAll(contactsGroupMap.keySet());
        contactsExpandableListAdapter = new ContactsExpandableListAdapter(contactsGroupTitleList, contactsGroupMap);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        contactsExpandableListAdapter.setMainActivity(mainActivity);

        contactsExpandableListChildClickListener = new ContactsExpandableListChildClickListener();

        View layoutView = inflater.inflate(R.layout.fragment_contacts_view_pager, container, false);

        usersGroupExpandableListView = (ExpandableListView) layoutView.findViewById(R.id.usersGroupExpandableListView);
        usersGroupExpandableListView.setAdapter(contactsExpandableListAdapter);

        for (int groupPos = 0; groupPos < contactsGroupMap.size(); groupPos++)
            usersGroupExpandableListView.expandGroup(groupPos);

        usersGroupExpandableListView.setOnChildClickListener(contactsExpandableListChildClickListener);

        Log.d(Constants.LOG_TAG, "Contacts Fragment Pager created View");

        return layoutView;
    }

    public void updateContactsMap() {
        contactsExpandableListAdapter.notifyDataSetChanged();
    }

    private class ContactsExpandableListChildClickListener implements ExpandableListView.OnChildClickListener {

        private FragmentManager mainActivityFragmentManager;
        private FragmentTransaction fragmentTransaction;

        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

            mainActivityFragmentManager = mainActivity.getSupportFragmentManager();

            XmppChatFragment xmppChatFragment = new XmppChatFragment();
            XmppUserInfo xmppUserInfo = contactsGroupMap.get(contactsGroupTitleList.get(groupPosition)).get(childPosition);

            xmppUserInfo.setUnreadMessages(0);
            contactsExpandableListAdapter.notifyDataSetChanged();

            xmppChatFragment.initChatFragment(clientInfo, xmppUserInfo);

            fragmentTransaction = mainActivityFragmentManager.beginTransaction();
            fragmentTransaction.remove(contactsFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.add(R.id.hi_body, xmppChatFragment, Constants.FRAGMENT_CHAT_TAG);
            fragmentTransaction.commit();

            return false;
        }
    }
}