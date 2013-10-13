package jp.ne.nissing.util.householdbudget.forms;

import java.util.*;

import jp.ne.nissing.util.householdbudget.DBManager;
import jp.ne.nissing.util.householdbudget.R;
import jp.ne.nissing.util.householdbudget.data.HouseholdData;
import jp.ne.nissing.util.householdbudget.data.Profile;
import jp.ne.nissing.util.householdbudget.service.GoogleGateWay;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.*;

public class AccountSettingForm extends ListActivity {
	private ArrayList _items;
	private ProfileListAdapter _adapter;
	private ProgressDialog _progressDialog = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accountsetting);
		
		// Item オブジェクトを保持するためのリストを生成し、アダプタに追加する
		_items = new ArrayList();
		_adapter = new ProfileListAdapter(this, _items);

		Item account = new Item();
		account.setItem("アカウント");
		account.setValue(HouseholdData.getInstance().getProfile().getAccountName());
		
		Item password = new Item();
		password.setItem("パスワード");
		password.setValue("Your Google Password");
		
		Item spreadSheetName = new Item();
		spreadSheetName.setItem("スプレッドシート名");
		spreadSheetName.setValue(HouseholdData.getInstance().getProfile().getSheetName());
		
		setListAdapter(_adapter);

		_adapter.add(account);
		_adapter.add(password);
		_adapter.add(spreadSheetName);
	}
	
	/**
	 * リストがクリックされた時の処理
	 * とりあえず、処理されないようにしている。
	 */
	@Override
	protected void onListItemClick(ListView listView, View v, int position, long id){
		boolean b = true;
		if(b) return;
		
		super.onListItemClick(listView, v, position, id);
		
		final EditText edit =new EditText(this); 
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("新しいデータを入力してください");
		dialog.setView(edit);
		
		int i = (int) id;
		switch(i){
		case 0:
			edit.setText(HouseholdData.getInstance().getProfile().getAccountName());
			dialog.setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//!< リスト更新
					_adapter.getItem(0).setValue(edit.getText().toString());
					setListAdapter(_adapter);
					//!< データベース更新
					DBManager.getInstance().writeAccount(edit.getText().toString());
					DBManager.getInstance().buildHouseholdData();
				}
			});
			break;

		case 1:
			edit.setText(HouseholdData.getInstance().getProfile().getPassword());
			edit.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
			dialog.setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which) {
					DBManager.getInstance().writePassword(edit.getText().toString());
					DBManager.getInstance().buildHouseholdData();
				}
			});
			break;
		case 2:
			edit.setText(HouseholdData.getInstance().getProfile().getSheetName());
			dialog.setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which) {
					final String newSheetName = edit.getText().toString();
					//!< サーバ上にシートが存在しない場合は、リスト上の情報を更新し、サーバ上にシートを新規作成
					if(!GoogleGateWay.getInstace().existSpreadSheet(newSheetName)) {
						AlertDialog.Builder bDialog = new AlertDialog.Builder(AccountSettingForm.this);
						bDialog.setTitle("新規シートを作成しますか？");
						bDialog.setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								_progressDialog = null;
								getProgressDialog().setMessage("初期化中...");
								getProgressDialog().show();
								
								//!< アカウント設定リストのスプレッドシート名を更新
								_adapter.getItem(2).setValue(newSheetName);
								setListAdapter(_adapter);
								
								//!< データベース更新
								DBManager.getInstance().writeSpreadSheetName(newSheetName);
								DBManager.getInstance().buildHouseholdData();

								//!< スプレッドシートをサーバ上に新規作成する
								GoogleGateWay.getInstace().cleanupSpreadSheet();
								if(GoogleGateWay.getInstace().activate()) {
									getProgressDialog().dismiss();
									ShowToastMessage("初期化に成功しました.");
								} else {
									getProgressDialog().dismiss();
									ShowToastMessage("初期化に失敗しました.");
								}
							}
						});
						bDialog.setNegativeButton(getText(R.string.cancel), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) { }
						});
						bDialog.show();
					} else {
						//!< アカウント設定リストのスプレッドシート名を更新
						_adapter.getItem(2).setValue(newSheetName);
						setListAdapter(_adapter);
						//!< データベース更新
						DBManager.getInstance().writeSpreadSheetName(newSheetName);
						DBManager.getInstance().buildHouseholdData();
					}
				}
			});
			break;
		}
		
		dialog.show();
	}
	
	/**
	 * ProgressDialogのインスタンスを取得
	 */
	private ProgressDialog getProgressDialog() {
		if (_progressDialog == null) {
			_progressDialog = new ProgressDialog(AccountSettingForm.this);
			_progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			_progressDialog.setCancelable(false);
		}
		return _progressDialog;
	}
	
	/**
	 * ToastMessageを表示する
	 * @param message
	 */
	private void ShowToastMessage(String message) {
		Toast.makeText(AccountSettingForm.this, message, Toast.LENGTH_SHORT).show();
	}
}
