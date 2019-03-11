package fixedass.barcode.com.fixed_ass_app.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fixedass.barcode.com.fixed_ass_app.tool.ToastUtils;
import fixedass.barcode.com.fixed_ass_app.util.Constant;
import fixedass.barcode.com.fixed_ass_app.util.DbManager;
import fixedass.barcode.com.fixed_ass_app.tool.ExitApplication;
import fixedass.barcode.com.fixed_ass_app.R;
import fixedass.barcode.com.fixed_ass_app.util.MySqliteHelper;
import fixedass.barcode.com.fixed_ass_app.view.ListViewEx;

/**
 * Created by Administrator on 2018/3/6.
 * 盘点单号
 */

public class LoadDataActivity extends Activity {
    private String userUUID, name, sysUUID;
    private EditText countBillCode;
    private String countBillCodeStr = "";
    private Button add;
    private TextView back;
    private ListViewEx lvx;

    public static String ROWID = "rowID";
    public static String COUNTBILLCODE = "countBillCode";       //盘点单
    public static String CREATEPEOPLE = "createPeople";         //创建人
    public static String CREATEDATE = "createDate";             //创建日期
    public static String COUNTNOTE = "countNote";               //使用部门

    private MySqliteHelper helper;

    private List<Map<String, Object>> infoList = null;


    private Handler handler;
    private List<Map<String, String>> contentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loaddata);

        ExitApplication.getInstance().addActivity(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userUUID = bundle.getString("user");
        name = bundle.getString("name");
        sysUUID = bundle.getString("sysUUID");

        initView();
        initContentDataList();
    }

    private void initView(){
        countBillCode = (EditText)findViewById(R.id.countBillCode);         //盘点单号
        countBillCode.setOnFocusChangeListener(focus_listener_noIM);
        countBillCode.setOnTouchListener(touch_listener_noIM);

        add = (Button)findViewById(R.id.add);                                   //添加按钮
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyThread().start();
                ToastUtils.showToast(LoadDataActivity.this,"数据加载成功");
                //正确--跳转界面
                redirect(MainActivity.class);
            }
        });

        back = (TextView)findViewById(R.id.back);                               //标题返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirect(MainActivity.class);
            }
        });

        initListViewHead(R.id.tv_list_item_load_tvhead1, false, "序列号");
        initListViewHead(R.id.tv_list_item_load_tvhead2, false, "盘点单号");
        initListViewHead(R.id.tv_list_item_load_tvhead3, false, "创建人");
        initListViewHead(R.id.tv_list_item_load_tvhead4, false, "创建日期");
        initListViewHead(R.id.tv_list_item_load_tvhead5, false, "盘点备注");
        lvx = (ListViewEx) this.findViewById(R.id.lv_query_lvBarcodeList);

        lvx.inital(R.layout.list_item_load, new String[] {ROWID,COUNTBILLCODE,CREATEPEOPLE,CREATEDATE,COUNTNOTE}, new int[] {
                R.id.tv_list_item_load_tvhead1,
                R.id.tv_list_item_load_tvhead2,
                R.id.tv_list_item_load_tvhead3,
                R.id.tv_list_item_load_tvhead4,
                R.id.tv_list_item_load_tvhead5
        });


        helper = DbManager.getIntance(this);                            //数据库

        handler = new MyHandler();

        lvx.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                countBillCodeStr = contentList.get(position).get("countBillCode");
                Message mg = handler.obtainMessage();
                mg.obj = countBillCodeStr;
                handler.sendMessage(mg);
            }
        });
    }

    int iListHead = 0;
    private void initListViewHead(int id, boolean isCheckBox, String text) {
        if (iListHead == 0) {
            iListHead = getResources().getColor(R.color.list_head);
        }

        if (!isCheckBox) {
            TextView view = (TextView) findViewById(id);
            view.setText(text);
            view.setBackgroundColor(iListHead);
            view.setTextAppearance(this, R.style.ListHeadText);
            view.setGravity(Gravity.CENTER);
        } else {
            CheckBox view = (CheckBox) findViewById(id);
            view.setText(text);
            view.setBackgroundColor(iListHead);
            view.setTextColor(getResources().getColor(R.color.white));
        }
    }

    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            countBillCode.setText(msg.obj+"");
        }
    }

    /**
     * 初始化content数据
     */
    private void initContentDataList() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select countBillCode,createPeople,createDate,countNote from "+ Constant.TABLE_NAME_COUNTBILL +" where sysUUID = "+sysUUID+" order by createDate desc";
        contentList = MySqliteHelper.query2(LoadDataActivity.this,sql,new String[]{"countBillCode","createPeople","createDate","countNote"},db);
        db.close();

        infoList = new ArrayList<Map<String, Object>>();
        if(contentList!=null){
            for (int i=0;i<contentList.size();i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put(ROWID, i+1);
                map.put(COUNTBILLCODE, contentList.get(i).get("countBillCode"));
                map.put(CREATEPEOPLE, contentList.get(i).get("createPeople"));
                map.put(CREATEDATE, contentList.get(i).get("createDate"));
                map.put(COUNTNOTE, contentList.get(i).get("countNote"));
                infoList.add(map);
            }
            lvx.add(infoList);
        }
    }

    /*根据盘点单号copy盘点详情*/
    class MyThread extends Thread{
        private Connection connection = null;

        @Override
        public void run() {
            /*读取数据*/
            SharedPreferences sharedPreferences = LoadDataActivity.super.getSharedPreferences(Constant.FILENAME,Activity.MODE_PRIVATE);
            String ipStr = sharedPreferences.getString("ipStr","");
            String dbPWD = sharedPreferences.getString("dbPWD","");
            try {
                Class.forName(Constant.DRIVER);
                connection = DriverManager.getConnection(Constant.URLPRE+ipStr+Constant.URLSUF, Constant.USER, dbPWD);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            try {
                insertData(connection);    //测试数据库连接
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }

        private void insertData(Connection con1) throws java.sql.SQLException{
            try {
                SQLiteDatabase db = helper.getWritableDatabase();

                /*删除表中的数据*/
                db.delete(Constant.TABLE_NAME_COUNT_DETAIL,null,null);

                /*盘点详情表*/
                Statement stmt = con1.createStatement();        //创建Statement

                db = helper.getWritableDatabase();
                String sql_cDetail = "select "+Constant.DETAIL_CON_SQL+" from "+
                        Constant.TABLE_NAME_COUNT_DETAIL+" where countbillCode = '"+countBillCodeStr+"' and sysUUID = '"+sysUUID+"'";
                ResultSet resultSet = stmt.executeQuery(sql_cDetail);          //ResultSet类似Cursor

                if (resultSet != null && resultSet.first()) {
                    while (!resultSet.isAfterLast()) {
                        ContentValues values = new ContentValues();
                        values.put("countUUID",resultSet.getString("countUUID"));
                        values.put("countbillCode",resultSet.getString("countbillCode"));
                        values.put("barCode",resultSet.getString("barCode"));
                        values.put("countGroup",resultSet.getString("countGroup"));
                        values.put("countCompany",resultSet.getString("countCompany"));
                        values.put("countDepartment",resultSet.getString("countDepartment"));
                        values.put("countPlace",resultSet.getString("countPlace"));
                        values.put("countPeople",resultSet.getString("countPeople"));
                        values.put("countTime",resultSet.getString("countTime"));
                        values.put("countState",resultSet.getString("countState"));
                        values.put("sysUUID",resultSet.getString("sysUUID"));
                        long result = db.insert(Constant.TABLE_NAME_COUNT_DETAIL,null,values);
                        resultSet.next();
                    }
                }
                resultSet.close();
                stmt.close();
                db.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (con1 != null){
                    try {
                        con1.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
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
        intent.setClass(LoadDataActivity.this, cls);
        startActivity(intent);//跳转
    }

    /*隐藏键盘*/
    private void hideIM(View edt){
        // try to hide input_method:
        try {
            InputMethodManager im = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            IBinder windowToken = edt.getWindowToken();
            if(windowToken != null) {
                // always de-activate IM
                im.hideSoftInputFromWindow(windowToken, 0);
            }
        } catch (Exception e) {
            Log.e("HideInputMethod", "failed:"+e.getMessage());
        }
    }
    private View.OnFocusChangeListener focus_listener_noIM = new View.OnFocusChangeListener(){
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus==true) {
                hideIM(v);
            }
        }
    };
    private View.OnTouchListener touch_listener_noIM = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction()== MotionEvent.ACTION_DOWN) {
                hideIM(v);
            }
            return false;    // dispatch the event further!
        }
    };

}
