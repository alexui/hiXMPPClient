package cs.pub.ro.hixmppclient.mainActivity.hiClient.statusFragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import cs.pub.ro.hixmppclient.R;
import cs.pub.ro.hixmppclient.common.XmppUserInfo;
import cs.pub.ro.hixmppclient.general.Constants;
import cs.pub.ro.hixmppclient.mainActivity.MainActivity;
import cs.pub.ro.hixmppclient.smack.xmppService.XmppStartedService;

public class StatusViewPagerFragment extends Fragment {

    private MainActivity mainActivity;
    private XmppStartedService startedService;

    private String clientJid;
    private XmppUserInfo clientInfo;
    private StatusFragment statusFragment;

    private ImageView userPhotoImageView;
    private TextView usernameTextView;
    private TextView statusModeTextView;
    private TextView statusMessageTextView;
    private TextView statusSeparatorTextView;
    private Spinner statusSpinner;
    private EditText statusMessageEditText;
    private Button updateStatusButton;
    private Button logOffButton;

    private StatusSpinnerAdapter statusSpinnerAdapter;
    private StatusButtonListener statusButtonListener;

    public StatusViewPagerFragment() {
    }

    public void initContactsViewPagerFragment(String clientJid, XmppUserInfo clientInfo, StatusFragment messagesFragment) {
        this.clientJid = clientJid;
        this.clientInfo = clientInfo;
        this.statusFragment = messagesFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        startedService = mainActivity.getStartedService();

        statusButtonListener = new StatusButtonListener();

        byte[] xmppUserInfoAvatar = clientInfo.getAvatar();
        String contactName = clientInfo.getName();
        String statusMessage = clientInfo.getStatusMessage();
        statusSpinnerAdapter = new StatusSpinnerAdapter(mainActivity);
        statusSpinnerAdapter.addStatusModeSpinnerItems();

        View layoutView = layoutInflater.inflate(R.layout.fragment_status_view_pager, container, false);

        userPhotoImageView = (ImageView) layoutView.findViewById(R.id.userPhotoImageViewStatus);
        if (xmppUserInfoAvatar != null) {
            Bitmap bitmapImage = BitmapFactory.decodeByteArray(xmppUserInfoAvatar, 0, xmppUserInfoAvatar.length);
            userPhotoImageView.setImageBitmap(bitmapImage);
        }

        usernameTextView = (TextView) layoutView.findViewById(R.id.usernameTextViewStatus);
        if (contactName.isEmpty())
            contactName = clientInfo.getJid().replaceAll("[@].*", "");
        usernameTextView.setText(contactName);

        statusModeTextView = (TextView) layoutView.findViewById(R.id.statusModeTextViewStatus);
        statusSeparatorTextView = (TextView) layoutView.findViewById(R.id.statusSeparatorStatus);
        statusMessageTextView = (TextView) layoutView.findViewById(R.id.statusMessageTextViewStatus);
        statusModeTextView.setText(clientInfo.getStatusMode());

        if (statusMessage != null && !statusMessage.isEmpty()) {
            statusSeparatorTextView.setVisibility(View.VISIBLE);
            statusMessageTextView.setVisibility(View.VISIBLE);
            statusMessageTextView.setText(statusMessage);
        }

        statusMessageEditText = (EditText) layoutView.findViewById(R.id.statusMessageEditTextStatus);
        updateStatusButton = (Button) layoutView.findViewById(R.id.updateStatusButtonStatus);
        logOffButton = (Button) layoutView.findViewById(R.id.logoutButtonStatus);

        updateStatusButton.setOnClickListener(statusButtonListener);
        logOffButton.setOnClickListener(statusButtonListener);

        statusSpinner = (Spinner) layoutView.findViewById(R.id.statusSpinner);
        statusSpinner.setAdapter(statusSpinnerAdapter);

        Log.d(Constants.LOG_TAG, "Status Fragment Pager created View");

        return layoutView;
    }

    private class StatusButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            View selectedSpinnerView = statusSpinner.getSelectedView();
            String statusMessage;
            String statusMode;

            switch (v.getId()) {
                case R.id.updateStatusButtonStatus:
                    statusMessage = statusMessageEditText.getText().toString();
                    statusMode = ((TextView) selectedSpinnerView.findViewById(R.id.statusSelectedItemTextView)).getText().toString();
                    startedService.sendPresenceStatus(
                            clientJid,
                            statusMode,
                            statusMessage
                            );

                    statusModeTextView.setText(statusMode);
                    clientInfo.setStatusMode(statusMode);
                    clientInfo.setStatusMessage(statusMessage);
                    if (!statusMessage.isEmpty()) {
                        statusSeparatorTextView.setVisibility(View.VISIBLE);
                        statusMessageTextView.setVisibility(View.VISIBLE);
                        statusMessageTextView.setText(statusMessage);
                    }
                    else {
                        statusSeparatorTextView.setVisibility(View.INVISIBLE);
                        statusMessageTextView.setVisibility(View.INVISIBLE);
                    }
                    statusMessageEditText.setText("");
                    break;
                case R.id.logoutButtonStatus:
                    mainActivity.disconnectClient(clientJid);
                    try {
                        StatusViewPagerFragment.this.finalize();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    break;
            }
        }
    }

}

