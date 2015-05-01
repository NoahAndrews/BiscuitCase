package me.noahandrews.biscuitcaseapp;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import me.noahandrews.biscuitcaselibrary.Item;
import me.noahandrews.biscuitcaselibrary.ItemListAdapter;

import java.util.ArrayList;

//TODO make sure that changes to either the menu or the checkout fragment sync to the other. This is where having the MainActivity implement a handler should be handy.

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CheckoutFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CheckoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckoutFragment extends Fragment implements ItemListAdapter.OnItemInteractionListener {
    static TextView nameField;
    ArrayList<Item> items;
    ItemListAdapter itemListAdapter;
    private OnFragmentInteractionListener mListener;

    public CheckoutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param items The list of items to be displayed.
     * @return A new instance of fragment CheckoutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CheckoutFragment newInstance(ArrayList<Item> items) {
        CheckoutFragment fragment = new CheckoutFragment();
        Bundle args = new Bundle();
        args.putSerializable("items", items);
        fragment.setArguments(args);
        ItemListAdapter.addListener(fragment);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            items = (ArrayList<Item>) getArguments().getSerializable("items");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_checkout, container, false);


        RecyclerView list = (RecyclerView) v.findViewById(R.id.order);
        list.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        itemListAdapter = new ItemListAdapter(getActivity(), items, true);
        list.setAdapter(itemListAdapter);

        nameField = (TextView) v.findViewById(R.id.name_field);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if(mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch(ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemInteraction(Item item, int position) {
        if(item.getQuantityDesired() < 1) {
            itemListAdapter.notifyItemRemoved(position);
            itemListAdapter.notifyItemRangeChanged(position, items.size());
            items.remove(item);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
