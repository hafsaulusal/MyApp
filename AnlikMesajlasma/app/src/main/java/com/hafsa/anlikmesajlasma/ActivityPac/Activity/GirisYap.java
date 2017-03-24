package com.hafsa.anlikmesajlasma.ActivityPac.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hafsa.anlikmesajlasma.R;

import org.json.JSONObject;

public class GirisYap extends AppCompatActivity {

    private int kullaniciId;

    Button btngirisyap;
    EditText kullanicitelefon,kullaniciparola;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris_yap);

        kullanicitelefon= (EditText) findViewById(R.id.refkullanicitel);
        kullaniciparola= (EditText) findViewById(R.id.parola);

       // Drawable drawable = kullanicitelefon.getBackground(); // get current EditText drawable
       // drawable.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
        //kullanicitelefon.setBackground(drawable);

        btngirisyap= (Button) findViewById(R.id.btngirisyap);
        btngirisyap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TelefonKontrol() && ParolaKontrol()) {
                    Login();
                } else {
                    Toast.makeText(getApplicationContext(), "Lütfen bilgileriniz tam giriniz", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void Login()
    {
        JSONObject jsonObject = null;
        String loginUrl="http://192.168.2.18:9090/InstantMess/webapi/kullanici/login?";
        loginUrl+="kullaniciTel="+kullanicitelefon.getText().toString()+"&parola="+kullaniciparola.getText().toString();

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET,
                loginUrl,
                jsonObject,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    kullaniciId = response.getInt("kullaniciId");
                    String kullanicitel=response.getString("kullaniciTel");
                    String kullaniciparola=response.getString("parola");
                    AnaMenuGit(kullanicitel,kullaniciparola);
                } catch (Exception e) {
                    Log.e("Web Servis", "bağlantı sağlanamadı" + e.getLocalizedMessage());
                    e.printStackTrace();
                }

            }
        },new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Web servis ile bağlantı kurulamadı.", Toast.LENGTH_LONG).show();
            }
        });
       RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }

    public boolean TelefonKontrol()
    {
        boolean onay=false;
        String kullanicitel = kullanicitelefon.getText().toString().trim();
        if(Patterns.PHONE.matcher(kullanicitel).matches())
        {
            kullanicitelefon.setError(null);
            onay=true;
        }
        else
        {
            kullanicitelefon.setError("Telefon numarası kurallara uygun değil!");
        }
        return onay;
    }

    public boolean ParolaKontrol()
    {
        boolean onay=false;
        String kullanicipar=kullaniciparola.getText().toString().trim();

        if(kullanicipar.length()<6 && kullanicipar.length()>14)
        {
            kullaniciparola.setError("Parola 6 ile 14 karakter arasında olmalıdır");
        }
        else
        {
            kullaniciparola.setError(null);
            onay=true;
        }
        return onay;
    }

    public void AnaMenuGit(String tel,String par )
    {
        if(tel==null && par==null)
        {
            Toast.makeText(getApplication(),"Hata",Toast.LENGTH_LONG).show();
        }
        else {

            SharedPreferences sharedPref = getSharedPreferences("Giris",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("kullaniciId",kullaniciId);
            editor.commit();

            Toast.makeText(getApplication(), "Hoşgeldiniz", Toast.LENGTH_LONG).show();
            Intent myIntent = new Intent(this, AnaForm.class);
            startActivity(myIntent);

        }
    }

}
