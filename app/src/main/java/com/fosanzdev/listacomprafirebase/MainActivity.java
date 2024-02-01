package com.fosanzdev.listacomprafirebase;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.fosanzdev.listacomprafirebase.fragments.MainFragment;
import com.fosanzdev.listacomprafirebase.fragments.ShoppingListFragment;
import com.fosanzdev.listacomprafirebase.managers.CategoryManager;
import com.fosanzdev.listacomprafirebase.managers.ItemManager;
import com.fosanzdev.listacomprafirebase.managers.ShoppingListManager;
import com.fosanzdev.listacomprafirebase.models.Category;
import com.fosanzdev.listacomprafirebase.models.ShoppingList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements MainFragment.IMainFragmentListener {

    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser == null) {
            ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult o) {
                            if (o.getResultCode() == RESULT_OK) {
                                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                Toast.makeText(MainActivity.this, "Bienvenido " + firebaseUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                                setContentView(R.layout.activity_main); // Set the content view after the user has logged in
                            } else {
                                Toast.makeText(MainActivity.this, "Acceso denegado", Toast.LENGTH_SHORT).show();
                                finish(); // Finish the activity if the user denies access
                            }
                        }
                    }
            );

            resultLauncher.launch(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .build());
        } else {
            setContentView(R.layout.activity_main);
        }
    }

    @Override
    public void onShoppingListSelected(ShoppingList shoppingList, ShoppingListManager manager) {
        ShoppingListFragment fragment = new ShoppingListFragment(shoppingList);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fcvMainContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}