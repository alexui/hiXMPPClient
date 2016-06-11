package cs.pub.ro.hixmppclient;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

//pas 1 am scapat de Title Bar - Activity si requestWindowFeature
public class WelcomeActivity extends Activity {

    private ImageView logoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);

        logoImageView = (ImageView) findViewById(R.id.logoImageViewMain);
        logoImageView.setImageBitmap(Utils.getRoundedCornerBitmap(
                BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.hand)));

        logoImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ImageView handImageView = (ImageView) v;
                int height = handImageView.getHeight();
                int width = handImageView.getWidth();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) handImageView.getLayoutParams();
                        layoutParams.width = width + 10;
                        layoutParams.height = height + 10;
                        handImageView.setLayoutParams(layoutParams);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) handImageView.getLayoutParams();
                        layoutParams.width = width - 10;
                        layoutParams.height = height - 10;
                        handImageView.setLayoutParams(layoutParams);
                        break;
                    }
                }
                return true;
            }
        });
    }

}
