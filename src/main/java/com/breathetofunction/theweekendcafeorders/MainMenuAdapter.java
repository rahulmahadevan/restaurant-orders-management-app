package com.breathetofunction.theweekendcafeorders;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.MainMenuViewHolder> {
    private List<CacheMenuRes> cacheMenuRes;
    private Context context;
    String mDataSet[];

    public class MainMenuViewHolder extends RecyclerView.ViewHolder
    {
        private RecyclerView subMenuRecycler;
        private TextView subcat;

        public MainMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            subMenuRecycler = itemView.findViewById(R.id.submenurecycler);
            subcat = itemView.findViewById(R.id.subcategoryname);

        }
    }

    public MainMenuAdapter(Context context, List<CacheMenuRes> cacheMenuRes)
    {
        this.context = context;
        this.cacheMenuRes = cacheMenuRes;
    }

    @NonNull
    @Override
    public MainMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_menu_item,parent,false);
        return new MainMenuViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull MainMenuViewHolder holder, int position) {
        CacheMenuRes cacheMenuRe = cacheMenuRes.get(position);
        holder.subcat.setText(cacheMenuRe.getSubCategoryName());
        mDataSet=new String[cacheMenuRe.getMenus().size()];
        for(int i=0;i<cacheMenuRe.getMenus().size();i++)
            mDataSet[i]= String.valueOf(cacheMenuRe.getMenus().get(i).getPrice());
        SubMenuAdapter subMenuAdapter = new SubMenuAdapter(context,cacheMenuRe.getMenus(),mDataSet);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        holder.subMenuRecycler.setLayoutManager(layoutManager);
        holder.subMenuRecycler.setItemViewCacheSize(20);

        holder.subMenuRecycler.setAdapter(subMenuAdapter);

    }

    @Override
    public int getItemCount() {
        return cacheMenuRes.size();
    }
    public void setCacheMenuRes(List<CacheMenuRes> cacheMenuRes)
    {
        this.cacheMenuRes = cacheMenuRes;
        notifyDataSetChanged();
    }

}