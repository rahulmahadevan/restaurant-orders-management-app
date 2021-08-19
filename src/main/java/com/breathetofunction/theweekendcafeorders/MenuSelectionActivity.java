package com.breathetofunction.theweekendcafeorders;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.circularreveal.CircularRevealLinearLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuSelectionActivity extends AppCompatActivity {

    private RecyclerView menuRecycler;
    private FloatingActionButton floatingActionButton;
    private MainMenuAdapter mainMenuAdapter;
    private RecyclerView quickmenu;
    private QuickMenuAdapter quickMenuAdapter;
    private List<QuickMenuItem> quickMenuItems;
    private List<CacheMenuRes> cacheMenuRes;
    private CircularRevealLinearLayout circularRevealLinearLayout;
    private Button next;
    private final String TAG = "MenuSelectionActivity";
    private static HashMap<String, Integer> items = new HashMap<>();
    String menuData;
    private static HashMap<String, Integer> menuPrices = new HashMap<>();

    public static void addItem(String name, int quant){
        items.put(name,quant);
    }

    public static void clearItems(){
        items.clear();
        menuPrices.clear();
    }

    public static void removeItem(String name){
        items.remove(name);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_selection);

        Bundle bundle = getIntent().getExtras();
        menuData = bundle.getString("menuData");
        Log.d(TAG, "JSON: "+ menuData);
        menuRecycler = findViewById(R.id.menurecycler);
        floatingActionButton = findViewById(R.id.menuFab);
        quickmenu = findViewById(R.id.quickemenu);
        cacheMenuRes = new ArrayList<>();
        next = findViewById(R.id.btn_next);
        try {
            JSONArray data = new JSONArray(menuData);
            int count = 0;
            for(int i=0; i<data.length();i++){
                JSONObject section = data.getJSONObject(i);
                Log.d(TAG, "Section "+i+":"+section);
                JSONArray items = new JSONArray(section.getString("items"));
                List<Menu> menus = new ArrayList<>();
                for(int j=0; j<items.length(); j++){
                    JSONObject item = items.getJSONObject(j);
                    Log.d(TAG, "ITEM "+j+":"+item);
                    String status = item.getString("status");
                    String itemName = Utils.capitalizeFirst(item.getString("name"));
                    String itemPrice = item.getString("price");
                    Log.d(TAG, "MENU ITEM"+"name:"+ itemName+", price"+itemName);
                    if(status.equals("active")){
                        Log.d(TAG, "Status: "+ status);
                        menuPrices.put(item.getString("name"), Integer.parseInt(itemPrice));
                        menus.add(new Menu(count,1,1,itemName,Integer.parseInt(itemPrice),"veg"));
                    }
                    count++;
                }
                String sectionName = section.getString("name");
                cacheMenuRes.add(new CacheMenuRes(sectionName,menus));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mainMenuAdapter = new MainMenuAdapter(MenuSelectionActivity.this,cacheMenuRes);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MenuSelectionActivity.this);
        menuRecycler.setLayoutManager(layoutManager);
        menuRecycler.setItemViewCacheSize(20);
        menuRecycler.setAdapter(mainMenuAdapter);

        menuRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                String submenu = ((TextView)recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.subcategoryname)).getText().toString();
                setTitle(submenu);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(items.size() == 0){
                    Toast.makeText(getApplicationContext(), "Please add Items to the order", Toast.LENGTH_SHORT).show();
                }else{
                    int grandTotal = 0;
                    JSONArray orderItems = new JSONArray();
                    for(String name : items.keySet()){
                        Log.d(TAG, "GOT ITEMS: "+name+" :"+ items.get(name));
                        int quantity = items.get(name);
                        int price = menuPrices.get(name);
                        int subTotal = price * quantity;
                        JSONObject item = new JSONObject();
                        try {
                            item.put("name", name);
                            item.put("subTotal", subTotal);
                            item.put("quantity", quantity);

                            orderItems.put(item);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "JSON ARRAY "+ orderItems);
                        grandTotal = grandTotal + subTotal;

                    }
                    //Intent to new Activity and put extra orderItems and grandTotal
                    Intent orderSummay = new Intent("android.intent.action.OrderSummary");
                    orderSummay.putExtra("itemsJson", orderItems.toString());
                    orderSummay.putExtra("grandTotal", grandTotal);
                    startActivity(orderSummay);
                }
            }
        });

        circularRevealLinearLayout = findViewById(R.id.subsheet);
        circularRevealLinearLayout.setVisibility(View.GONE);

        RecyclerView.LayoutManager lm = new LinearLayoutManager(MenuSelectionActivity.this);

        quickMenuItems = new ArrayList<>();
        for(int i=0;i<cacheMenuRes.size();i++){
            quickMenuItems.add(new QuickMenuItem(i+1,cacheMenuRes.get(i).getSubCategoryName()));
        }

        quickMenuAdapter = new QuickMenuAdapter(quickMenuItems,MenuSelectionActivity.this);
        quickmenu.setLayoutManager(lm);
        quickmenu.setAdapter(quickMenuAdapter);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(circularRevealLinearLayout.getVisibility() == View.GONE) {
                    floatingActionButton.setExpanded(true);
                    circularRevealLinearLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    floatingActionButton.setExpanded(false);
                    circularRevealLinearLayout.setVisibility(View.GONE);
                }
            }
        });
        floatingActionButton.setVisibility(View.GONE);
    }
    public void changeFocusOfRecycler(int index)
    {
        menuRecycler.scrollToPosition(index);
    }
    public void hideQuickadapter(){circularRevealLinearLayout.setVisibility(View.GONE);}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        items.clear();
        menuPrices.clear();
        finish();
    }
}

