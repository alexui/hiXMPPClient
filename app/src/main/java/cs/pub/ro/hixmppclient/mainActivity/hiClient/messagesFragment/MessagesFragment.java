package cs.pub.ro.hixmppclient.mainActivity.hiClient.messagesFragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import cs.pub.ro.hixmppclient.R;
import cs.pub.ro.hixmppclient.common.XmppUserInfo;
import cs.pub.ro.hixmppclient.general.Constants;
import cs.pub.ro.hixmppclient.mainActivity.MainActivity;
import cs.pub.ro.hixmppclient.mainActivity.hiClient.ViewPagerAdapter;

public class MessagesFragment extends Fragment {

    private MainActivity mainActivity;

    private TabLayout clientsTabLayout;
    private ViewPager clientMessagesViewPager;

    private ViewPagerAdapter viewPagerAdapter;
    private HashMap<String, MessagesViewPagerFragment> viewPagerAdapterFragmentMap;

    public MessagesFragment() {
        viewPagerAdapterFragmentMap = new HashMap<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();

        View layoutView = layoutInflater.inflate(R.layout.fragment_messages, container, false);

        clientMessagesViewPager = (ViewPager) layoutView.findViewById(R.id.messagesFragmentViewPager);
        clientsTabLayout = (TabLayout) layoutView.findViewById(R.id.messagesFragmentTabLayout);

        viewPagerAdapter = new ViewPagerAdapter(mainActivity.getSupportFragmentManager());

        clientMessagesViewPager.setAdapter(viewPagerAdapter);
        clientsTabLayout.setupWithViewPager(clientMessagesViewPager);

        for (String viewPagerTitle : viewPagerAdapterFragmentMap.keySet()) {
            MessagesViewPagerFragment messagesViewPagerFragment = viewPagerAdapterFragmentMap.get(viewPagerTitle);
            viewPagerAdapter.addFragment(messagesViewPagerFragment, viewPagerTitle);
        }
        viewPagerAdapter.notifyDataSetChanged();

        Log.d(Constants.LOG_TAG, "Messages Fragment created View");

        return layoutView;
    }

    public void addFragmentToTabLayout(XmppUserInfo clientInfo, ArrayList<XmppUserInfo> userInfoList) {
        MessagesViewPagerFragment messagesViewPagerFragment = new MessagesViewPagerFragment();
        messagesViewPagerFragment.initContactsViewPagerFragment(clientInfo, userInfoList, this);
        viewPagerAdapterFragmentMap.put(clientInfo.getJid(), messagesViewPagerFragment);
    }

    public void updateFragmentInTabLayout(String clientJid) {
        MessagesViewPagerFragment messagesViewPagerFragment = viewPagerAdapterFragmentMap.get(clientJid);
        messagesViewPagerFragment.updateContactsMap();
    }

    public void removeFragmentFromTabLayout(String clientJid) {
        viewPagerAdapterFragmentMap.remove(clientJid);
    }

    private void removeViewPagerFragment() {
        for (String viewPagerTitle : viewPagerAdapterFragmentMap.keySet())
            viewPagerAdapter.removeFragment(viewPagerAdapterFragmentMap.get(viewPagerTitle), viewPagerTitle);
        viewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        removeViewPagerFragment();

        Log.d(Constants.LOG_TAG, "Messages Fragment destroyed View");
    }
}
