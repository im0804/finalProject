package com.example.finalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapterCM extends BaseAdapter {
    private Context context;
    private ArrayList<MatchClass> arrMatches;
    private LayoutInflater inflater;

    public CustomAdapterCM(Context context, ArrayList<MatchClass> arrMatches) {
        this.context = context;
        this.arrMatches = arrMatches;
        inflater = (LayoutInflater.from(context));
    }

    public int getCount() {
        return arrMatches.size();
    }

    public Object getItem(int position) {
        return arrMatches.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {
        CustomAdapterCM.ViewHolder viewholder;
        if (view == null){
            view = inflater.inflate(R.layout.custom_lv_close_matches, parent, false);
            viewholder = new ViewHolder(view);
            view.setTag(viewholder);
        }
        else
            viewholder = (CustomAdapterCM.ViewHolder) view.getTag();


        if (!arrMatches.isEmpty()){
            viewholder.tvInviteUser.setText(arrMatches.get(position).getUserNameInviter());
            viewholder.tvInvitedUser.setText(arrMatches.get(position).getUserNameInvited());
            viewholder.tvDate.setText(arrMatches.get(position).getDate());
            viewholder.tvHour.setText(arrMatches.get(position).getHour());
            //viewholder.tvLocation.setText(arrMatches.get(position).getLocation());
        }
        return view;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvInviteUser, tvInvitedUser, tvDate, tvHour, tvLocation;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInviteUser = (TextView) itemView.findViewById(R.id.tvInviteUser);
            tvInvitedUser = (TextView) itemView.findViewById(R.id.tvInvitedUser);
            tvDate = (TextView) itemView.findViewById(R.id.DateTV1);
            tvHour = (TextView) itemView.findViewById(R.id.TimeTV1);
            tvLocation = (TextView) itemView.findViewById(R.id.LocationTV1);
            //Button scoreBTN = (Button) itemView.findViewById(R.id.scoreBTN);
        }
    }
}
