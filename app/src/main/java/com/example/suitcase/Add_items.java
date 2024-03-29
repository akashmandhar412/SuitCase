package com.example.suitcase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.suitcase.databinding.ActivityAddItemsBinding;

public class Add_items extends AppCompatActivity {
    ActivityAddItemsBinding binding;
    DatabaseHelper items_dbHelper;
    private Uri imageUri;
    public static Intent getIntent(Context context) {
        return new Intent(context, Add_items.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        items_dbHelper = new DatabaseHelper(this);
        imageUri = Uri.EMPTY;
        binding.itemsImg.setOnClickListener(this::pickImage);
        binding.addItems.setOnClickListener(this::saveItem);

    }

    private void saveItem(View view) {
        String name = binding.itemName.getText().toString().trim();
        if (name.isEmpty()) {
            binding.itemName.setError("Name field is empty");
            binding.itemName.requestFocus();
        }
        double price = 0;
        try {
            price = Double.parseDouble(binding.itemPrice.getText().toString().trim());

        } catch (NullPointerException e) {
            Toast.makeText(this, "Something wrong with price ", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Price should be a number", Toast.LENGTH_SHORT).show();
        }
        if (price <= 0) {
            binding.itemPrice.setError("price should be greater than 0 . ");
            binding.itemPrice.requestFocus();
        }
        String description = binding.itemDescription.getText().toString().trim();
        if (description.isEmpty()) {
            binding.itemDescription.setError("Description is empty ");
            binding.itemDescription.requestFocus();
        }
        if (items_dbHelper.insertItem(name, price, description, imageUri.toString(), false)) {
            Toast.makeText(this, "Save Successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void pickImage(View view) {
        ImagePickUtility.pickImage(view, Add_items.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null) {
            imageUri = data.getData();
            binding.itemsImg.setImageURI(imageUri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}