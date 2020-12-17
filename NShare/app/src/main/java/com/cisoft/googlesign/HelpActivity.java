package com.cisoft.googlesign;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {
    TextView myTv;
    String strManual;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        myTv = findViewById(R.id.tvManual);
        myManua();
        //String myManual = getText(R.string.manual).toString();
        myTv.setText(Html.fromHtml(strManual, Html.FROM_HTML_MODE_COMPACT));
    }

    public void myManua() {
        strManual = "Don’t know where to start? <br/> Relax, let us help you! <br/>\n" +
                "<b> NShare</b> is designed to be simple and yet effective.<br/><br/>\n" +
                "How to write new note;<br/>\n" +
                "1. Click the plus (+) icon on bottom right.<br/>\n" +
                "2. Enter title and description of your note.<br/>\n" +
                "3. Click save.<br/>\n" +
                "It is that simple!<br/>\n" +
                "<br/>\n" +
                "How to share a note;<br/>\n" +
                "1. Click the share icon on bottom right of note.<br/>\n" +
                "2. Choose App/Channel to share.<br/>\n" +
                "3. Share it.<br/>\n" +
                "Sounds interesting right?<br/>\n" +
                "<br/>\n" +
                "How to edit note;<br/>\n" +
                "1. Click the pencil icon on bottom left of note.<br/>\n" +
                "2. Edit title and/or description of your note.<br/>\n" +
                "3. Click save.<br/>\n" +
                "So easy, right?<br/>\n" +
                "<br/>\n" +
                "How to delete a note;<br/>\n" +
                "1. Slide a note either left or right.<br/>\n" +
                "It’s gone!<br/>\n" +
                "<b>NB:</b> No recovery possible, choose wisely!<br/>\n" +
                "<br/>\n" +
                "How to refresh all notes list;<br/>\n" +
                "1. Click three dots on top right.<br/>\n" +
                "2. Click ‘Refresh’.<br/>\n" +
                "<br/>\n" +
                "How to delete all notes;<br/>\n" +
                "1. Click three dots on top right.<br/>\n" +
                "2. Click ‘Clear all notes’.<br/>\n" +
                "<b>NB:</b> No recovery possible, choose wisely!<br/>\n" +
                "<br/>\n" +
                "To know which version of <b>NShare</b> installed;<br/>\n" +
                "1. Click three dots on top right.<br/>\n" +
                "2. Click ‘About NShare’.<br/>\n" +
                "<br/>\n" +
                "To know your information;<br/>\n" +
                "1. Click three dots on top right.<br/>\n" +
                "2. Click ‘My Information’.<br/><br/>\n" +
                "Need a break? To sign-out;<br/>\n" +
                "1. Click three dots on top right.<br/>\n" +
                "2. Click ‘Sign Out’.<br/>\n" +
                "<br/>\n" +
                "For more information;<br/>\n" +
                "Please contact developer: cngirwa2007@gmail.com";
    }
}