package com.leman.diyaobao.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class jankinDBOpenHelper extends SQLiteOpenHelper {

	private static final String DataBaseName = "JanKin.db"; 	//数据库名称
    private static final int 	DataBaseVersion = 1;			//数据库版本,大于0
    
	public jankinDBOpenHelper(Context context)
	{
		super(context, DataBaseName, null, DataBaseVersion);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(	"CREATE TABLE jankinData (keyid integer primary key autoincrement, uname varchar(20), imagepath varchar(50), lati varchar(20)," +
					"longi varchar(20), date varchar(20), address varchar(20), duty varchar(20), lai varchar(20), model varchar(20),"+
					"imei  varchar(20), cost varchar(20), uid varchar(25), munsell varchar(50), isupload varchar(10) )");		//执行有更改的sql语句
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS jankinData");  
        onCreate(db);
	}
	
	public void SaveData(Map<String,String> Info)
	{
		//如果要对数据进行更改，就调用此方法得到用于操作数据库的实例,该方法以读和写方式打开数据库  
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("insert into jankinData (uname,imagepath,lati,longi,date,address,duty,lai,model,imei,cost,uid,isupload,munsell) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                new Object[]{	Info.get("username"), Info.get("imagepath"), Info.get("latitude"),Info.get("longitude"),
        						Info.get("date"),Info.get("address"),Info.get("duty"),Info.get("lai"),Info.get("model"),
        						Info.get("imei"),Info.get("cost"), Info.get("uid"), Info.get("isupload"), Info.get("munsell")} );
//        db.close();
	}
	
	public void UpdateData(Map<String,String> Info){
		//如果要对数据进行更改，就调用此方法得到用于操作数据库的实例,该方法以读和写方式打开数据库  
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("update jankinData set isupload=? where keyid=?",  new Object[]{Info.get("isupload"), Info.get("keyid")});
//        ContentValues values = new ContentValues();
//        for (Map.Entry<String, String> entry:Info.entrySet()) {
//        	values.put(entry.getKey(), entry.getValue());
//		}
//        db.update("jankinData", values, "keyid=?", new String[]{Info.get("keyid")});
	}
	
	public List<Map<String,String>> getScrollData(){
        List<Map<String,String>> ListMap = new ArrayList<Map<String,String>>();
        //如果只对数据进行读取，建议使用此方法
        SQLiteDatabase db = getReadableDatabase();
    //    Cursor cursor = db.rawQuery("select * from jankinData limit ?,?",  new String[]{""+offset, ""+maxResult});  原来写法
		Cursor cursor = db.rawQuery("select * from jankinData",new String[]{});
        while(cursor.moveToNext()){
        	Map<String,String> Info = new HashMap<String, String>();
        	Info.put("keyid",	cursor.getString(cursor.getColumnIndex("keyid")));
        	Info.put("uname",	cursor.getString(cursor.getColumnIndex("uname")));
        	Info.put("imagepath", cursor.getString(cursor.getColumnIndex("imagepath")));
        	Info.put("lati",	cursor.getString(cursor.getColumnIndex("lati")));
        	Info.put("longi",	cursor.getString(cursor.getColumnIndex("longi")));
        	Info.put("date",	cursor.getString(cursor.getColumnIndex("date")));
        	Info.put("address", cursor.getString(cursor.getColumnIndex("address")));
        	Info.put("duty",	cursor.getString(cursor.getColumnIndex("duty")));
        	Info.put("lai",		cursor.getString(cursor.getColumnIndex("lai")));
        	Info.put("model",	cursor.getString(cursor.getColumnIndex("model")));
        	Info.put("imei",	cursor.getString(cursor.getColumnIndex("imei")));
        	Info.put("cost",	cursor.getString(cursor.getColumnIndex("cost")));
        	Info.put("uid",		cursor.getString(cursor.getColumnIndex("uid")));
            Info.put("munsell",cursor.getString(cursor.getColumnIndex("munsell")));
        	Info.put("isupload",cursor.getString(cursor.getColumnIndex("isupload")));
        	ListMap.add(Info);  
        }
        cursor.close();  
        return ListMap;  
    }
	
	public void delete(int[] keyids)
	{  
        SQLiteDatabase db = getWritableDatabase();
//        String sqliteCmd = "delete from jankinData where uid=";
//        for (int i = 0; i < uids.length; i++) {
//        	if(i == 0)
//        		sqliteCmd += uids[i];
//        	else
//        		sqliteCmd += uids[i]+",";
//		}
//        if (uids.length > 0) {
//        	db.execSQL(sqliteCmd);
//		}
//        db.execSQL("delete from jankinData where uid in ?", new Object[]{uids});
        db.delete("jankinData", "keyid in ?", new String[]{keyids.toString()});
    } 
	
	public void delete(int keyid)
	{  
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("delete from jankinData where keyid=?", new Object[]{keyid});
//		db.delete("jankinData", "uid=?", new String[]{""+uid});
//		db.close();
    } 
	
	public void deleteTable()
	{
		SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS jankinData"); 
	}
	
	public long getCount() {  
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from jankinData", null);
        cursor.moveToFirst();  
        return cursor.getLong(0);  
    } 

}
