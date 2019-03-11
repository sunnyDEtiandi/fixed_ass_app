package fixedass.barcode.com.fixed_ass_app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import fixedass.barcode.com.fixed_ass_app.bean.CountDetail;
import fixedass.barcode.com.fixed_ass_app.tool.Redirect;
import fixedass.barcode.com.fixed_ass_app.tool.ToastUtils;
import fixedass.barcode.com.fixed_ass_app.util.Constant;
import fixedass.barcode.com.fixed_ass_app.util.DbManager;
import fixedass.barcode.com.fixed_ass_app.tool.ExitApplication;
import fixedass.barcode.com.fixed_ass_app.R;
import fixedass.barcode.com.fixed_ass_app.util.MySqliteHelper;

public class MainActivity extends Activity {
    /*private Button exitBtn;*/
    private ImageView exitBtn;
    private TextView userName;

    //加载数据 进行盘点 盘点查询 资产查询 关于我们 导出数据
    private ImageView loadData, checkData, inquiryCheck, inquiryAss, aboutUs, exportData;          //导出数据

    private String userUUID, name, sysUUID;

    private MySqliteHelper helper;
    private int rowCount;
    private int num;

    private String ipStr,dbPWD;
    // 创建等待框
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ExitApplication.getInstance().addActivity(this);    //记录activity

        /*获得用户的信息*/
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userUUID = bundle.getString("user");
        name = bundle.getString("name");
        sysUUID = bundle.getString("sysUUID");
        userName = (TextView) findViewById(R.id.userName);
        userName.setText(name);

        /*读取数据*/
        SharedPreferences sharedPreferences = MainActivity.super.getSharedPreferences(Constant.FILENAME, Activity.MODE_PRIVATE);
        ipStr = sharedPreferences.getString("ipStr","");
        dbPWD = sharedPreferences.getString("dbPWD","");

        /*loadData = (MyImageTextViewNew)findViewById(R.id.loaddata);*/
        loadData = (ImageView) findViewById(R.id.loaddata);
        loadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadBasisData();
                /*判断是否有数据*/
                /*SQLiteDatabase db = helper.getWritableDatabase();
                Cursor cursor = db.query(Constant.TABLE_NAME_COUNTBILL, null, null, null, null, null, null);
                int count = cursor.getCount();
                if(count==0){
                    Toast.makeText(MainActivity.this,"请先在网页中添加盘点单！",Toast.LENGTH_SHORT).show();
                }else{
                    redirect(LoadDataActivity.class);
                }*/
            }
        });

        checkData = (ImageView) findViewById(R.id.checkData);
        checkData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*判断是否有数据*/
                SQLiteDatabase db = helper.getWritableDatabase();
                Cursor cursor = db.query(Constant.TABLE_NAME_COUNT_DETAIL, null, null, null, null, null, null);
                int count = cursor.getCount();
                if(count==0){
                    Toast.makeText(MainActivity.this,"请先导入数据！",Toast.LENGTH_SHORT).show();
                }else{
                    redirect(CheckDataActivity.class);
                }
            }
        });

        inquiryCheck = (ImageView) findViewById(R.id.inquiryCheck);
        inquiryCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*判断是否有数据*/
                SQLiteDatabase db = helper.getWritableDatabase();
                Cursor cursor = db.query(Constant.TABLE_NAME_COUNT_DETAIL, null, null, null, null, null, null);
                int count = cursor.getCount();
                if(count==0){
                    Toast.makeText(MainActivity.this,"请先导入数据！",Toast.LENGTH_SHORT).show();
                }else{
                    redirect(InquiryCheckActivity.class);
                }
            }
        });

        inquiryAss = (ImageView) findViewById(R.id.inquiryAss);
        inquiryAss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*判断是否有数据*/
                SQLiteDatabase db = helper.getWritableDatabase();
                Cursor cursor = db.query(Constant.TABLE_NAME_STORAGE, null, null, null, null, null, null);
                int count = cursor.getCount();
                if(count==0){
                    Toast.makeText(MainActivity.this,"请先在网页中添加资产！",Toast.LENGTH_SHORT).show();
                }else{
                    redirect(InquiryAssActivity.class);
                }
            }
        });

        aboutUs = (ImageView) findViewById(R.id.aboutUs);
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirect(AboutUsActivity.class);
            }
        });

        exitBtn = (ImageView) findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(new ExitBtnListener());

        helper = DbManager.getIntance(this);

        exportData = (ImageView) findViewById(R.id.exportData);
        exportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /*判断是否有数据*/
                SQLiteDatabase db = helper.getWritableDatabase();
                Cursor cursor = db.query(Constant.TABLE_NAME_COUNT_DETAIL,null, "countState <> 0",
                        null,null,null,null);
                int count = cursor.getCount();
                if(count==0){
                    Toast.makeText(MainActivity.this,"请先进行盘点在导出数据！",Toast.LENGTH_SHORT).show();
                }else{
                    new MyThread().start();
                    if (rowCount == num){
                        ToastUtils.showToast(MainActivity.this,"数据导出成功");
                    }
                }
            }
        });
    }

    /*加载数据*/
    private void loadBasisData(){
        if (!Redirect.checkNetwork(MainActivity.this)) {
            Toast toast = Toast.makeText(MainActivity .this,"网络未连接", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }else{
            // 提示框
            dialog = new ProgressDialog(this);
            dialog.setTitle("提示");
            dialog.setMessage("正在加载数据，请稍后...");
            dialog.setCancelable(false);
            dialog.show();

            // 创建子线程，分别进行Get和Post传输
            new LoadBasicDataThread().start();
        }
    }

    //加载基础数据
    class LoadBasicDataThread extends Thread{
        private Connection connection = null;

        @Override
        public void run() {
            try {
                Class.forName(Constant.DRIVER);
                connection = DriverManager.getConnection(Constant.URLPRE+ipStr+Constant.URLSUF, Constant.USER, dbPWD);
                insertData();    //测试数据库连接
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        private void insertData() throws java.sql.SQLException {
            Statement stmt = connection.createStatement();        //创建Statement
            SQLiteDatabase db = helper.getWritableDatabase();

            /*删除表中的数据*/
            db.delete(Constant.TABLE_NAME_USER,null,null);
            db.delete(Constant.TABLE_NAME_DEPT,null,null);
            db.delete(Constant.TABLE_NAME_ADDRESS,null,null);
            db.delete(Constant.TABLE_NAME_STORAGE,null,null);
            db.delete(Constant.TABLE_NAME_COUNTBILL,null,null);
            db.delete(Constant.TABLE_NAME_PEOPLE,null,null);
            db.close();

            db = helper.getWritableDatabase();

            String deptUUID = "";

            /*用户表*/
            String sql_user = "select "+Constant.USER_CON_SQL+" from "+Constant.TABLE_NAME_USER+" where userState = '1' and sysUUID='"+sysUUID+"'";
            ResultSet resultSet = stmt.executeQuery(sql_user);          //ResultSet类似Cursor
            if (resultSet != null && resultSet.first()) {
                while (!resultSet.isAfterLast()) {
                    if(userUUID.equals(resultSet.getString("userUUID"))){
                        deptUUID = resultSet.getString("deptUUID");
                    }
                    ContentValues values = new ContentValues();
                    values.put("userUUID",resultSet.getString("userUUID"));
                    values.put("deptUUID",resultSet.getString("deptUUID"));
                    values.put("userPWD", resultSet.getString("userPWD"));
                    values.put("userNo", resultSet.getString("userNo"));
                    values.put("userName",resultSet.getString("userName"));
                    values.put("sysUUID",resultSet.getString("sysUUID"));
                    values.put("creatorID", resultSet.getString("creatorID"));
                    long count = db.insert(Constant.TABLE_NAME_USER, null, values);
                    resultSet.next();
                }
            }
            resultSet.close();
            db.close();

            db = helper.getWritableDatabase();
            String deptCode = "";
            /*部门表*/
            String sql_dept = "select "+Constant.DEPT_CON_SQL+" from " + Constant.TABLE_NAME_DEPT+" where sysUUID = '"+sysUUID+"'";
            ResultSet deptResultSet = stmt.executeQuery(sql_dept);
            if (deptResultSet != null && deptResultSet.first()) {
                while (!deptResultSet.isAfterLast()) {
                    if(deptResultSet.getString("deptUUID").equals(deptUUID)){
                        deptCode = deptResultSet.getString("deptCode");
                    }
                    ContentValues values = new ContentValues();
                    values.put("deptUUID",deptResultSet.getString("deptUUID"));
                    values.put("pDeptUUID",deptResultSet.getString("pDeptUUID"));
                    values.put("deptName",deptResultSet.getString("deptName"));
                    values.put("deptType",deptResultSet.getString("deptType"));
                    values.put("sysUUID",deptResultSet.getString("sysUUID"));
                    db.insert(Constant.TABLE_NAME_DEPT,null,values);
                    deptResultSet.next();
                }
            }
            deptResultSet.close();
            db.close();

            db = helper.getWritableDatabase();
            /*地址表*/
            String sql_add = "select "+Constant.ADD_CON_SQL+" from "+Constant.TABLE_NAME_ADDRESS;

            ResultSet addResultSet = stmt.executeQuery(sql_add);
            if (addResultSet != null && addResultSet.first()) {
                while (!addResultSet.isAfterLast()) {
                    ContentValues values = new ContentValues();
                    values.put("addrUUID",addResultSet.getString("addrUUID"));
                    values.put("deptUUID",addResultSet.getString("deptUUID"));
                    values.put("addrName",addResultSet.getString("addrName"));
                    db.insert(Constant.TABLE_NAME_ADDRESS,null,values);
                    addResultSet.next();
                }
            }
            addResultSet.close();
            db.close();

            db = helper.getWritableDatabase();
            /*资产入库表*/
            String sql_sto = "select " + Constant.STO_CON_SQL+ " from "+Constant.TABLE_NAME_STORAGE +" where isDel = true AND sysUUID = '"+sysUUID+"'";
            ResultSet stoResultSet = stmt.executeQuery(sql_sto);
            if (stoResultSet != null && stoResultSet.first()) {
                while (!stoResultSet.isAfterLast()) {
                    ContentValues values = new ContentValues();
                    values.put("barCode",stoResultSet.getString("barCode"));
                    values.put("assName",stoResultSet.getString("assName"));
                    values.put("className",stoResultSet.getString("className"));
                    values.put("useGroup",stoResultSet.getString("useGroup"));
                    values.put("useCompany",stoResultSet.getString("useCompany"));
                    values.put("usePeople",stoResultSet.getString("usePeople"));
                    values.put("useDept",stoResultSet.getString("useDept"));
                    values.put("storeAddress",stoResultSet.getString("storeAddress"));
                    values.put("assType",stoResultSet.getString("assType"));
                    values.put("bookDate",stoResultSet.getString("bookDate"));
                    values.put("assPrice",stoResultSet.getString("assPrice"));
                    values.put("sysUUID",stoResultSet.getString("sysUUID"));
                    db.insert(Constant.TABLE_NAME_STORAGE,null,values);
                    stoResultSet.next();
                }
            }
            stoResultSet.close();
            db.close();

            db = helper.getWritableDatabase();
            /*盘点单据表*/
            int num = 0;
            /*登录者所在公司的部门*/
            String useDept = "";
            String user_com_sql = "select deptUUID from "+Constant.TABLE_NAME_DEPT + " where deptCode like '"+deptCode+"%' AND sysUUID = '"+sysUUID+"'";
            ResultSet rs = stmt.executeQuery(user_com_sql);
            while(rs.next()){
                useDept += "'"+rs.getString("deptUUID")+"',";
            }
            rs.close();

            //查询部门相关的用户的集合
            String creatorUUID = "";
            String creator_uuid_sql = "select userUUID from "+Constant.TABLE_NAME_USER+" where deptUUID in ("+useDept.substring(0,useDept.length()-1)+")";
            final ResultSet creatorRS = stmt.executeQuery(creator_uuid_sql);
            while (creatorRS.next()){
                creatorUUID += "'"+creatorRS.getString("userUUID")+"',";
            }
            creatorRS.close();

            //根据创建用户的集合获取怕你淡淡
            if (!creatorUUID.equals("")){
                String sql_cBill = "select "+Constant.BILL_CON_SQL+" from "+Constant.TABLE_NAME_COUNTBILL+
                        " where createPeople in ("+creatorUUID.substring(0,creatorUUID.length()-1)+") and isDel = true AND sysUUID = '"+sysUUID+"'";
                ResultSet cBillResultSet = stmt.executeQuery(sql_cBill);
                if (cBillResultSet != null && cBillResultSet.first()) {
                    while (!cBillResultSet.isAfterLast()) {
                        num ++;
                        ContentValues values = new ContentValues();
                        values.put("createDate",cBillResultSet.getString("createDate"));
                        values.put("countBillCode",cBillResultSet.getString("countBillCode"));
                        values.put("createPeople",cBillResultSet.getString("createPeople"));
                        values.put("countNote",cBillResultSet.getString("countNote"));
                        values.put("sysUUID",cBillResultSet.getString("sysUUID"));
                        db.insert(Constant.TABLE_NAME_COUNTBILL,null,values);
                        cBillResultSet.next();
                    }
                }
                cBillResultSet.close();
            }

            /*查询和部门相关的资产的集合*/
            /*String barCodes = "";
            String sql_stoAbout = "select barCode from "+Constant.TABLE_NAME_STORAGE+
                    " where useDept in ("+useDept.substring(0,useDept.length()-1)+") and isDel=true and sysUUID = '"+sysUUID+"'";
            ResultSet stoAboutRS = stmt.executeQuery(sql_stoAbout);
            while (stoAboutRS.next()){
                barCodes += "'"+stoAboutRS.getString("barCode")+"',";
            }*/

           /* if (!barCodes.equals("")){
                String sql_cDetail = "select distinct countBillCode from "+Constant.TABLE_NAME_COUNT_DETAIL+
                        " where barCode in ("+barCodes.substring(0,barCodes.length()-1)+") and sysUUID = '"+sysUUID+"'";
                ResultSet barCodeAboutRS = stmt.executeQuery(sql_cDetail);
                String countBillCodes = "";
                while (barCodeAboutRS.next()){
                    countBillCodes += "'"+barCodeAboutRS.getString("countBillCode")+"',";
                }
                String sql_cBill = "select "+Constant.BILL_CON_SQL+" from "+Constant.TABLE_NAME_COUNTBILL+
                        " where countBillCode in ("+countBillCodes.substring(0,countBillCodes.length()-1)+") and isDel = true AND sysUUID = '"+sysUUID+"'";
                ResultSet cBillResultSet = stmt.executeQuery(sql_cBill);
                if (cBillResultSet != null && cBillResultSet.first()) {
                    while (!cBillResultSet.isAfterLast()) {
                        num ++;
                        ContentValues values = new ContentValues();
                        values.put("createDate",cBillResultSet.getString("createDate"));
                        values.put("countBillCode",cBillResultSet.getString("countBillCode"));
                        values.put("createPeople",cBillResultSet.getString("createPeople"));
                        values.put("countNote",cBillResultSet.getString("countNote"));
                        values.put("sysUUID",cBillResultSet.getString("sysUUID"));
                        db.insert(Constant.TABLE_NAME_COUNTBILL,null,values);
                        cBillResultSet.next();
                    }
                }
                cBillResultSet.close();
            }*/
            db.close();

            db = helper.getWritableDatabase();
            /*部门员工表*/
            String sql_peo = "select "+Constant.PEO_CON_SQL+" from "+Constant.TABLE_NAME_PEOPLE + " where isDel = true AND sysUUID = '"+sysUUID+"'";
            ResultSet peoResultSet = stmt.executeQuery(sql_peo);
            if (peoResultSet != null && peoResultSet.first()) {
                while (!peoResultSet.isAfterLast()) {
                    ContentValues values = new ContentValues();
                    values.put("pUUID",peoResultSet.getString("pUUID"));
                    values.put("pName",peoResultSet.getString("pName"));
                    values.put("sysUUID",peoResultSet.getString("sysUUID"));
                    db.insert(Constant.TABLE_NAME_PEOPLE,null,values);
                    peoResultSet.next();
                }
            }
            peoResultSet.close();

            stmt.close();
            db.close();
            dialog.dismiss();
            if (num==0){
                Looper.prepare();
                Toast.makeText(MainActivity.this,"请先在网页中添加盘点单！",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }else{
                redirect(LoadDataActivity.class);
            }
        }
    }


    /*将数据写回MySQL*/
    class MyThread extends Thread{
        private Connection connection = null;

        @Override
        public void run() {
            try {
                Class.forName(Constant.DRIVER);
                connection = DriverManager.getConnection(Constant.URLPRE+ipStr+Constant.URLSUF, Constant.USER, dbPWD);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            try {
                test(connection);    //测试数据库连接
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }

        private void test(Connection con1) throws java.sql.SQLException {
            try {
                SQLiteDatabase db = helper.getWritableDatabase();
                retrunWrite(con1, db);
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
        };

        private void retrunWrite(Connection con1, SQLiteDatabase db) throws java.sql.SQLException{
            Statement stmt = con1.createStatement();        //创建Statement

            /*查询SQLite里面的数据*/
            Cursor cursor = db.query(Constant.TABLE_NAME_COUNT_DETAIL,null, "countState <> 0",
                    null,null,null,null);
            List<CountDetail> list = DbManager.cursorToList(cursor);
            rowCount = list.size();

            /*修改数据以及插入数据根据countUUID*/
            for (CountDetail cd :list){
                if(cd.getCountUUID()!=null){
                    //修改数据
                    String updatSql = "update "+Constant.TABLE_NAME_COUNT_DETAIL +
                            " set countGroup='"+cd.getCountGroup()+"',"+
                            "countCompany='"+cd.getCountCompany()+"',"+
                            "countDepartment='"+cd.getCountDepartment()+"',"+
                            "countPlace='"+cd.getCountPlace()+"',"+
                            "countPeople='"+cd.getCountPeople()+"',"+
                            "countTime='"+cd.getCountTime()+"',"+
                            "countState="+cd.getCountState()+" where countUUID='"+cd.getCountUUID()+"' AND sysUUID = '"+sysUUID+"'";

                    int count = stmt.executeUpdate(updatSql);
                    if(count>0){
                        num++;
                    }
                }else{
                    //插入数据
                    /*INSERT INTO table_name (列1, 列2,...) VALUES (值1, 值2,....)*/
                    String countUUID = UUID.randomUUID().toString();

                    /*修改SQLite的数据*/
                    ContentValues values = new ContentValues();
                    values.put("countUUID",countUUID);
                    db.update(Constant.TABLE_NAME_COUNT_DETAIL,values,"barCode=? and sysUUID='"+sysUUID+"'" ,new String[]{cd.getBarCode()});

                    /*插入MySQL的数据*/
                    String insertSql = "insert into "+Constant.TABLE_NAME_COUNT_DETAIL+
                            " (countUUID, countbillCode, barCode, countGroup, countCompany, " +
                            "countDepartment, countPlace, countPeople, countTime, countState, sysUUID) " +
                            "values( '"+countUUID+"','"+cd.getCountbillCode()+"','"+cd.getBarCode()+"','"+
                            cd.getCountGroup()+"','"+cd.getCountCompany()+"','"+cd.getCountDepartment()+"','"+
                            cd.getCountPlace()+"','"+cd.getCountPeople()+"','"+cd.getCountTime()+"',"+
                            cd.getCountState()+",'"+sysUUID+"')";
                    int count = stmt.executeUpdate(insertSql);
                    if(count>0){
                        num++;
                    }
                }
            }
            stmt.close();
            db.close();
        }
    }

    /*跳转*/
    private void redirect(Class<?> cls) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("user",userUUID);
            bundle.putString("name",name);
            bundle.putString("sysUUID", sysUUID);
            intent.putExtras(bundle);
            intent.setClass(MainActivity.this, cls);
            startActivity(intent);//跳转
    }

    /*退出系统的按钮监听*/
    class ExitBtnListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
            build.setTitle("注意").setMessage("确定要退出登录吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ExitApplication.getInstance().exit(MainActivity.this);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        }
    }

    /*返回键事件*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                AlertDialog.Builder build = new AlertDialog.Builder(this);
                build.setTitle("注意").setMessage("确定要退出登录吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ExitApplication.getInstance().exit(MainActivity.this);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                break;
            default:
                break;
        }
        return false;
    }
}
