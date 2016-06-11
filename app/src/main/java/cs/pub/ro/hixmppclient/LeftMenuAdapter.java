package cs.pub.ro.hixmppclient;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by Alex on 6/5/2016.
 */
public class LeftMenuAdapter extends RecyclerView.Adapter<LeftMenuAdapter.RecyclerViewHolder> {

    private LayoutInflater layoutInflater;

    List<MenuItem> menuItemsList = Collections.emptyList();

    public LeftMenuAdapter(Context context, List<MenuItem> menuItemsList) {
        layoutInflater = LayoutInflater.from(context);
        this.menuItemsList = menuItemsList;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = layoutInflater.inflate(R.layout.left_menu_item, parent, false);
        return new RecyclerViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        MenuItem current = menuItemsList.get(position);
        holder.menuItemTextView.setText(current.getTitle());
    }

    @Override
    public int getItemCount() {
        return menuItemsList.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView menuItemTextView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            menuItemTextView = (TextView) itemView.findViewById(R.id.menu_item_title);
        }
    }
}
