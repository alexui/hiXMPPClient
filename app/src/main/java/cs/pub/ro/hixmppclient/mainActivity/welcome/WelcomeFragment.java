package cs.pub.ro.hixmppclient.mainActivity.welcome;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import cs.pub.ro.hixmppclient.R;
import cs.pub.ro.hixmppclient.general.Constants;
import cs.pub.ro.hixmppclient.general.Utils;
import cs.pub.ro.hixmppclient.mainActivity.MainActivity;

public class WelcomeFragment extends android.support.v4.app.Fragment {

    private ImageView logoImageView;
    private Button registerButton;
    private Button logInButton;

    private MainActivity mainActivity;

    private FragmentManager mainActivityFragmentManager;
    private FragmentTransaction fragmentTransaction;

    private LogoImageViewTouchListener logoImageViewTouchListener;
    private RegisterButtonListener registerButtonListener;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {

        registerButtonListener = new RegisterButtonListener();
        logoImageViewTouchListener = new LogoImageViewTouchListener();

        mainActivity = (MainActivity) getActivity();
        mainActivityFragmentManager = mainActivity.getSupportFragmentManager();

        View layoutView = layoutInflater.inflate(R.layout.fragment_welcome, container, false);

        logoImageView = (ImageView) layoutView.findViewById(R.id.logoImageViewMain);
        logoImageView.setImageBitmap(Utils.getRoundedCornerBitmap(
                BitmapFactory.decodeResource(this.getResources(), R.drawable.hand)));

        logoImageView.setOnTouchListener(logoImageViewTouchListener);

        registerButton = (Button) layoutView.findViewById(R.id.registerButtonMain);
        logInButton = (Button) layoutView.findViewById(R.id.loginButtonMain);

        registerButton.setOnClickListener(registerButtonListener);
        logInButton.setOnClickListener(registerButtonListener);

        return layoutView;
    }

    private class RegisterButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            Fragment welcomeFragment = mainActivityFragmentManager.findFragmentByTag(Constants.FRAGMENT_WELCOME_TAG);
            Fragment mainActivityMainFragment = null;
            String fragment_tag = null;

            switch (v.getId()) {
                case R.id.registerButtonMain:
                    mainActivityMainFragment = new RegisterFragment();
                    fragment_tag = Constants.FRAGMENT_REGISTER_TAG;
                    break;
                case R.id.loginButtonMain:
                    mainActivityMainFragment = new LogInFragment();
                    fragment_tag = Constants.FRAGMENT_LOG_IN_TAG;
                    break;
            }

            fragmentTransaction = mainActivityFragmentManager.beginTransaction();
            fragmentTransaction.remove(welcomeFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.add(R.id.mainFrame, mainActivityMainFragment, fragment_tag);
            fragmentTransaction.commit();
        }
    }

    private class LogoImageViewTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ImageView handImageView = (ImageView) v;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    Utils.handleImageViewActionDown(handImageView, 10);
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
