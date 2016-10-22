package cs.pub.ro.hixmppclient.mainActivity.welcome.waitFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cs.pub.ro.hixmppclient.R;
import cs.pub.ro.hixmppclient.common.BackgroundTaskResult;
import cs.pub.ro.hixmppclient.common.ClientInfo;
import cs.pub.ro.hixmppclient.general.Constants;
import cs.pub.ro.hixmppclient.general.Utils;
import cs.pub.ro.hixmppclient.mainActivity.MainActivity;
import cs.pub.ro.hixmppclient.smack.xmppService.XmppStartedService;

public class WaitFragment extends android.support.v4.app.Fragment {

    private MainActivity mainActivity;
    private XmppStartedService startedService;
    private FragmentManager fragmentManager;

    private int waitBackgroundAction;
    private boolean retryRunningTask;

    private ClientInfo clientInfo;
    private LogoImageViewTouchListener logoImageViewTouchListener;

    private String actionRegister;
    private String actionLogIn;
    private WaitFragmentBroadcastReceiver waitFragmentBroadcastReceiver;
    private IntentFilter intentFilter;

    private WaitProgressBar waitProgressBar;
    private ImageView logoImageView;
    private TextView infoMessageTextView;
    private TextView retryTextViewWait;

    public void setWaitBackgroundAction(int waitBackgroundAction) {
        this.waitBackgroundAction = waitBackgroundAction;
    }

    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionLogIn = cs.pub.ro.hixmppclient.general.Constants.ACTION_LOG_IN + clientInfo.getJid();
        actionRegister = cs.pub.ro.hixmppclient.general.Constants.ACTION_REGISTER + clientInfo.getJid();

        waitFragmentBroadcastReceiver = new WaitFragmentBroadcastReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(actionLogIn);
        intentFilter.addAction(actionRegister);
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity.registerReceiver(waitFragmentBroadcastReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        mainActivity.unregisterReceiver(waitFragmentBroadcastReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();
        startedService = mainActivity.getStartedService();
        logoImageViewTouchListener = new LogoImageViewTouchListener();

        fragmentManager = mainActivity.getSupportFragmentManager();

        View layoutView = layoutInflater.inflate(R.layout.fragment_wait, container, false);

        waitProgressBar = (WaitProgressBar) layoutView.findViewById(R.id.dotsProgressBar);
        waitProgressBar.setDotsCount(4);

        infoMessageTextView = (TextView) layoutView.findViewById(R.id.infoMessageTextView);
        infoMessageTextView.setVisibility(View.INVISIBLE);

        retryTextViewWait = (TextView) layoutView.findViewById(R.id.retryTextViewWait);
        retryTextViewWait.setVisibility(View.INVISIBLE);

        logoImageView = (ImageView) layoutView.findViewById(R.id.logoImageViewWait);
        logoImageView.setImageBitmap(Utils.getRoundedCornerBitmap(
                BitmapFactory.decodeResource(this.getResources(), R.drawable.hand)));
        logoImageView.setOnTouchListener(logoImageViewTouchListener);

        startWaitBackgroundAction();

        return layoutView;
    }

    private void startWaitBackgroundAction() {

        retryRunningTask = false;
        retryTextViewWait.setVisibility(View.INVISIBLE);
        infoMessageTextView.setVisibility(View.INVISIBLE);
        waitProgressBar.setVisibility(View.VISIBLE);
        waitProgressBar.start();

        switch (waitBackgroundAction) {
            case Constants.LOGIN_WAIT_BACKGROUND_ACTION:
                startedService.logIn(clientInfo);

                break;
            case Constants.REGISTER_WAIT_BACKGROUND_ACTION:
                startedService.register(clientInfo);
                break;
        }
    }

    private BackgroundTaskResult stopWaitBackgroundAction(Intent intent) {
        BackgroundTaskResult backgroundTaskResult = intent.getParcelableExtra(cs.pub.ro.hixmppclient.general.Constants.TAG_BROADCAST_DATA);
        waitProgressBar.stop();
        waitProgressBar.setVisibility(View.INVISIBLE);
        infoMessageTextView.setVisibility(View.VISIBLE);
        return backgroundTaskResult;
    }

    private class WaitFragmentBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BackgroundTaskResult backgroundTaskResult;

            if (waitBackgroundAction == Constants.LOGIN_WAIT_BACKGROUND_ACTION && actionLogIn.equals(action)) {

                backgroundTaskResult = stopWaitBackgroundAction(intent);
                if (backgroundTaskResult.isSuccess()) {
                    mainActivity.createHiLayout();
                } else {
                    retryTextViewWait.setVisibility(View.VISIBLE);
                    retryRunningTask = true;
                }
                infoMessageTextView.setText(backgroundTaskResult.getMessage());
            }

            if (waitBackgroundAction == Constants.REGISTER_WAIT_BACKGROUND_ACTION && actionRegister.equals(action)) {
                backgroundTaskResult = stopWaitBackgroundAction(intent);
                if (backgroundTaskResult.isSuccess()) {
                    fragmentManager.popBackStack();
                } else {
                    retryTextViewWait.setVisibility(View.VISIBLE);
                    retryRunningTask = true;
                }
                infoMessageTextView.setText(backgroundTaskResult.getMessage());
            }
        }
    }

    private class LogoImageViewTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ImageView handImageView = (ImageView) v;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    Utils.handleImageViewActionDown(handImageView, 10);

                    if (retryRunningTask) {
                        startWaitBackgroundAction();
                    }

                    break;
                }
                case MotionEvent.ACTION_UP: {
                    Utils.handleImageViewActionUp(handImageView, 10);
                    break;
                }
            }
            return true;
        }
    }
}
