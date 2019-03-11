package fixedass.barcode.com.fixed_ass_app.tool;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/2.
 * 关闭所有的activity
 */

public class ExitApplication extends Application {
    private List<Activity> list = new ArrayList<Activity>();
    private static ExitApplication ea;

    private ExitApplication() {  }

    public static ExitApplication getInstance() {
        if (null == ea) {
            ea = new ExitApplication();
        }
        return ea;
    }

    public void addActivity(Activity activity) {
        list.add(activity);
    }

    public void exit(Context context) {
        for (Activity activity : list) {
            activity.finish();
        }
        System.exit(0);
    }

    public Activity getPreActivity(){
        return list.get(list.size()-2);
    }
}
