package cs.pub.ro.hixmppclient.mainActivity.hiClient.statusFragment;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cs.pub.ro.hixmppclient.R;

public class StatusSpinnerAdapter extends BaseAdapter {

    private Activity context;
    private List<StatusSpinnerItems.SpinnerItem> spinnerItemsList;

    private TextView statusModeTextView;
    private ImageView statusIconImageView;
    private TextView selectedStatusModeTextView;
    private ImageView selectedStatusIconImageView;

    public StatusSpinnerAdapter(Activity context) {
        this.context = context;
    }

    public void addStatusModeSpinnerItems() {
        spinnerItemsList =  StatusSpinnerItems.getList();
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
            layoutView = context.getLayoutInflater().inflate(R.layout.status_spinner_item, parent, false);
            layoutView.setTag("DROPDOWN");
        }

        statusModeTextView = (TextView) layoutView.findViewById(R.id.statusSpinnerTextView);
        statusModeTextView.setText(getStatusMode(position));

        statusIconImageView = (ImageView) layoutView.findViewById(R.id.statusSpinnerIconImageView);
        statusIconImageView.setImageDrawable(context.getResources().getDrawable(getStatusIconId(position)));

        return layoutView;
    }

    @Override
    public View getView(int position, View layoutView, ViewGroup parent) {
        if (layoutView == null || !layoutView.getTag().toString().equals("NON_DROPDOWN")) {
            layoutView = context.getLayoutInflater().inflate(R.layout.
                    status_spinner_selected_item, parent, false);
            layoutView.setTag("NON_DROPDOWN");
        }
        selectedStatusModeTextView = (TextView) layoutView.findViewById(R.id.statusSelectedItemTextView);
        selectedStatusModeTextView.setText(getStatusMode(position));

        selectedStatusIconImageView = (ImageView) layoutView.findViewById(R.id.statusSelectedItemIconImageView);
        selectedStatusIconImageView.setImageDrawable(context.getResources().getDrawable(getStatusIconId(position)));

        return layoutView;
    }

    private String getStatusMode(int position) {
        return position >= 0 && position < spinnerItemsList.size() ? spinnerItemsList.get(position).statusMode : "";
    }

    private int getStatusIconId(int position) {

        return position >= 0 && position < spinnerItemsList.size() ? spinnerItemsList.get(position).statusIconId : 0;
    }
}

