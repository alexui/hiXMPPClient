package cs.pub.ro.hixmppclient.mainActivity.welcome;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import cs.pub.ro.hixmppclient.R;
import cs.pub.ro.hixmppclient.common.ClientInfo;
import cs.pub.ro.hixmppclient.general.Constants;
import cs.pub.ro.hixmppclient.general.Utils;
import cs.pub.ro.hixmppclient.mainActivity.MainActivity;
import cs.pub.ro.hixmppclient.mainActivity.utils.ServerNameSpinnerAdapter;
import cs.pub.ro.hixmppclient.mainActivity.welcome.waitFragment.WaitFragment;

public class LogInFragment extends android.support.v4.app.Fragment {

    private ImageView logoImageView;

    private ServerNameSpinnerAdapter serverNameSpinnerAdapter;
    private Spinner spinner;

    private EditText hostAddressEditTextLogIn;
    private boolean hostAddressEditTextLogInVisible;

    private EditText userNameEditTextLogIn;
    private EditText passwordEditTextLogIn;
    private Button logInButton;

    private MainActivity mainActivity;
    private FragmentManager mainActivityFragmentManager;
    private FragmentTransaction fragmentTransaction;

    private LogInButtonListener logInButtonListener;
    private AdapterView.OnItemSelectedListener onSpinnerItemSelectedListener;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();

        serverNameSpinnerAdapter = new ServerNameSpinnerAdapter(mainActivity);
        serverNameSpinnerAdapter.addServerNameSpinnerItems();
        onSpinnerItemSelectedListener = new LogInSpinnerItemSelectedListener();

        logInButtonListener = new LogInButtonListener();

        mainActivityFragmentManager = mainActivity.getSupportFragmentManager();

        View layoutView = layoutInflater.inflate(R.layout.fragment_log_in, container, false);

        logoImageView = (ImageView) layoutView.findViewById(R.id.logoImageViewLogIn);
        logoImageView.setImageBitmap(Utils.getRoundedCornerBitmap(
                BitmapFactory.decodeResource(this.getResources(), R.drawable.hand)));

        userNameEditTextLogIn = (EditText) layoutView.findViewById(R.id.usernameEditTextLLogIn);
        passwordEditTextLogIn = (EditText) layoutView.findViewById(R.id.passwordEditTextLogIn);

        hostAddressEditTextLogIn = (EditText) layoutView.findViewById(R.id.hostAddressEditTextLogIn);
        hostAddressEditTextLogIn.setVisibility(View.GONE);

        spinner = (Spinner) layoutView.findViewById(R.id.serverNameSpinnerLogIn);
        spinner.setAdapter(serverNameSpinnerAdapter);
        spinner.setOnItemSelectedListener(onSpinnerItemSelectedListener);

        logInButton = (Button) layoutView.findViewById(R.id.logInButtonProceed);
        logInButton.setOnClickListener(logInButtonListener);

        Log.d(Constants.LOG_TAG, "LogInFragment onCreateViewMethodInvoked()");

        return layoutView;
    }

    private class LogInButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            View selectedSpinnerView = spinner.getSelectedView();

            ClientInfo clientInfo = new ClientInfo(
                    userNameEditTextLogIn.getText().toString(),
                    passwordEditTextLogIn.getText().toString(),
                    ((TextView) selectedSpinnerView.findViewById(R.id.welcomeSelectedItemTextView)).getText().toString()
            );

            if (hostAddressEditTextLogInVisible) {
                String hostAddress = hostAddressEditTextLogIn.getText().toString();
                if (Utils.isStringValidIP(hostAddress))
                    clientInfo.setHostAddress(hostAddress);
                else clientInfo.setHostAddress(null);
            }

            Log.d(Constants.LOG_TAG, "Log In. Client info: " + clientInfo);

            ConnectivityManager cm = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() == null) {
                Toast.makeText(mainActivity, Constants.TOAST_CHECK_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
            }
            else {
                Fragment logInFragment = mainActivityFragmentManager.findFragmentByTag(Constants.FRAGMENT_LOG_IN_TAG);
                WaitFragment waitFragment = new WaitFragment();
                waitFragment.setWaitBackgroundAction(Constants.LOGIN_WAIT_BACKGROUND_ACTION);
                waitFragment.setClientInfo(clientInfo);

                fragmentTransaction = mainActivityFragmentManager.beginTransaction();
                fragmentTransaction.remove(logInFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.add(R.id.mainFrame, waitFragment, Constants.FRAGMENT_WAIT_TAG);
                fragmentTransaction.commit();
            }
        }
    }

    private class LogInSpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (id == Constants.OPENFIRE_ID) {
                hostAddressEditTextLogIn.setVisibility(View.VISIBLE);
                hostAddressEditTextLogInVisible = true;
            }
            else {
                hostAddressEditTextLogIn.setVisibility(View.GONE);
                hostAddressEditTextLogInVisible = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
