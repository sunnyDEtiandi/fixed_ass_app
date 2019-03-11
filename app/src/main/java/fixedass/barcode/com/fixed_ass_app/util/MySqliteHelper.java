package fixedass.barcode.com.fixed_ass_app.util;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fixedass.barcode.com.fixed_ass_app.activity.LoginActivity;

/**
 * Created by Administrator on 2018/3/7.
 */

public class MySqliteHelper extends SQLiteOpenHelper {
    /**
     * 构造函数
     * @param context 上下文函数
     * @param name  表示创建数据库的名称
     * @param factory   游标工厂
     * @param version   创建数据库的版本 >=1
     */
    public MySqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MySqliteHelper(Context context){
        super(context, Constant.DATABASE_NAME, null, Constant.DATABASE_VERSION);
    }

    /*当数据库创建是回调的函数   数据库对象*/
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("tag","===============onCreate===============");
        String sql1 = "create table "+Constant.TABLE_NAME_USER+"(userUUID varchar(50), userNo varchar(50)," +
                "userName varchar(50), userPWD varchar(50), deptUUID varchar(50), sysUUID varchar(50), creatorID varchar(50))";
        String sql2 = "create table "+Constant.TABLE_NAME_STORAGE+"(barCode varchar(50),"+
                "assName varchar(50), className varchar(50), assType varchar(30), assPrice double(30,2)," +
                "bookDate date, useGroup varchar(50), useCompany varchar(50), usePeople varchar(50),"+
                "useDept varchar(50), storeAddress varchar(50), sysUUID varchar(50))";
        String sql3 = "create table "+Constant.TABLE_NAME_ADDRESS+"(addrUUID varchar(50), " +
                "deptUUID varchar(50), addrName varchar(20))";
        String sql4 = "create table "+Constant.TABLE_NAME_DEPT+"(deptUUID varchar(50), " +
                "pDeptUUID varchar(50), deptName varchar(100), deptType varchar(20), sysUUID varchar(50))";
        String sql5 = "create table "+Constant.TABLE_NAME_COUNTBILL+"(countBillCode varchar(50)," +
                "createPeople varchar(50),createDate date, countNote varchar(200), sysUUID varchar(50))";
        String sql6 = "create table "+Constant.TABLE_NAME_COUNT_DETAIL+"(countUUID varchar(50)," +
                "countbillCode varchar(50), barCode varchar(50), countGroup varchar(50)," +
                "countCompany varchar(50), countDepartment varchar(50), countPlace varchar(50)," +
                "countPeople varchar(50), countTime datetime, countState int, sysUUID varchar(50))";
        String sql7 = "create table "+Constant.TABLE_NAME_PEOPLE+"(pUUID varchar(50), pName varchar(30), sysUUID varchar(50))";
        db.execSQL(sql1);    //执行sql语句
        db.execSQL(sql2);    //执行sql语句
        db.execSQL(sql3);    //执行sql语句
        db.execSQL(sql4);    //执行sql语句
        db.execSQL(sql5);    //执行sql语句
        db.execSQL(sql6);    //执行sql语句
        db.execSQL(sql7);    //执行sql语句
    }

    /*当数据库版本更新时回调的函数   数据库对象    数据库旧版本  数据库新版本*/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("tag","===============onUpgrade===============");
    }

    /*数据库打开时回调的函数*/
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        Log.i("tag","===============onOpen===============");
    }

    /**
     * 查询
     * @param context 上下文
     * @param sql     SQl查询语句
     * @param fields  字段名集合
     * @return  查询结果集 List<String>类型
     */
    public static List<String> query(Context context, String sql, String[] fields, SQLiteDatabase db) {
        List<String> dataList = new ArrayList<String>();
        Cursor cursor;

        try {
            cursor = db.rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    for (int i = 0; i < fields.length; i++) {
                        String temp = cursor.getString(cursor.getColumnIndex(fields[i]));
                        if(fields[i].equals("countDepartment")||fields[i].equals("useCompany")||fields[i].equals("useDept")){
                            String deptSql = "select deptName from "+Constant.TABLE_NAME_DEPT +
                                    " where deptUUID='"+cursor.getString(cursor.getColumnIndex(fields[i]))+"'";
                            Cursor cursor1 = db.rawQuery(deptSql, null);
                            while (cursor1.moveToNext()){
                                temp = cursor1.getString(cursor1.getColumnIndex("deptName"));
                            }
                        }
                        if (fields[i].equals("countPlace")||fields[i].equals("storeAddress")){
                            String placeSql = "select addrName from "+Constant.TABLE_NAME_ADDRESS +
                                    " where addrUUID='"+cursor.getString(cursor.getColumnIndex(fields[i]))+"'";
                            Cursor cursor1 = db.rawQuery(placeSql, null);
                            while (cursor1.moveToNext()){
                                temp = cursor1.getString(cursor1.getColumnIndex("addrName"));
                            }
                        }
                        if(fields[i].equals("usePeople")){
                            String peoSql = "select pName from "+Constant.TABLE_NAME_PEOPLE+
                                    " where pUUID='"+cursor.getString(cursor.getColumnIndex(fields[i]))+"'";
                            Cursor cursor1 = db.rawQuery(peoSql, null);
                            while (cursor1.moveToNext()){
                                temp = cursor1.getString(cursor1.getColumnIndex("pName"));
                            }
                        }
                        dataList.add(temp);
                    }
                }
            }
        }catch(Exception e) {
            new AlertDialog.Builder(context)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("数据库连接错误：")
                    .setMessage("数据访问异常。")
                    .show();
        } finally {
            db.close();
        }
        return dataList;
    }

    /**
     * 数据记录总条数
     * @param context 上下文
     * @param sql     SQL查询语句
     * @return        记录条数
     */
    public static int getCount(Context context, String sql, SQLiteDatabase db) {
        int totalCounty = 0;
        if(db!=null){

            Cursor cursor = db.rawQuery(sql, null);
            totalCounty = cursor.getCount();
        }
        return totalCounty;
    }

    /**
     * 查询
     * @param context 上下文
     * @param sql     SQl查询语句
     * @param fields  字段名集合
     * @return  查询结果集 ArrayList<HashMap<String,String>>类型
     */
    public static  ArrayList<Map<String,String>>  query2(Context context, String sql, String[] fields, SQLiteDatabase db) {
        ArrayList<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
        Cursor cursor;  //游标
        try{
            cursor = db.rawQuery(sql, null);    //获取数据集游标
            if (cursor != null) {
                while (cursor.moveToNext()) {   //游标递增，访问数据集
                    Map<String, String> map = new HashMap<String, String>();
                    for (int i = 0; i < fields.length; i++) {
                        String temp = cursor.getString(cursor.getColumnIndex(fields[i]));        //获取对应数据项

                        if(fields[i].equals("createPeople")||fields[i].equals("countPeople")){
                            String userSql = "select userName from "+Constant.TABLE_NAME_USER +
                                    " where userUUID='"+cursor.getString(cursor.getColumnIndex(fields[i]))+"'";
                            Cursor cursor1 = db.rawQuery(userSql, null);
                            while (cursor1.moveToNext()){
                                temp = cursor1.getString(cursor1.getColumnIndex("userName"));
                            }
                        }
                        if(fields[i].equals("countState")){
                            switch (cursor.getInt(cursor.getColumnIndex(fields[i]))){
                                case 0:
                                    temp = "未盘点";
                                    break;
                                case 1:
                                    temp = "已盘点";
                                    break;
                                case 2:
                                    temp = "差异";
                                    break;
                            }
                        }
                        /*int num = (i+1)%fields.length==0?fields.length:(i+1)%fields.length;*/
                        map.put(fields[i], temp);
                    }
                    dataList.add(map);
                }
            }
        }catch(Exception e) {
            new AlertDialog.Builder(context)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("数据库连接错误：")
                    .setMessage("数据访问异常。")
                    .show();
        }finally {
            db.close();
        }
        return dataList;
    }

    /**
     * 查询 -->UUID
     * @param context 上下文
     * @param sql     SQl查询语句
     * @param fields  字段名集合
     * @return  查询结果集 List<String>类型
     */
    public static List<String> queryUUID(Context context, String sql, String[] fields, SQLiteDatabase db) {
        List<String> dataList = new ArrayList<String>();
        Cursor cursor;

        try {
            cursor = db.rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    for (int i = 0; i < fields.length; i++) {
                        String temp = cursor.getString(cursor.getColumnIndex(fields[i]));
                        dataList.add(temp);
                    }
                }
            }
        }catch(Exception e) {
            new AlertDialog.Builder(context)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("数据库连接错误：")
                    .setMessage("数据访问异常。")
                    .show();
        } finally {
            db.close();
        }
        return dataList;
    }

    public static long insert(String tableName,ContentValues values, SQLiteDatabase db){
        return db.insert(tableName,null,values);
    }

    public static long update(String tableName,ContentValues values, SQLiteDatabase db,String[] countUUIDs){
        return db.update(tableName,values,"countUUID=?",countUUIDs);
    }
}
