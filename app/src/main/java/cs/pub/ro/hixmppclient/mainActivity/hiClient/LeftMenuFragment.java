package cs.pub.ro.hixmppclient.mainActivity.hiClient;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.View;

import java.util.List;
import java.util.ArrayList;

import cs.pub.ro.hixmppclient.R;

public class LeftMenuFragment extends Fragment {

    private static String[] menuItems = null;

    private View leftMenuFragment;
    private DrawerLayout leftMenuDrawerLayout;
    private RecyclerView recyclerView;
    private LeftMenuAdapter leftMenuAdapter;
    private LeftFragmentMenuListener leftFragmentMenuListener;
    private RecyclerTouchListener recyclerTouchListener;
    private LeftMenuItemClickListener leftMenuItemClickListener;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    public LeftMenuFragment() {

    }

    public void setLeftFragmentMenuListener(LeftFragmentMenuListener listener) {
        this.leftFragmentMenuListener = listener;
    }

    public static List<MenuItem> getMenuItemsList() {
        List<MenuItem> menuItemsList = new ArrayList<>();
        for (int i = 0; i < menuItems.length; i++) {
            MenuItem navItem = new MenuItem();
            navItem.setTitle(menuItems[i]);
            menuItemsList.add(navItem);
        }
        return menuItemsList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuItems = getActivity().getResources().getStringArray(R.array.nav_drawer_items);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_left_menu, container, false);
        recyclerView = (RecyclerView) layoutView.findViewById(R.id.leftMenuLRecyclerView);

        leftMenuAdapter = new LeftMenuAdapter(getActivity(), getMenuItemsList());
        recyclerView.setAdapter(leftMenuAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        leftMenuItemClickListener = new LeftMenuItemClickListener();
        recyclerTouchListener = new RecyclerTouchListener(getActivity(), recyclerView, leftMenuItemClickListener);
        recyclerView.addOnItemTouchListener(recyclerTouchListener);

        return layoutView;
    }


    public void setUpFragmentMenu(final Toolbar toolbar) {

        leftMenuFragment = getActivity().findViewById(R.id.left_menu_fragment);
        leftMenuDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.hi_drawer_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), leftMenuDrawerLayout,
                toolbar, R.string.left_menu_drawer_open, R.string.left_menu_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };


        leftMenuDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        leftMenuDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                actionBarDrawerToggle.syncState();
            }
        });
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    private class LeftMenuItemClickListener implements ClickListener {

        @Override
        public void onClick(View view, int position) {
            leftFragmentMenuListener.onMenuItemSelected(view, position);
            leftMenuDrawerLayout.closeDrawer(LeftMenuFragment.this.leftMenuFragment);
        }

        @Override
        public void onLongClick(View view, int position) {

        }
    }

    public interface LeftFragmentMenuListener {
        void onMenuItemSelected(View view, int position);
    }
}