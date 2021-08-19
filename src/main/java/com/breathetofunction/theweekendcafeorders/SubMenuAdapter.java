package com.breathetofunction.theweekendcafeorders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SubMenuAdapter extends RecyclerView.Adapter<SubMenuAdapter.SubMenuViewHolder> {
    private List<Menu> menus;
    private Context context;
    private String[] mDataset;
    private int[] countervalues;
    private MenuSelectionActivity menuActivity;
    private Date date;
    private DateFormat dateFormat;
    private int orderid;
    private static HashMap<String, Integer> orderItems;
    private SharedPreferences sharedPreferences;

    public class SubMenuViewHolder extends  RecyclerView.ViewHolder
    {
        private TextView name;
        private TextView price;
        private ImageView type;
        private ElegantNumberButton button;
        private int itemcount=0;


        public SubMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.menuname);
            price = itemView.findViewById(R.id.price);
            button = itemView.findViewById(R.id.btn_quant);
        }
    }
    public SubMenuAdapter(Context context, List<Menu> menus, String[] mDataset)
    {
        this.context = context;
        this.menus = menus;
        this.mDataset=mDataset;
        this.menuActivity = (MenuSelectionActivity) context;
        this.date = new Date();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.orderid = (int) (date.getTime()/1000);
        countervalues=new int[mDataset.length];
    }
    @NonNull
    @Override
    public SubMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_menu_item,parent,false);
        return new SubMenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubMenuViewHolder holder, int position) {
        Menu menu = menus.get(position);
        String itemName = menu.getName();
        String itemPrice = Integer.toString((int) menu.getPrice());
        this.orderItems = new HashMap<>();
        holder.name.setText(itemName);
        if(!(countervalues[position]>=1))
            holder.price.setText("Rs."+itemPrice); //String.valueOf(mDataset[position].substring(0,mDataset[position].length()-2))

        holder.button.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                MenuSelectionActivity.addItem(itemName.toUpperCase(), newValue);
                if(newValue == 0){
                    MenuSelectionActivity.removeItem(itemName.toUpperCase());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return menus.size();
    }
}