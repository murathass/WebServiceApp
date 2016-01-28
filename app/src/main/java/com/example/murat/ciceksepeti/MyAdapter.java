package com.example.murat.ciceksepeti;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import Entity.Flower;

public class MyAdapter extends ArrayAdapter<Flower> {


    private Context context=null;
    private List<Flower> flowerList=null;
    private LruCache<Integer,Bitmap> imageCache;

    public MyAdapter(Context context, int resource,List<Flower> list) {
        super(context, resource,list);
        this.context=context;
        this.flowerList=list;

        final int maxMemorySize=(int)(Runtime.getRuntime().maxMemory()/1024);
        final int maxCacheSize=maxMemorySize/8;
        imageCache=new LruCache<>(maxCacheSize);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.flower_list_item, parent, false);

        Flower flower = flowerList.get(position);
        TextView tv = (TextView) view.findViewById(R.id.textView);
        tv.setText(flower.getName());

        Bitmap bitmep=imageCache.get(flower.getProductId());
        if(bitmep!=null) {
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            imageView.setImageBitmap(bitmep);
        }
        else {
            FlowerAndView container = new FlowerAndView();
            container.flower=flower;
            container.view=view;
            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }
        return view;
    }

    class FlowerAndView {
        public Flower flower;
        public View view;
        public  Bitmap bitmap;
    }

    private class ImageLoader extends AsyncTask<FlowerAndView,Void,FlowerAndView>{
        @Override
        protected FlowerAndView doInBackground(FlowerAndView... params) {

            FlowerAndView container = params[0];
            Flower flower = container.flower;

            try {
                InputStream is = (InputStream) new URL(MainActivity.imageUrl+flower.getPhoto()).getContent();
                Bitmap image= BitmapFactory.decodeStream(is);
                flower.setImage(image);
                is.close();
                container.bitmap=image;
                return container;

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(FlowerAndView container) {
            ImageView imageView = (ImageView)container.view.findViewById(R.id.imageView);
            imageView.setImageBitmap(container.flower.getImage());
            container.flower.setImage(container.flower.getImage());
            imageCache.put(container.flower.getProductId(),container.flower.getImage());
        }
    }
}
