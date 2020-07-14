package com.example.tabproject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

public class WordPopup extends Activity {

    private static String URL = "http://ec2-13-125-208-213.ap-northeast-2.compute.amazonaws.com/insert_word.php";
    private static String TAG = "insert";

    String wordlist_id;
    String wordlist_lan;

    //음성인식 관련 변수
    Intent intent;
    SpeechRecognizer mRecognizer;

    //번역관련 변수
    String result;

    //위젯들
    Button record_button;
    Button translation_button;
    Button add_button;
    TextView translationText;
    TextView resultText;
    TextView country;


    String k_word;
    String e_word;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_word);

        Intent gintent = getIntent();
        wordlist_id = gintent.getStringExtra("wordlist_id");
        wordlist_lan = gintent.getStringExtra("wordlist_lan");

        record_button = (Button)findViewById(R.id.record);
        translation_button = (Button)findViewById(R.id.translation);
        add_button = (Button)findViewById(R.id.add);
        translationText = (TextView)findViewById(R.id.translationText);
        resultText = (TextView)findViewById(R.id.resultText);
        country = (TextView)findViewById(R.id.country);

        //음성인식

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,  getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);

        if(wordlist_lan.equals("en")){
            country.setText("영어:");
        }
        if(wordlist_lan.equals("ja")){
            country.setText("일본어:");
        }
        if(wordlist_lan.equals("ch")){
            country.setText("중국어:");
        }

        record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                    //권한을 허용하지 않는 경우
                } else {
                    //권한을 허용한 경우
                    try {
                        mRecognizer.startListening(intent);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        translation_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(){
                    @Override
                    public void run(){
                        StringBuffer output = new StringBuffer();
                        String clinentID = "gwaURDfC5ayAxTiFvPgy";
                        String clientSecret = "XCxDOvNoRz";
                        try{
                            String text = URLEncoder.encode(translationText.getText().toString(), "UTF-8");
                            String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
                            String postParams;

                            //파파고 API와 연결
                            URL url = new URL(apiURL);
                            HttpURLConnection con = (HttpURLConnection)url.openConnection();
                            con.setRequestMethod("POST");
                            con.setRequestProperty("X-Naver-Client-Id", clinentID);
                            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);

                            //번역할 문장을 파라미터로 전송
                            if(wordlist_lan.equals("en")){
                                postParams = "source=ko&target=en&text=" + text;
                            }else if(wordlist_lan.equals("ja")){
                                postParams = "source=ko&target=ja&text=" + text;
                            }else{
                                postParams = "source=ko&target=zh-CN&text=" + text;
                            }
                            con.setDoOutput(true);
                            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                            wr.writeBytes(postParams);
                            wr.flush();
                            wr.close();

                            //번역결과를 받아온다.

                            int responseCode = con.getResponseCode();
                            BufferedReader br;
                            if(responseCode == 200){
                                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            }
                            else{
                                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                            }
                            String inputLine;
                            while((inputLine = br.readLine()) != null){
                                output.append(inputLine);
                            }
                            br.close();


                            JSONObject first = new JSONObject(output.toString());
                            JSONObject second = first.getJSONObject("message");
                            JSONObject third = second.getJSONObject("result");
                            e_word = third.getString("translatedText");

                            WordPopup.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    resultText.setText(e_word);
                                }
                            });
                        }catch (Exception ex){
                            Log.e("SampleHTTP", "Exception in processing response.", ex);
                            ex.printStackTrace();
                        }

                    }
                }.start();
            }

        });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InsertData task = new InsertData();
                task.execute(URL, wordlist_id, k_word, e_word);

                // 프래그먼트 3번으로 바로 가야 하는디....
                Intent intent = new Intent(getApplicationContext(), Words.class);
                intent.putExtra("wordlist_id", wordlist_id);
                intent.putExtra("wordlist_lan", wordlist_lan);
                startActivity(intent);
            }
        });

    }

    RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
        }
        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            resultText.setText("녹음중...");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int error) {
            resultText.setText("Error");
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            resultText.setText("PartialResult");
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            resultText.setText("onEvent");
        }

        @Override
        public void onResults(Bundle results) {
            String key= "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            k_word = ""+rs[0];
            translationText.setText(k_word);
        }


    };


    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(WordPopup.this,
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
            String wordlist_id = (String)params[1];
            String k_word = (String)params[2];
            String e_word = (String)params[3];

            String parameters = "wordlist_id="+wordlist_id+"&k_word="+k_word+"&e_word="+e_word;
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

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

}