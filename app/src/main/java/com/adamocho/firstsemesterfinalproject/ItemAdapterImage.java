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

public class ItemAdapterImage extends RecyclerView.Adapter<ItemAdapterImage.ViewHolder> {
    String[] item_desc;
    String[] item_price;
    int[] item_imgs;
    boolean[] item_checked;
    Context context;

    public ItemAdapterImage(String[] item_desc, int[] item_imgs, String[] item_price, Context context) {
        this.item_desc = item_desc;
        this.item_imgs = item_imgs;
        this.item_price = item_price;
        this.context = context;
        this.item_checked = new boolean[item_desc.length];
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
        holder.checkBox.setOnClickListener(view -> {
            CheckBox checkBox = (CheckBox) view;
            Log.i(TAG, holder.getAdapterPosition() + " " + checkBox.isChecked());
            item_checked[index] = checkBox.isChecked();

            ((MainActivity) context).refreshOrderPrice();
//             Do I just add it to the JSON string and then update the sum?
        });
    }

    @Override
    public int getItemCount() {
        return item_imgs.length;
    }

    public int getItemPrice(int index) {
        return index >= 0 && index < item_price.length ? Integer.parseInt(item_price[index]) : 0;
    }

    public boolean getItemChecked(int index) {
        return index >= 0 && index < item_checked.length && item_checked[index];
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
