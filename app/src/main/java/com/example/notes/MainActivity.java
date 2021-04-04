package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    EditText trackName, trackAuthor, trackYear, trackDuration;
    HelperDB helper;
    Cursor tracks;
    SimpleCursorAdapter adapter;
    SQLiteDatabase trackDB;
    TextView allTrack, allTrackTime;
    Button btnAuthor, btnName, btnYear;

    int sortName = 0, sortAuthor = 0, sortYear = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.playlist);
        trackName = findViewById(R.id.name);
        trackAuthor = findViewById(R.id.author);
        trackYear = findViewById(R.id.year);
        trackDuration = findViewById(R.id.editTextTime);

        allTrack = findViewById(R.id.allTrack);
        allTrackTime = findViewById(R.id.allTrackTime);

        helper = new HelperDB(this);
        trackDB = helper.getWritableDatabase();

        btnAuthor = findViewById(R.id.btnAuthor);
        btnName = findViewById(R.id.btnName);
        btnYear = findViewById(R.id.btnYear);

        tracks = trackDB.rawQuery("SELECT * FROM tracks", null);
        String[] track_fields = tracks.getColumnNames();

        // int[] - ссылки на id элементов разметки playlist_item
        // полученный Cursor использовать для создания адаптера
        // готовый адаптер назначить для ListView
        int[] views = {
                R.id.trackId,
                R.id.trackName,
                R.id.trackAuthor,
                R.id.trackYear,
                R.id.trackDuration
        };

        // этот адаптер отображает в ListView перечень полей (столбцов)
        adapter = new SimpleCursorAdapter(this, R.layout.element, tracks, track_fields, views, 0);
        listView.setAdapter(adapter);
        recountFields();
    }



    private void recountFields(){

        Cursor tmp = trackDB.rawQuery("SELECT count(_id) as count FROM tracks", null);
        String data = "";
        if (tmp.moveToFirst()){
            do {
                data = tmp.getString(tmp.getColumnIndex("count"));
            } while (tmp.moveToNext());
        }
        allTrack.setText("Всего треков: " + data);

        tmp = trackDB.rawQuery("SELECT duration FROM tracks", null);
        int sec = 0;
        if (tmp.moveToFirst()){
            do{
                data = tmp.getString(tmp.getColumnIndex("duration"));
                String[] time = data.split(":");
                sec += Integer.parseInt(time[0]) * 60 * 60 + Integer.parseInt(time[1]) * 60 +
                        Integer.parseInt(time[2]);

            } while (tmp.moveToNext());
        }
        tmp.close();
        int hour = sec / 3600, min = (sec % 3600) / 60;
        sec %= 60;

        allTrackTime.setText("Общая длительность " + hour + ":" + min + ":" + sec);

    }

    public void addTrack(View v) throws IOException {
        String _name, _author, _year, _duration;

        _name = trackName.getText().toString();
        _author = trackAuthor.getText().toString();
        _year = trackYear.getText().toString();
        _duration = trackDuration.getText().toString();

        ContentValues values = new ContentValues();
        values.put("name", _name);
        values.put("author", _author);
        values.put("year", _year);
        values.put("duration", _duration);
        trackDB.insert("tracks", null, values);
        tracks = trackDB.rawQuery("SELECT * FROM tracks", null);
        adapter.swapCursor(tracks);
        recountFields();
    }

    public void sortByName(View v) {
        clearTextBtn();
        if (sortName <= 0) {
            tracks = trackDB.rawQuery("SELECT * FROM tracks ORDER BY name ASC", null);
            sortName = 1;
            btnName.setText("^ название");
        } else {
            tracks = trackDB.rawQuery("SELECT * FROM tracks ORDER BY name DESC", null);
            sortName = -1;
            btnName.setText("v название");
        }
        adapter.swapCursor(tracks);
    }

    public void sortByAuthor(View v) {
        clearTextBtn();
        if (sortAuthor <= 0) {
            tracks = trackDB.rawQuery("SELECT * FROM tracks ORDER BY author ASC", null);
            sortAuthor = 1;
            btnAuthor.setText("^ автор");
        } else {
            tracks = trackDB.rawQuery("SELECT * FROM tracks ORDER BY author DESC", null);
            sortAuthor = -1;
            btnAuthor.setText("v автор");
        }
        adapter.swapCursor(tracks);
    }

    public void sortByYear(View v) {
        clearTextBtn();
        if (sortYear <= 0) {
            tracks = trackDB.rawQuery("SELECT * FROM tracks ORDER BY year ASC", null);
            sortYear = 1;
            btnYear.setText("^ год");
        } else {
            tracks = trackDB.rawQuery("SELECT * FROM tracks ORDER BY year DESC", null);
            sortYear = -1;
            btnYear.setText("v год");
        }
        adapter.swapCursor(tracks);
    }

    private void clearTextBtn() {
        btnName.setText("название");
        btnAuthor.setText("автор");
        btnYear.setText("год");
    }

}