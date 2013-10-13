package jp.ne.nissing.util.householdbudget.forms;

import java.util.*;

import jp.ne.nissing.util.householdbudget.*;
import jp.ne.nissing.util.householdbudget.data.HouseholdData;
import jp.ne.nissing.util.householdbudget.data.category.*;
import jp.ne.nissing.util.householdbudget.data.paymentmethod.PaymentMethod;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class CategorySettingForm extends Activity implements OnClickListener{
	private static final int MENU_ID_MENU1 = (Menu.FIRST + 1);
	private static final int MENU_ID_MENU2 = (Menu.FIRST + 2);
	private static final int MENU_ID_MENU3 = (Menu.FIRST + 3);
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.categorysetting);
		
		Spinner paySpinner = (Spinner)findViewById(R.id.categorysetting_paymethodspinner);
		paySpinner.removeAllViewsInLayout();
		
		ArrayAdapter<String> payAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		payAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		for(PaymentMethod pay : HouseholdData.getInstance().getPaymentMethod()){
			payAdapter.add(pay.getPaymentMethodName());
		}
		paySpinner.setAdapter(payAdapter);
		
		Button setCategoryButton = (Button)findViewById(R.id.categorysetting_okbutton);
		setCategoryButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		EditText category = (EditText)findViewById(R.id.categorysetting_nametext);
		String categoryName = category.getText().toString();
		
		// カテゴリが空欄なら警告を出してその後無視
		if(categoryName.equals("")){
			showToastMessage("カテゴリ名を入力してください");
			return;
		}
		
		// 指定したカテゴリがすでに存在するならば、警告を出してその後無視
		if(DBManager.getInstance().categoryExists(categoryName)){
			showToastMessage("そのカテゴリは既に存在します");
			return;
		}
		
		Spinner paySpinner = (Spinner)findViewById(R.id.categorysetting_paymethodspinner);
		int payId = DBManager.getInstance().readPaymethodId(paySpinner.getSelectedItem().toString());
		
		DBManager.getInstance().addCategory(categoryName, payId);
		
		showToastMessage("カテゴリ：" + categoryName + "を設定しました");

		category.setText("");
		paySpinner.setSelection(0);
		
		DBManager.getInstance().buildHouseholdData();
	}
	
	private void showToastMessage(String message) {
		Toast.makeText(CategorySettingForm.this, message, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * メニューボタンが押された時
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_ID_MENU1, Menu.NONE, getText(R.string.change_relation_category_paymethod)).setIcon(android.R.drawable.ic_menu_edit);
		menu.add(Menu.NONE, MENU_ID_MENU2, Menu.NONE, getText(R.string.delete)).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		menu.add(Menu.NONE, MENU_ID_MENU3, Menu.NONE, getText(R.string.sort_item)).setIcon(android.R.drawable.ic_menu_sort_alphabetically);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * メニューが選択された時
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret = false;

		switch(item.getItemId()) {
		case MENU_ID_MENU1:
			//!< 画面遷移
			Intent intent = new Intent();
			intent = new Intent(CategorySettingForm.this, CategoryRelationForm.class);
			startActivity(intent);
			break;
		
		case MENU_ID_MENU2:
			//!< カテゴリ一覧を取得
			final String[] itemList = createCategoryList();
			
			//!< 削除選択ダイアログを表示する
			new AlertDialog.Builder(CategorySettingForm.this)
			.setTitle("削除する項目を選択してください")
			.setItems(itemList, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					final int index = which;
					AlertDialog.Builder builder = new AlertDialog.Builder(CategorySettingForm.this);
					builder.setTitle("確認");
					builder.setMessage("本当に削除しますか？");
					builder.setPositiveButton(getText(R.string.yes), 
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									//!< データベースから選択したカテゴリを削除する
									DBManager.getInstance().removeCategory(itemList[index]);
									//!< フォーム上のデータ更新
									DBManager.getInstance().buildHouseholdData();
								}
							});
					builder.setNegativeButton(getText(R.string.no), 
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
								}
							});
					AlertDialog alertDialog = builder.create();
					alertDialog.show();
				}
			})
			.show();
			break;
		case MENU_ID_MENU3:
			//!< 並び替えフォームを表示する
			break;
		}
		
		return ret;
	}
	
	/**
	 * カテゴリの一覧を作成
	 */
	private String[] createCategoryList(){
		//!< DBからカテゴリ一覧を取得
		List<Category> list = DBManager.getInstance().readCategory();
		String[] itemList = new String[list.size()];

		//!< カテゴリを文字列配列に変換
		for(int i = 0; i < list.size(); i++) {
			itemList[i] = list.get(i).getCategoryName();
		}
		
		return itemList;
	}
}
