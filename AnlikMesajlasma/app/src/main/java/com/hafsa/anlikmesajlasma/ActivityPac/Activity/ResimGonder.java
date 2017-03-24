package com.hafsa.anlikmesajlasma.ActivityPac.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hafsa.anlikmesajlasma.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ResimGonder extends AppCompatActivity {

    private static int LOAD_IMAGE_RESULTS = 1;

    ImageView resim;
    Button gonder,sec;
   //private String  image;
    //private Bitmap bitmap;

    private  int id;
    private int kid;
    private int aid;


    private Button btnSec,btnYolla; //Buttonlar
    private ImageView imageView; // Seçilen resimin ön izlemesini sunmak için imageview
    private int PICK_IMAGE_REQUEST = 1; // 1 adet resim seçmemizi sağlayan int değeri
    private Bitmap bitmap; // seçtiğimiz resimi imageview'e set ederken yardımcı olacak map
    private Uri filePath; // telefonun içindeki data(resim)e ulaşmamızı sağlayan araç
    private Context con = this;

    private String imageURL;

    private String im;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resim_gonder);

        resim = (ImageView) findViewById(R.id.imageView);
        gonder = (Button) findViewById(R.id.gonderresim);
        sec= (Button) findViewById(R.id.buttonsec);

     /*   Intent iin= getIntent();
        Bundle b = iin.getExtras();

        if(b!=null)
        {
                image =(String) b.get("image");
        }

        resim.setImageBitmap(BitmapFactory.decodeFile(image));
        */

        SharedPreferences sp=getSharedPreferences("Giris", Context.MODE_PRIVATE);
        id=sp.getInt("kullaniciId", 0);

        SharedPreferences ap = getSharedPreferences("kId", Context.MODE_PRIVATE);
        kid=ap.getInt("kullaniciId",0);

        SharedPreferences ai = getSharedPreferences("aId", Context.MODE_PRIVATE);
        aid=ai.getInt("arkadasId", 0);


        gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new yollaASYNC().execute();
                try {
                    uploadImage();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resimSec();
            }
        });

    }

    private void resimSec() // galeriniizi açıp "1" adet resim seçmemizi sağlayan method ( activity resulttan yardım alıyoruz)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Resim Seçiniz"), PICK_IMAGE_REQUEST);
    }

    public String getPath(Uri uri)  //dataya ulaşmamızı sağlayan method
    {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);

        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

        cursor.close();

        return path;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                resim.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


   private void uploadImage() throws JSONException {

       Map config = new HashMap();
       config.put("cloud_name", "instantmess"); // cloudinary sitesinde dashboard sayfasında gördüğümüz bilgiler..
       config.put("api_key", "792261352711249");
       config.put("api_secret", "s0UtwK1eBmPaY2daWpRZQXGixKI");
       Cloudinary cloudinary = new Cloudinary(config);
       String path = getPath(filePath);

           try {
               Map result=cloudinary.uploader().upload(path, ObjectUtils.emptyMap()); // cloudinary'a upload edeceğimiz method
               imageURL = (String) result.get("url");

           } catch (IOException e) {
               e.printStackTrace();
           }

        String URL = "http://192.168.2.18:9090/InstantMess/webapi/kullanici/resim";

        String image = imageURL;
        JSONObject arkadasId=new JSONObject();
        arkadasId.put("arkadasId", aid);

        JSONObject kullaniciId=new JSONObject();
        kullaniciId.put("kullaniciId",id);

        JSONObject jsonObject=new JSONObject();

        jsonObject.put("resim",image);
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

                Toast.makeText(ResimGonder.this, toastString, Toast.LENGTH_LONG).show();
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

}