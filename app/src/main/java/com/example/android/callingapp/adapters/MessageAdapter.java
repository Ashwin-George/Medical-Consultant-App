package com.example.android.callingapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android.callingapp.DataObjects.Message;
import com.example.android.callingapp.utils.DateUtils;
import com.example.android.callingapp.R;

import java.util.List;

import javax.sql.DataSource;

public class MessageAdapter extends ArrayAdapter<Message> {
    public MessageAdapter(@NonNull Context context, int resource, @NonNull List<Message> objects) {
        super(context, resource, objects);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message, parent, false);
        }
        Message message = getItem(position);
        TextView msgtextView = (TextView) convertView.findViewById(R.id.messageTextView);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
        ImageView photoImageView = (ImageView) convertView.findViewById(R.id.photoImageView);
        ProgressBar imageProgressBar = (ProgressBar) convertView.findViewById(R.id.image_progressbar);
        TextView timeTextView = (TextView) convertView.findViewById(R.id.timeTextView);

        imageProgressBar.setVisibility(View.GONE);
        msgtextView.setVisibility(View.VISIBLE);
        photoImageView.setVisibility(View.GONE);
        msgtextView.setText(message.getMessage());

        nameTextView.setText(message.getAuthor());
        String dateString = DateUtils.getSampleFormattedDate(message.getTimeStamp());
        timeTextView.setText(dateString);

        return convertView;
    }

}
