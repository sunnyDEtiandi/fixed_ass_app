package fixedass.barcode.com.fixed_ass_app.tool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import fixedass.barcode.com.fixed_ass_app.R;
import fixedass.barcode.com.fixed_ass_app.bean.CountState;

/**
 * Created by Administrator on 2018/3/21.
 */

public class CountStateAdapter extends BaseAdapter {
    private List<CountState> mList;
    private Context mContext;

    public CountStateAdapter(List<CountState> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 下面是重要代码
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater _LayoutInflater=LayoutInflater.from(mContext);
        convertView=_LayoutInflater.inflate(R.layout.item_custom, null);
        if(convertView!=null) {
            TextView _TextView1=(TextView)convertView.findViewById(R.id.textView1);
            TextView _TextView2=(TextView)convertView.findViewById(R.id.textView2);
            _TextView1.setText(mList.get(position).getState());
            _TextView2.setText(""+mList.get(position).getCount());
        }
        return convertView;
    }

}
