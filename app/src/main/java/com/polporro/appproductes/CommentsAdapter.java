package com.polporro.appproductes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class CommentsAdapter extends ArrayAdapter<String> {

    public CommentsAdapter(Context context, ArrayList<String> comments) {
        super(context, 0, comments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_comment, parent, false);
        }
        String comment = getItem(position);
        TextView commentTextView = convertView.findViewById(R.id.commentTextView);
        commentTextView.setText(comment);

        return convertView;
    }
}

