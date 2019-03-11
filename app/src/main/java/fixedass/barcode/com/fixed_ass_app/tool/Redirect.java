package fixedass.barcode.com.fixed_ass_app.tool;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Administrator on 2018/4/24.
 * 跳转--退出
 */

public class Redirect {
    /*跳转*/
    public static void redirect(Context context, Class<?> cls,Bundle bundle) {
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(context, cls);
        context.startActivity(intent);//跳转
    }

    /*退出*/
    public static void quit(final Context context){
        AlertDialog.Builder build = new AlertDialog.Builder(context);
        build.setTitle("注意").setMessage("确定要退出吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ExitApplication.getInstance().exit(context);
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    /*隐藏键盘*/
    public static void hideIM(View edt,Context context){
        // try to hide input_method:
        try {
            InputMethodManager im = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            IBinder windowToken = edt.getWindowToken();
            if(windowToken != null) {
                // always de-activate IM
                im.hideSoftInputFromWindow(windowToken, 0);
            }
        } catch (Exception e) {
            Log.e("HideInputMethod", "failed:"+e.getMessage());
        }
    }

    /*检验是否联网*/
    public static boolean checkNetwork(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }
}
