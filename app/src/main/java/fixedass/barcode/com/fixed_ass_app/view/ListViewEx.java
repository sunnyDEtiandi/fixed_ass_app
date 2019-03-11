package fixedass.barcode.com.fixed_ass_app.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * 
 * @author Administrator 调用方法: 1.ListViewEx(..) 2.initContext(..) 3.inital(,,) 4
 */

public class ListViewEx extends ListView {

	public interface IItemClickEvent {
		public void onItemClick(int position);
	}

	public interface IItemChangeEvent {
		public void onItemChange(int position);
	}

	// private Context mContext;
	private List<Map<String, Object>> mDataSource;
	private BaseAdapter mAdapter;
	public int iListSelectedIndex = -1;
	public IItemClickEvent itemClick;
 
	public IItemChangeEvent itemChange;
	private int itemCount = 20; // 显示记录数

	public ListViewEx(Context context) {
		super(context);
		init();
	}

	/**
	 * 设置自定义事件
	 * 
	 * @param itemclick
	 */
	public void SetOnIItemClick(IItemClickEvent itemclick) {
		this.itemClick = itemclick;
	}
	
 

	// This example uses this method since being built from XML
	public ListViewEx(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	// Build from XML layout
	public ListViewEx(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		// setFocusableInTouchMode(true);
		//
		// this.setSelector()
	}

	public void inital(int itemLayoutResId, String[] arrayColName,
			int[] arrayColResId) {
		if (mDataSource == null) {
			mDataSource = new ArrayList<Map<String, Object>>();
		}

		mAdapter = new SimpleAdapter(this.getContext(), mDataSource,
				itemLayoutResId, arrayColName, arrayColResId);
		this.setAdapter(mAdapter);
		setFocusableInTouchMode(true);
		this.setCacheColorHint(0);
		// cacheColorHint

		this.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				iListSelectedIndex = position;

				if (itemClick != null) {
					itemClick.onItemClick(position);
				}
			}
		});

		this.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				 
				iListSelectedIndex = position;
				if (itemChange != null) {
					itemChange.onItemChange(position);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				 

			}
		});
	}

	/**
	 * 插入到最前面
	 * 
	 * @param value
	 */
	public void add(Map<String, Object> value, String barcode, String barcodeKey) {
		// 是否存在
		// if (isExists(barcode, barcodeKey) < 0) {
		if (mDataSource.size() >= itemCount) {
			mDataSource.remove(mDataSource.size() - 1);
		}
		mDataSource.add(0, value);
		notifyDataSetChanged();
		// }
	}

	/**
	 * 插入到最前面
	 * 
	 * @param value
	 */
	public void add(Map<String, Object> value, String[] args, String[] columns) {
		// 是否存在
		if (isExists(args, columns) < 0) {
			if (mDataSource.size() >= itemCount) {
				mDataSource.remove(mDataSource.size() - 1);
			}
			mDataSource.add(0, value);
			notifyDataSetChanged();
		}
	}

	/**
	 * 插入到最前面
	 * 
	 * @param value
	 */
	public void add(Map<String, Object> value) {

		if (mDataSource.size() >= itemCount) {
			mDataSource.remove(mDataSource.size() - 1);
		}
		mDataSource.add(0, value);
		notifyDataSetChanged();
	}

	/**
	 * 插入到最后
	 * 
	 * @param map
	 */
	public void addAtLast(Map<String, Object> map) {
		mDataSource.add(map);
		notifyDataSetChanged();
	}

	public void add(List<Map<String, Object>> list) {
		mDataSource.clear();
		mDataSource.addAll(list);
		notifyDataSetChanged();
	}

	public void clear() {
		if (this.mDataSource != null) {
			this.mDataSource.clear();
			notifyDataSetChanged();
		}
	}

	public void delete(Object paramObject) {
		if (this.mDataSource.remove(paramObject)) {
			notifyDataSetChanged();
		}
	}

	public void delete(String barcode, String mapKey) {
		int index = isExists(barcode, mapKey);
		if (index > -1) {
			mDataSource.remove(index);
			notifyDataSetChanged();
		}
	}

	public void delete(int index) {
		if (index > -1) {
			mDataSource.remove(index);
			notifyDataSetChanged();
		}
	}

	/**
	 * 用于删除成功后执行的回调方法 并返回删除成功的列名及列值
	 * 
	 * @author Administrator
	 * 
	 */
	public interface IListenDelSelRowSuc {
		public void delSuc(String columnName, String value);
	}

	public IListenDelSelRowSuc deleteSelectedRowListener;

	public void SetDelSelectedRowListener(IListenDelSelRowSuc listener) {
		this.deleteSelectedRowListener = listener;
	}


	public interface IListenDelData {
		public int deleteDataInDatabase(String barcode);
	}

	public IListenDelData delDataListener;

	public void setIListenDelData(IListenDelData listener) {
		this.delDataListener = listener;
	}

	public void removeIListenDelData() {
		this.delDataListener = null;
	}

	public void notifyDataSetChanged() {
		if (this.mAdapter != null) {
			this.mAdapter.notifyDataSetChanged();
			iListSelectedIndex = -1;
			if (mDataSource.size() > 0) {
				this.requestFocusFromTouch();
				this.setSelection(0);
				iListSelectedIndex = 0;
			}
		}

	}

	/**
	 * 获取当前ListView的大小
	 * 
	 * @return
	 */
	public int getSize() {
		if (mDataSource == null) {
			return 0;
		}
		return mDataSource.size();
	}

	/**
	 * 获取当前List第location项的值的集合
	 * 
	 * @param location
	 * @return
	 */
	public Map<String, Object> GetValue(int location) {
		if (mDataSource.size() == 0 || location > mDataSource.size() - 1) {
			return null;
		}
		return mDataSource.get(location); // 它和getItemAtPosition的区别是？
	}

	/**
	 * 获取当前选择的项
	 * 
	 * @return
	 */
	public Map<String, Object> GetSelValue() {
		if (iListSelectedIndex != -1) {
			return mDataSource.get(iListSelectedIndex);
		}
		return null;
	}

	/**
	 * 根据列名来判断某项值所在的索引 如未找到则返回-1
	 * 
	 * @param value
	 * @param columnName
	 * @return
	 */
	public int isExists(String value, String columnName) {
		int index = -1;
		for (int i = 0; i < mDataSource.size(); i++) {
			Map<String, Object> map = GetValue(i);
			if (value.equals(map.get(columnName))) {
				index = i;
				break;
			}
		}

		return index;
	}

	/**
	 * 是否存在
	 * 
	 * @param value
	 * @param columnName
	 * @return
	 */
	public int isExists(String[] value, String[] columnName) {
		int index = -1;
		for (int i = 0; i < mDataSource.size(); i++) {
			Map<String, Object> map = GetValue(i);
			if (value[0].equals(map.get(columnName[0]))
					&& value[1].equals(map.get(columnName[1]))
					&& value[2].equals(map.get(columnName[2]))) {
				index = i;
				break;
			}
		}
		return index;
	}

}
