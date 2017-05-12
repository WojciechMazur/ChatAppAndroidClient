package com.wickedmonkstudio.chatapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

import java.util.Objects;

public class LoginActivity extends Activity {

    private Button joinButton;
    private EditText nameEditText;
    private EditText serverAdressEditText;
    private EditText serverPortEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        joinButton=(Button)findViewById(R.id.btnJoin);
        nameEditText=(EditText)findViewById(R.id.name);
        serverAdressEditText=(EditText)findViewById(R.id.server_address);
        serverPortEditText=(EditText)findViewById(R.id.server_port);

        try {
            getActionBar().hide();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        joinButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(nameEditText.getText().toString().trim().length()>0){
                    String name=nameEditText.getText().toString().trim();
                    String address=serverAdressEditText.getText().toString().trim();
                    String port = serverPortEditText.getText().toString().trim();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("address", address);
                    intent.putExtra("port", port);

                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your name", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
