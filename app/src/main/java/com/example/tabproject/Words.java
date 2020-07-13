package com.example.tabproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Words extends AppCompatActivity {
    private static String TAG = "getWord";
    private static final String URL = "http://ec2-13-125-208-213.ap-northeast-2.compute.amazonaws.com/get_words.php";

    private static final  String TAG_JSON="firstproject";
    private static final String TAG_K_WORD = "k_word";
    private static final String TAG_E_WORD = "e_word";

    RecyclerView wordView;
    ArrayList<Word> words;
    WordAdapter wAdapter;
    private String mJsonString;

    String wordlist_id;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.words);

        Intent intent = getIntent();
        wordlist_id = intent.getStringExtra("wordlist_id");

        wordView = (RecyclerView)findViewById(R.id.wordView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        wordView.setLayoutManager(mLinearLayoutManager);

        GetData task = new GetData();
        task.execute(wordlist_id);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(wordView.getContext(), mLinearLayoutManager.getOrientation());
        wordView.addItemDecoration(dividerItemDecoration);

        ImageView plus = (ImageView)findViewById(R.id.plus);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WordPopup.class);
                intent.putExtra("wordlist_id", wordlist_id);
                startActivity(intent);
            }
        });
    }

    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Words.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);

            if (result == null){
                Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
            }
            else {
                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword = params[0];
            String parameters = "wordlist_id=" + searchKeyword;

            try {

                java.net.URL url = new URL(URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(parameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

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
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            words = new ArrayList<>(); // arraylist
            wAdapter = new WordAdapter(words);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String k_word = item.getString(TAG_K_WORD);
                String e_word = item.getString(TAG_E_WORD);

                Word word = new Word(k_word, e_word);
                words.add(word);
                //wAdapter.notifyDataSetChanged();
            }
            wordView.setAdapter(wAdapter);

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }
}
