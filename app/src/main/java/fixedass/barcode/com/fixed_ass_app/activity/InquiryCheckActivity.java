package fixedass.barcode.com.fixed_ass_app.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fixedass.barcode.com.fixed_ass_app.bean.CountState;
import fixedass.barcode.com.fixed_ass_app.table.InquiryCheckPanelListAdapter;
import fixedass.barcode.com.fixed_ass_app.tool.CountStateAdapter;
import fixedass.barcode.com.fixed_ass_app.util.Constant;
import fixedass.barcode.com.fixed_ass_app.tool.ExitApplication;
import fixedass.barcode.com.fixed_ass_app.R;
import fixedass.barcode.com.fixed_ass_app.util.DbManager;
import fixedass.barcode.com.fixed_ass_app.util.MySqliteHelper;
import fixedass.barcode.com.fixed_ass_app.view.ListViewEx;

/**
 * Created by Administrator on 2018/3/5.
 * 盘点查询
 */

public class InquiryCheckActivity extends Activity {
    private String userUUID, name, sysUUID;
    private Spinner countStatu;
    private TextView back;
    private ListViewEx lvx;
    private MySqliteHelper helper;


    public static String ROWID = "rowID";
    public static String BARCODE = "barCode";             //资产编码
    public static String ASSNAME = "assName";             //资产名称
    public static String ASSTYPE = "assType";             //资产规格
    public static String ASSPRICE = "assPrice";         //资产价格
    public static String BOOKDATE = "bookDate";         //入账日期
    public static String COUNTPEOPLE = "countPeople";   // 盘点人
    public static String COUNTTIME = "countTime";       //盘点时间
    public static String COUNTSTATE = "countState";     //盘点状态


    private InquiryCheckPanelListAdapter adapter;
    private List<Map<String, Object>> infoList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inquirycheck);

        ExitApplication.getInstance().addActivity(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userUUID = bundle.getString("user");
        name = bundle.getString("name");
        sysUUID = bundle.getString("sysUUID");

       initView();
    }

    private void initView(){
        helper = DbManager.getIntance(this);                            //数据库
        // 初始化控件
        countStatu = (Spinner)findViewById(R.id.countStatu);
        // 建立数据源
        List<CountState>  countStateList = setSpinnerList();
        //  建立Adapter绑定数据源
        final CountStateAdapter countStateAdapter = new CountStateAdapter(countStateList,this);
        //绑定Adapter
        countStatu.setAdapter(countStateAdapter);
        countStatu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sql = "select barCode,countPeople, countTime, countState from "+ Constant.TABLE_NAME_COUNT_DETAIL;
                CountState countState = (CountState)countStateAdapter.getItem(position);
                String state = countState.getState();
                int count = countState.getCount();
                TextView textView1 = (TextView)view.findViewById(R.id.textView1);
                TextView textView2 = (TextView)view.findViewById(R.id.textView2);
                switch (state){
                    case "已盘点":
                        textView1.setTextColor(textView1.getResources().getColor(R.color.status_yes));
                        textView2.setTextColor(textView2.getResources().getColor(R.color.status_yes)); //可以随意设置自己要的颜色值
                        sql = sql + " where countState = 1";
                        break;
                    case "未盘点":
                        textView1.setTextColor(textView1.getResources().getColor(R.color.status_no));
                        textView2.setTextColor(textView2.getResources().getColor(R.color.status_no));
                        sql = sql + " where countState = 0";
                        break;
                    case "差异":
                        textView1.setTextColor(textView1.getResources().getColor(R.color.status_error));
                        textView2.setTextColor(textView2.getResources().getColor(R.color.status_error));
                        sql = sql + " where countState = 2";
                        break;
                    default:
                        textView1.setTextColor(textView1.getResources().getColor(R.color.status_all));
                        textView2.setTextColor(textView2.getResources().getColor(R.color.status_all));
                        break;
                }
                sql += " order by countTime desc";
                /*adapter.setContentList(initContentDataList(sql));*/
                initContentDataList(sql);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        back = (TextView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirect(MainActivity.class);
            }
        });

        initListViewHead(R.id.tv_list_item_count_tvhead1, false, "序列号");
        initListViewHead(R.id.tv_list_item_count_tvhead2, false, "资产编码");
        initListViewHead(R.id.tv_list_item_count_tvhead3, false, "资产名称");
        initListViewHead(R.id.tv_list_item_count_tvhead4, false, "资产规格");
        initListViewHead(R.id.tv_list_item_count_tvhead5, false, "资产价格");
        initListViewHead(R.id.tv_list_item_count_tvhead6, false, "入账日期");
        initListViewHead(R.id.tv_list_item_count_tvhead7, false, "盘点人");
        initListViewHead(R.id.tv_list_item_count_tvhead8, false, "盘点时间");
        initListViewHead(R.id.tv_list_item_count_tvhead9, false, "盘点状态");
        lvx = (ListViewEx) this.findViewById(R.id.lv_inquiry_lvBarcodeList);

        lvx.inital(R.layout.list_item_count, new String[] {ROWID,BARCODE,ASSNAME,ASSTYPE,ASSPRICE,BOOKDATE,COUNTPEOPLE,COUNTTIME,COUNTSTATE}, new int[] {
                R.id.tv_list_item_count_tvhead1,
                R.id.tv_list_item_count_tvhead2,
                R.id.tv_list_item_count_tvhead3,
                R.id.tv_list_item_count_tvhead4,
                R.id.tv_list_item_count_tvhead5,
                R.id.tv_list_item_count_tvhead6,
                R.id.tv_list_item_count_tvhead7,
                R.id.tv_list_item_count_tvhead8,
                R.id.tv_list_item_count_tvhead9
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

    /**
     * 初始化content数据
     */
    private void initContentDataList(String sql) {
        List<Map<String, String>> contentList = new ArrayList<>();

        SQLiteDatabase db = helper.getWritableDatabase();

        //得到盘点从表中的数据
        contentList = MySqliteHelper.query2(InquiryCheckActivity.this,sql,
                new String[]{"barCode","countPeople","countTime","countState"},db);

        for (int i=0;i<contentList.size();i++){
            db = helper.getWritableDatabase();
            String sto_sql = "select assName,assType,assPrice,bookDate from "+Constant.TABLE_NAME_STORAGE
                    + " where barCode = '"+contentList.get(i).get("barCode")+"'";
            List<Map<String, String>> stoList = MySqliteHelper.query2(InquiryCheckActivity.this,sto_sql,
                    new String[]{"assName","assType","assPrice","bookDate"},db);
            for (int j=0;j<stoList.size();j++){
                contentList.get(i).put("assName",stoList.get(j).get("assName"));
                contentList.get(i).put("assType",stoList.get(j).get("assType"));
                contentList.get(i).put("assPrice",stoList.get(j).get("assPrice"));
                contentList.get(i).put("bookDate",stoList.get(j).get("bookDate"));
            }
        }
        db.close();
        System.out.println("222==========contentList================"+contentList);

        infoList = new ArrayList<Map<String, Object>>();
        if(contentList!=null){
            for (int i=0;i<contentList.size();i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put(ROWID, i+1);
                map.put(BARCODE, contentList.get(i).get("barCode"));
                map.put(ASSNAME, contentList.get(i).get("assName"));
                map.put(ASSTYPE, contentList.get(i).get("assType"));
                map.put(ASSPRICE, contentList.get(i).get("assPrice"));
                map.put(BOOKDATE, contentList.get(i).get("bookDate"));
                map.put(COUNTPEOPLE,contentList.get(i).get("countPeople"));
                map.put(COUNTTIME, contentList.get(i).get("countTime"));
                map.put(COUNTSTATE, contentList.get(i).get("countState"));
                infoList.add(map);
            }
            lvx.add(infoList);
        }
    }

    /*设置下拉选*/
    protected List<CountState> setSpinnerList(){
        List<CountState>  countStateList = new ArrayList<CountState>();

        SQLiteDatabase db = helper.getWritableDatabase();
        /*count(*)*/
        String sql = "select barCode from "+Constant.TABLE_NAME_COUNT_DETAIL;
         /*查询所有、已盘点、未盘点、差异的数量*/
        int all_count = MySqliteHelper.getCount(InquiryCheckActivity.this,sql,db);
        countStateList.add(new CountState("所有", all_count));

        sql = "select barCode from "+Constant.TABLE_NAME_COUNT_DETAIL +" where countState = 1";
        int yes_count = MySqliteHelper.getCount(InquiryCheckActivity.this,sql,db);
        countStateList.add(new CountState("已盘点", yes_count));

        sql = "select barCode from "+Constant.TABLE_NAME_COUNT_DETAIL +" where countState = 0";
        int no_count = MySqliteHelper.getCount(InquiryCheckActivity.this,sql,db);
        countStateList.add(new CountState("未盘点", no_count));

        sql = "select barCode from "+Constant.TABLE_NAME_COUNT_DETAIL +" where countState = 2";
        int error_count = MySqliteHelper.getCount(InquiryCheckActivity.this,sql,db);
        countStateList.add(new CountState("差异", error_count));

        db.close();
        return countStateList;
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
        bundle.putString("sysUUID",sysUUID);
        intent.putExtras(bundle);
        intent.setClass(InquiryCheckActivity.this, cls);
        startActivity(intent);//跳转
    }
}
