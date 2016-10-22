package cs.pub.ro.hixmppclient.mainActivity.hiClient.statusFragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

import cs.pub.ro.hixmppclient.R;
import cs.pub.ro.hixmppclient.common.XmppUserInfo;
import cs.pub.ro.hixmppclient.general.Constants;
import cs.pub.ro.hixmppclient.mainActivity.MainActivity;
import cs.pub.ro.hixmppclient.mainActivity.hiClient.ViewPagerAdapter;

public class StatusFragment extends Fragment {

    private MainActivity mainActivity;

    private TabLayout clientsTabLayout;
    private ViewPager clientStatusViewPager;

    private ViewPagerAdapter viewPagerAdapter;
    private HashMap<String, StatusViewPagerFragment> viewPagerAdapterFragmentMap;

    public StatusFragment() {
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

        View layoutView = layoutInflater.inflate(R.layout.fragment_status, container, false);

        clientStatusViewPager = (ViewPager) layoutView.findViewById(R.id.statusFragmentViewPager);
        clientsTabLayout = (TabLayout) layoutView.findViewById(R.id.statusFragmentTabLayout);

        viewPagerAdapter = new ViewPagerAdapter(mainActivity.getSupportFragmentManager());

        clientStatusViewPager.setAdapter(viewPagerAdapter);
        clientsTabLayout.setupWithViewPager(clientStatusViewPager);

        for (String viewPagerTitle : viewPagerAdapterFragmentMap.keySet()) {
            StatusViewPagerFragment statusViewPagerFragment = viewPagerAdapterFragmentMap.get(viewPagerTitle);
            viewPagerAdapter.addFragment(statusViewPagerFragment, viewPagerTitle);
        }
        viewPagerAdapter.notifyDataSetChanged();

        Log.d(Constants.LOG_TAG, "Status Fragment created View");

        return layoutView;
    }

    public void addFragmentToTabLayout(String clientJid, XmppUserInfo clientInfo) {
        StatusViewPagerFragment statusViewPagerFragment = new StatusViewPagerFragment();
        statusViewPagerFragment.initContactsViewPagerFragment(clientJid, clientInfo, this);
        viewPagerAdapterFragmentMap.put(clientJid, statusViewPagerFragment);
    }

    public void removeFragmentFromTabLayout(String clientJid) {
        removeViewPagerFragment(clientJid);
        viewPagerAdapterFragmentMap.remove(clientJid);
    }

    private void removeViewPagerFragment() {
        for (String viewPagerTitle : viewPagerAdapterFragmentMap.keySet())
            viewPagerAdapter.removeFragment(viewPagerAdapterFragmentMap.get(viewPagerTitle), viewPagerTitle);
        viewPagerAdapter.notifyDataSetChanged();
    }

    private void removeViewPagerFragment(String clientJid) {
        for (String viewPagerTitle : viewPagerAdapterFragmentMap.keySet()) {
            if (clientJid.equals(viewPagerTitle))
                viewPagerAdapter.removeFragment(viewPagerAdapterFragmentMap.get(viewPagerTitle), viewPagerTitle);
        }
        viewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        removeViewPagerFragment();

        Log.d(Constants.LOG_TAG, "Status Fragment destroyed View");
    }

}
