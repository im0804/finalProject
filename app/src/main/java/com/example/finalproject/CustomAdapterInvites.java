package com.example.finalproject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapterInvites extends BaseAdapter {
    private Context context;
    private ArrayList<InviteClass> arrInvites;
    private LayoutInflater inflater;

    public CustomAdapterInvites(Context context, ArrayList<InviteClass> arrInvites) {
        this.context = context;
        this.arrInvites = arrInvites;
        inflater = (LayoutInflater.from(context));
    }

    public int getCount() {
        return arrInvites.size();
    }

    public Object getItem(int position) {
        return arrInvites.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {
        CustomAdapterInvites.ViewHolder viewholder;
        if (view == null) {
            view = inflater.inflate(R.layout.custom_lv_invite, parent, false);
            viewholder = new ViewHolder(view);
            view.setTag(viewholder);
        }
        else
            viewholder = (CustomAdapterInvites.ViewHolder) view.getTag();


        if (!arrInvites.isEmpty()) {
            viewholder.tvUserName.setText(arrInvites.get(position).getUserName());
            viewholder.tvDate.setText(arrInvites.get(position).getDate());
            viewholder.tvTime.setText(arrInvites.get(position).getStartTime());
            viewholder.tvLocation.setText(arrInvites.get(position).getCity());
        }
        return view;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvUserName, tvDate, tvTime, tvLocation;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
        }
    }

}
