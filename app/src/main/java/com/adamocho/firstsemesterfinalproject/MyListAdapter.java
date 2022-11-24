package com.adamocho.firstsemesterfinalproject;

import static com.adamocho.firstsemesterfinalproject.MainActivity.TAG;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyListAdapter extends BaseAdapter {

    Context context;
    String[] orders;
    LayoutInflater inflater;
    TextView textView;
    Button cancelBtn;
    FeedReaderContract dbHelper;
    SQLiteDatabase db;

    public MyListAdapter(Context context, String[] orders) {
        super();
        this.context = context;
        this.orders = orders;
        this.inflater = LayoutInflater.from(context);
        this.dbHelper = new FeedReaderContract(context);
    }

    @Override
    public int getCount()
    {
        return orders.length;
    }

    @Override
    public Object getItem(int i)
    {
        return null;
    }

    @Override
    public long getItemId(int i)
    {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        view = inflater.inflate(R.layout.list_orders, null);
        textView = view.findViewById(R.id.order_list_text);
        cancelBtn = view.findViewById(R.id.cancel_order_btn);

        textView.setText(orders[i]);
        cancelBtn.setOnClickListener(v -> {
            String id = orders[i].split("\"")[3].trim();
//            Log.i(TAG, "Canceling item with id: " + id);

            deleteId(id);
//            Log.i(TAG, "Order canceled");
        });

        return view;
    }

    public void deleteId(String id) {
        db = dbHelper.getWritableDatabase();
        String selection = FeedReaderContract.FeedEntry.COLUMN_DATA + " LIKE ?";
        String[] selectionArgs = { "%" + id + "%" };
        int deletedRows = db.delete(FeedReaderContract.FeedEntry.TABLE_NAME, selection, selectionArgs);
//        Log.i(TAG, "deleteId: DELETED " + deletedRows + " number of rows");
        db.close();

        if (deletedRows > 0) {
            List<String> newOrders = new ArrayList<>();
            for (String order : orders)
                if (!order.contains(id))
                    newOrders.add(order);

            orders = newOrders.toArray(new String[0]);
            this.notifyDataSetChanged();
        }
    }
}
