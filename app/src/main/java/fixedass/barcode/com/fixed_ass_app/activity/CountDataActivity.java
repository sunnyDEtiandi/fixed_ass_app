package fixedass.barcode.com.fixed_ass_app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import fixedass.barcode.com.fixed_ass_app.R;
import fixedass.barcode.com.fixed_ass_app.tool.ToastUtils;
import fixedass.barcode.com.fixed_ass_app.util.Constant;
import fixedass.barcode.com.fixed_ass_app.tool.ExitApplication;
import fixedass.barcode.com.fixed_ass_app.util.MySqliteHelper;

/**
 * Created by Administrator on 2018/3/5.
 * 进行盘点
 */

public class CountDataActivity extends Activity {
    private String userUUID, name, sysUUID, countDept, countPlace;
    private MySqliteHelper helper;
    private TextView back;
    private EditText barCode;
    private EditText barCodeValue;
    private EditText assName;
    private EditText className;
    private EditText assPrice;
    private EditText assType;
    private EditText bookDate;
    private EditText useCompany;
    private EditText useDept;
    private EditText usePeople;
    private EditText storeAddress;
    private RelativeLayout relative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.countdata);

        ExitApplication.getInstance().addActivity(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userUUID = bundle.getString("user");
        name = bundle.getString("name");
        sysUUID = bundle.getString("sysUUID");
        countDept = bundle.getString("countDept");
        countPlace = bundle.getString("countPlace");

        helper = new MySqliteHelper(this);

        back = (TextView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirect(CheckDataActivity.class);
            }
        });

        barCode = (EditText)findViewById(R.id.barCode);
        barCodeValue = (EditText)findViewById(R.id.barCodeValue);
        assName = (EditText)findViewById(R.id.assName);
        className = (EditText)findViewById(R.id.className);
        assType = (EditText)findViewById(R.id.assType);
        assPrice = (EditText)findViewById(R.id.assPrice);
        bookDate = (EditText)findViewById(R.id.bookDate);
        useCompany = (EditText)findViewById(R.id.useCompany);
        useDept = (EditText)findViewById(R.id.useDept);
        usePeople = (EditText)findViewById(R.id.usePeople);
        storeAddress = (EditText)findViewById(R.id.storeAddress);

        barCode.setOnKeyListener(onKey);

        relative = (RelativeLayout)findViewById(R.id.relative);
    }

    View.OnKeyListener onKey=new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_UP) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(relative.getWindowToken(), 0);
                barCode.setText(barCode.getText().toString());// 添加这句后实现效果
                barCode.selectAll();
                selectByBarCode();
                return true;
            }else {
                return false;
            }
        }
    };

    /*private Toast mToast;
    public void showToast(String text) {
        if(mToast == null) {
            mToast = Toast.makeText(CountDataActivity.this, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }
    public void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
    public void onBackPressed() {
        cancelToast();
        super.onBackPressed();
    }*/

    private void selectByBarCode(){
        final String barCodeValue = barCode.getText().toString().toUpperCase();
        if (barCodeValue == "" || barCodeValue.equals("")){
            this.barCodeValue.setText("");
            assName.setText("");
            className.setText("");
            assType.setText("");
            assPrice.setText("");
            bookDate.setText("");
            useCompany.setText("");
            useDept.setText("");
            usePeople.setText("");
            storeAddress.setText("");
            ToastUtils.showToast(CountDataActivity.this,"请输入或扫描要查询的资产编码");
        }else{
            String sql = "select "+Constant.STO_CON_SEL+" from "+
                    Constant.TABLE_NAME_STORAGE + " where barCode='"+barCodeValue+"' and sysUUID = '"+sysUUID+"'";
            SQLiteDatabase db = helper.getWritableDatabase();
            String [] fileds = new String[]{
                   "barCode", "assName","className","assType","assPrice","bookDate","useCompany","useDept","usePeople","storeAddress"
            };

            List<String> list = MySqliteHelper.query(CountDataActivity.this,sql,fileds,db);

            if (list.size()>0){
                //判断是否已盘点
                //获取当前编号的盘点状态
                String detil_sql = "select countState from "+
                        Constant.TABLE_NAME_COUNT_DETAIL + " where barCode='"+barCodeValue+"' and sysUUID = '"+sysUUID+"'";
                String [] countStates = new String[]{ "countState" };
                db = helper.getWritableDatabase();
                String countState = MySqliteHelper.queryUUID(CountDataActivity.this,detil_sql,countStates,db).get(0);
                int state = Integer.parseInt(countState);
                if(state!=0){           //已盘点--已处理
                    this.barCodeValue.setText("");
                    assName.setText("");
                    className.setText("");
                    assType.setText("");
                    assPrice.setText("");
                    bookDate.setText("");
                    useCompany.setText("");
                    useDept.setText("");
                    usePeople.setText("");
                    storeAddress.setText("");
                    ToastUtils.showToast(CountDataActivity.this,"该物品已盘点！");
                }else{                  //未盘点--未处理
                    /*barCode.setText("");*/
                    this.barCodeValue.setText(list.get(0));
                    assName.setText(list.get(1));
                    className.setText(list.get(2));
                    assType.setText(list.get(3));
                    assPrice.setText(list.get(4));
                    bookDate.setText(list.get(5));
                    useCompany.setText(list.get(6));
                    useDept.setText(list.get(7));
                    usePeople.setText(list.get(8));
                    storeAddress.setText(list.get(9));

                    db = helper.getWritableDatabase();
                    final List<String> stoList = MySqliteHelper.queryUUID(CountDataActivity.this,sql,fileds,db);

                /*查询该资产的地址部门等信息与盘点地址部门的信息是否相同-- 盘点盘点部门和盘点地址是否相同，
                * 相同，设置盘点人，盘点时间，盘点状态1
                * 不相同，询问是否需要对使用部门，存放地址进行修改，差异2
                * 没盘点的，就是本地没有的，0
                * */
                    String sqlCount = "select countUUID from "+
                            Constant.TABLE_NAME_COUNT_DETAIL + " where barCode='"+barCodeValue+"' and sysUUID = '"+sysUUID+"'";
                    String [] filedsUUID = new String[]{ "countUUID" };

                    db = helper.getWritableDatabase();
                    final List<String> countUUID = MySqliteHelper.queryUUID(CountDataActivity.this,sqlCount,filedsUUID,db);

                    //盘点时间
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    final String countTime = df.format(System.currentTimeMillis());

                    if(countUUID.size()>0){
                        //相同
                        if(stoList.get(7).equals(countDept)&&stoList.get(9).equals(countPlace)){
                            ContentValues values = new ContentValues();
                            values.put("countPeople",userUUID);
                            values.put("countState",1);
                            values.put("countTime",countTime);
                            db = helper.getWritableDatabase();
                            long count = MySqliteHelper.update(Constant.TABLE_NAME_COUNT_DETAIL,values,db,new String[]{countUUID.get(0)});
                            if (count>0){
                                ToastUtils.showToast(CountDataActivity.this,"记录成功");
                            }else {
                                ToastUtils.showToast(CountDataActivity.this,"请输入或扫描要查询的资产编码");
                            }
                        }else{
                            //本地有别的地或者部门的资产
                            AlertDialog.Builder build = new AlertDialog.Builder(this);
                            build.setTitle("注意").setMessage("该资产不属于本部门或本地址，是否变更？")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SQLiteDatabase db = helper.getWritableDatabase();

                                            /*查询盘点公司*/
                                            String deptSql = "select pDeptUUID from "+Constant.TABLE_NAME_DEPT+" where deptUUID = '"+stoList.get(7)+"'";
                                            List<String> deptUUID = MySqliteHelper.queryUUID(CountDataActivity.this,deptSql,new String[]{"pDeptUUID"},db);
                                            ContentValues values = new ContentValues();
                                            values.put("countCompany",deptUUID.get(0));
                                            values.put("countDepartment",countDept);
                                            values.put("countPlace",countPlace);
                                            values.put("countPeople",userUUID);
                                            values.put("countState",2);
                                            values.put("countTime",countTime);

                                            db = helper.getWritableDatabase();
                                            long count = MySqliteHelper.update(Constant.TABLE_NAME_COUNT_DETAIL,values,db,new String[]{countUUID.get(0)});
                                            if (count>0){
                                                ToastUtils.showToast(CountDataActivity.this,"记录成功");
                                            }else {
                                                ToastUtils.showToast(CountDataActivity.this,"请输入或扫描要查询的资产编码");
                                            }
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SQLiteDatabase db = helper.getWritableDatabase();

                                            ContentValues values = new ContentValues();
                                            values.put("countState",0);
                                            values.put("countDepartment",stoList.get(7));
                                            values.put("countPlace",stoList.get(9));
                                            values.put("countPeople","");
                                            values.put("countTime","");

                                            MySqliteHelper.update(Constant.TABLE_NAME_COUNT_DETAIL,values,db,new String[]{countUUID.get(0)});
                                            db.close();
                                        }
                                    }).show();
                        }
                    }
                    else{
                        //盘点单中没有，又是这个部门这个的资产
                        AlertDialog.Builder build = new AlertDialog.Builder(this);
                        build.setTitle("注意").setMessage("该盘点单不存在该资产，是否添加？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SQLiteDatabase db = helper.getWritableDatabase();
                                        /*查询盘点公司*/
                                        String deptSql = "select pDeptUUID from "+Constant.TABLE_NAME_DEPT+" where deptUUID = '"+stoList.get(7)+"'";
                                        List<String> comUUID = MySqliteHelper.queryUUID(CountDataActivity.this,deptSql,new String[]{"pDeptUUID"},db);

                                        /*查询盘点集团*/
                                        db = helper.getWritableDatabase();
                                        String groupSql = "select pDeptUUID from "+Constant.TABLE_NAME_DEPT+" where deptUUID = '"+comUUID.get(0)+"'";
                                        List<String> groupUUID = MySqliteHelper.queryUUID(CountDataActivity.this,groupSql,new String[]{"pDeptUUID"},db);

                                        /*查询盘点单号*/
                                        db = helper.getWritableDatabase();
                                        String codeSql = "select distinct countbillCode from "+Constant.TABLE_NAME_COUNT_DETAIL + "where sysUUID = '"+sysUUID+"'";
                                        List<String> codeList = MySqliteHelper.query(CountDataActivity.this,codeSql,new String[]{"countbillCode"},db);

                                        ContentValues values = new ContentValues();
                                        values.put("countbillCode",codeList.get(0));
                                        values.put("barCode",barCodeValue);
                                        values.put("countGroup",groupUUID.get(0));
                                        values.put("countCompany",comUUID.get(0));
                                        values.put("countDepartment",countDept);
                                        values.put("countPlace",countPlace);
                                        values.put("countPeople",userUUID);
                                        values.put("countState",2);
                                        values.put("countTime",countTime);

                                        db = helper.getWritableDatabase();
                                        long count = MySqliteHelper.insert(Constant.TABLE_NAME_COUNT_DETAIL,values,db);
                                        if (count>0){
                                            ToastUtils.showToast(CountDataActivity.this,"记录成功");
                                        }else {
                                            ToastUtils.showToast(CountDataActivity.this,"请输入或扫描要查询的资产编码");
                                        }
                                        db.close();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
                    }
                }
            }else {
                this.barCodeValue.setText("");
                assName.setText("");
                className.setText("");
                assType.setText("");
                assPrice.setText("");
                bookDate.setText("");
                useCompany.setText("");
                useDept.setText("");
                usePeople.setText("");
                storeAddress.setText("");
                ToastUtils.showToast(CountDataActivity.this,"不存在该编码的资产");
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                redirect(CheckDataActivity.class);
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
        bundle.putString("countDept",countDept);
        bundle.putString("countPlace",countPlace);
        bundle.putString("sysUUID",sysUUID);
        intent.putExtras(bundle);
        intent.setClass(CountDataActivity.this, cls);
        startActivity(intent);//跳转
    }
}
