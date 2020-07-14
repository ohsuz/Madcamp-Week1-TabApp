package com.example.tabproject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

public class WordListPopup extends Activity {

    private static String BASE_URL = "http://ec2-13-125-208-213.ap-northeast-2.compute.amazonaws.com/";
    private static String INSERT =  BASE_URL + "insert_wordlist.php";
    private static String UPDATE =  BASE_URL + "update_wordlist.php";
    private static String TAG = "insert";

    EditText wname;
    Button btn;

    String wordlist_name;
    String wordlist_lan;
    private RadioButton en, ja, ch;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_wordlist);

        Intent intent = getIntent();
        final int opt = intent.getIntExtra("option", -1);
        final String wordlist_id = intent.getStringExtra("wordlist_id");

        btn = (Button)findViewById(R.id.btn);

        // 라디오 버튼 설정
        en = (RadioButton)findViewById(R.id.en);
        ja = (RadioButton)findViewById(R.id.ja);
        ch = (RadioButton)findViewById(R.id.ch);

        // 라디오 그룹 설정
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);

        if(opt==1){
            radioGroup.setVisibility(View.GONE);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wname = (EditText)findViewById(R.id.wordlist_name);
                wordlist_name = wname.getText().toString();

                // 단어장을 처음 입력하는 경우
                if(opt == 0){
                    InsertData task = new InsertData();
                    task.execute(INSERT, wordlist_name, wordlist_lan);
                }
                // 단어장 이름을 수정하는 경우
                else{
                    UpdateData update = new UpdateData();
                    update.execute(UPDATE, wordlist_name, wordlist_id);
                }

                // 프래그먼트 3번으로 바로 이동
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("tab3", 2);
                startActivity(intent);
            }
        });
    }

    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            if(i==R.id.en){
                wordlist_lan ="en";
            }else if(i==R.id.ja){
                wordlist_lan = "ja";
            }else{
                wordlist_lan = "ch";
            }
        }
    };

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(WordListPopup.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "등록 성공", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = (String)params[0];
            String wordlist = (String)params[1];
            String wordlist_lan = (String)params[2];

            String parameters = "wordlist="+wordlist+"&wordlist_lan="+wordlist_lan;

            try {

                java.net.URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(parameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    class UpdateData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(WordListPopup.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "수정 성공", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = (String)params[0];
            String wordlist = (String)params[1];
            String wordlist_id = (String)params[2];

            String parameters = "wordlist="+wordlist+"&wordlist_id="+wordlist_id;
            //String postParameters = "name=" + name + "&country=" + country;


            try {

                java.net.URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(parameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "UpdateData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }



}
