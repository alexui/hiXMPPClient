package cs.pub.ro.hixmppclient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class HiActivity extends AppCompatActivity implements LeftMenuFragment.LeftFragmentMenuListener {

    private Toolbar toolbar;
    private LeftMenuFragment leftMenuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hi);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        leftMenuFragment = (LeftMenuFragment)
                getSupportFragmentManager().findFragmentById(R.id.left_menu_fragment);
        leftMenuFragment.setUpFragmentMenu(toolbar);
        leftMenuFragment.setLeftFragmentMenuListener(this);

        itemOptionFragmentDisplay(0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.app_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMenuItemSelected(View view, int position) {
        itemOptionFragmentDisplay(position);
    }

    private void itemOptionFragmentDisplay(int position) {
        Fragment fragment = null;
        String title = null;
        switch (position) {
            case 0:
                fragment = new MessagesFragment();
                title = getString(R.string.title_messages);
                break;
            case 1:
                fragment = new ContactsFragment();
                title = getString(R.string.title_contacts);
                break;
            case 2:
                fragment = new GroupChatsFragment();
                title = getString(R.string.title_group_chats);
                break;
            case 3:
                fragment = new StatusFragment();
                title = getString(R.string.title_status);
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //Replace an existing fragment that was added to a container
            fragmentTransaction.replace(R.id.hi_body, fragment);
            fragmentTransaction.commit();

            getSupportActionBar().setTitle(title);
        }
    }
}
