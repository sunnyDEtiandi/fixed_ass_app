package fixedass.barcode.com.fixed_ass_app.util;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import fixedass.barcode.com.fixed_ass_app.bean.CountDetail;

/**
 * Created by Administrator on 2018/3/7.
 */

public class DbManager {
    private static MySqliteHelper helper;

    public static MySqliteHelper getIntance(Context context){
        if(helper == null){
            helper = new MySqliteHelper(context);
        }
        return helper;
    }

    /**
     * 将查询的cursor对象转换成list对象
     * @param cursor 游标对象
     * @return  集合对象
     */
    public static List<CountDetail> cursorToList(Cursor cursor){
        List<CountDetail> list = new ArrayList<>();

        //moveToNext() 如果返回true表示下一条记录存在 否则表示游标中数据读取完毕
        while (cursor.moveToNext()){
            String countUUID = cursor.getString(cursor.getColumnIndex("countUUID"));
            String countbillCode = cursor.getString(cursor.getColumnIndex("countbillCode"));
            String barCode = cursor.getString(cursor.getColumnIndex("barCode"));
            String countGroup = cursor.getString(cursor.getColumnIndex("countGroup"));
            String countCompany = cursor.getString(cursor.getColumnIndex("countCompany"));
            String countDepartment = cursor.getString(cursor.getColumnIndex("countDepartment"));
            String countPlace = cursor.getString(cursor.getColumnIndex("countPlace"));
            String countPeople = cursor.getString(cursor.getColumnIndex("countPeople"));
            String countTime = cursor.getString(cursor.getColumnIndex("countTime"));
            int countState = cursor.getInt(cursor.getColumnIndex("countState"));

            CountDetail detail = new CountDetail(countUUID,countbillCode,barCode,countGroup,
                    countCompany,countDepartment,countPlace,countPeople,countTime,countState);

            System.out.println("==========detail===="+detail);
            list.add(detail);
        }
        return list;
    }

}
