package com.example.hembit.idict.Controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;

import com.example.hembit.idict.Model.Word;
import com.example.hembit.idict.R;

import java.util.List;

/**
 * Created by hembit on 23/12/2016.
 */

public class WordAdapter extends BaseAdapter {
    private List<Word> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public WordAdapter(Context aContext, List<Word> listData) {
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
        ViewHolder holder;
        if (layoutInflater == null)
            layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.fragment_word, null);
            holder = new ViewHolder();
            holder.word_text = (TextView) convertView.findViewById(R.id.word_text);
            holder.word_pronounce = (TextView) convertView.findViewById(R.id.pronounce);
            holder.word_meaning = (TextView) convertView.findViewById(R.id.meaning);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Word word = this.listData.get(position);
        holder.word_text.setText(String.valueOf(word.getWord_text()) );
        holder.word_pronounce.setText(String.valueOf(word.getWord_pronounce()));
        holder.word_meaning.setText(String.valueOf(word.getWord_meaning()));
        return convertView;
    }

    public static class ViewHolder {
        TextView word_pronounce;
        TextView word_meaning;
        TextView word_text;
    }
}
