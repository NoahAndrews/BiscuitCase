package me.noahandrews.biscuitcasemanagement.app;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import me.noahandrews.biscuitcaselibrary.Category;
import me.noahandrews.biscuitcaselibrary.Constants;

import java.util.ArrayList;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryHolder> {
    private ArrayList<Category> categories;

    public CategoryListAdapter(ArrayList<Category> categories){
        this.categories = categories;
    }

    @Override
    public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category, parent, false);
        return new CategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryHolder holder, int position) {
        holder.bindCategory(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private final TextView nameView;
        private final Button removeButton;
        private Category category;

        public CategoryHolder(View categoryView){
            super(categoryView);
            nameView = (TextView)categoryView.findViewById(R.id.category_name);
            removeButton = (Button)categoryView.findViewById(R.id.remove_button);
            removeButton.setOnClickListener(this);
            categoryView.setOnClickListener(this);
            categoryView.setOnLongClickListener(this);
        }

        public void bindCategory(Category category){
            this.category = category;
            nameView.setText(category.getName());
        }

        @Override
        public boolean onLongClick(View v) {
            //TODO Launch dialog box to allow user to rename the category
            return true;
        }

        @Override
        public void onClick(View v) {
            Log.d(Constants.DEBUG_TAG, v + " clicked.");
        }
    }
}
