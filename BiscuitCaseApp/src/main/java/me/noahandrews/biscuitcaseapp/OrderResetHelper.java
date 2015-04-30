package me.noahandrews.biscuitcaseapp;

import java.util.ArrayList;

interface OrderResetListener {
    void onOrderReset();
}

public class OrderResetHelper {
    static ArrayList<OrderResetListener> listeners = new ArrayList<>();

    public static void addListener(OrderResetListener listener) {
        listeners.add(listener);
    }

    public static void resetOrder() {
        for(OrderResetListener listener : listeners) {
            listener.onOrderReset();
        }
    }
}
