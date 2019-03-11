package fixedass.barcode.com.fixed_ass_app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import fixedass.barcode.com.fixed_ass_app.tool.Redirect;
import fixedass.barcode.com.fixed_ass_app.util.Constant;
import fixedass.barcode.com.fixed_ass_app.util.DbManager;
import fixedass.barcode.com.fixed_ass_app.tool.ExitApplication;
import fixedass.barcode.com.fixed_ass_app.R;
import fixedass.barcode.com.fixed_ass_app.util.MySqliteHelper;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

/**
 * Created by Administrator on 2018/2/27.
 * 用户登录
 */

public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText userName, passWord;
    private String deptUUID, sysUUID, ipStr, dbPWD;
    private TextView point, setting;
    /*private Button cancelBtn;*/
   /* private Button loginBtn;*/
    private ImageView loginBtn;
    private TextView cancelBtn;
    private Handler handler;
    private MySqliteHelper helper;
    private RelativeLayout relative;
    // 创建等待框
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        ExitApplication.getInstance().addActivity(this);

        userName = (EditText)findViewById(R.id.userName);
        passWord = (EditText)findViewById(R.id.passWord);
        passWord.setOnKeyListener(onKey);

        point = (TextView)findViewById(R.id.point);
        /*cancelBtn = (Button)findViewById(R.id.cancelBtn);
        loginBtn = (Button)findViewById(R.id.loginBtn);*/
        cancelBtn = (TextView) findViewById(R.id.cancelBtn);
        loginBtn = (ImageView) findViewById(R.id.loginBtn);

        setting = (TextView) findViewById(R.id.setting);
        setting.setKeyListener(null);
        setting.setOnClickListener(this);

        handler = new MyHandler();

        loginBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        helper = DbManager.getIntance(this);

        SQLiteStudioService.instance().start(this);     //可视化查看数据库

        relative = (RelativeLayout)findViewById(R.id.relative);

        /*读取数据*/
        SharedPreferences sharedPreferences = LoginActivity.super.getSharedPreferences(Constant.FILENAME,Activity.MODE_PRIVATE);
        ipStr = sharedPreferences.getString("ipStr","");
        dbPWD = sharedPreferences.getString("dbPWD","");
    }

    View.OnKeyListener onKey=new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                Redirect.hideIM(v, LoginActivity.this);
                login();
                return true;
            }
            return false;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        case R.id.loginBtn:
            login();
            break;
        case R.id.cancelBtn:
            AlertDialog.Builder build = new AlertDialog.Builder(LoginActivity.this);
            build.setTitle("注意").setMessage("确定要退出吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("取消", null).show();
            break;
        case R.id.setting:
            LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);          //创建LayoutInflater的对象
            View dialogView = inflater.inflate(R.layout.seeting_dialog,null);   //将布局文件转换为view
            final EditText editText, pwdText;
            editText = (EditText)dialogView.findViewById(R.id.ip);
            pwdText = (EditText)dialogView.findViewById(R.id.dbpwd);
            if(ipStr!=""){
                editText.setText(ipStr);
                editText.selectAll();
            }
            if(dbPWD!=""){
                pwdText.setText(dbPWD);
                pwdText.selectAll();
            }

            Dialog dialog = new AlertDialog.Builder(LoginActivity.this)        //创建dialog
                    .setTitle("系统设置")
                    .setView(dialogView)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //指定操作的文件名称
                            SharedPreferences sh = LoginActivity.super.getSharedPreferences(Constant.FILENAME,Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = sh.edit();                      //编辑文件
                            String ipStr1 = editText.getText().toString();
                            String dbPWD1 = pwdText.getText().toString();

                            if(ipStr1.trim().equals("")){
                                 AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
                                        .setTitle("注意").setMessage("请先填写系统设置中的服务器地址！")
                                        .setPositiveButton("确定", null).create();
                                 alertDialog.show();
                                return;
                            }
                            if(dbPWD1.trim().equals("")){
                                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
                                        .setTitle("注意").setMessage("请先填写系统设置中的数据库密码！")
                                        .setPositiveButton("确定", null).create();
                                alertDialog.show();
                                return;
                            }

                            if(!ipStr.equals(ipStr1)){
                                ipStr = ipStr1;
                                edit.putString("ipStr",ipStr);                                 //保存
                            }
                            if(!dbPWD.equals(dbPWD1)){
                                dbPWD = dbPWD1;
                                edit.putString("dbPWD", dbPWD);
                            }
                            edit.commit();                                                  //提交跟新
                        }
                    })
                    .setNegativeButton("取消", null).create();
            dialog.show();
            break;
        }
    }

    /*登录*/
    private void login(){
        String userN = userName.getText().toString();
        String pwd = passWord.getText().toString();

        if(userN.equals("")){
            Message msg = new Message();
            msg.obj = "用户名不能为空";
            handler.sendMessage(msg);
        }else if(passWord.getText().toString().equals("")){
            Message msg = new Message();
            msg.obj = "密码不能为空";
            handler.sendMessage(msg);
        }else if (userN.equals("admin")) {
            Message msg = new Message();
            msg.obj = "系统超级管理员没有登录盘点系统的权限";
            handler.sendMessage(msg);
        }else {
            if (!Redirect.checkNetwork(LoginActivity.this)) {
                //判断是否有用户
                SQLiteDatabase db = helper.getReadableDatabase();
                Cursor cursor = db.query(Constant.TABLE_NAME_USER, null, null, null, null, null, null);
                if (cursor.getCount()>0){
                    pwd = new SimpleHash("MD5", pwd, ByteSource.Util.bytes(userN), 5).toString();

                    /*String sql = "select " +Constant.USER_CON_SQL+
                            " from "+Constant.TABLE_NAME_USER+
                            " where userNo='" + userN + "' and userState = '1'";
                    Cursor query = db.rawQuery(sql, null);*/
                    Cursor query = db.query(Constant.TABLE_NAME_USER, null, "userNo='" + userN + "'", null,null,null,null);
                    if (query.moveToNext()){
                        if (query.getString(query.getColumnIndex("userPWD")).equals(pwd)){
                            Message msg = new Message();
                            msg.obj = "";
                            handler.sendMessage(msg);

                            if (query.getString(query.getColumnIndex("creatorID")).equals("1")) {
                                Message msgs = new Message();
                                msgs.obj = "系统管理员没有登录盘点系统的权限";
                                handler.sendMessage(msgs);
                            } else {
                                //传递登陆者的信息
                                String userUUID = query.getString(query.getColumnIndex("userUUID"));
                                String name = query.getString(query.getColumnIndex("userName"));
                                deptUUID = query.getString(query.getColumnIndex("deptUUID"));
                                sysUUID = query.getString(query.getColumnIndex("sysUUID"));

                                Bundle bundle = new Bundle();
                                bundle.putString("user", userUUID);
                                bundle.putString("name", name);
                                bundle.putString("sysUUID", sysUUID);

                                //正确--跳转界面
                                Intent loginIntent = new Intent();
                                loginIntent.putExtras(bundle);
                                loginIntent.setClass(LoginActivity.this, MainActivity.class);
                                startActivity(loginIntent);
                            }
                        }else {
                            Message msg = new Message();
                            msg.obj = "密码错误";
                            handler.sendMessage(msg);
                        }
                    }else {
                        Message msg = new Message();
                        msg.obj = "该用户名不存在";
                        handler.sendMessage(msg);
                    }
                }else {
                    Toast toast = Toast.makeText(LoginActivity.this, "网络未连接", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }else{
                if(ipStr.trim().equals("")){
                    AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("注意").setMessage("请先填写系统设置中的服务器地址！")
                            .setPositiveButton("确定", null).create();
                    alertDialog.show();
                }else if(dbPWD.trim().equals("")){
                    AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("注意").setMessage("请先填写系统设置中的数据库密码！")
                            .setPositiveButton("确定",null).create();
                    alertDialog.show();
                }else{
                    // 提示框
                    dialog = new ProgressDialog(this);
                    dialog.setTitle("提示");
                    dialog.setMessage("正在登陆，请稍后...");
                    dialog.setCancelable(false);
                    dialog.show();

                    // 创建子线程，分别进行Get和Post传输
                    //new Thread(new MyThread()).start();
                    new MyThread().start();
                }
            }
        }
    }

    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
           String str = (String)msg.obj;
           point.setText(str);
        }
    }

    class MyThread extends Thread{
        private Connection connection = null;

        @Override
        public void run() {
            String url = Constant.URLPRE+ipStr+Constant.URLSUF;
            Log.i("url", url);
            Log.i("dpPWD", dbPWD);
            try {
                Class.forName(Constant.DRIVER);
                connection = DriverManager.getConnection(url, Constant.USER, dbPWD);
                /* connection.*/
            } catch (Exception e1) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("注意").setMessage("请输入正确的服务器地址或者数据库密码！")
                                .setPositiveButton("确定", null).create();
                        alertDialog.show();
                    }
                });
                e1.printStackTrace();
                dialog.dismiss();
                return;
            }

            if(connection==null){
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("注意").setMessage("请输入正确的服务器地址或者数据库密码！")
                                .setPositiveButton("确定", null).create();
                        alertDialog.show();
                    }
                });
                dialog.dismiss();
                return;
            }
            try {
                test(connection);    //测试数据库连接
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
            finally {
                dialog.dismiss();
            }
        }

        public void test(Connection con1) throws java.sql.SQLException {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(relative.getWindowToken(), 0);

            String userN = userName.getText().toString();
            String pwd = passWord.getText().toString();

            pwd = new SimpleHash("MD5", pwd, ByteSource.Util.bytes(userN), 5).toString();
            /*if (userN.equals("") || pwd.equals("")) {
                Message msg = new Message();
                msg.obj = "用户名或密码不能为空";
                handler.sendMessage(msg);
            } else */
            /*if (userN.equals("admin")) {
                Message msg = new Message();
                msg.obj = "系统超级管理员没有登录盘点系统的权限";
                handler.sendMessage(msg);
            }else {*/
                try {
                    String sql = "select " +Constant.USER_CON_SQL+
                            " from "+Constant.TABLE_NAME_USER+
                            " where userNo='" + userN + "' and userState = '1'";
                    Statement stmt = con1.createStatement();        //创建Statement
                    ResultSet resultSet = stmt.executeQuery(sql);          //ResultSet类似Cursor

                    if (resultSet.next()) {
                        if (resultSet.getString("userPWD").equals(pwd)) {
                            Message msg = new Message();
                            msg.obj = "";
                            handler.sendMessage(msg);

                            if (resultSet.getString("creatorID").equals("1")) {
                                Message msgs = new Message();
                                msgs.obj = "系统管理员没有登录盘点系统的权限";
                                handler.sendMessage(msgs);
                            } else {
                                //传递登陆者的信息
                                String userUUID = resultSet.getString("userUUID");
                                String name = resultSet.getString("userName");
                                deptUUID = resultSet.getString("deptUUID");
                                sysUUID = resultSet.getString("sysUUID");
                                String userState = resultSet.getString("userState");

                                //判断所属组织是否被删除
                                String deptSql = "select * from " + Constant.TABLE_NAME_DEPT + " where deptUUID='" + deptUUID + "'";
                                Log.i("deptSql", deptSql);
                                final ResultSet deptResultSet = stmt.executeQuery(deptSql);
                                if (deptResultSet.next()) {
                                    String deptUUID = deptResultSet.getString("deptUUID");
                                    Log.i("deptUUID",deptUUID);
                                    //判断管理员是否被禁用
                                    String systemSql = "select * from " + Constant.TABLE_NAME_SYSTEM + " where sysUUID='" + sysUUID + "'";
                                    ResultSet systemResultSet = stmt.executeQuery(systemSql);
                                    String creatorID = "";
                                    if (systemResultSet.next()) {
                                        creatorID = systemResultSet.getString("creatorID");
                                    }
                                    systemResultSet.close();

                                    String userSql = "select * from " + Constant.TABLE_NAME_USER + " where sysUUID='" + sysUUID + "' AND creatorID='" + creatorID + "'";
                                    final ResultSet userResultSet = stmt.executeQuery(userSql);
                                    int num = 0;
                                    if (userResultSet.next()) {
                                        if (userResultSet.getString("userState").equals("0")) {
                                            num++;
                                        }
                                        String useUUID = userResultSet.getString("creatorID");
                                        String uuidSql = "select * from " + Constant.TABLE_NAME_USER + " where userUUID='" + useUUID + "'";
                                        ResultSet uuidResultSet = stmt.executeQuery(uuidSql);
                                        if (uuidResultSet.next()) {
                                            if (uuidResultSet.getString("userState").equals("0")) {
                                                num++;
                                            }
                                        }
                                        uuidResultSet.close();
                                    }
                                    userResultSet.close();
                                    if (num > 0) {
                                        Message msgd = new Message();
                                        msgd.obj = "您已被暂停使用，具体情况请联系管理员！";
                                        handler.sendMessage(msgd);
                                    } else if (userState.equals("0")) {
                                        Message msgx = new Message();
                                        msgx.obj = "您已被暂停使用，具体情况请联系管理员！";
                                        handler.sendMessage(msgx);
                                    }else{
                                        Bundle bundle = new Bundle();
                                        bundle.putString("user", userUUID);
                                        bundle.putString("name", name);
                                        bundle.putString("sysUUID", sysUUID);

                                        //正确--跳转界面
                                        Intent loginIntent = new Intent();
                                        loginIntent.putExtras(bundle);
                                        loginIntent.setClass(LoginActivity.this, MainActivity.class);
                                        startActivity(loginIntent);
                                    }
                                } else {
                                    Message msgs = new Message();
                                    msgs.obj = "您所在的组织已被注销，请联系管理员！";
                                    handler.sendMessage(msgs);
                                }
                                deptResultSet.close();
                                //insertData(con1);
                            }
                        } else {
                            Message msg = new Message();
                            msg.obj = "密码错误";
                            handler.sendMessage(msg);
                        }
                    }else{
                        Message msg = new Message();
                        msg.obj = "用户名不存在";
                        handler.sendMessage(msg);
                    }
                    dialog.dismiss();
                    resultSet.close();
                    stmt.close();
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
            /*}*/
        }
    };

    private void insertData(Connection con1) throws java.sql.SQLException{
        Statement stmt = con1.createStatement();        //创建Statement
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
        /*用户表*/
        String sql_user = "select "+Constant.USER_CON_SQL+" from "+Constant.TABLE_NAME_USER+" where userState = '1' and sysUUID='"+sysUUID+"'";
        ResultSet resultSet = stmt.executeQuery(sql_user);          //ResultSet类似Cursor
        if (resultSet != null && resultSet.first()) {
            while (!resultSet.isAfterLast()) {
                ContentValues values = new ContentValues();
                values.put("userUUID",resultSet.getString("userUUID"));
                values.put("deptUUID",resultSet.getString("deptUUID"));
                values.put("userName",resultSet.getString("userName"));
                values.put("sysUUID",resultSet.getString("sysUUID"));
                db.insert(Constant.TABLE_NAME_USER,null,values);
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
        /*登录者所在公司的部门*/
        String useDept = "";
        String user_com_sql = "select deptUUID from "+Constant.TABLE_NAME_DEPT + " where deptCode like '"+deptCode+"%' AND sysUUID = '"+sysUUID+"'";
        ResultSet rs = stmt.executeQuery(user_com_sql);
        while(rs.next()){
            useDept += "'"+rs.getString("deptUUID")+"',";
        }

        /*查询和部门相关的资产的集合*/
        String barCodes = "";
        String sql_stoAbout = "select barCode from "+Constant.TABLE_NAME_STORAGE+
                " where useDept in ("+useDept.substring(0,useDept.length()-1)+") and isDel=true and sysUUID = '"+sysUUID+"'";
        ResultSet stoAboutRS = stmt.executeQuery(sql_stoAbout);
        while (stoAboutRS.next()){
            barCodes += "'"+stoAboutRS.getString("barCode")+"',";
        }



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
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                AlertDialog.Builder build = new AlertDialog.Builder(this);
                build.setTitle("注意").setMessage("确定要退出吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("取消", null).show();
                break;
            default:
                break;
        }
        return false;
    }
}
