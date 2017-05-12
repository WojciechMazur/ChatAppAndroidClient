package com.wickedmonkstudio.chatapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wickedmonkstudio.chatapp.other.Message;

import java.util.List;

/**
 * Created by Wojciech on 03.05.2017.
 */

public class MessageListAdapter extends BaseAdapter{

    private Context context;
    private List<Message> messageList;

    public MessageListAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Message message = messageList.get(position);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if(messageList.get(position).isSelf()){
            convertView=inflater.inflate(R.layout.list_item_message_right, null);
        }else{
            convertView=inflater.inflate(R.layout.list_item_message_left, null);
        }

        TextView lblFrom = (TextView)convertView.findViewById(R.id.lblMsgFrom);
        TextView textMsg = (TextView)convertView.findViewById(R.id.txtMsg);

        textMsg.setText(message.getMessage());
        lblFrom.setText(message.getFromName());
        return convertView;
    }
}
