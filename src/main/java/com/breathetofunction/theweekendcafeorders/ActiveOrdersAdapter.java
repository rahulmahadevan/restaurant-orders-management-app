package com.breathetofunction.theweekendcafeorders;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ActiveOrdersAdapter extends RecyclerView.Adapter<ActiveOrdersAdapter.ViewHolder>{
    private Context context;
    private List<Order> orders;
    private static final String TAG = "ActiveOrdersAdapter";

    public ActiveOrdersAdapter(Context context, List<Order> orders){
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @NotNull
    @Override
    public ActiveOrdersAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_order_row, parent, false);
        return new ActiveOrdersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ActiveOrdersAdapter.ViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvTime.setText(order.getTime());
        holder.tvTableNumber.setText(order.getTableNumber());
        holder.tvItemsCount.setText("Items:"+Integer.toString(order.getItemsCount()));
        holder.tvTotal.setText("Rs."+Integer.toString(order.getGrandTotal()));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTime;
        TextView tvTableNumber;
        TextView tvItemsCount;
        TextView tvTotal;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        itemView.setBackgroundColor(context.getResources().getColor(R.color.green_medium));
                    }else if(event.getAction() == MotionEvent.ACTION_DOWN){
                        itemView.setBackgroundColor(context.getResources().getColor(R.color.green_light));

                    }
                    return false;
                }
            });
            
            tvTime = itemView.findViewById(R.id.tv_time);
            tvTableNumber = itemView.findViewById(R.id.tv_table);
            tvItemsCount = itemView.findViewById(R.id.tv_items_count);
            tvTotal = itemView.findViewById(R.id.tv_active_total);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Order order = orders.get(pos);
            String orderData = order.getOrderData();
            Log.d(TAG, "onClick: ORDER DATA"+orderData);
            Intent intent = new Intent("android.intent.action.OrderSummary");
            intent.putExtra("grandTotal",order.getGrandTotal());
            intent.putExtra("itemsJson", order.getItemsJson().toString());
            intent.putExtra("comingFrom", "MainActivity");
            intent.putExtra("orderData", orderData);
            context.startActivity(intent);
        }
    }
}
