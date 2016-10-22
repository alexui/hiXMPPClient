package cs.pub.ro.hixmppclient.mainActivity.hiClient;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> titleFragmentList = new ArrayList<>();
    private FragmentManager fragmentManager;

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
        fragmentManager = manager;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return titleFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        titleFragmentList.add(title);
    }

    public void removeFragment(Fragment fragment, String title) {
        fragmentList.remove(fragment);
        fragmentManager.beginTransaction().remove(fragment).commit();
        titleFragmentList.remove(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleFragmentList.get(position);
    }

    public List<Fragment> getMessagesFragmentList() {
        return fragmentList;
    }
}

