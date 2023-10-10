package com.example.suitcase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.suitcase.Adapter.ItemsAdapter;
import com.example.suitcase.Adapter.RecyclerItemsClickView;
import com.example.suitcase.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding  binding;
    private DatabaseHelper items_dbHelper;
    private RecyclerItemsClickView recyclerItemsClickView;
    private ItemsAdapter itemsAdapter;
    private ArrayList<ItemsModel> itemsModels;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Nav Menu Item Click
        binding.nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                switch (id)
                {

                    case R.id.item_home:
                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.item_logOut:
                        confirmLogoutDialog();
                        binding.drawer.closeDrawer(GravityCompat.START);
                        break;
                    default:
                        return true;

                }
                return true;
            }
        });
        // Set an OnClickListener for the shareAll button
        binding.shareAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAllItems();
            }
        });

        final DrawerLayout drawerLayout=findViewById(R.id.drawer);
        findViewById(R.id.nav_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //initialize data
        itemsModels=new ArrayList<>();
        items_dbHelper=new DatabaseHelper(this);
        setRecyclerView();
        setupItemTouchHelper();
        binding.fab.setOnClickListener(view->startActivity(Add_items.getIntent(getApplicationContext())));
    }

    private void shareAllItems() {
        StringBuilder messageBuilder = new StringBuilder("--------ALL ITEMS LISTS--------\n\n");
        int itemNumber = 1;

        for (int i = 0; i < itemsModels.size(); i++) {
            ItemsModel item = itemsModels.get(i);
            String itemName = item.getName();
            double itemPrice = item.getPrice();
            String itemDescription = item.getDescription();

            // Append item number and details to the message
            messageBuilder.append(itemNumber).append(".\n")
                    .append("Item: ").append(itemName).append("\n")
                    .append("Price: ").append(itemPrice).append("\n")
                    .append("Description: ").append(itemDescription).append("\n\n");

            itemNumber++; // Increment the item number
        }

        String message = messageBuilder.toString().trim();

        // Create an intent to share the item information
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message);

        // Check if there is an app available to handle the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No messaging app found to share.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupItemTouchHelper(){
        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position=viewHolder.getAdapterPosition();
                        ItemsModel itemsModel=itemsModels.get(position);
                        if (direction==ItemTouchHelper.LEFT){
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Delete");
                            builder.setMessage("Do you really want to delete this item?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    items_dbHelper.deleteItem(itemsModel.getId());
                                    itemsModels.remove(position);
                                    itemsAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                                    Toast.makeText(MainActivity.this, "Item Deleted", Toast.LENGTH_SHORT).show();
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    itemsAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }else if(direction==ItemTouchHelper.RIGHT){
                            itemsModel.setPurchased(true);
                            items_dbHelper.update(
                                    itemsModel.getId(),
                                    itemsModel.getName(),
                                    itemsModel.getPrice(),
                                    itemsModel.getDescription(),
                                    itemsModel.getImage().toString(),
                                    itemsModel.isPurchased()
                            );
                            itemsAdapter.notifyItemChanged(position);
                            Toast.makeText(MainActivity.this, "Item Purchased ", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        itemTouchHelper.attachToRecyclerView(binding.recycler);
    }

    @Override
    protected void onStart() {
        super.onStart();
        retrieveData();
    }
    private void retrieveData(){
        Cursor cursor=items_dbHelper.getAllItem();
        if (cursor==null){
            return;
        }
        itemsModels.clear();
        while (cursor.moveToNext()){
            ItemsModel itemsModel=new ItemsModel();
            itemsModel.setId(cursor.getInt(0));
            itemsModel.setName(cursor.getString(1));
            itemsModel.setPrice(cursor.getDouble(2));
            itemsModel.setDescription(cursor.getString(3));
            itemsModel.setImage(Uri.parse(cursor.getString(4)));

            itemsModels.add(cursor.getPosition(),itemsModel);
            itemsAdapter.notifyItemChanged(cursor.getPosition());
            Log.d("MainActivity","Items" +itemsModel.getId()+"added at "+cursor.getPosition());
        }

    }
    private void setRecyclerView(){
        itemsAdapter=new ItemsAdapter(itemsModels,
                (view ,position)->startActivity(Items_Details_Page.getIntent(
                        getApplicationContext(),
                        itemsModels.get(position).getId())
                ));
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setAdapter(itemsAdapter);
    }
    void confirmLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log out from SuitCase?");
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, Login_Page.class);
                startActivity(intent);
                finish();
                Toast.makeText(MainActivity.this, "Logged out from SuitCase!", Toast.LENGTH_SHORT).show();
                overridePendingTransition(0, 0);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user click No on the dialogue box user stays in the same page
            }
        });
        // Show the dialog
        builder.create().show();
    }
}

