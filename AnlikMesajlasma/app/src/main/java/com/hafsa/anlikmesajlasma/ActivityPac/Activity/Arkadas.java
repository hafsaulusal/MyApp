package com.hafsa.anlikmesajlasma.ActivityPac.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hafsa.anlikmesajlasma.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hafsa on 13.03.2017.
 */
public class Arkadas extends AppCompatActivity{
    static{
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }



    public static final String kullanici1 = "kullanici1";
    public static final String kullanici2 = "kullanici2";

    private static final int EKLE=0,ENGELLE= 1;

    EditText kullaniciAra;
    ListView kullanicilar;

    private ListAdapter adapter = null;
    ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

    private static int arkadasId;
    private static int Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arkadas_ekle);

        kullaniciAra = (EditText) findViewById(R.id.EditKullanici);
        kullanicilar = (ListView) findViewById(R.id.listView);

        kullaniciAra.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listviewGetir();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        registerForContextMenu(kullanicilar);
        kullanicilar.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //  arkid=position;
                return false;
            }
        });
    }

    public void listviewGetir() {
        String URL = "http://192.168.2.18:9090/InstantMess/webapi/kullanici/"+kullaniciAra.getText().toString();

        JsonArrayRequest req = new JsonArrayRequest(URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject person = (JSONObject) response.get(i);
                                data.clear();

                                arkadasId = person.getInt("kullaniciId");
                                //JSONObject kullanici=person.getJSONObject("kullanici2");
                                String kullaniciad=person.getString("kullaniciAd");
                                String kullanicitel=person.getString("kullaniciTel");

                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("kullaniciAd", kullaniciad);
                                map.put("kullaniciTel",kullanicitel);
                                data.add(map);
                            }
                        } catch (JSONException e) {
                            Log.e("Web Servis", "bağlantı sağlanamadı" + e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Web servis ile bağlantı kurulamadı.", Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(req);

        adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2,
                new String[]{"kullaniciAd","kullaniciTel"},
                new int[]{android.R.id.text1,android.R.id.text2});
        kullanicilar.setAdapter(adapter);
    }

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        menu.add(Menu.NONE, EKLE, Menu.NONE, "Ekle");
        menu.add(Menu.NONE, ENGELLE, Menu.NONE, "Engelle");

    }

    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case EKLE:
                try {
                    AnaForm();
                }
                catch (JSONException E)
                {
                 E.printStackTrace();
                }

                break;

            case ENGELLE:

                break;
        }
        return super.onContextItemSelected(item);
    }

    public void AnaForm() throws JSONException
    {
        String URL = "http://192.168.2.18:9090/InstantMess/webapi/kullanici/arkadas";

        SharedPreferences sp=getSharedPreferences("Giris", Context.MODE_PRIVATE);
        int id=sp.getInt("kullaniciId",0);

        HashMap<String,Integer> params = new HashMap<String,Integer>();
        params.put(kullanici1, id);
        params.put(kullanici2, arkadasId);

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST,URL,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;

                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){
                    switch(response.statusCode){
                        case 400:
                            json = new String(response.data);
                            json = trimMessage(json, "message");
                            if(json != null) displayMessage(json);
                            break;
                    }
                    //Additional cases
                }
            }
            public String trimMessage(String json, String key){
                String trimmedString = null;

                try{
                    JSONObject obj = new JSONObject(json);
                    trimmedString = obj.getString(key);
                } catch(JSONException e){
                    e.printStackTrace();
                    return null;
                }

                return trimmedString;
            }

            public void displayMessage(String toastString){

                Toast.makeText(Arkadas.this, toastString, Toast.LENGTH_LONG).show();
            }
        }){
            @Override

            public String getBodyContentType()
            {
                return "application/json;charset=utf-8";
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        Toast.makeText(getApplication(), "Arkadaş Eklendi!", Toast.LENGTH_LONG).show();
        Intent myIntent = new Intent(this, AnaForm.class);
        startActivity(myIntent);
    }

}
