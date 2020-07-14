package com.example.tabproject;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.util.Log.ERROR;

public class Words extends AppCompatActivity {
    private static String TAG = "getWord";
    private static String BASE_URL = "http://ec2-13-125-208-213.ap-northeast-2.compute.amazonaws.com/";
    private static String GET =  BASE_URL + "get_words.php";
    private static String DELETE =  BASE_URL + "delete_word.php";

    private static final  String TAG_JSON="firstproject";
    private static final String TAG_K_WORD = "k_word";
    private static final String TAG_E_WORD = "e_word";
    private static final String TAG_WORD_ID = "word_id";

    RecyclerView wordView;
    ArrayList<Word> words;
    WordAdapter wAdapter;
    private String mJsonString;

    String wordlist_id;
    String wordlist_lan;

    // TTS 변수 선언
    private TextToSpeech tts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.words);

        Intent intent = getIntent();
        wordlist_id = intent.getStringExtra("wordlist_id");
        wordlist_lan = intent.getStringExtra("wordlist_lan");

        if(wordlist_lan.equals("en")){
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    tts.setLanguage(Locale.ENGLISH);
                    tts.setSpeechRate(0.8f);
                }else{
                    Toast.makeText(getApplicationContext(), "TTS 작업에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }); }

        if(wordlist_lan.equals("ja")){
            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status == TextToSpeech.SUCCESS){
                        tts.setLanguage(Locale.JAPANESE);
                        tts.setSpeechRate(0.8f);
                    }else{
                        Toast.makeText(getApplicationContext(), "TTS 작업에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }); }

        if(wordlist_lan.equals("ch")){
            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status == TextToSpeech.SUCCESS){
                        tts.setLanguage(Locale.CHINESE);
                        tts.setSpeechRate(0.8f);
                    }else{
                        Toast.makeText(getApplicationContext(), "TTS 작업에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }); }


        wordView = (RecyclerView)findViewById(R.id.wordView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        wordView.setLayoutManager(mLinearLayoutManager);

        // 스와이프로 아이템 삭제를 구현하기 위한 설정
        //ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        //itemTouchHelper.attachToRecyclerView(wordView);

        GetData task = new GetData();
        task.execute(wordlist_id);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(wordView.getContext(), mLinearLayoutManager.getOrientation());
        wordView.addItemDecoration(dividerItemDecoration);

        wordView.addOnItemTouchListener(new RecyclerTouchListener(this, wordView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Toast.makeText(getApplicationContext(), words.get(position).getE_word(), Toast.LENGTH_SHORT).show();
                    tts.speak(words.get(position).getE_word(), TextToSpeech.QUEUE_FLUSH, null, null);
                    // API 20
                }else {
                    tts.speak(words.get(position).getE_word(), TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }

            @Override
            public void onLongClick(View view, int position) {


            }
        }));

        ImageView plus = (ImageView)findViewById(R.id.plus);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WordPopup.class);
                intent.putExtra("wordlist_id", wordlist_id);
                intent.putExtra("wordlist_lan", wordlist_lan);
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

                java.net.URL url = new URL(GET);
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

    /*
    private class DeleteData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getApplicationContext(),
                    null, "Delete", true, true);
    }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);

            // 에러가 있는 경우: 에러메세지를 보여줌
            if (result == null){
                Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
            }
            // 에러가 없는 경우: Intent로 다시 탭 3로 이동
            else {
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                // 프래그먼트 3번으로 바로 이동
                //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //intent.putExtra("tab3", 2);
                //startActivity(intent);
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String searchKeyword = params[0];
            String parameters = "word_id=" + searchKeyword;

            try {

               // Log.d("delete", parameters);
                java.net.URL url = new URL(DELETE);
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
                Log.d(TAG, "delete response code - " + responseStatusCode);

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

                Log.d(TAG, "DeleteData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

     */


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
                String word_id = item.getString(TAG_WORD_ID);

                Word word = new Word(k_word, e_word, word_id);
                words.add(word);
                //wAdapter.notifyDataSetChanged();
            }
            wordView.setAdapter(wAdapter);

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

    /*
    // 삭제할 수 있는 방향을 더 추가할 수도 있음 ex) ItemTouchHelper.RIGHT | ItemTouchHelper.RIGHT
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            Word word= words.get(position);
            DeleteData delete = new DeleteData();
            delete.execute(word.getWord_id());
        }
    };

     */
}
