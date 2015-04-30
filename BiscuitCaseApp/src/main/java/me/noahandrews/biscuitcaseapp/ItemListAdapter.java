package me.noahandrews.biscuitcaseapp;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemHolder> {
    static ArrayList<OnItemInteractionListener> mListeners = new ArrayList<>();
    private ArrayList<Item> items;
    private Context context;
    private boolean addEraseButton;

    public ItemListAdapter(Context context, ArrayList<Item> items, boolean addEraseButton) {
        this.context = context;
        this.items = items;
        this.addEraseButton = addEraseButton;
    }

    public static void addListener(OnItemInteractionListener listener) {
        mListeners.add(listener);
    }

    public static void onItemInteraction(Item item, int position) {
        for(OnItemInteractionListener listener : mListeners) {
            listener.onItemInteraction(item, position);
        }
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder itemHolder, int position) {
        itemHolder.bindItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnItemInteractionListener {
        void onItemInteraction(Item item, int position);
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener, OrderResetListener {
        private final TextView nameView;
        private final TextView priceView;
        private final TextView quantityView;
        private final Button increaseQuantityButton;
        private final Button decreaseQuantityButton;
        private Item item;
        private Button removeButton = null;

        public ItemHolder(View itemView) {
            super(itemView);
            increaseQuantityButton = (Button) itemView.findViewById(R.id.item_increase);
            increaseQuantityButton.setOnClickListener(this);
            decreaseQuantityButton = (Button) itemView.findViewById(R.id.item_decrease);
            decreaseQuantityButton.setOnClickListener(this);
            if(addEraseButton) {
                removeButton = (Button) itemView.findViewById(R.id.remove_button);
                removeButton.setOnClickListener(this);
                removeButton.setVisibility(View.VISIBLE);
            }
            nameView = (TextView) itemView.findViewById(R.id.item_name);
            priceView = (TextView) itemView.findViewById(R.id.item_price);
            quantityView = (TextView) itemView.findViewById(R.id.quantity);
            quantityView.setOnClickListener(this);
            OrderResetHelper.addListener(this); //This allows us to respond to order reset events
        }

        public void bindItem(Item item) {
            this.item = item;
            nameView.setText(item.getName());
            priceView.setText("$" + String.format("%.2f", item.getPrice()));
            quantityView.setText(String.valueOf(item.getQuantityDesired()));
        }

        @Override
        public void onClick(View view) {
            Log.d("DEBUG", "Item clicked!");
            switch(view.getId()) {
                case R.id.item_increase:
                    try {
                        item.increaseQuantityDesired();
                    } catch(Exception e) {
                        Toast.makeText(MyApplication.getAppContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    quantityView.setText(String.valueOf(item.getQuantityDesired()));
                    onItemInteraction(item, getAdapterPosition());
                    break;
                case R.id.item_decrease:
                    item.decreaseQuantityDesired();
                    quantityView.setText(String.valueOf(item.getQuantityDesired()));
                    onItemInteraction(item, getAdapterPosition());
                    break;
                case R.id.remove_button:
                    item.setQuantityDesired(0);
                    onItemInteraction(item, getAdapterPosition());
                    break;
                case R.id.quantity:
                    AlertDialog.Builder quantityPrompt = new AlertDialog.Builder(context);
                    quantityPrompt.setTitle("Quantity");
                    quantityPrompt.setMessage("How many items?");
                    final EditText newQuantityField = new EditText(context);
                    newQuantityField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
                    newQuantityField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            try {
                                item.setQuantityDesired(Integer.parseInt(newQuantityField.getText().toString()));
                                onItemInteraction(item, getAdapterPosition());
                                quantityView.setText(String.valueOf(item.getQuantityDesired()));
                                return true;
                            } catch(NumberFormatException e) {
                                Toast.makeText(MyApplication.getAppContext(), "Give a quantity.", Toast.LENGTH_LONG).show();
                                return false;
                            } catch(IndexOutOfBoundsException e) {
                                Toast.makeText(MyApplication.getAppContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                return false;
                            }


                        }
                    });
                    quantityPrompt.setView(newQuantityField);
                    quantityPrompt.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                item.setQuantityDesired(Integer.parseInt(newQuantityField.getText().toString()));
                                onItemInteraction(item, getAdapterPosition());
                                quantityView.setText(String.valueOf(item.getQuantityDesired()));
                            } catch(NumberFormatException e) {
                                Toast.makeText(MyApplication.getAppContext(), "Give a quantity.", Toast.LENGTH_LONG).show();
                            } catch(IndexOutOfBoundsException e) {
                                Toast.makeText(MyApplication.getAppContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    quantityPrompt.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
                    quantityPrompt.show();
                    break;
            }
        }

        @Override
        public void onOrderReset() {
            item.setQuantityDesired(0);
            quantityView.setText("0");
            Log.d(MainActivity.DEBUG_TAG, item.getName() + "'s quantity has been reset.");
        }
    }

}