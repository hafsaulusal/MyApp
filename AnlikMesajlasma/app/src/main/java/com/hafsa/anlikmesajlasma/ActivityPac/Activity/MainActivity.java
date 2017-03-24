package com.hafsa.anlikmesajlasma.ActivityPac.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.hafsa.anlikmesajlasma.R;

public class MainActivity extends AppCompatActivity {

    Button girisyap;
    Button kayitol;
    Button referans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        girisyap= (Button) findViewById(R.id.btngirisyap);
        kayitol= (Button) findViewById(R.id.refbtnkayitol);
        referans= (Button) findViewById(R.id.btnreferans);

        girisyap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btngirisyap) {
                    Intent git;
                    git = new Intent(MainActivity.this,GirisYap.class);
                    startActivity(git);

                }

            }
        });

        kayitol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent git;
                    git = new Intent(MainActivity.this,KayitOl.class);
                    startActivity(git);


            }
        });

        referans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent git;
                git = new Intent(MainActivity.this,ReferansKayit.class);
                startActivity(git);

            }
        });
    }

}
