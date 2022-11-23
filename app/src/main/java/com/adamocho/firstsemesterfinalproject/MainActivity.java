package com.adamocho.firstsemesterfinalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    public static final String TAG = "OrdersWithSQLite";
    private static int sumPrice = 0;
    private int basePrice = 0;
    FeedReaderContract dbHelper;
    Menu menu;
    Spinner main_spinner;
    String[] camera_desc;
    String[] acc_desc;
    String[] acc_price;
    String[] camera_price;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    TextView sumPriceView;
    JSONObject orderJSON;

    int[] camera_pics = {
            R.drawable.canon_f1,
            R.drawable.olympus_om1,
            R.drawable.nikon_fm2_t,
    };

    int[] acc_pics = {
            R.drawable.kodak_portra_400,
            R.drawable.ilford_delta_3200,
            R.drawable.cinestill_800t,
            R.drawable.ilford_hp5,
            R.drawable.kodak_gold_200,
            R.drawable.hama_tripod,
            R.drawable.cable_release,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new FeedReaderContract(this);
        try
        {
            initializeJSON();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        main_spinner = findViewById(R.id.main_item_spinner);
        recyclerView = findViewById(R.id.acc_recycler_view);
        sumPriceView = findViewById(R.id.sum_price_view);

        camera_desc = getResources().getStringArray(R.array.camera_desc);
        acc_desc = getResources().getStringArray(R.array.accessories_desc);
        acc_price = getResources().getStringArray(R.array.accessories_price);
        camera_price = getResources().getStringArray(R.array.camera_price);

        main_spinner.setOnItemSelectedListener(this);
        MySpinnerAdapter adapter = new MySpinnerAdapter(getApplicationContext(), camera_pics, camera_desc, camera_price);
        main_spinner.setAdapter(adapter);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        ItemAdapterImage accItemAdapter = new ItemAdapterImage(acc_desc, acc_pics, acc_price, this);
        recyclerView.setAdapter(accItemAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.order_list:

                break;
            case R.id.send_sms:

                break;
            case R.id.send_email:

                break;
            case R.id.share:

                break;
            case R.id.settings:

                break;
            case R.id.log_out:
//            { for testing only
//                SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//                ContentValues values = new ContentValues();
//                values.put(FeedReaderContract.FeedEntry.COLUMN_DATA, "YES");
//
//                long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
//            }
                break;
            case R.id.about:

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        switch (position) {
            case 0:
            case 1:
            case 2:
                basePrice = Integer.parseInt(camera_price[position]);
                break;
        }
        Log.i(TAG, "Item " + position + " selected. Base price: " + basePrice);
        refreshOrderPrice();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.i(TAG, "Nothing was selected");
    }

    public void refreshOrderPrice() {
        int accPrice = 0;
//        ItemAdapterImage.ViewHolder holder;
//        ItemAdapterImage adapter = (ItemAdapterImage) recyclerView.getAdapter();
//        int len = recyclerView.getAdapter().getItemCount();
//        for (int i = 0; i <= len; i++) {
//            if (adapter.getItemChecked(i))
//                accPrice += adapter.getItemPrice(i);
//        }
//        Log.i(TAG, "refreshing order price: " + basePrice + " + " + accPrice + " " + len);

        accPrice = ((ItemAdapterImage) recyclerView.getAdapter()).getSumOfChecked();
        sumPrice = basePrice + accPrice;
        Log.i(TAG, "refreshing order price: " + basePrice + " + " + accPrice);

        sumPriceView.setText(String.format(Locale.US, "%d$", sumPrice));
    }

    private JSONObject getProduct(int id, String name, int price) throws JSONException {
        JSONObject product = new JSONObject();
        product .put("id", id);
        product .put("name", name);
        product .put("price", price);
        return product ;
    }

    private void initializeJSON() throws JSONException {
//          If id inside the JSON will be needed
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + FeedReaderContract.FeedEntry.TABLE_NAME, null);
//        cursor.moveToFirst();   <-- This is so f**king bad
//        int db_length = cursor.getInt(0);
//        cursor.close();
//        db.close();

        JSONArray products = new JSONArray();

        orderJSON = new JSONObject();
//        orderJSON.put("id", )
    }

    public void sumPlus(int amount) {
        sumPrice += amount;
    }

    public void sumMinus(int amount) {
        sumPrice += amount;
    }
}