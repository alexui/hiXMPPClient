package cs.pub.ro.hixmppclient.mainActivity.welcome.waitFragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import cs.pub.ro.hixmppclient.R;

public class WaitProgressBar extends View {

    private boolean started;
    private float radius;

    private Paint paintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Handler mHandler = new Handler();

    private int index = 0;
    private int widthSize, heightSize;
    private int margin = 10;
    private int dotsNumber = 3;

    public WaitProgressBar(Context context) {
        super(context);
        init(context);
    }

    public WaitProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WaitProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        started = false;
        radius = context.getResources().getDimension(R.dimen.waiting_animation_dot_radius);
        paintFill.setStyle(Style.FILL);
        paintFill.setColor(getResources().getColor(R.color.welcomeEditTextBackground));
        paint.setStyle(Style.FILL_AND_STROKE);
        paint.setColor(getResources().getColor(R.color.textColorPrimary));
    }

    public void setDotsCount(int count) {
        dotsNumber = count;
    }

    public void start() {
        started = true;
        index = -1;
        mHandler.removeCallbacks(runnable);
        mHandler.post(runnable);
    }

    public void stop() {
        mHandler.removeCallbacks(runnable);
    }

    private int dotStep = 1;
    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            index += dotStep;
            if (index < 0) {
                index = 1;
                dotStep = 1;
            } else if (index > (dotsNumber - 1)) {
                if ((dotsNumber - 2) >= 0) {
                    index = dotsNumber - 2;
                    dotStep = -1;
                } else {
                    index = 0;
                    dotStep = 1;
                }

            }
            invalidate();
            mHandler.postDelayed(runnable, 500);
        }

    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        widthSize = MeasureSpec.getSize(widthMeasureSpec);
        heightSize = (int) radius * 2 + getPaddingBottom() + getPaddingTop();
        setMeasuredDimension(widthSize, heightSize);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float x = (widthSize / 2.0f) - (dotsNumber * radius * 2.0f) / 2.0f + ((float) (dotsNumber - 1) * margin) / 2.0f - radius;
        float y = heightSize / 2;
        for (int i = 0; i < dotsNumber; i++) {
            if (i == index) {
                canvas.drawCircle(x, y, radius, paintFill);
            } else {
                canvas.drawCircle(x, y, radius, paint);
            }

            x += (2 * radius + margin);
        }

    }
}
