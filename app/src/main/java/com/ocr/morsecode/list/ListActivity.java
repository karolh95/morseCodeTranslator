package com.ocr.morsecode.list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ocr.morsecode.R;

import java.util.ArrayList;
import java.util.Arrays;

public class ListActivity extends AppCompatActivity {

    public static final String EXTRAS = "EXTRAS";

//    private MySQLite db;
//    private SimpleCursorAdapter adapter;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
/*
        db = new MySQLite(this);
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,
                db.lista(),
                new String[]{
                        MySQLite.ID,
                        MySQLite.INPUT},
                new int[]{
                        android.R.id.text1,
                        android.R.id.text2},
                SimpleCursorAdapter.IGNORE_ITEM_VIEW_TYPE);
*/

        String[] values = new String[]{"SOS", "HELP", "QWERTYUIOP\nASDFGHJKL\nZXCVBNM", "0123456789"};

        ListView listView = findViewById(R.id.listView);

        target = new ArrayList<>();
        target.addAll(Arrays.asList(values));

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, target);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, pos, id) -> {
            TextView name = view.findViewById(android.R.id.text1);
            Intent data = new Intent();
            data.putExtra(EXTRAS, name.getText().toString());
            setResult(Activity.RESULT_OK, data);
            finish();
        });
    }

    public void cancel(View view) {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }


}
