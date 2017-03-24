package com.hafsa.anlikmesajlasma.ActivityPac.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hafsa.anlikmesajlasma.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KayitOl extends AppCompatActivity {

    String URL = "http://192.168.2.18:9090/InstantMess/webapi/kullanici/";

    public static final String KullaniciAd = "kullaniciAd";
    public static final String KullaniciTel = "kullaniciTel";
    public static final String Parola = "parola";
    public static final String ParolaTekrar = "parolaTekrar";

    EditText kullaniciAd, kullaniciTel, parola, parolaTekrar;
    Button btnkayitol;
    ProgressBar progressBar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_ol);

        kullaniciAd = (EditText) findViewById(R.id.refkullaniciad);
        kullaniciTel = (EditText) findViewById(R.id.refkullanicitel);
        parola = (EditText) findViewById(R.id.kullaniciparola);
        parolaTekrar = (EditText) findViewById(R.id.kullaniciparolatekrar);

        btnkayitol = (Button) findViewById(R.id.refbtnkayitol);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        parola.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(parola.getText().toString().length()==0)
                {
                    parola.setError("Parola girişi yapınız!");
                }

                else
                {
                    parolaGüc();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });

        btnkayitol.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (v == btnkayitol) {
                    try {
                        if (TelefonKontrol() && parolaKontrol()) {
                            KullaniciKayitOl();
                        } else {
                            Toast.makeText(getApplicationContext(), "Lütfen bilgileriniz tam giriniz", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void KullaniciKayitOl() throws JSONException {

        final String kullaniciad = kullaniciAd.getText().toString().trim();
        final String kullanicitel = kullaniciTel.getText().toString().trim();
        final String kullaniciparola = parola.getText().toString().trim();
        final String kullaniciparolatekrar = parolaTekrar.getText().toString().trim();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(KullaniciAd, kullaniciad);
        params.put(KullaniciTel, kullanicitel);
        params.put(Parola, kullaniciparola);
        params.put(ParolaTekrar, kullaniciparolatekrar);

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

               Toast.makeText(KayitOl.this, toastString, Toast.LENGTH_LONG).show();
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
        GirisYapGit();
    }

    //kullanıcı telefonu doğru girildiyse true dönüyor
    public boolean TelefonKontrol()
    {
        boolean onay=false;
        String kullanicitel = kullaniciTel.getText().toString().trim();
        if(Patterns.PHONE.matcher(kullanicitel).matches())
        {
            kullaniciTel.setError(null);
            onay=true;
        }
        else
        {
        kullaniciTel.setError("Telefon numarası kurallara uygun değil!");
        }
        return onay;
    }


    //kullanıcı parola ve parola tekrar alanlarına doğru giriş yaptı mı?
    //parolanın gücünü ölçen metoda gidiyor
    //doğru girmiş ise true dönüyor
    public boolean parolaKontrol()
    {
        boolean onay=false;
        String kullaniciparola = parola.getText().toString().trim();
        String kullaniciparolatekrar = parolaTekrar.getText().toString().trim();

        if(kullaniciparola.length()==0 || kullaniciparolatekrar.length()==0)
        {
            Toast.makeText(getApplicationContext(), "parola veya parola tekrar alanları boş bırakılamaz!", Toast.LENGTH_LONG).show();
        }
        else if(!kullaniciparola.equals(kullaniciparolatekrar))
        {
            Toast.makeText(getApplicationContext(), "parola ve parola tekrar alanları aynı olmalıdır!", Toast.LENGTH_LONG).show();
            parola.setText("");
            parolaTekrar.setText("");
        }
        else
        {
            if(parolaGüc()) {
                onay = true;
            }

        }
        return onay;
    }

    //parola gücünü ölçüyor parola ne kadar güçlüyse progressbar ona göre şekilleniyor

    public boolean parolaGüc()
    {
        boolean onay=false;

        String kullaniciparola = parola.getText().toString().trim();

        String rgx="((?=.*\\d).{6,20})";
        String rgxsmall="((?=.*\\d)(?=.*[a-z]).{6,20})";
        String rgxmedium="((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})";
        String rgxlarge="((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%*+]).{6,20})";

        Pattern patternrgx=Pattern.compile(rgx);
        Pattern patternrgxsmall=Pattern.compile(rgxsmall);
        Pattern patternrgxmedium=Pattern.compile(rgxmedium);
        Pattern patternrgxlarge=Pattern.compile(rgxlarge);

        Matcher matcher=patternrgx.matcher(kullaniciparola);
        Matcher matchersmall=patternrgxsmall.matcher(kullaniciparola);
        Matcher matchermedium=patternrgxmedium.matcher(kullaniciparola);
        Matcher matcherlarge=patternrgxlarge.matcher(kullaniciparola);

        if(!matcher.matches() &&!matchersmall.matches() && !matchermedium.matches() && !matcherlarge.matches())
        {
            Toast.makeText(getApplicationContext(), "parola 7 ile 20 karakter arasında olmalıdır!", Toast.LENGTH_SHORT).show();
            progressBar.setProgress(0);
        }
        else if(!matchersmall.matches() && !matchermedium.matches() && !matcherlarge.matches())
        {
            progressBar.setProgress(25);
            onay=true;
        }
        else if(!matchermedium.matches() && !matcherlarge.matches())
        {
            progressBar.setProgress(50);
            onay=true;
        }
        else if(!matcherlarge.matches())
        {
            progressBar.setProgress(75);
            onay=true;
        }
        else
        {
            progressBar.setProgress(100);
            onay=true;
        }

            return onay;
    }

    public void GirisYapGit()
    {
        finish();
        Intent git;
        git = new Intent(this,GirisYap.class);
        startActivity(git);
    }

}