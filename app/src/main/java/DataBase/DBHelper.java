package DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import Entity.Flower;

/**
 * Created by murat on 26.01.2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "DBFlowers";

    private static final String TABLE_NAME = "Flowers";

    //colums
    private static final String KEY_PRODUCT_ID = "productId";
    private static final String KEY_NAME = "name";
    private static final String KEY_INSTRUCTIONS = "instructions";
    private static final String KEY_PRICE = "price";
    private static final String KEY_PHOTO = "photo";
    private static final String KEY_CATEGORY = "category";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME +
                "(" +
                KEY_PRODUCT_ID + " INTEGER PRIMARY KEY," +
                KEY_PRICE + " DOUBLE," +
                KEY_CATEGORY + " TEXT," +
                KEY_INSTRUCTIONS + " TEXT," +
                KEY_NAME + " TEXT," +
                KEY_PHOTO + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_NAME);
        onCreate(db);
    }

    public void insert(List<Flower> list) {

        SQLiteDatabase db = getWritableDatabase();

        for (Flower flower : list) {
            ContentValues values = new ContentValues();
            values.put(KEY_PRODUCT_ID, flower.getProductId());
            values.put(KEY_PRICE, flower.getPrice());
            values.put(KEY_CATEGORY, flower.getCategory());
            values.put(KEY_INSTRUCTIONS, flower.getInstructions());
            values.put(KEY_NAME, flower.getName());
            values.put(KEY_PHOTO, flower.getPhoto());
            db.insert(TABLE_NAME, null, values);
        }
    }

    public List<Flower> getFlowers(){
        List<Flower> flowers = new ArrayList<>();

        SQLiteDatabase db=getWritableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM "+TABLE_NAME, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Flower flower= new Flower();
            flower.setProductId(res.getInt(res.getColumnIndex(KEY_PRODUCT_ID)));
            flower.setPrice(res.getDouble(res.getColumnIndex(KEY_PRICE)));
            flower.setCategory(res.getString(res.getColumnIndex(KEY_CATEGORY)));
            flower.setInstructions(res.getString(res.getColumnIndex(KEY_INSTRUCTIONS)));
            flower.setName(res.getString(res.getColumnIndex(KEY_NAME)));
            flower.setPhoto(res.getString(res.getColumnIndex(KEY_PHOTO)));
            flowers.add(flower);
            res.moveToNext();
        }
        return flowers;
    }

    public  void  deleteTable(){
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }

}