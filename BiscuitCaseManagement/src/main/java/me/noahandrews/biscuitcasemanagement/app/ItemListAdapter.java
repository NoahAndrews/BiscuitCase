package me.noahandrews.biscuitcasemanagement.app;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import me.noahandrews.biscuitcaselibrary.Constants;
import me.noahandrews.biscuitcaselibrary.Item;

import java.util.ArrayList;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemHolder> {
    private ArrayList<Item> items;

    public ItemListAdapter(ArrayList<Item> items){
        this.items = items;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.bindItem(items.get(position));
    }

    @Override
    public int getItemCount(){
        return items.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView nameView;
        private final Button removeButton;
        private Item item;

        public ItemHolder(View itemView){
            super(itemView);
            nameView = (TextView)itemView.findViewById(R.id.item_name);
            removeButton = (Button)itemView.findViewById(R.id.remove_button);
            removeButton.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void bindItem(Item item){
            this.item = item;
            nameView.setText(item.getName());
        }

        @Override
        public void onClick(View v){
            Log.d(Constants.DEBUG_TAG, v + " clicked");
        }
    }
}
