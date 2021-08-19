package com.breathetofunction.theweekendcafeorders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder>{
    private Context context;
    private List<OrderItem> orderItems;

    public OrderDetailsAdapter(Context context, List<OrderItem> orderItems){
        this.context = context;
        this.orderItems = orderItems;
    }

    @NonNull
    @NotNull
    @Override
    public OrderDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_summary_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OrderDetailsAdapter.ViewHolder holder, int position) {
        OrderItem orderItem = orderItems.get(position);
        holder.itemTv.setText(orderItem.getItem());
        holder.subTotal.setText(orderItem.getPrice());
        holder.quant.setText(orderItem.getQuant());
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemTv;
        TextView subTotal;
        TextView quant;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            itemTv = itemView.findViewById(R.id.tv_item);
            subTotal = itemView.findViewById(R.id.tv_price);
            quant = itemView.findViewById(R.id.tv_quant);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
