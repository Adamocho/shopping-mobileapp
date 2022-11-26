package com.adamocho.firstsemesterfinalproject;

import static com.adamocho.firstsemesterfinalproject.MainActivity.TAG;
import static com.adamocho.firstsemesterfinalproject.MainActivity.username;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity
{
    ListView listView;
    FeedReaderContract dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new FeedReaderContract(this);
        String[] ord = getJSONData();

        MyListAdapter listAdapter = new MyListAdapter(this, ord);

        listView = findViewById(R.id.listView);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener((adapterView, view, index, id) -> {
//            Log.i(TAG, "Item was clicked " + index + " " + view.findViewById(R.id.order_list_text));
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public String[] getJSONData() {
        db = dbHelper.getReadableDatabase();

        String[] projection = { FeedReaderContract.FeedEntry.COLUMN_DATA };
        String selection = FeedReaderContract.FeedEntry.COLUMN_BUYER + " = ?";
        String[] selectionArgs = { username };
        String sortOrder = FeedReaderContract.FeedEntry._ID + " DESC";

        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        List<String> orders = new ArrayList<>();

        while(cursor.moveToNext()) {
            String orderStr = cursor.getString(0);
            orders.add(orderStr);
        }
        cursor.close();
        db.close();

//        Log.i(TAG, "getJSONData: " + orders);
        return orders.toArray(new String[0]);
    }
}