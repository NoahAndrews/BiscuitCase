package me.noahandrews.biscuitcasemanagement.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import me.noahandrews.biscuitcaselibrary.Item;

public class NewItemDialogFragment extends DialogFragment {

    public interface NewItemDialogListener {
        public void onDialogPositiveClick(Item newItem);
    }

    NewItemDialogListener mListener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add new item");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_new_item,null);
        builder.setView(dialogView);
        final EditText nameField = (EditText)dialogView.findViewById(R.id.nameField);
        final EditText priceField = (EditText)dialogView.findViewById(R.id.priceField);
        final CheckBox quantityLimitedCheckbox = (CheckBox)dialogView.findViewById(R.id.quantityLimitedCheckbox);
        final TextView quantityAvailableLable = (TextView)dialogView.findViewById(R.id.quantityLabel);
        final EditText quantityAvailableField = (EditText)dialogView.findViewById(R.id.quantityField);
        quantityLimitedCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()){
                    quantityAvailableField.setVisibility(View.VISIBLE);
                    quantityAvailableLable.setVisibility(View.VISIBLE);
                } else {
                    quantityAvailableField.setVisibility(View.GONE);
                    quantityAvailableLable.setVisibility(View.GONE);
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = nameField.getText().toString();
                double price = Double.parseDouble(priceField.getText().toString());
                boolean limitedQuantity = quantityLimitedCheckbox.isChecked();
                int quantity = Integer.parseInt(quantityAvailableField.getText().toString());
                Item newItem = new Item(name, price, null, limitedQuantity, quantity);
                mListener.onDialogPositiveClick(newItem);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NewItemDialogFragment.this.getDialog().cancel();
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NewItemDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NewItemDialogListener");
        }
    }
}
