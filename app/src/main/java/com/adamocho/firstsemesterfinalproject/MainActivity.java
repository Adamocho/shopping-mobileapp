package com.adamocho.firstsemesterfinalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.slider.Slider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    public static final String TAG = "OrdersWithSQLite";
    private static int mainIndex = -1;
    private static int previous = -1;
    private int basePrice = 0;
    FeedReaderContract dbHelper;
    Spinner main_spinner;
    String[] camera_desc;
    String[] acc_desc;
    String[] acc_price;
    String[] camera_price;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    TextView sumPriceView;
    Slider slider;
    Button placeOrderBtn;

    JSONObject orderJSON;

    public static final String username = "Johnny";

    int[] camera_pics = {
            R.drawable.canon_f1,
            R.drawable.olympus_om1,
            R.drawable.nikon_fm2_t,
    };

    int[] camera_ids = {
            7,
            5,
            3,
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
        slider = findViewById(R.id.main_slider);
        placeOrderBtn = findViewById(R.id.order_btn);

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

        slider.addOnChangeListener((slider1, value, fromUser) -> {
            try
            {
                if (mainIndex != -1)
                    orderJSON.getJSONArray("products").getJSONObject(mainIndex).put("qty", value);
            } catch (JSONException e) {}

//            refreshOrderPrice();
            refreshOrderPriceWithJSON();
        });
        
        placeOrderBtn.setOnClickListener(view -> {
            Toast.makeText(this, "Order placed", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Order placed");
        });
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
                StringBuilder message = new StringBuilder();
                try
                {
                    message.append("Sum: ").append(String.valueOf(orderJSON.getInt("sum"))).append("$");
                    JSONArray jarray = orderJSON.getJSONArray("products");
                    message.append("\n");
                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject obj = jarray.getJSONObject(i);
                        message.append("\n").append(obj.getString("name")).append(", ").append(obj.getInt("price")).append("$ x ").append(obj.getInt("qty"));
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                smsIntent.setData(Uri.parse("smsto:"));
                smsIntent.putExtra("sms_body", message.toString());
                
                Log.i(TAG, "Sending SMS");
                startActivity(smsIntent);
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
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
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
        try
        {
            JSONArray jarray = orderJSON.getJSONArray("products");

            if (mainIndex == -1) {
                mainIndex = jarray.length();
                JSONObject object = MainActivity.getProduct(
                        camera_ids[position],
                        camera_desc[position].split("-")[0].trim(),
                        (int) slider.getValue(),
                        Integer.parseInt(camera_price[position]));
                jarray.put(object);
            } else if (previous != position) {
                jarray.remove(mainIndex);
                mainIndex = jarray.length();
                JSONObject object = MainActivity.getProduct(
                        camera_ids[position],
                        camera_desc[position].split("-")[0].trim(),
                        (int) slider.getValue(),
                        Integer.parseInt(camera_price[position]));
                jarray.put(object);
            }
            previous = position;
        } catch (JSONException e) {}

        Log.i(TAG, "JSON: " + orderJSON);
        Log.i(TAG, "Item " + position + " selected. Base price: " + basePrice);
//        refreshOrderPrice();
        refreshOrderPriceWithJSON();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.i(TAG, "Nothing was selected");
    }

    public void refreshOrderPrice() {
        int accPrice = ((ItemAdapterImage) recyclerView.getAdapter()).getSumOfChecked();
        int sumPrice = (int) ((basePrice * slider.getValue()) + accPrice);
//        Log.i(TAG, "refreshing order price..");

        sumPriceView.setText(String.format(Locale.US, "%d$", sumPrice));
        try
        {
            orderJSON.put("sum", sumPrice);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void refreshOrderPriceWithJSON() {
        int sum = 0;

        try {
            JSONArray prods = orderJSON.getJSONArray("products");
            for (int i = 0; i < prods.length(); i++) {
                JSONObject obj = prods.getJSONObject(i);
                sum += obj.getInt("price") * obj.getInt("qty");
            }
            orderJSON.put("sum", sum);
        } catch (JSONException e) {e.printStackTrace();}

        sumPriceView.setText(String.format(Locale.US, "%d$", sum));
    }

    public static JSONObject getProduct(int id, String name, int qty, int price) throws JSONException {
        JSONObject product = new JSONObject();
        product .put("id", id);
        product .put("name", name);
        product .put("qty", qty);
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
        orderJSON.put("id", UUID.randomUUID().toString());
        orderJSON.put("name", username);
        orderJSON.put("sum", 0);
        orderJSON.put("date", "");
        orderJSON.put("products", products);

        Log.i(TAG, String.valueOf(orderJSON));
    }
}