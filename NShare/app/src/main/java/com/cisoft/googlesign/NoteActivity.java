package com.cisoft.googlesign;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Icon;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class NoteActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    GoogleSignInClient mgoogleSignInClient;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    ArrayList<NoteModel> arrayList;
    RecyclerView recyclerView;
    FloatingActionButton actionButton;
    DatabaseHelper database_helper;

    String myemail, myname, myid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        actionButton = (FloatingActionButton) findViewById(R.id.add);
        database_helper = new DatabaseHelper(this);
        displayNotes();
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            myemail = firebaseUser.getEmail();
            myname = firebaseUser.getDisplayName();
            myid = firebaseUser.getUid();
        } else {
            Intent myintent = new Intent(this, MainActivity.class);
            startActivity(myintent);
            Toast.makeText(this, "Unable to retrieve your details!", Toast.LENGTH_LONG).show();
        }

        mgoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                database_helper.delete(arrayList.get(position).getID());
                arrayList.remove(position);
                Toast.makeText(getApplicationContext(), "Note deleted!", Toast.LENGTH_LONG).show();
                displayNotes();
            }
        });
        helper.attachToRecyclerView(recyclerView);
    }


    //display notes list
    public void displayNotes() {
        arrayList = new ArrayList<>(database_helper.getNotes());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        NotesAdapter adapter = new NotesAdapter(getApplicationContext(), this, arrayList);
        recyclerView.setAdapter(adapter);
    }

    //display dialog
    public void showDialog() {
        final EditText title, des;
        Button submit;
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        dialog.setContentView(R.layout.dialog);
        params.copyFrom(dialog.getWindow().getAttributes());
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        title = (EditText) dialog.findViewById(R.id.title);
        des = (EditText) dialog.findViewById(R.id.description);
        submit = (Button) dialog.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString().isEmpty()) {
                    title.setError("Please Enter Title");
                } else if (des.getText().toString().isEmpty()) {
                    des.setError("Please Enter Description");
                } else {
                    database_helper.addNotes(title.getText().toString(), des.getText().toString());
                    dialog.cancel();
                    Toast.makeText(getApplicationContext(), "Note added!", Toast.LENGTH_LONG).show();
                    displayNotes();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    public void deleteAll() {
        database_helper.deleteAll();
        displayNotes();
        Toast.makeText(this, "All notes deleted", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                displayNotes();
                Toast.makeText(this, "List refreshed!", Toast.LENGTH_LONG).show();
                return (true);
            case R.id.clear_all:
                deleteAll();
                return (true);
            case R.id.about_app:
                showAbout();
                return (true);
            case R.id.my_info:
                showInfo();
                return (true);
            case R.id.signout:
                mgoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseAuth.signOut();
                            Toast.makeText(NoteActivity.this, "Signed Out", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });
                Toast.makeText(this, "See you later...Boss!", Toast.LENGTH_SHORT).show();
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }

    public void showInfo() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View myPopwindow = inflater.inflate(R.layout.pop_layout, null);
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        Button ok = myPopwindow.findViewById(R.id.btnOk);
        final ImageView iv = myPopwindow.findViewById(R.id.iv_image);
        final TextView name = myPopwindow.findViewById(R.id.tvName);
        final TextView email = myPopwindow.findViewById(R.id.tvEmail);
        final TextView id = myPopwindow.findViewById(R.id.tvId);
        String idBababa = "ID : " + myid;
        Glide.with(this)
                .load(firebaseUser.getPhotoUrl())
                .into(iv);
        name.setText(myname);
        email.setText(myemail);
        id.setText(idBababa);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setView(myPopwindow);
        final AlertDialog mydialog = alert.create();
        mydialog.show();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydialog.cancel();
            }
        });
    }

    public void showAbout() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View myPopwindow = inflater.inflate(R.layout.about_layout, null);
        Button close = myPopwindow.findViewById(R.id.btnClose);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setView(myPopwindow);
        final AlertDialog mydialog = alert.create();
        mydialog.show();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydialog.cancel();
            }
        });
    }
}