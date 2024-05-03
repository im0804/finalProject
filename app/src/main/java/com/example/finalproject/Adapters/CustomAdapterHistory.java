package com.example.finalproject.Adapters;

import android.annotation.SuppressLint;
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
import com.example.finalproject.R;

import java.util.ArrayList;

public class CustomAdapterHistory extends BaseAdapter {
    private Context context;
    private ArrayList<MatchClass> arrHistory;
    private LayoutInflater inflater;

    public static String score = new String();

    public CustomAdapterHistory(Context context, ArrayList<MatchClass> arrHistory) {
        this.context = context;
        this.arrHistory = arrHistory;
        inflater = (LayoutInflater.from(context));
    }

    public int getCount() {
        return arrHistory.size();
    }

    public Object getItem(int position) {
        return arrHistory.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {
        CustomAdapterHistory.ViewHolder viewholder;
        if (view == null){
            view = inflater.inflate(R.layout.custom_lv_history, parent, false);
            viewholder = new ViewHolder(view);
            view.setTag(viewholder);
        }
        else
            viewholder = (CustomAdapterHistory.ViewHolder) view.getTag();


        if (!arrHistory.isEmpty()){
            viewholder.u1VSu2TV.setText(arrHistory.get(position).getUserNameInviter()
                    + " VS "
                    + arrHistory.get(position).getUserNameInvited());
            score = "";
            for (int i = 0; i < arrHistory.get(position).getEndMatch().getScore().size();i++) {
                Log.e("i", i + "");
                Log.e("array", arrHistory.get(position).getEndMatch().getScore().get(i) + " ");
                Log.e("size", arrHistory.get(position).getEndMatch().getScore().size() + " ");
                score += arrHistory.get(position).getEndMatch().getScore().get(i) + " ";
            }
            viewholder.tvScore.setText(score);
            viewholder.tvWinner.setText("winner: " + arrHistory.get(position).getEndMatch().getWinner());
            viewholder.tvDateTime.setText(arrHistory.get(position).getDate() + " - " +arrHistory.get(position).getHour());
        }
        return view;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView u1VSu2TV, tvScore, tvWinner, tvDateTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            u1VSu2TV = (TextView) itemView.findViewById(R.id.tvu1VSu2);
            tvScore = (TextView) itemView.findViewById(R.id.tvScore);
            tvWinner = (TextView) itemView.findViewById(R.id.tvWinner);
            tvDateTime = (TextView) itemView.findViewById(R.id.tvDateTime);
        }
    }
}
