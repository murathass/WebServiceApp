package Parsers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Entity.Flower;

public class JSONParser {
    public static List<Flower> parseJSON(String content){
        List<Flower> flowers = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(content);

            for (int i = 0; i <array.length() ; i++) {
                JSONObject obj= array.getJSONObject(i);
                Flower flower = new Flower();
                flower.setProductId(obj.getInt("productId"));
                flower.setPrice(obj.getDouble("price"));
                flower.setName(obj.getString("name"));
                flower.setInstructions(obj.getString("instructions"));
                flower.setPhoto(obj.getString("photo"));
                flowers.add(flower);
            }

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return flowers;
    }
}
