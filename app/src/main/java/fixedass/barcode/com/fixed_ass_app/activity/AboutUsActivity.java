package fixedass.barcode.com.fixed_ass_app.activity;

import android.app.Activity;
import android.app.admin.SystemUpdateInfo;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import fixedass.barcode.com.fixed_ass_app.tool.ExitApplication;
import fixedass.barcode.com.fixed_ass_app.R;

/**
 * Created by Administrator on 2018/3/2.
 * 关于我们
 */

public class AboutUsActivity extends Activity {
    private String userUUID, name, sysUUID;
    /*private EditText edt_url;*/
    private TextView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutus);

        ExitApplication.getInstance().addActivity(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userUUID = bundle.getString("user");
        name = bundle.getString("name");
        sysUUID = bundle.getString("sysUUID");

        //设置文本信息不可编辑
        /*edt_url = (EditText)findViewById(R.id.edt_url);
        edt_url.setOnFocusChangeListener(focus_listener_noIM);
        edt_url.setOnTouchListener(touch_listener_noIM);*/

        back = (TextView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirect(MainActivity.class);
            }
        });
    }

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
            if(event.getAction()==MotionEvent.ACTION_DOWN) {
                hideIM(v);
            }
            return false;    // dispatch the event further!
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
        intent.setClass(AboutUsActivity.this, cls);
        startActivity(intent);//跳转
    }
}
