package cs.pub.ro.hixmppclient.mainActivity.hiClient.contactsFragment;


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
import java.util.TreeMap;

import cs.pub.ro.hixmppclient.R;
import cs.pub.ro.hixmppclient.common.XmppUserInfo;
import cs.pub.ro.hixmppclient.general.Constants;
import cs.pub.ro.hixmppclient.mainActivity.MainActivity;
import cs.pub.ro.hixmppclient.mainActivity.hiClient.ViewPagerAdapter;

public class ContactsFragment extends Fragment {

    private MainActivity mainActivity;

    private TabLayout clientsTabLayout;
    private ViewPager clientContactsViewPager;

    private ViewPagerAdapter viewPagerAdapter;
    private HashMap<String, ContactsViewPagerFragment> viewPagerAdapterFragmentMap;

    public ContactsFragment() {
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

        View layoutView = layoutInflater.inflate(R.layout.fragment_contacts, container, false);

        clientContactsViewPager = (ViewPager) layoutView.findViewById(R.id.contactsFragmentViewPager);
        clientsTabLayout = (TabLayout) layoutView.findViewById(R.id.contactsFragmentTabLayout);

        viewPagerAdapter = new ViewPagerAdapter(mainActivity.getSupportFragmentManager());

        clientContactsViewPager.setAdapter(viewPagerAdapter);
        clientsTabLayout.setupWithViewPager(clientContactsViewPager);

        for (String viewPagerTitle : viewPagerAdapterFragmentMap.keySet()) {
            ContactsViewPagerFragment contactsViewPagerFragment = viewPagerAdapterFragmentMap.get(viewPagerTitle);
            viewPagerAdapter.addFragment(contactsViewPagerFragment, viewPagerTitle);
        }
        viewPagerAdapter.notifyDataSetChanged();

        Log.d(Constants.LOG_TAG, "Contacts Fragment created View");

        return layoutView;
    }

    public void addFragmentToTabLayout(XmppUserInfo clientInfo, TreeMap<String, ArrayList<XmppUserInfo>> userInfoMap) {
        ContactsViewPagerFragment contactsViewPagerFragment = new ContactsViewPagerFragment();
        contactsViewPagerFragment.initContactsViewPagerFragment(clientInfo, userInfoMap, this);
        viewPagerAdapterFragmentMap.put(clientInfo.getJid(), contactsViewPagerFragment);
    }

    public void updateFragmentInTabLayout(String clientJid) {
        ContactsViewPagerFragment contactsViewPagerFragment = viewPagerAdapterFragmentMap.get(clientJid);
        contactsViewPagerFragment.updateContactsMap();
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

        Log.d(Constants.LOG_TAG, "Contacts Fragment destroyed View");
    }
}
