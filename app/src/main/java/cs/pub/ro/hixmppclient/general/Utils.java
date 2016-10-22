package cs.pub.ro.hixmppclient.general;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class Utils {

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 1200;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static void handleImageViewActionDown(ImageView handImageView, int dp) {
        int height = handImageView.getHeight();
        int width = handImageView.getWidth();

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) handImageView.getLayoutParams();
        layoutParams.width = width + dp;
        layoutParams.height = height + dp;
        handImageView.setLayoutParams(layoutParams);
    }

    public static void handleImageViewActionUp(ImageView handImageView, int dp) {
        int height = handImageView.getHeight();
        int width = handImageView.getWidth();

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) handImageView.getLayoutParams();
        layoutParams.width = width - dp;
        layoutParams.height = height - dp;
        handImageView.setLayoutParams(layoutParams);
    }

    public static boolean isStringValidIP (String string) {
        try {
            if ( string == null || string.isEmpty() ) {
                return false;
            }

            String[] stringParts = string.split( "\\." );
            if ( stringParts.length != 4 ) {
                return false;
            }

            for ( String s : stringParts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static String prettyFormat(String input) {
        try {
            Source xmlInput = new StreamSource(new StringReader(input));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (Exception e) {
            throw new RuntimeException(e); // simple exception handling, please review it
        }
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static ColorMatrixColorFilter getGreyScaleFilter() {
        ColorMatrixColorFilter filter;
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        filter = new ColorMatrixColorFilter(matrix);

        return filter;
    }
}
