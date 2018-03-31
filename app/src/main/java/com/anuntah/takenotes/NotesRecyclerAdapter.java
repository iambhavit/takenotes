package com.anuntah.takenotes;

import android.app.Notification;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

/**
 * Created by Bhavit Yadav on 19-03-2018.
 */

public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder> {


    interface OnItemClickListener{
        void OnItemClick(int pos);
        void OnItemLongClick(int pos);
    }

    OnItemClickListener listener;
    ArrayList<Notes> notesArrayList =new ArrayList<>();
    Context context;

    public NotesRecyclerAdapter(Context context, ArrayList<Notes> notesArrayList, OnItemClickListener listener){
        this.context=context;
        this.notesArrayList = notesArrayList;
        this.listener=listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.notess,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Notes notes=notesArrayList.get(position);
        holder.description.setVisibility(View.GONE);
        holder.title.setVisibility(View.GONE);
        holder.datetime.setVisibility(View.GONE);
        if(notes.getSelected())
            holder.cardView.setBackgroundColor(Color.LTGRAY);
        else
            holder.cardView.setBackgroundColor(Color.WHITE);
        if(notes.getDescription()!=null&&!notes.getDescription().equals("")) {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(notes.getDescription());
        }
        if(notes.getLabel()!=null){
            holder.label.setVisibility(View.VISIBLE);
            holder.label.setText(notes.getLabel());
        }
        if(notes.getTitle()!=null&&!notes.getTitle().equals("")){
            holder.title.setVisibility(View.VISIBLE);
            holder.title.setText(notes.getTitle());
        }
        if(notes.getDatetime()!=null&&!notes.getDatetime().equals("")) {
            holder.datetime.setVisibility(View.VISIBLE);
            holder.datetime.setText(notes.getDatetime());
        }
        else {
            holder.datetime.setVisibility(View.GONE);
            holder.datetime.setText("");
        }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnItemClick(holder.getAdapterPosition());
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.OnItemLongClick(holder.getAdapterPosition());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return notesArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView cardView;
        TextView title;
        TextView description;
        TextView datetime;
        TextView label;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.cd);
            title=itemView.findViewById(R.id.noteTitle);
            description=itemView.findViewById(R.id.description);
            datetime=itemView.findViewById(R.id.dateTime);
        }
    }
}
