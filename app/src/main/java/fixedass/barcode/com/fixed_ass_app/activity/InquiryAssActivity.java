package fixedass.barcode.com.fixed_ass_app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import fixedass.barcode.com.fixed_ass_app.tool.ToastUtils;
import fixedass.barcode.com.fixed_ass_app.util.Constant;
import fixedass.barcode.com.fixed_ass_app.tool.ExitApplication;
import fixedass.barcode.com.fixed_ass_app.R;
import fixedass.barcode.com.fixed_ass_app.util.MySqliteHelper;

/**
 * Created by Administrator on 2018/3/5.
 * 资产查询
 */

public class InquiryAssActivity extends Activity {
    private String userUUID, name, sysUUID;
    private EditText barCode;
    private Button selectCon;
    private MySqliteHelper helper;
    private EditText assName;
    private EditText className;
    private EditText assType;
    private EditText assPrice;
    private EditText bookDate;
    private EditText useCompany;
    private EditText useDept;
    private EditText usePeople;
    private EditText storeAddress;
    private TextView back;
    private RelativeLayout relative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inquiryass);

        ExitApplication.getInstance().addActivity(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userUUID = bundle.getString("user");
        name = bundle.getString("name");
        sysUUID = bundle.getString("sysUUID");

        helper = new MySqliteHelper(this);

        assName = (EditText)findViewById(R.id.assName);
        className = (EditText)findViewById(R.id.className);
        assType = (EditText)findViewById(R.id.assType);
        assPrice = (EditText)findViewById(R.id.assPrice);
        bookDate = (EditText)findViewById(R.id.bookDate);
        useCompany = (EditText)findViewById(R.id.useCompany);
        useDept = (EditText)findViewById(R.id.useDept);
        usePeople = (EditText)findViewById(R.id.usePeople);
        storeAddress = (EditText)findViewById(R.id.storeAddress);

        barCode = (EditText)findViewById(R.id.barCode);
        barCode.setOnKeyListener(onKey);
        selectCon = (Button)findViewById(R.id.select);
        selectCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(relative.getWindowToken(), 0);
                barCode.setText(barCode.getText().toString());// 添加这句后实现效果
                barCode.selectAll();
                selectByBarCode();
            }
        });

        back = (TextView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirect(MainActivity.class);
            }
        });

        relative = (RelativeLayout)findViewById(R.id.relative);
    }

    public void selectByBarCode(){
        String barCodeValue = barCode.getText().toString().toUpperCase();
        if (barCodeValue == "" || barCodeValue.equals("")){
            assName.setText("");
            className.setText("");
            assType.setText("");
            assPrice.setText("");
            bookDate.setText("");
            useCompany.setText("");
            useDept.setText("");
            usePeople.setText("");
            storeAddress.setText("");
            ToastUtils.showToast(InquiryAssActivity.this,"请输入或扫描要查询的资产编码");
        }else{
            String sql = "select "+Constant.STO_CON_SEL+" from "+
                    Constant.TABLE_NAME_STORAGE + " where barCode='"+barCodeValue+"'";
            SQLiteDatabase db = helper.getWritableDatabase();
            String [] fileds = new String[]{
                    "barCode","assName","className","assType","assPrice","bookDate","useCompany","useDept","usePeople","storeAddress"
            };

            List<String> list = MySqliteHelper.query(InquiryAssActivity.this,sql,fileds,db);
            if (list.size()>0){
                assName.setText(list.get(1));
                className.setText(list.get(2));
                assType.setText(list.get(3));
                assPrice.setText(list.get(4));
                bookDate.setText(list.get(5));
                useCompany.setText(list.get(6));
                useDept.setText(list.get(7));
                usePeople.setText(list.get(8));
                storeAddress.setText(list.get(9));
            }else {
                assName.setText("");
                className.setText("");
                assType.setText("");
                assPrice.setText("");
                bookDate.setText("");
                useCompany.setText("");
                useDept.setText("");
                usePeople.setText("");
                storeAddress.setText("");
                ToastUtils.showToast(InquiryAssActivity.this,"不存在该编码的资产");
            }
        }
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
            }
            return false;
        }
    };

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
        intent.setClass(InquiryAssActivity.this, cls);
        startActivity(intent);//跳转
    }
}
