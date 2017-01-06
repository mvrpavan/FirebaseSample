package com.freelanceinc.mvrpavan.firebasesample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnDatabaseDemo, btnAuthDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDatabaseDemo = (Button) findViewById(R.id.btnDatabaseDemo);
        btnAuthDemo = (Button) findViewById(R.id.btnAuthDemo);

        btnDatabaseDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DatabaseDemoActivity.class));
            }
        });

        btnAuthDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AuthenticationActivity.class));
            }
        });
    }
}
