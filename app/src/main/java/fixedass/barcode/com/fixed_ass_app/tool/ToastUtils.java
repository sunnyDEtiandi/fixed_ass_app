package fixedass.barcode.com.fixed_ass_app.tool;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/3/22.
 */

public class ToastUtils {
    private static Context context = null;
    private static Toast toast = null;

    public static void showToast(Context context,String text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}
