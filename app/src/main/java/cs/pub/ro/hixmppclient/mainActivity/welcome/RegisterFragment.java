package cs.pub.ro.hixmppclient.mainActivity.welcome;

import android.content.Context;
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

import java.util.ArrayList;

import cs.pub.ro.hixmppclient.R;
import cs.pub.ro.hixmppclient.common.ClientInfo;
import cs.pub.ro.hixmppclient.general.Constants;
import cs.pub.ro.hixmppclient.general.Utils;
import cs.pub.ro.hixmppclient.mainActivity.MainActivity;
import cs.pub.ro.hixmppclient.mainActivity.utils.ServerNameSpinnerAdapter;
import cs.pub.ro.hixmppclient.mainActivity.welcome.waitFragment.WaitFragment;

public class RegisterFragment extends android.support.v4.app.Fragment {

    private ServerNameSpinnerAdapter serverNameSpinnerAdapter;
    private EditText userNameEditTextRegister;
    private EditText passwordEditTextRegister;

    private Spinner spinner;
    private View selectedSpinnerView;
    private long selectedItemId;

    private EditText hostAddressEditTextRegister;
    private boolean hostAddressEditTextRegisterVisible;

    private ImageView optionalInfoIconImageView;
    private boolean optionalInfoViewVisible;

    private TextView emailTextView;
    private EditText emailEditText;
    private TextView phoneTextView;
    private EditText phoneEditText;

    private ImageView previousOptionImageView, nextOptionImageView;
    private Button registerButton;
    private MainActivity mainActivity;

    private FragmentManager mainActivityFragmentManager;
    private FragmentTransaction fragmentTransaction;
    private OptionalInfoIconImageViewListener optionalInfoIconImageViewListener;
    private RegisterButtonListener registerButtonListener;
    private AdapterView.OnItemSelectedListener onSpinnerItemSelectedListener;

    public RegisterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {

        registerButtonListener = new RegisterButtonListener();
        onSpinnerItemSelectedListener = new RegisterSpinnerItemSelectedListener();

        mainActivity = (MainActivity) getActivity();
        mainActivityFragmentManager = mainActivity.getSupportFragmentManager();

        View layoutView = layoutInflater.inflate(R.layout.fragment_register, container, false);

        userNameEditTextRegister = (EditText) layoutView.findViewById(R.id.usernameEditTextLRegister);
        passwordEditTextRegister = (EditText) layoutView.findViewById(R.id.passwordEditTextRegister);
        hostAddressEditTextRegister = (EditText) layoutView.findViewById(R.id.hostAddressEditTextRegister);

        serverNameSpinnerAdapter = new ServerNameSpinnerAdapter(getActivity());
        serverNameSpinnerAdapter.addServerNameSpinnerItems();

        spinner = (Spinner) layoutView.findViewById(R.id.serverNameSpinnerRegister);
        spinner.setAdapter(serverNameSpinnerAdapter);
        spinner.setOnItemSelectedListener(onSpinnerItemSelectedListener);

        emailTextView = (TextView) layoutView.findViewById(R.id.emailTextViewRegister);
        emailEditText = (EditText) layoutView.findViewById(R.id.emailEditTextLRegister);

        phoneTextView = (TextView) layoutView.findViewById(R.id.phoneNumberTextViewRegister);
        phoneEditText = (EditText) layoutView.findViewById(R.id.phoneNumberEditTextLRegister);

        optionalInfoIconImageViewListener = new OptionalInfoIconImageViewListener();

        optionalInfoIconImageView = (ImageView) layoutView.findViewById(R.id.optionalInfoIconImageView);
        optionalInfoIconImageView.setOnClickListener(optionalInfoIconImageViewListener);
        previousOptionImageView = (ImageView) layoutView.findViewById((R.id.previousOptionImageView));
        previousOptionImageView.setOnClickListener(optionalInfoIconImageViewListener);
        nextOptionImageView = (ImageView) layoutView.findViewById((R.id.nextOptionImageView));
        nextOptionImageView.setOnClickListener(optionalInfoIconImageViewListener);

        registerButton = (Button) layoutView.findViewById(R.id.registerButtonProceed);
        registerButton.setOnClickListener(registerButtonListener);

        return layoutView;
    }

    private class OptionalInfoIconImageViewListener implements View.OnClickListener {

        private ArrayList<OptionalInfoView> optionalInfoViewList;
        private int currentOptionalInfoView;

        private class OptionalInfoView {
            public TextView optionalInfoTextView;

            public EditText optionalInfoEditText;

            public OptionalInfoView(TextView optionalInfoTextView, EditText optionalInfoEditText) {
                this.optionalInfoTextView = optionalInfoTextView;
                this.optionalInfoEditText = optionalInfoEditText;
            }

        }

        public OptionalInfoIconImageViewListener() {
            currentOptionalInfoView = 0;
            optionalInfoViewVisible = false;

            optionalInfoViewList = new ArrayList<>();
            optionalInfoViewList.add(new OptionalInfoView(emailTextView, emailEditText));
            optionalInfoViewList.add(new OptionalInfoView(phoneTextView, phoneEditText));
        }

        @Override
        public void onClick(View v) {
            ImageView thisImageView = (ImageView) v;
            switch (v.getId()) {
                case R.id.optionalInfoIconImageView:
                    if (!optionalInfoViewVisible) {
                        OptionalInfoView optionalInfoView = optionalInfoViewList.get(currentOptionalInfoView);
                        optionalInfoView.optionalInfoTextView.setVisibility(View.VISIBLE);
                        optionalInfoView.optionalInfoEditText.setVisibility(View.VISIBLE);
                        thisImageView.setImageDrawable(mainActivity.getResources().getDrawable(R.drawable.upside_triangle));

                        previousOptionImageView.setVisibility(View.VISIBLE);
                        nextOptionImageView.setVisibility(View.VISIBLE);

                        if (hostAddressEditTextRegisterVisible) {
                            hostAddressEditTextRegister.setVisibility(View.GONE);
                            hostAddressEditTextRegisterVisible = false;
                        }
                    } else {
                        OptionalInfoView optionalInfoView = optionalInfoViewList.get(currentOptionalInfoView);
                        optionalInfoView.optionalInfoTextView.setVisibility(View.GONE);
                        optionalInfoView.optionalInfoEditText.setVisibility(View.GONE);
                        thisImageView.setImageDrawable(mainActivity.getResources().getDrawable(R.drawable.downside_triangle));

                        previousOptionImageView.setVisibility(View.GONE);
                        nextOptionImageView.setVisibility(View.GONE);

                        if (selectedItemId == Constants.OPENFIRE_ID) {
                            hostAddressEditTextRegister.setVisibility(View.VISIBLE);
                            hostAddressEditTextRegisterVisible = true;
                        }
                    }
                    optionalInfoViewVisible = !optionalInfoViewVisible;

                    break;
                case R.id.previousOptionImageView:
                    if (currentOptionalInfoView > 0) {
                        OptionalInfoView optionalInfoView = optionalInfoViewList.get(currentOptionalInfoView);
                        optionalInfoView.optionalInfoTextView.setVisibility(View.GONE);
                        optionalInfoView.optionalInfoEditText.setVisibility(View.GONE);
                        currentOptionalInfoView--;
                        optionalInfoView = optionalInfoViewList.get(currentOptionalInfoView);
                        optionalInfoView.optionalInfoTextView.setVisibility(View.VISIBLE);
                        optionalInfoView.optionalInfoEditText.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.nextOptionImageView:
                    if (currentOptionalInfoView < optionalInfoViewList.size() - 1) {
                        OptionalInfoView optionalInfoView = optionalInfoViewList.get(currentOptionalInfoView);
                        optionalInfoView.optionalInfoTextView.setVisibility(View.GONE);
                        optionalInfoView.optionalInfoEditText.setVisibility(View.GONE);
                        currentOptionalInfoView++;
                        optionalInfoView = optionalInfoViewList.get(currentOptionalInfoView);
                        optionalInfoView.optionalInfoTextView.setVisibility(View.VISIBLE);
                        optionalInfoView.optionalInfoEditText.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    }

    private class RegisterButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            ClientInfo clientInfo = new ClientInfo(
                    userNameEditTextRegister.getText().toString(),
                    passwordEditTextRegister.getText().toString(),
                    ((TextView) selectedSpinnerView.findViewById(R.id.welcomeSelectedItemTextView)).getText().toString()
            );

            if (hostAddressEditTextRegisterVisible) {
                String hostAddress = hostAddressEditTextRegister.getText().toString();
                if (Utils.isStringValidIP(hostAddress))
                    clientInfo.setHostAddress(hostAddress);
            }

            Log.d(Constants.LOG_TAG, "Register. Client info: " + clientInfo);

            ConnectivityManager cm = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() == null) {
                Toast.makeText(mainActivity, Constants.TOAST_CHECK_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
            }
            else {
                Fragment registerFragment = mainActivityFragmentManager.findFragmentByTag(Constants.FRAGMENT_REGISTER_TAG);
                WaitFragment waitFragment = new WaitFragment();
                waitFragment.setWaitBackgroundAction(Constants.REGISTER_WAIT_BACKGROUND_ACTION);
                waitFragment.setClientInfo(clientInfo);

                fragmentTransaction = mainActivityFragmentManager.beginTransaction();
                fragmentTransaction.remove(registerFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.add(R.id.mainFrame, waitFragment, Constants.FRAGMENT_WAIT_TAG);
                fragmentTransaction.commit();
            }
        }
    }

    private class RegisterSpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedSpinnerView = view;
            selectedItemId = id;
            if (id == Constants.OPENFIRE_ID) {
                if (!optionalInfoViewVisible) {
                    hostAddressEditTextRegister.setVisibility(View.VISIBLE);
                    hostAddressEditTextRegisterVisible = true;
                }
            } else
                hostAddressEditTextRegister.setVisibility(View.GONE);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
