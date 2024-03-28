package com.example.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapterUserInvites extends BaseAdapter {
    private Context context;
    private ArrayList<InviteClass> userArrInvites;
    private LayoutInflater inflater;

    public CustomAdapterUserInvites(Context context, ArrayList<InviteClass> userArrInvites) {
        this.context = context;
        this.userArrInvites = userArrInvites;
        inflater = (LayoutInflater.from(context));
    }

    public int getCount() {
        return userArrInvites.size();
    }

    public Object getItem(int position) {
        return userArrInvites.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {
        CustomAdapterUserInvites.ViewHolder viewholder;
        if (view == null){
            view = inflater.inflate(R.layout.custom_lv_user_invites, parent, false);
            viewholder = new ViewHolder(view);
            view.setTag(viewholder);
        }
        else {
            viewholder = (CustomAdapterUserInvites.ViewHolder) view.getTag();
        }
        if (!userArrInvites.isEmpty()) {
            viewholder.tvDate.setText(userArrInvites.get(position).getDate());
            viewholder.tvTime.setText(userArrInvites.get(position).getStartTime());
            viewholder.tvLocation.setText(userArrInvites.get(position).getCity());
        }
        return view;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvDate, tvTime, tvLocation;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
        }
    }
}
