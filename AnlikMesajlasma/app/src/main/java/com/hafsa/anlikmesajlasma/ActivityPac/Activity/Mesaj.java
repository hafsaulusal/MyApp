package com.hafsa.anlikmesajlasma.ActivityPac.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Timer;
import java.util.TimerTask;


public class Mesaj extends AppCompatActivity {

    ListView sohbetlistele;
    Button gonder,resim;
    EditText mesaj;
    private  int id;
    private int kid;
    private int aid;

    static final int PICK_CONTACT_REQUEST = 1;

    ArrayList<HashMap<String, String>> data2 = new ArrayList<HashMap<String, String>>();
    private BaseAdapter adapter2 ;
    ArrayList<HashMap<String, String>> adapterData = new ArrayList<HashMap<String, String>>();

    private static int LOAD_IMAGE_RESULTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesaj);

        sohbetlistele= (ListView) findViewById(R.id.listMessages);
        gonder= (Button) findViewById(R.id.sendButton);
        mesaj= (EditText) findViewById(R.id.messageBodyField);
        resim= (Button) findViewById(R.id.Btnresim);

        sohbetlistele.setDivider(null);

        final Handler handler = new Handler();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                       mesajGetir();
                    }
                });
            }
        };
        Timer timer = new Timer();
        timer.schedule(doAsynchronousTask,0,1000);

        SharedPreferences sp=getSharedPreferences("Giris", Context.MODE_PRIVATE);
        id=sp.getInt("kullaniciId",0);


        SharedPreferences ap = getSharedPreferences("kId", Context.MODE_PRIVATE);
        kid=ap.getInt("kullaniciId",0);

        SharedPreferences ai = getSharedPreferences("aId", Context.MODE_PRIVATE);
        aid=ai.getInt("arkadasId",0);

        mesajGetir();

        gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mesajEkle();
                    mesaj.setText("");
                    mesajGetir();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });



        resim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create the Intent for Image Gallery.
                 //  Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                // Start new activity with the LOAD_IMAGE_RESULTS to handle back the results when image is picked from the Image Gallery.
                 //  startActivityForResult(i, LOAD_IMAGE_RESULTS);

                Intent i=new Intent(getApplicationContext(),ResimGonder.class);
                startActivity(i);

            }
        });


    }

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Here we need to check if the activity that was triggers was the Image Gallery.
        // If it is the requestCode will match the LOAD_IMAGE_RESULTS value.
        // If the resultCode is RESULT_OK and there is some data we know that an image was picked.
        if (requestCode == LOAD_IMAGE_RESULTS && resultCode == RESULT_OK && data != null) {
            // Let's read picked image data - its URI
            Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            Intent ii=new Intent(Mesaj.this, ResimGonder.class);
            ii.putExtra("image", imagePath);
            startActivity(ii);
            // Now we need to set the GUI ImageView data with data read from the picked file.

            // At the end remember to close the cursor or you will end with the RuntimeException!
           // cursor.close();

        }
    }
*/

    public void mesajEkle() throws JSONException {

        String URL = "http://192.168.2.18:9090/InstantMess/webapi/kullanici/mesaj/";

        final String mess = mesaj.getText().toString().trim();

       // JSONObject js=new JSONObject();

        JSONObject arkadasId=new JSONObject();
        arkadasId.put("arkadasId", aid);

        JSONObject kullaniciId=new JSONObject();
        kullaniciId.put("kullaniciId",id);

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("mesaj",mess);
        jsonObject.put("arkadasId",arkadasId);
        jsonObject.put("kullaniciId", kullaniciId);

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST,URL,
                jsonObject,
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

                Toast.makeText(Mesaj.this, toastString, Toast.LENGTH_LONG).show();
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
    }


    public void mesajGetir()
    {
        JSONObject jsonObject = null;

        String loginUrl="http://192.168.2.18:9090/InstantMess/webapi/kullanici/mesaj/"+id+"/"+kid;

        JsonArrayRequest req = new JsonArrayRequest(loginUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            adapter2.notifyDataSetChanged();
                            data2.clear();

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject person = (JSONObject) response.get(i);


                                String mesaj=person.getString("mesaj");

                                JSONObject arkId=person.getJSONObject("arkadasId");
                                int aid=arkId.getInt("arkadasId");
                                JSONObject kullanici2=arkId.getJSONObject("kullanici2");
                                int kullanici2kullaniciId=kullanici2.getInt("kullaniciId");
                                String kullanici2kullaniciAd=kullanici2.getString("kullaniciAd");

                                JSONObject kullaniciId=person.getJSONObject("kullaniciId");
                                int kullaniciIdkullaniciId=kullaniciId.getInt("kullaniciId");
                                String kullaniciIdkullaniciAd=kullaniciId.getString("kullaniciAd");

                                HashMap<String, String> map2 = new HashMap<String, String>();
                                map2.put("mesaj",mesaj);
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
                new String[]{"mesaj"},
                new int[]{android.R.id.text1});

        sohbetlistele.setAdapter(adapter2);

    }


}
