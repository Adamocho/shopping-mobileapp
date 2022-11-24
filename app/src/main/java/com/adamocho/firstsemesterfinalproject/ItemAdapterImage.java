package com.adamocho.firstsemesterfinalproject;

import static com.adamocho.firstsemesterfinalproject.MainActivity.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ItemAdapterImage extends RecyclerView.Adapter<ItemAdapterImage.ViewHolder> {
    String[] item_desc;
    String[] item_price;
    int[] item_imgs;
    boolean[] item_checked;
    Context context;

    int[] item_ids = {
            10,
            20,
            30,
            40,
            50,
            60,
            70,
            80,
            90,
            100
    };

    public ItemAdapterImage(String[] item_desc, int[] item_imgs, String[] item_price, Context context) {
        this.item_desc = item_desc;
        this.item_imgs = item_imgs;
        this.item_price = item_price;
        this.context = context;
        this.item_checked = new boolean[item_desc.length];
    }

    public boolean[] getItem_checked() {
        return item_checked;
    }

    public void setItem_checked(boolean[] item_checked) {
        this.item_checked = item_checked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int index) {
        holder.textView.setText(item_desc[index]);
        holder.imageView.setImageResource(item_imgs[index]);
        holder.priceView.setText(String.format("Price: %s$", item_price[index]));

        holder.checkBox.setChecked(item_checked[index]);

        holder.checkBox.setOnClickListener(view -> {
            CheckBox checkBox = holder.checkBox;
            item_checked[index] = checkBox.isChecked();

//            Log.i(TAG, "Button checked: " + index + " " + item_checked[index]);

            try {
                boolean found = false;
                JSONArray jarray = ((MainActivity) context).orderJSON.getJSONArray("products");
                for (int i = 0; i < jarray.length(); i++) {

                    if (jarray.getJSONObject(i).getInt("id") == item_ids[index]) {
                        jarray.remove(i);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    JSONObject object = MainActivity.getProduct(
                            item_ids[index],
                            item_desc[index].split("-")[0].trim(),
                            1,
                            Integer.parseInt(item_price[index]));
                    jarray.put(object);
                }

//                Log.i(TAG, "JSON: " + ((MainActivity) context).orderJSON);
            } catch (JSONException e) {}

            //            ((MainActivity) context).refreshOrderPrice();
            ((MainActivity) context).refreshOrderPriceWithJSON();
        });
    }

    @Override
    public int getItemCount() {
        return item_imgs.length;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getSumOfChecked() {
        int sum = 0;
        for (int i = 0; i < item_price.length; i++) {
            if (item_checked[i])
                sum += Integer.parseInt(item_price[i]);
        }
        return sum;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView priceView;
        ImageView imageView;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.rec_textView);
            imageView = itemView.findViewById(R.id.rec_imageView);
            priceView = itemView.findViewById(R.id.rec_priceTV);
            checkBox = itemView.findViewById(R.id.rec_checkbox);
        }
    }
}
