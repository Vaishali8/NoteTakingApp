package com.vaishali.admin.notetakingapp;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Admin on 7/5/2016.
 */
public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.MyViewHolder> {
    private List<Notes> notesList;
    private Context mcontext;
    DatabaseAdapter d;




    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title, content;
        public ImageButton delete;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.text_view_note_title);
            content = (TextView) view.findViewById(R.id.text_content);
            delete= (ImageButton) view.findViewById(R.id.deletebutton);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {


             notesList.remove(getAdapterPosition());
            notifyItemRemoved(getAdapterPosition());
            notifyItemRangeChanged(getAdapterPosition(),notesList.size());
        }
    }
    public NoteListAdapter(List<Notes> mnotesList,Context context) {
        notesList=mnotesList;
        mcontext=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_recyclerview, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Notes note = notesList.get(position);
        holder.title.setText(note.getTitle());
        holder.content.setText(note.getContent());

    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

}