package com.breathetofunction.theweekendcafeorders;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.breathetofunction.theweekendcafeorders.Utils.makePostRequest;

public class OrderSummaryActivity extends AppCompatActivity {

    String TAG = "OrderSummary";
    String itemsJsonString;
    int grandTotal;
    Button submit;
    EditText tableNumber;
    JSONArray items;
    RecyclerView recyclerView;
    OrderDetailsAdapter orderDetailsAdapter;
    ArrayList<OrderItem> selectedItems;
    TextView total;
    static final String defaultStr = "default";
    String comingFrom;
    String orderData;
    FloatingActionButton addItemButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        Bundle bundle = getIntent().getExtras();
        String requestUrl = getApplicationContext().getResources().getString(R.string.url)+"putOrder";
        String authToken = getApplicationContext().getResources().getString(R.string.token);
        itemsJsonString = bundle.getString("itemsJson");
        grandTotal = bundle.getInt("grandTotal");
        comingFrom = bundle.getString("comingFrom", defaultStr);
        orderData = bundle.getString("orderData");
        submit = (Button) findViewById(R.id.btn_submit);
        tableNumber = (EditText) findViewById(R.id.et_table_number);
        total = (TextView) findViewById(R.id.tv_total);
        total.setText("Total: Rs."+Integer.toString(grandTotal));
        addItemButton = (FloatingActionButton) findViewById(R.id.add_item);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        JSONObject order = new JSONObject();
        selectedItems = new ArrayList<>();
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String status = "active";
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        Log.d(TAG, "TimeStamp: "+timestamp);
        try {
            items = new JSONArray(itemsJsonString);
            Log.d(TAG, "onCreate: "+ items.toString());
            for(int i=0; i< items.length(); i++){
                JSONObject item = items.getJSONObject(i);
                String name = item.getString("name");
                String subTotal = "Rs." + Integer.toString(item.getInt("subTotal"));
                String quantity = Integer.toString(item.getInt("quantity"));
                selectedItems.add(new OrderItem(Utils.capitalizeFirst(name), quantity+"x", subTotal));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        orderDetailsAdapter = new OrderDetailsAdapter(OrderSummaryActivity.this, selectedItems);
        recyclerView.setAdapter(orderDetailsAdapter);

        if(comingFrom.equals("MainActivity")){
            submit.setText("Complete this Order");
            try {
                JSONObject currOrder = new JSONObject(orderData);
                String tableNumberStr = currOrder.getString("tableNumber");
                tableNumber.setText(tableNumberStr);
                tableNumber.setFocusable(false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(comingFrom.equals("CompletedOrders")){
            submit.setText("Close");
            try {
                JSONObject currOrder = new JSONObject(orderData);
                String tableNumberStr = currOrder.getString("tableNumber");
                tableNumber.setText(tableNumberStr);
                tableNumber.setFocusable(false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comingFrom.equals(defaultStr)){
                    onBackPressed();
                }else {
                    Toast.makeText(getApplicationContext(),"Coming Soon", Toast.LENGTH_SHORT).show();
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                if(comingFrom.equals(defaultStr)){
                    String tableNumberStr = tableNumber.getText().toString();
                    if (tableNumberStr.equals("")) {
                        Toast.makeText(getApplicationContext(), "Please enter Table Number", Toast.LENGTH_SHORT).show();
                    } else {
                        String orderId = tableNumberStr + timestamp;
                        try {
                            order.put("orderId", orderId);
                            order.put("date", date);
                            order.put("time", time);
                            order.put("status", status);
                            order.put("grandTotal", grandTotal);
                            order.put("tableNumber", tableNumberStr);
                            order.put("items", items);
                            Log.d(TAG, "ORDER JSON: " + order);
                            JSONObject request = new JSONObject();
                            request.put("token", authToken);
                            request.put("orderId", orderId);
                            request.put("order", order);
                            Log.d(TAG, "Request JSON: " + request);

                            new AsyncTask<String, String, String>() {
                                final ProgressDialog dialog = new ProgressDialog(OrderSummaryActivity.this);

                                @Override
                                protected void onPreExecute() {
                                    super.onPreExecute();
                                    dialog.setMessage("Updating Order...");
                                    dialog.setCanceledOnTouchOutside(false);
                                    dialog.show();
                                }

                                @Override
                                protected String doInBackground(String... strings) {
                                    try {
                                        String response = makePostRequest(requestUrl, request.toString(), getApplicationContext());
                                        Log.d(TAG, "HTTP Response: " + response);
                                        return response;
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        return "";
                                    }
                                }

                                @Override
                                protected void onPostExecute(String response) {
                                    super.onPostExecute(response);
                                    dialog.dismiss();
                                    if (response.equals("")) {
                                        Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                                    } else {
                                        try {
                                            JSONObject jsonResponse = new JSONObject(response);
                                            String responseCode = jsonResponse.getString("responseCode");
                                            if (responseCode.equals("200")) {
                                                Toast.makeText(getApplicationContext(), "Order Taken!", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent("android.intent.action.MainActivity");
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                MenuSelectionActivity.clearItems();
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            }.execute("");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }else if(comingFrom.equals("CompletedOrders")){
                    onBackPressed();
                }else {
                    try {
                        JSONObject order1 = new JSONObject(orderData);
                        order1.put("status","inactive");
                        order1.remove("_id");
                        String orderId = order1.getString("orderId");
                        JSONObject request = new JSONObject();
                        request.put("orderId", orderId);
                        request.put("token", authToken);
                        request.put("order", order1);

                        AlertDialog.Builder builder = new AlertDialog.Builder(OrderSummaryActivity.this);
                        builder.setMessage("Are you sure this Order is Completed?");
                        builder.setCancelable(true);

                        builder.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new AsyncTask<String, String, String>() {
                                            final ProgressDialog dialog = new ProgressDialog(OrderSummaryActivity.this);

                                            @Override
                                            protected void onPreExecute() {
                                                super.onPreExecute();
                                                dialog.setMessage("Updating Order...");
                                                dialog.setCanceledOnTouchOutside(false);
                                                dialog.show();
                                            }

                                            @Override
                                            protected String doInBackground(String... strings) {
                                                try {
                                                    Log.d(TAG, "HTTP URL: "+requestUrl);
                                                    Log.d(TAG, "POST Data: "+ request.toString());
                                                    String response = makePostRequest(requestUrl, request.toString(), getApplicationContext());
                                                    Log.d(TAG, "HTTP Response: " + response);
                                                    return response;
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                    return "";
                                                }
                                            }

                                            @Override
                                            protected void onPostExecute(String response) {
                                                super.onPostExecute(response);
                                                dialog.dismiss();
                                                if (response.equals("")) {
                                                    Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    try {
                                                        JSONObject jsonResponse = new JSONObject(response);
                                                        String responseCode = jsonResponse.getString("responseCode");
                                                        if (responseCode.equals("200")) {
                                                            Toast.makeText(getApplicationContext(), "Order Completed!", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent("android.intent.action.MainActivity");
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            MenuSelectionActivity.clearItems();
                                                            startActivity(intent);
                                                        } else {
                                                            Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }
                                        }.execute("");
                                    }
                                });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
