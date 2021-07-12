package com.breathetofunction.theweekendcafeorders;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


public class OrderInfoActivity extends AppCompatActivity {

    TextView section;
    Button next;
    RecyclerView recyclerView;
    private final String TAG = "OrderInfoActivity";
    String response;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);

        Bundle bundle = getIntent().getExtras();
        response = bundle.getString("response");
        Document xmlMenu = convertStringToXMLDocument(response);
        NodeList items = xmlMenu.getElementsByTagName("item");
        String names[] = new String[items.getLength()];
        String ids[] = new String[items.getLength()];
        String prices[] = new String[items.getLength()];
        for(int i=0;i<items.getLength();i++){
            Node menuItem = items.item(i);
            if(menuItem.getNodeType() == Node.ELEMENT_NODE){
                //For Item Names
                Element element = (Element) menuItem;
                NodeList nodeNames = element.getElementsByTagName("name");
                Element name = (Element) nodeNames.item(0);
                NodeList nodeNameValue = name.getChildNodes();
                names[i] = nodeNameValue.item(0).getNodeValue();

                //For Item IDs
                Element element1 = (Element) menuItem;
                NodeList nodeNames1 = element1.getElementsByTagName("id");
                Element id = (Element) nodeNames1.item(0);
                NodeList nodeIdValue = id.getChildNodes();
                ids[i] = nodeIdValue.item(0).getNodeValue();

                //For Item Prices
                Element element2 = (Element) menuItem;
                NodeList nodeNames2 = element2.getElementsByTagName("price");
                Element price = (Element) nodeNames2.item(0);
                NodeList nodePriceValue = price.getChildNodes();
                prices[i] = nodePriceValue.item(0).getNodeValue();
            }
        }
        for(int i=0;i<names.length;i++){
            Log.d(TAG, "ITEM ID: "+ids[i]);
            Log.d(TAG, "ITEM NAME: "+names[i]);
            Log.d(TAG, "ITEM PRICE: "+prices[i]);
        }

        next = findViewById(R.id.btn_next);
        section = findViewById(R.id.tv_section);
        recyclerView = findViewById(R.id.rv_items);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Nothing Yet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static Document convertStringToXMLDocument(String xmlString)
    {
        //Parser that produces DOM object trees from XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try
        {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();

            //Parse the content to Document object
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}

