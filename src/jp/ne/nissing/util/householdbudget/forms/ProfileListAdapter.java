package jp.ne.nissing.util.householdbudget.forms;

import java.util.List;

import jp.ne.nissing.util.householdbudget.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ProfileListAdapter extends ArrayAdapter<Item>
{
	private LayoutInflater _inflater;
	private TextView _title;
	private TextView _value;
	
	/**
	 * コンストラクタ
	 * @param context
	 * @param objects
	 */
	public ProfileListAdapter(Context context, List<Item> objects){
		super(context, 0, objects);
		_inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	/**
	 * １行ごとのビューを生成する
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View view = convertView;
		
		if(convertView == null){
			view = _inflater.inflate(R.layout.listrow, null);
		}
		
		// 現在参照しているリストの位置からItemを取得する
		Item item = this.getItem(position);
		if(item != null){
			// Itemから必要なデータを取り出し、それぞれTextViewにセット
			String title = item.getTitle();
			_title = (TextView)view.findViewById(R.id.rowitem);
			_title.setText(title);
			String value = item.getValue();
			_value = (TextView)view.findViewById(R.id.rowvalue);
			_value.setText(value);
		}
		
		return view;
	}
}
