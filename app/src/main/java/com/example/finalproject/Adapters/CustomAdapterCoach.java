package com.example.finalproject.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Objs.MatchClass;
import com.example.finalproject.Objs.UserDistanceClass;
import com.example.finalproject.Objs.UsersClass;
import com.example.finalproject.R;

import java.util.ArrayList;

public class CustomAdapterCoach extends BaseAdapter {
    private Context context;
    private ArrayList<UserDistanceClass> arrCoaches;
    private LayoutInflater inflater;

    public CustomAdapterCoach(Context context, ArrayList<UserDistanceClass> arrCoaches) {
        this.context = context;
        this.arrCoaches = arrCoaches;
        inflater = (LayoutInflater.from(context));
    }

    public int getCount() {
        return arrCoaches.size();
    }

    public Object getItem(int position) {
        return arrCoaches.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {
        CustomAdapterCoach.ViewHolder viewholder;
        if (view == null){
            view = inflater.inflate(R.layout.custom_lv_coach, parent, false);
            viewholder = new ViewHolder(view);
            view.setTag(viewholder);
        }
        else
            viewholder = (CustomAdapterCoach.ViewHolder) view.getTag();


        if (!arrCoaches.isEmpty()){
            viewholder.tvUsername.setText(arrCoaches.get(position).getUser().getUserName());
            viewholder.tvName.setText(arrCoaches.get(position).getUser().getFullName());
            viewholder.tvCity.setText(arrCoaches.get(position).getUser().getCity());
        }
        return view;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvUsername, tvName, tvCity;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername= (TextView) itemView.findViewById(R.id.tvUN);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvCity = (TextView) itemView.findViewById(R.id.tvCity);
        }
    }
}
