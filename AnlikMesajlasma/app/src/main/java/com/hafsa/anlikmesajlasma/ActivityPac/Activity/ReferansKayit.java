package com.hafsa.anlikmesajlasma.ActivityPac.Activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.UUID;

public class ReferansKayit extends AppCompatActivity {

    String URL = "http://172.20.10.18:9090/InstantMess/webapi/kullanici/";

    public static final String KullaniciAd = "kullaniciAd";
    public static final String KullaniciTel = "kullaniciTel";
    public static final String Parola = "parola";
    public static final String ParolaTekrar = "parolaTekrar";

    Button refbtnkayıtol;
    EditText refkullanicitelefon,refkullaniciad;
    TextView smsucret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referans_kayit);

        smsucret= (TextView) findViewById(R.id.smsucret);
        refkullanicitelefon= (EditText) findViewById(R.id.refkullanicitel);
        refkullaniciad= (EditText) findViewById(R.id.refkullaniciad);

        refbtnkayıtol= (Button) findViewById(R.id.refbtnkayitol);
        refbtnkayıtol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v==refbtnkayıtol)
                {
                    if(TelefonKontrol())
                    {
                        try {
                            KullaniciKayitOl();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Lütfen bilgileriniz tam giriniz", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
    private void KullaniciKayitOl() throws JSONException {

        final String kullaniciad = refkullaniciad.getText().toString().trim();
        final String kullanicitel = refkullanicitelefon.getText().toString().trim();

        final String shortparola=shortUUID();
        final String kullaniciparola = shortparola.toString().trim();
        final String kullaniciparolatekrar = shortparola.toString().trim();

        final String mesaj="Anlık mesajlaşma uygulamasına kaydınız yapıldı." +"\n"+ "Kullanıcı adınız:" +
                kullaniciad + "\n" +"Parolanız: " +kullaniciparola+ " şeklindedir." ;

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

                Toast.makeText(ReferansKayit.this, toastString, Toast.LENGTH_LONG).show();
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
        sendSMS(kullanicitel, mesaj);
        GirisYapGit();
    }
    public boolean TelefonKontrol()
    {
        boolean onay=false;
        String kullanicitel = refkullanicitelefon.getText().toString().trim();
        if(Patterns.PHONE.matcher(kullanicitel).matches())
        {
            refkullanicitelefon.setError(null);
            onay=true;
        }
        else
        {
            refkullanicitelefon.setError("Telefon numarası kurallara uygun değil!");
        }
        return onay;
    }
    //referans parolasını üretiyor
    public static String shortUUID() {
        UUID uuid = UUID.randomUUID();
        long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
        return Long.toString(l, Character.MAX_RADIX);
    }
    private void sendSMS(String telefonNo, String mesaj)
    {
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(telefonNo, null, mesaj, pi, null);
    }
    public void GirisYapGit()
    {
        finish();
        Intent git;
        git = new Intent(this,GirisYap.class);
        startActivity(git);
    }

}
