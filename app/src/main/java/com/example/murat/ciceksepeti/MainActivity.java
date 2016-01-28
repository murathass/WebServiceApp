package com.example.murat.ciceksepeti;

import android.app.ListActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.tv.TvContract;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import DataBase.DBHelper;
import Entity.Flower;
import Parsers.*;

public class MainActivity extends ListActivity {

    public final DBHelper dbHelper= new DBHelper(this);
    private List<Flower> parseflowers = new ArrayList<>();
    private List<Flower> listflowers = new ArrayList<>();
    private List<Flower> tflowers = new ArrayList<>();
    private final String url="http://services.hanselandpetal.com/feeds/flowers.json";
    public static String imageUrl="http://services.hanselandpetal.com/photos/";

    String resultText="";
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        new GetJSON().execute(url);

        listflowers=dbHelper.getFlowers();

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
            adapter = new MyAdapter(MainActivity.this,R.layout.flower_list_item,flowers);
            setListAdapter(adapter);
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
}