package cs.pub.ro.hixmppclient;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Alex on 6/6/2016.
 */
public class ServerNameSpinnerAdapter extends BaseAdapter {

    private Activity context;
    private List<ServerNameSpinnerItems.SpinnerItem> spinnerItemsList;

    ServerNameSpinnerAdapter(Activity context) {
        this.context = context;
    }

    public void addServerNameSpinnerItems() {
        spinnerItemsList =  ServerNameSpinnerItems.getList();
    }

    @Override
    public int getCount() {
        return spinnerItemsList.size();
    }

    @Override
    public Object getItem(int position) {
        return spinnerItemsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View layoutView, ViewGroup parent) {
        if (layoutView == null || !layoutView.getTag().toString().equals("DROPDOWN")) {
            layoutView = context.getLayoutInflater().inflate(R.layout.spinner_item, parent, false);
            layoutView.setTag("DROPDOWN");
        }

        TextView serverNameTextView = (TextView) layoutView.findViewById(R.id.serverNameTextView);
        serverNameTextView.setText(getServerName(position));
        ImageView serverIconImageView = (ImageView) layoutView.findViewById(R.id.serverIconImageView);
//        serverIconImageView.setImageDrawable(context.getResources().getDrawable(getServerIconId(position)));

        serverIconImageView.setImageBitmap(Utils.getRoundedCornerBitmap(
                BitmapFactory.decodeResource(context.getResources(),
                        getServerIconId(position)))
        );
        return layoutView;
    }

    @Override
    public View getView(int position, View layoutView, ViewGroup parent) {
        if (layoutView == null || !layoutView.getTag().toString().equals("NON_DROPDOWN")) {
            layoutView = context.getLayoutInflater().inflate(R.layout.
                    spinner_selected_item, parent, false);
            layoutView.setTag("NON_DROPDOWN");
        }
        TextView selectedServerNameTextView = (TextView) layoutView.findViewById(R.id.selectedServerNameTextView);
        selectedServerNameTextView.setText(getServerName(position));
        ImageView selectedServerIconImageView = (ImageView) layoutView.findViewById(R.id.selectedServerIconImageView);
//        selectedServerIconImageView.setImageDrawable(context.getResources().getDrawable(getServerIconId(position)));

        selectedServerIconImageView.setImageBitmap(Utils.getRoundedCornerBitmap(
                BitmapFactory.decodeResource(context.getResources(),
                        getServerIconId(position)))
                );
        return layoutView;
    }

    private String getServerName(int position) {
        return position >= 0 && position < spinnerItemsList.size() ? spinnerItemsList.get(position).serverName : "";
    }

    private int getServerIconId(int position) {

        return position >= 0 && position < spinnerItemsList.size() ? spinnerItemsList.get(position).serverIconId : 0;
    }
}

