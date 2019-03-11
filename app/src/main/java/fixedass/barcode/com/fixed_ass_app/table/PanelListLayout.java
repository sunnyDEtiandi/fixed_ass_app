package fixedass.barcode.com.fixed_ass_app.table;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2018/3/19.
 */

public class PanelListLayout extends RelativeLayout {

    private AbstractPanelListAdapter adapter;

    public AbstractPanelListAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(AbstractPanelListAdapter adapter) {
        this.adapter = adapter;
        adapter.initAdapter();
    }

    public PanelListLayout(Context context) {
        super(context);
    }

    public PanelListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PanelListLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
