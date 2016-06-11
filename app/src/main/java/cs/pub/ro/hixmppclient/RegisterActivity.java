package cs.pub.ro.hixmppclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import static android.view.View.OnClickListener;

public class RegisterActivity extends AppCompatActivity {

    private ServerNameSpinnerAdapter serverNameSpinnerAdapter;
    private Spinner spinner;
    private ImageView optionalInfoIconImageView;

    private TextView emailTextView;
    private EditText emailEditText;
    private TextView phoneTextView;
    private EditText phoneEditText;

    private ImageView previousOptionImageView, nextOptionImageView;

    private OnClickListener optionalInfoIconImageViewListener;

    public RegisterActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        serverNameSpinnerAdapter = new ServerNameSpinnerAdapter(this);
        serverNameSpinnerAdapter.addServerNameSpinnerItems();
        spinner = (Spinner) findViewById(R.id.serverNameSpinnerRegister);
        spinner.setAdapter(serverNameSpinnerAdapter);

        emailTextView = (TextView) findViewById(R.id.emailTextViewRegister);
        emailEditText = (EditText) findViewById(R.id.emailEditTextLRegister);
        phoneTextView = (TextView) findViewById(R.id.phoneNumberTextViewRegister);
        phoneEditText = (EditText) findViewById(R.id.phoneNumberEditTextLRegister);

        optionalInfoIconImageViewListener = new OptionalInfoIconImageViewListener();

        optionalInfoIconImageView = (ImageView) findViewById(R.id.optionalInfoIconImageView);
        optionalInfoIconImageView.setOnClickListener(optionalInfoIconImageViewListener);
        previousOptionImageView = (ImageView) findViewById((R.id.previousOptionImageView));
        previousOptionImageView.setOnClickListener(optionalInfoIconImageViewListener);
        nextOptionImageView = (ImageView) findViewById((R.id.nextOptionImageView));
        nextOptionImageView.setOnClickListener(optionalInfoIconImageViewListener);
    }

    private class OptionalInfoIconImageViewListener implements View.OnClickListener {

        private ArrayList<OptionalInfoView> optionalInfoViewList;
        private boolean visibility;
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
            visibility = false;

            optionalInfoViewList = new ArrayList<>();
            optionalInfoViewList.add(new OptionalInfoView(emailTextView, emailEditText));
            optionalInfoViewList.add(new OptionalInfoView(phoneTextView, phoneEditText));
        }

        @Override
        public void onClick(View v) {
            ImageView thisImageView = (ImageView) v;
            switch (v.getId()) {
                case R.id.optionalInfoIconImageView:
                    if (!visibility) {
                        OptionalInfoView optionalInfoView = optionalInfoViewList.get(currentOptionalInfoView);
                        optionalInfoView.optionalInfoTextView.setVisibility(View.VISIBLE);
                        optionalInfoView.optionalInfoEditText.setVisibility(View.VISIBLE);
                        thisImageView.setImageDrawable(
                                RegisterActivity.this.getResources().getDrawable(R.drawable.upside_triangle));

                        RegisterActivity.this.previousOptionImageView.setVisibility(View.VISIBLE);
                        RegisterActivity.this.nextOptionImageView.setVisibility(View.VISIBLE);
                    } else {
                        OptionalInfoView optionalInfoView = optionalInfoViewList.get(currentOptionalInfoView);
                        optionalInfoView.optionalInfoTextView.setVisibility(View.INVISIBLE);
                        optionalInfoView.optionalInfoEditText.setVisibility(View.INVISIBLE);
                        thisImageView.setImageDrawable(
                                RegisterActivity.this.getResources().getDrawable(R.drawable.downside_triangle));

                        RegisterActivity.this.previousOptionImageView.setVisibility(View.INVISIBLE);
                        RegisterActivity.this.nextOptionImageView.setVisibility(View.INVISIBLE);
                    }
                    visibility = !visibility;
                    break;
                case R.id.previousOptionImageView:
                    if (currentOptionalInfoView > 0) {
                        OptionalInfoView optionalInfoView = optionalInfoViewList.get(currentOptionalInfoView);
                        optionalInfoView.optionalInfoTextView.setVisibility(View.INVISIBLE);
                        optionalInfoView.optionalInfoEditText.setVisibility(View.INVISIBLE);
                        currentOptionalInfoView--;
                        optionalInfoView = optionalInfoViewList.get(currentOptionalInfoView);
                        optionalInfoView.optionalInfoTextView.setVisibility(View.VISIBLE);
                        optionalInfoView.optionalInfoEditText.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.nextOptionImageView:
                    if (currentOptionalInfoView < optionalInfoViewList.size() - 1) {
                        OptionalInfoView optionalInfoView = optionalInfoViewList.get(currentOptionalInfoView);
                        optionalInfoView.optionalInfoTextView.setVisibility(View.INVISIBLE);
                        optionalInfoView.optionalInfoEditText.setVisibility(View.INVISIBLE);
                        currentOptionalInfoView++;
                        optionalInfoView = optionalInfoViewList.get(currentOptionalInfoView);
                        optionalInfoView.optionalInfoTextView.setVisibility(View.VISIBLE);
                        optionalInfoView.optionalInfoEditText.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    }
}
