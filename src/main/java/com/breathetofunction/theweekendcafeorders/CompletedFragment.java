package com.breathetofunction.theweekendcafeorders;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static com.breathetofunction.theweekendcafeorders.Utils.makePostRequest;

public class CompletedFragment extends Fragment {
    private static final String TAG = "Completed Fragment";
    static RecyclerView recyclerView;
    static Context context;

    @Nullable
    @SuppressLint("StaticFieldLeak")
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        String requestUrl = getActivity().getApplicationContext().getResources().getString(R.string.url)+"fetchOrders";
        String authToken = getActivity().getApplicationContext().getResources().getString(R.string.token);

        View view = inflater.inflate(R.layout.fragment_completed,container,false);
        context = getActivity();
        recyclerView = view.findViewById(R.id.rv_inactive);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));

        new AsyncTask<String, String, String>() {
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setMessage("Getting Orders...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                try {
                    JSONObject requestBody = new JSONObject();
                    requestBody.put("token",authToken);
                    requestBody.put("status","inactive");
                    String response = makePostRequest(requestUrl, requestBody.toString(), getActivity().getApplicationContext());
                    Log.d(TAG, "HTTP Response: Inactive Orders- " + response);
                    return response;
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    return "";
                }
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                dialog.dismiss();
                if(response.equals("")){
                    Toast.makeText(getActivity().getApplicationContext(), "Could not fetch Order History",Toast.LENGTH_SHORT).show();
                }else {
                    try {
                        JSONObject responseObj = new JSONObject(response);
                        String responseCode = responseObj.getString("responseCode");
                        Log.d(TAG, "onPostExecute Response: "+response);
                        if(responseCode.equals("200")){
                            JSONArray inactiveData = new JSONArray(responseObj.getString("data"));
                            Log.d(TAG, "onPostExecute: INA: "+inactiveData.toString());
                            CompletedFragment.setOrderToView(inactiveData.toString());
                        }else {
                            //No Data available
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.execute("");
        return inflater.inflate(R.layout.fragment_completed,container,false);
    }

    public static void setOrderToView(String inactiveData) throws JSONException {
        Log.d(TAG, "setOrderToView-Inactive: SUCCESSFULLY CALLED");
        InactiveOrdersAdapter adapter;
        ArrayList<Order> orders = new ArrayList<>();
        JSONArray inactiveOrders = new JSONArray(inactiveData);
        for(int i=0;i<inactiveOrders.length();i++){
            JSONObject singleOrder = inactiveOrders.getJSONObject(i);
            String time = singleOrder.getString("time");
            int total = singleOrder.getInt("grandTotal");
            String table = singleOrder.getString("tableNumber");
            JSONArray items = singleOrder.getJSONArray("items");
            int itemsCount = items.length();
            orders.add(new Order(time, table, itemsCount, total, singleOrder.toString()));
        }
        adapter = new InactiveOrdersAdapter(context, orders);
        recyclerView.setAdapter(adapter);
    }
}
