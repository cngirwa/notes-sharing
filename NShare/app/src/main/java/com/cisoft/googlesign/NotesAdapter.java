package com.cisoft.googlesign;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ShareCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Programu hii (GoogleSign)
 * Imetengezwa na Canon C. Ngirwa tarehe 11/16/2020.
 * Haki zote zimehifadhiwa. Ci-Software 2020
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.viewHolder> {

    Context context;
    Activity activity;
    ArrayList<NoteModel> arrayList;
    DatabaseHelper database_helper;
    Integer pos;
    String myTitle, myDesc;

    public NotesAdapter(Context context, Activity activity, ArrayList<NoteModel> arrayList) {
        this.context = context;
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @Override
    public NotesAdapter.viewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.notes_list, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NotesAdapter.viewHolder holder, final int position) {
        holder.title.setText(arrayList.get(position).getTitle());
        holder.description.setText(arrayList.get(position).getDes());
        database_helper = new DatabaseHelper(context);

        myTitle = holder.title.getText().toString();
        myDesc = holder.description.getText().toString();

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //display edit dialog
                showDialog(position);
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show apps
                showShare();
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void showDialog(final int pos) {
        final EditText title, des;
        Button submit;
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        dialog.setContentView(R.layout.dialog);
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setTitle("Update Note");
        dialog.show();

        title = (EditText) dialog.findViewById(R.id.title);
        des = (EditText) dialog.findViewById(R.id.description);
        submit = (Button) dialog.findViewById(R.id.submit);

        title.setText(arrayList.get(pos).getTitle());
        des.setText(arrayList.get(pos).getDes());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString().isEmpty()) {
                    title.setError("Please Enter Title");
                } else if (des.getText().toString().isEmpty()) {
                    des.setError("Please Enter Description");
                } else {
                    //updating note
                    database_helper.updateNote(title.getText().toString(), des.getText().toString(), arrayList.get(pos).getID());
                    arrayList.get(pos).setTitle(title.getText().toString());
                    arrayList.get(pos).setDes(des.getText().toString());
                    dialog.cancel();
                    //notify list
                    notifyDataSetChanged();
                    Toast.makeText(activity, "Note updated!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void showShare() {

        Toast.makeText(context, myTitle + " myToast share " + myDesc, Toast.LENGTH_LONG).show();
        String txt = "Hi! I shared with you my note. Please reply ASAP!\n" +
                "TITLE : " + myTitle + "\n" +
                "DESCRIPTIONS : " + myDesc + "\n\n" +
                "Shared using NoteSharing App";
        String mimetype = "text/plain";
        ShareCompat.IntentBuilder.from(activity).setType(mimetype).setChooserTitle("Share note with").setText(txt).startChooser();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView delete, edit, share;

        public viewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            edit = (ImageView) itemView.findViewById(R.id.edit);
            share = (ImageView) itemView.findViewById(R.id.share);
            //delete = (ImageView) itemView.findViewById(R.id.delete);
        }
    }
}