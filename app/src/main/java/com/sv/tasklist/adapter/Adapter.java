package com.sv.tasklist.adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.sv.tasklist.R;
import com.sv.tasklist.activity.App;
import com.sv.tasklist.activity.notes.NoteActivity;
import com.sv.tasklist.model.Note;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.NoteViewHolder> {

    private SortedList<Note> sortedList;

    public Adapter() {
        sortedList = new SortedList<>(Note.class, new SortedList.Callback<Note>() {
            @Override
            public int compare(Note o1, Note o2) {
                if (!o2.done && o1.done) {
                    return 1;
                } else if (o2.done && !o1.done) {
                    return -1;
                }
                return (int) (o2.timestamp - o1.timestamp);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(Note oldItem, Note newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(Note item1, Note item2) {
                return item1.uid == item2.uid;
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
        });
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note_list, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bind(sortedList.get(position));
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    public void setItems(List<Note> notes){
        sortedList.replaceAll(notes);
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
//        TextView tvNote;
        TextView tvDate;
        CheckBox cbDone;
        View delete;

        Note note;
        boolean silentUpdate;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
//            tvNote = itemView.findViewById(R.id.note_text);
            tvDate = itemView.findViewById(R.id.tvDate);
            cbDone = itemView.findViewById(R.id.done);
            delete = itemView.findViewById(R.id.btnDelete);

            itemView.setOnClickListener(v -> NoteActivity.start((Activity) itemView.getContext(), note));

            delete.setOnClickListener(v -> App.getInstance().getNoteDao().delete(note));

            cbDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!silentUpdate) {
                    note.done = isChecked;
                    App.getInstance().getNoteDao().update(note);
                }
                updateStringOut();
            });
        }

        public void bind(Note note) {
            this.note = note;
            tvTitle.setText(note.title);
//            tvNote.setText(note.text);
            tvDate.setText(note.date);
            updateStringOut();

            silentUpdate = true;
            cbDone.setChecked(note.done);
            silentUpdate = false;
        }

        private void updateStringOut() {
            if(note.done) {
//                tvNote.setPaintFlags(tvNote.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvDate.setPaintFlags(tvDate.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
//                tvNote.setPaintFlags(tvNote.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                tvDate.setPaintFlags(tvDate.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }
    }
}
