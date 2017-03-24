package com.hafsa.anlikmesajlasma.ActivityPac.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.hafsa.anlikmesajlasma.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AnaForm extends AppCompatActivity {

    ListView arkadaslistele,sohbetlistele;
    Button btnkullaniciad,btnadresdefteri;

    private ListAdapter adapter = null;
    private ListAdapter adapter2 = null;
    ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> data2 = new ArrayList<HashMap<String, String>>();

    private int kullaniciId;
    private int id;
    private String kid;
    private String aid;
    private  int arkadasid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana_form);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btnkullaniciad= (Button) findViewById(R.id.btnkullaniciad);
        btnadresdefteri= (Button) findViewById(R.id.btnadresdefteri);
        sohbetlistele= (ListView) findViewById(R.id.sohbetlistele);
        arkadaslistele= (ListView) findViewById(R.id.arkadaslistele);
        arkadaslistele.setItemsCanFocus(false);

        TabHost tabHost= (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec=tabHost.newTabSpec("arkadas ekle");
        tabSpec.setContent(R.id.ekle);
        tabSpec.setIndicator("Arkadaş Ekle");
        tabHost.addTab(tabSpec);

        tabSpec=tabHost.newTabSpec("arkadas liste");
        tabSpec.setContent(R.id.arkadaslar);
        tabSpec.setIndicator("Arkadaş Liste");
        tabHost.addTab(tabSpec);

        tabSpec=tabHost.newTabSpec("sohbet");
        tabSpec.setContent(R.id.sohbet);
        tabSpec.setIndicator("Sohbet");
        tabHost.addTab(tabSpec);

        btnkullaniciad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btnkullaniciad) {
                    Intent git;
                    git = new Intent(AnaForm.this, Arkadas.class);
                    startActivity(git);
                }
            }
        });

        SharedPreferences sp=getSharedPreferences("Giris", Context.MODE_PRIVATE);
        id=sp.getInt("kullaniciId",0);

        Getir();

        adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2,
                new String[]{"kullaniciAd","kullaniciTel","kullaniciId","arkadasId"},
                new int[]{android.R.id.text1,android.R.id.text2});

        arkadaslistele.setAdapter(adapter);

        Git();

        arkadaslistele.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                HashMap<String, Object> obj = (HashMap<String, Object>) adapter.getItem(position);
                kid = (String) obj.get("kullaniciId");
                aid = (String) obj.get("arkadasId");

                SharedPreferences sharedPref = getSharedPreferences("kId", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("kullaniciId", Integer.parseInt(kid));
                editor.commit();

                SharedPreferences sharedPrefa = getSharedPreferences("aId", Context.MODE_PRIVATE);
                SharedPreferences.Editor editora = sharedPrefa.edit();
                editora.putInt("arkadasId", Integer.parseInt(aid));
                editora.commit();

                Intent i = new Intent(getApplicationContext(), Mesaj.class);
                startActivity(i);

            }
        });


        sohbetlistele.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                HashMap<String, Object> obj = (HashMap<String, Object>) adapter2.getItem(position);
                kid= (String) obj.get("kullaniciId");
                aid= (String) obj.get("arkadasId");

                Intent i=new Intent(getApplicationContext(),Mesaj.class);
                startActivity(i);

            }
        });

    }

    public void Getir()
    {
        JSONObject jsonObject = null;
        String loginUrl="http://192.168.2.18:9090/InstantMess/webapi/kullanici/arkadas/"+id;
        JsonArrayRequest req = new JsonArrayRequest(loginUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject person = (JSONObject) response.get(i);
                                 arkadasid= person.getInt("arkadasId");
                                //kullaniciId = response.getInt("kullaniciId");
                                JSONObject kullanici=person.getJSONObject("kullanici2");
                                int kullanicikullaniciid=kullanici.getInt("kullaniciId");
                                String kullaniciad=kullanici.getString("kullaniciAd");
                                String kullanicitel=kullanici.getString("kullaniciTel");

                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("arkadasId" , String.valueOf(arkadasid));
                                map.put("kullaniciId", String.valueOf(kullanicikullaniciid));
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


    }


    public void Git()
    {
        JSONObject jsonObject = null;
        String loginUrl="http://192.168.2.18:9090/InstantMess/webapi/kullanici/mesaj/arkadas/"+id;

        JsonArrayRequest req = new JsonArrayRequest(loginUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject person = (JSONObject) response.get(i);

                                JSONObject arkId=person.getJSONObject("arkadasId");
                                int aid=arkId.getInt("arkadasId");
                                JSONObject kullanici2=arkId.getJSONObject("kullanici2");
                                int kullanici2kullaniciId=kullanici2.getInt("kullaniciId");
                                String kullanici2kullaniciAd=kullanici2.getString("kullaniciAd");

                                JSONObject kullaniciId=person.getJSONObject("kullaniciId");
                                int kullaniciIdkullaniciId=kullaniciId.getInt("kullaniciId");
                                String kullaniciIdkullaniciAd=kullaniciId.getString("kullaniciAd");

                                HashMap<String, String> map2 = new HashMap<String, String>();
                                map2.put("arkadasId", String.valueOf(aid));
                                map2.put("kullaniciId1", String.valueOf(kullanici2kullaniciId));
                                map2.put("kullaniciAd1",kullanici2kullaniciAd);
                                map2.put("kullaniciId2", String.valueOf(kullaniciIdkullaniciId));
                                map2.put("kullaniciAd2", kullaniciIdkullaniciAd);
                                data2.add(map2);

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

       /* HashMap<String,Integer> map2 = new HashMap<String, Integer>();
        map2.put("arkadasId", Integer.valueOf(arkadasId));
        data2.add(map2);
        */

        adapter2 = new SimpleAdapter(this, data2, android.R.layout.simple_list_item_2,
                new String[]{"kullaniciAd1"},
                new int[]{android.R.id.text1});

        sohbetlistele.setAdapter(adapter2);
    }


}
