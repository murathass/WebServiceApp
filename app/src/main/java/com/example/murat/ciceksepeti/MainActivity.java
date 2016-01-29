package com.example.murat.ciceksepeti;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import DataBase.DBHelper;
import Entity.Flower;
import Parsers.*;

public class MainActivity extends Activity {

    public final DBHelper dbHelper= new DBHelper(this);
    private List<Flower> parseflowers = new ArrayList<>();
    private List<Flower> listflowers = new ArrayList<>();
    private List<Flower> tflowers = new ArrayList<>();
    private final String url="http://services.hanselandpetal.com/secure/flowers.json";
    private final String sendUrl="http://192.168.43.189:8014/restful.php";
    public static String imageUrl="http://services.hanselandpetal.com/photos/";

    TextView tv;

    String resultText="";
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv= (TextView) findViewById(R.id.textView2);
        //new GetJSON().execute(url);

        //listflowers=dbHelper.getFlowers();
        final RequestCreater rCreater = new RequestCreater();
        rCreater.setMethod("POST");
        rCreater.setUri(sendUrl);
        rCreater.addParams("key_1", "value 1");
        rCreater.addParams("key_2", "value 2");
        rCreater.addParams("key_3", "value 3");

                ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new SendParams().execute(rCreater);
                    }
                });



    }

    private class GetJSON extends AsyncTask<String,Void,List<Flower>>{
        ProgressBar p;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p=(ProgressBar)findViewById(R.id.progress);
        }

        @Override
        protected List<Flower> doInBackground(String... params) {
           try {
               URL url=new URL(params[0]);
               HttpURLConnection conn=(HttpURLConnection)url.openConnection();

               byte[] loginBytes=("feeduser"+":"+"feedpassword").getBytes();
               StringBuilder builder = new StringBuilder();
               builder.append("Basic ")
                       .append(Base64.encodeToString(loginBytes, Base64.DEFAULT));
               conn.addRequestProperty("Authorization", builder.toString());

               BufferedReader reader = new BufferedReader( new InputStreamReader( conn.getInputStream()));
               String temp="";
               while ((temp=reader.readLine())!=null){
                    resultText+=temp;
               }
                parseflowers=JSONParser.parseJSON(resultText);

           }catch (Exception e){
               e.printStackTrace();
           }

            return parseflowers;
        }

        @Override
        protected void onPostExecute(List<Flower> flowers) {
            p.setVisibility(View.INVISIBLE);
          //  adapter = new MyAdapter(MainActivity.this,R.layout.flower_list_item,flowers);
          //  setListAdapter(adapter);
        }
    }
    private class InsertFlowers extends AsyncTask<List<Flower>,Void,Void>{

        @Override
        protected Void doInBackground(List<Flower>... params) {
            dbHelper.insert(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new ShowFlowers().execute();
        }
    }



    private class ShowFlowers extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {

            for(Flower f:dbHelper.getFlowers()){
                Log.d("TESTCİCİCİ::::",f.getProductId()+":"+f.getName());
            }
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.deleteTable();
    }

    private class SendParams extends AsyncTask<RequestCreater,Void,String> {
        RequestCreater requestCreater;
        @Override
        protected String doInBackground(RequestCreater... params) {
            requestCreater=params[0];
            StringBuilder sb= new StringBuilder();
            sb.append(sendUrl);
            if (requestCreater.getParams().size() > 0 && requestCreater.getMethod().equals("GET")){
                sb.append("?");
                sb.append(requestCreater.getEncodeParams());
            }

            try {
                URL url = new URL(sb.toString());
                HttpURLConnection con=(HttpURLConnection)url.openConnection();
                con.setRequestMethod(requestCreater.getMethod());

                if(requestCreater.getMethod().equals("POST")){
                    con.setDoInput(true);
                    OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                    writer.write(requestCreater.getEncodeParams());
                    writer.flush();
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String temp="";
                while ((temp = reader.readLine())!=null){
                    resultText+=temp;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("aswq::::",resultText);
            return resultText;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            tv.setText(s);
        }
    }
}