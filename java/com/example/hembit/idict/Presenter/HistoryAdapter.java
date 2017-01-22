package com.example.hembit.idict.Presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hembit.idict.Model.History;
import com.example.hembit.idict.R;

import java.util.List;

/**
 * Created by hembit on 20/01/2017.
 */

public class HistoryAdapter extends BaseAdapter{
    private List<History> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public HistoryAdapter(Context aContext, List<History> listData) {
        this.context = aContext;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        HistoryAdapter.ViewHolder holder;
        if (layoutInflater == null)
            layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_history, null);
            holder = new HistoryAdapter.ViewHolder();
            holder.word_text = (TextView) convertView.findViewById(R.id.word_item);
            holder.access_time = (TextView) convertView.findViewById(R.id.access_time);

            convertView.setTag(holder);
        } else {
            holder = (HistoryAdapter.ViewHolder) convertView.getTag();
        }
        History history = this.listData.get(position);
        holder.word_text.setText(String.valueOf(history.getWord()) );
        holder.access_time.setText(String.valueOf(history.getAccess_time()));

        return convertView;
    }

    public static class ViewHolder {
        TextView access_time;
        TextView word_text;
    }
}
