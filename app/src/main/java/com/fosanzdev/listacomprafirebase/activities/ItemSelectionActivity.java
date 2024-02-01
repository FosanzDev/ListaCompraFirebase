package com.fosanzdev.listacomprafirebase.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fosanzdev.listacomprafirebase.R;
import com.fosanzdev.listacomprafirebase.adapters.ItemGridAdapter;
import com.fosanzdev.listacomprafirebase.fragments.CategorySelectionFragment;
import com.fosanzdev.listacomprafirebase.fragments.ItemSelectionFragment;
import com.fosanzdev.listacomprafirebase.managers.ItemManager;
import com.fosanzdev.listacomprafirebase.models.Category;
import com.fosanzdev.listacomprafirebase.models.Item;
import com.fosanzdev.listacomprafirebase.models.ShoppingList;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ItemSelectionActivity extends AppCompatActivity implements CategorySelectionFragment.ICategorySelectionFragmentListener, ItemSelectionFragment.IItemSelectionFragmentListener {
    /*
    This activity is used to select on an item from the list of items.
    The activity is launched and receives a series of items that are going to be
    deprecated when selecting an item.
    This means that if you already selected an item, you can't select it again.
    On the item selection it returns via setResult the id of the selected item.
     */

    FragmentContainerView fcvGridSelection;
    ArrayList<String> excludeItems;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_selection);
        fcvGridSelection = findViewById(R.id.fcvGridSelection);
        excludeItems = getIntent().getStringArrayListExtra("excludeItems");

        Button bShowAllItems = findViewById(R.id.bShowAllItems);

        // Create a boolean flag to track the current state of the button
        final boolean[] isCategoryBased = {false};

        View.OnClickListener twoStateListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCategoryBased[0]) {
                    // Perform the category-based action
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fcvGridSelection, new CategorySelectionFragment())
                            .commit();

                    bShowAllItems.setText("Mostrar todos los items");
                    isCategoryBased[0] = false;
                } else {
                    // Perform the all-items action
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fcvGridSelection, new ItemSelectionFragment(excludeItems))
                            .commit();

                    bShowAllItems.setText("Mostrar por categorías");
                    isCategoryBased[0] = true;
                }
            }
        };

        bShowAllItems.setOnClickListener(twoStateListener);
    }

    /**
     * This method is used to finish the activity and return the selected item id
     *
     * @param selectedItem The selected item to return
     */
    public void finishWithResult(Item selectedItem) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selectedItemId", selectedItem.getId()); // Added Item id to the result intent
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    /**
     * This method is used to end the category selection and start the item selection based on the category selected
     *
     * @param category The category selected
     */
    @Override
    public void onCategorySelected(Category category) {
        System.out.println("Category selected: " + category.getNombre());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fcvGridSelection, new ItemSelectionFragment(category, excludeItems))
                .addToBackStack(null)
                .commit();
    }

    /**
     * This method is used to end the item selection and return the selected item
     *
     * @param item The selected item
     */
    @Override
    public void onItemSelection(Item item) {
        finishWithResult(item);
    }

    @Override
    public void onBackPressed() {
        Button bShowAllItems = findViewById(R.id.bShowAllItems);
        bShowAllItems.setVisibility(Button.VISIBLE);
        super.onBackPressed();
    }
}
