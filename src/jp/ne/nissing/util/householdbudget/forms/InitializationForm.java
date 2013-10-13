package jp.ne.nissing.util.householdbudget.forms;

import jp.ne.nissing.util.householdbudget.*;
import jp.ne.nissing.util.householdbudget.data.HouseholdData;
import jp.ne.nissing.util.householdbudget.service.GoogleGateWay;
import android.app.*;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.*;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class InitializationForm extends Activity implements OnClickListener {
	EditText account;
	EditText pass;
	EditText spreadSheet;

	private ProgressDialog _progressDialog = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.initializationform);

		account = (EditText) findViewById(R.id.initmailaddresstext);
		pass = (EditText) findViewById(R.id.initpasswordtext);
		spreadSheet = (EditText) findViewById(R.id.initspreadsheetnametext);

		Button createAccountButton = (Button) findViewById(R.id.sendprofilebutton);
		createAccountButton.setOnClickListener(this);
	}

	/**
	 * 現在のフォームを閉じる
	 */
	public void finishInitialization() {
		finish();
	}

	/**
	 * ProgressDialogのインスタンスを取得する
	 * @return ProgressDialog
	 */
	private ProgressDialog getProgressDialog() {
		if (_progressDialog == null) {
			_progressDialog = new ProgressDialog(InitializationForm.this);
			_progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			_progressDialog.setCancelable(false);
		}
		return _progressDialog;
	}

	@Override
	public void onClick(View v) {
		if (account.getText().toString().equals("")
				|| pass.getText().toString().equals("")
				|| spreadSheet.getText().toString().equals("")) {
			ShowToastMessage("入力されていない欄が存在します");
			return;
		}

		//!< 指定したスプレッドシートが存在しなければ、新規作成。存在すれば関連付けのみ
		if(!GoogleGateWay.getInstace().existSpreadSheet(spreadSheet.getText().toString())) {
			_progressDialog = null;
			getProgressDialog().setMessage("初期化中...");
			getProgressDialog().show();

			//!< データベース更新
			HouseholdData.getInstance().setProfile(account.getText().toString(),
					pass.getText().toString(), spreadSheet.getText().toString());
			DBManager.getInstance().writeProfile(account.getText().toString(),
					pass.getText().toString(), spreadSheet.getText().toString());
			DBManager.getInstance().buildHouseholdData();
	
			//!< サーバ上にファイルを作成
			GoogleGateWay.getInstace().clear();
			if (GoogleGateWay.getInstace().cleanupProfile() == false) {
				getProgressDialog().dismiss();
				ShowToastMessage("初期化に失敗しました.\nメールアドレスかパスワードが間違っています.");
				return;
			}
			if (GoogleGateWay.getInstace().cleanupSpreadSheet() == false) {
				getProgressDialog().dismiss();
				ShowToastMessage("初期化に失敗しました.\n同名のドキュメントが存在します．削除するか名前を変更してください.");
				return;
			}
			if (GoogleGateWay.getInstace().activate()) {
				// 初期設定画面を閉じる
				getProgressDialog().dismiss();
				ShowToastMessage("初期化に成功しました.");
				finishInitialization();
			} else {
				getProgressDialog().dismiss();
				ShowToastMessage("初期化に失敗しました.");
			}
	
			// (new Thread(initializeRunnable)).start();
		} else {
			AlertDialog.Builder bDialog = new AlertDialog.Builder(InitializationForm.this);
			bDialog.setTitle(getText(R.string.existing_sheet));
			bDialog.setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					//!< データベース更新
					HouseholdData.getInstance().setProfile(account.getText().toString(),
							pass.getText().toString(), spreadSheet.getText().toString());
					DBManager.getInstance().writeProfile(account.getText().toString(),
							pass.getText().toString(), spreadSheet.getText().toString());
					DBManager.getInstance().buildHouseholdData();
					
					ShowToastMessage("初期設定が完了しました.");
					finishInitialization();
				}
			});
			bDialog.setNegativeButton(getText(R.string.cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) { }
			});
		}
	}

	private Runnable initializeRunnable = new Runnable() {

		@Override
		public void run() {
			GoogleGateWay.getInstace().clear();
			if (GoogleGateWay.getInstace().cleanupProfile() == false) {
				getProgressDialog().dismiss();
				ShowToastMessage("初期化に失敗しました.\nメールアドレスかパスワードが間違っています.");
				return;
			}
			if (GoogleGateWay.getInstace().cleanupSpreadSheet() == false) {
				getProgressDialog().dismiss();
				ShowToastMessage("初期化に失敗しました.\n同名のドキュメントが存在します．削除するか名前を変更してください.");
				return;
			}
			if (GoogleGateWay.getInstace().activate()) {
				// 初期設定画面を閉じる
				getProgressDialog().dismiss();
				ShowToastMessage("初期化に成功しました.");
				finishInitialization();
			} else {
				getProgressDialog().dismiss();
				ShowToastMessage("初期化に失敗しました.");
			}
		}
	};

	private void ShowToastMessage(String message) {
		// Looper.prepare();
		Toast.makeText(InitializationForm.this, message, Toast.LENGTH_SHORT)
				.show();
		// Looper.loop();
	}

}
