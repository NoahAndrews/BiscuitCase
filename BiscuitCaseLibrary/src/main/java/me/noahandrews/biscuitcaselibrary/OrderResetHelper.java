package me.noahandrews.biscuitcaselibrary;

import java.util.ArrayList;

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

    public interface OrderResetListener {
        void onOrderReset();
    }
}
