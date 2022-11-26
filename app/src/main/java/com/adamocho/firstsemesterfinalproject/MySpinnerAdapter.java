package com.adamocho.firstsemesterfinalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MySpinnerAdapter extends BaseAdapter {

    Context context;
    int[] img;
    String[] desc;
    String[] price;
    LayoutInflater layoutInflater;
    ImageView imageView;
    TextView textView;
    TextView priceView;

    public MySpinnerAdapter(Context context, int[] img, String[] desc, String[] price) {
        super();
        this.context = context;
        this.img = img;
        this.desc = desc;
        this.price = price;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return img.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        view = layoutInflater.inflate(R.layout.main_spinner_items, null);
        imageView = view.findViewById(R.id.spinner_image_view);
        textView = view.findViewById(R.id.spinner_text_view);
        priceView = view.findViewById(R.id.spinner_price_view);
        imageView.setImageResource(img[i]);
        textView.setText(desc[i]);
        priceView.setText(String.format("Price: %s$", price[i]));

        return view;
    }
}
