package cs.pub.ro.hixmppclient;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Spinner;

public class LogInActivity extends AppCompatActivity {

    private ImageView logoImageView;
    private ServerNameSpinnerAdapter serverNameSpinnerAdapter;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        logoImageView = (ImageView) findViewById(R.id.logoImageViewLogIn);
        logoImageView.setImageBitmap(Utils.getRoundedCornerBitmap(
                BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.hand)));

        serverNameSpinnerAdapter = new ServerNameSpinnerAdapter(this);
        serverNameSpinnerAdapter.addServerNameSpinnerItems();

        spinner = (Spinner) findViewById(R.id.serverNameSpinnerLogIn);
        spinner.setAdapter(serverNameSpinnerAdapter);
    }
}
