package com.example.tabproject;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.telephony.SmsManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public class MessagePopup extends Activity {
    EditText msg;
    TextView phone;
    Button btn;

    String phone_number;
    String message;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_message);

        btn = (Button)findViewById(R.id.btn);
        phone = (TextView)findViewById(R.id.phone);

        Intent intent = getIntent();
        phone_number = intent.getStringExtra("phone");
        phone.setText(phone_number);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msg = (EditText)findViewById(R.id.msg);
                message = msg.getText().toString();

                ActivityCompat.requestPermissions(MessagePopup.this, new String[]{Manifest.permission.SEND_SMS},1000);

                if(ContextCompat.checkSelfPermission(MessagePopup.this, Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_GRANTED) {
                    sendSMS(phone_number, message);
                    Toast.makeText(MessagePopup.this, "Success", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MessagePopup.this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()== MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

     */
    private void sendSMS(String phoneNumber, String message){
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
}
