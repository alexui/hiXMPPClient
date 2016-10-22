package cs.pub.ro.hixmppclient.mainActivity.utils;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cs.pub.ro.hixmppclient.R;
import cs.pub.ro.hixmppclient.general.Utils;

public class ServerNameSpinnerAdapter extends BaseAdapter {

    private Activity context;
    private List<ServerNameSpinnerItems.SpinnerItem> spinnerItemsList;
    private TextView serverNameTextView;
    private ImageView serverIconImageView;
    private TextView selectedServerNameTextView;
    private ImageView selectedServerIconImageView;

    public ServerNameSpinnerAdapter(Activity context) {
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
        return spinnerItemsList.get(position).id;
    }

    @Override
    public View getDropDownView(int position, View layoutView, ViewGroup parent) {
        if (layoutView == null || !layoutView.getTag().toString().equals("DROPDOWN")) {
            layoutView = context.getLayoutInflater().inflate(R.layout.welcome_spinner_item, parent, false);
            layoutView.setTag("DROPDOWN");
        }

        serverNameTextView = (TextView) layoutView.findViewById(R.id.welcomeSpinnerTextView);
        serverNameTextView.setText(getServerName(position));

        serverIconImageView = (ImageView) layoutView.findViewById(R.id.welcomeSpinnerIconImageView);
        serverIconImageView.setImageBitmap(Utils.getRoundedCornerBitmap(
                BitmapFactory.decodeResource(context.getResources(), getServerIconId(position)))
        );
        return layoutView;
    }

    @Override
    public View getView(int position, View layoutView, ViewGroup parent) {
        if (layoutView == null || !layoutView.getTag().toString().equals("NON_DROPDOWN")) {
            layoutView = context.getLayoutInflater().inflate(R.layout.
                    welcome_spinner_selected_item, parent, false);
            layoutView.setTag("NON_DROPDOWN");
        }
        selectedServerNameTextView = (TextView) layoutView.findViewById(R.id.welcomeSelectedItemTextView);
        selectedServerNameTextView.setText(getServerName(position));

        selectedServerIconImageView = (ImageView) layoutView.findViewById(R.id.welcomeSelectedItemIconImageView);
        selectedServerIconImageView.setImageBitmap(Utils.getRoundedCornerBitmap(
                BitmapFactory.decodeResource(context.getResources(), getServerIconId(position)))
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

