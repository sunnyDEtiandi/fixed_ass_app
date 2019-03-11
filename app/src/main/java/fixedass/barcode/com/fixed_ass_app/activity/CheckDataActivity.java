package fixedass.barcode.com.fixed_ass_app.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import fixedass.barcode.com.fixed_ass_app.util.Constant;
import fixedass.barcode.com.fixed_ass_app.tool.ExitApplication;
import fixedass.barcode.com.fixed_ass_app.R;
import fixedass.barcode.com.fixed_ass_app.util.MySqliteHelper;

/**
 * Created by Administrator on 2018/3/5.
 * 进行盘点
 */

public class CheckDataActivity extends Activity {
    private String userUUID, name, sysUUID;
    private Spinner countDept;
    private Spinner countPlace;
    private MySqliteHelper helper;
    private TextView back;
    private Button startCheck;
    private String countDeptUUID = "";
    private String countPlaceUUID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkdata);

        ExitApplication.getInstance().addActivity(this);

        helper = new MySqliteHelper(this);

        countDept = (Spinner)findViewById(R.id.countDept_value);
        countPlace = (Spinner)findViewById(R.id.countPlace_value);

        List<String> list = getCountDept();
        final List<String> listUUID = getCountDeptUUID();
        List<String> placeList = getCountPlace();
        final List<String> placeListUUID = getCountPlaceUUID();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userUUID = bundle.getString("user");
        name = bundle.getString("name");
        sysUUID = bundle.getString("sysUUID");

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countDept.setAdapter(adapter);

        final ArrayAdapter<String> palceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, placeList);
        palceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countPlace.setAdapter(palceAdapter);

        if(ExitApplication.getInstance().getPreActivity().toString().indexOf("CountDataActivity")>0){
            countDeptUUID = bundle.getString("countDept");
            setSpinnerItemSelectedByValue(countDept,listUUID,countDeptUUID);

            countPlaceUUID = bundle.getString("countPlace");
            setSpinnerItemSelectedByValue(countPlace,placeListUUID,countPlaceUUID);
        }

        countDept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countDeptUUID = listUUID.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {  }
        });

        countPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countPlaceUUID = placeListUUID.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {  }
        });

        back = (TextView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirect(MainActivity.class);
            }
        });

        startCheck = (Button)findViewById(R.id.startCheck);
        startCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("user",userUUID);
                bundle.putString("name",name);
                bundle.putString("countDept",countDeptUUID);
                bundle.putString("countPlace",countPlaceUUID);
                bundle.putString("sysUUID",sysUUID);
                intent.putExtras(bundle);
                intent.setClass(CheckDataActivity.this, CountDataActivity.class);
                startActivity(intent);//跳转
            }
        });
    }

    /**
     * 根据值, 设置spinner默认选中:
     * @param spinner
     * @param value
     */
    public void setSpinnerItemSelectedByValue(Spinner spinner,List<String> listUUID,String value){
        for (int i=0;i<listUUID.size();i++){
            if (value.equals(listUUID.get(i))){
                spinner.setSelection(i,true);
                break;
            }
        }
    }

    /*获得部门的名称*/
    public List<String> getCountDept(){
        String sql = "select distinct countDepartment from "+Constant.TABLE_NAME_COUNT_DETAIL;
        SQLiteDatabase db = helper.getWritableDatabase();
        List<String> list = MySqliteHelper.query(CheckDataActivity.this,sql,new String[]{"countDepartment"},db);
        db.close();
        return list;
    }
    /*获得部门名称编码*/
    public List<String> getCountDeptUUID(){
        String sql = "select distinct countDepartment from "+Constant.TABLE_NAME_COUNT_DETAIL;
        SQLiteDatabase db = helper.getWritableDatabase();
        List<String> list = MySqliteHelper.queryUUID(CheckDataActivity.this,sql,new String[]{"countDepartment"},db);
        db.close();
        return list;
    }
    /*获得地址的名称*/
    public List<String> getCountPlace(){
        String sql = "select distinct countPlace from "+Constant.TABLE_NAME_COUNT_DETAIL;
        SQLiteDatabase db = helper.getWritableDatabase();
        List<String> list = MySqliteHelper.query(CheckDataActivity.this,sql,new String[]{"countPlace"},db);
        db.close();
        return list;
    }
    /*获得地址编码*/
    public List<String> getCountPlaceUUID(){
        String sql = "select distinct countPlace from "+Constant.TABLE_NAME_COUNT_DETAIL;
        SQLiteDatabase db = helper.getWritableDatabase();
        List<String> list = MySqliteHelper.queryUUID(CheckDataActivity.this,sql,new String[]{"countPlace"},db);
        db.close();
        return list;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                redirect(MainActivity.class);
                break;
            default:
                break;
        }
        return false;
    }

    /*跳转*/
    private void redirect(Class<?> cls) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("user",userUUID);
        bundle.putString("name",name);
        bundle.putString("sysUUID",sysUUID);
        intent.putExtras(bundle);
        intent.setClass(CheckDataActivity.this, cls);
        startActivity(intent);//跳转
    }
}
