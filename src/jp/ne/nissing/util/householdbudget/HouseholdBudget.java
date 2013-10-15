package jp.ne.nissing.util.householdbudget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import jp.ne.nissing.util.householdbudget.data.HouseholdData;
import jp.ne.nissing.util.householdbudget.data.Profile;
import jp.ne.nissing.util.householdbudget.data.cashstorage.CashStorage;
import jp.ne.nissing.util.householdbudget.data.category.Category;
import jp.ne.nissing.util.householdbudget.data.paymentmethod.PaymentMethod;
import jp.ne.nissing.util.householdbudget.forms.InitializationForm;
import jp.ne.nissing.util.householdbudget.forms.MenuForm;
import jp.ne.nissing.util.householdbudget.service.GoogleGateWay;
import jp.ne.nissing.util.householdbudget.service.element.RowElement;
import jp.ne.nissing.util.householdbudget.service.element.RowElementFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HouseholdBudget extends Activity implements OnClickListener {

    private static final int MENU_ID_MENU1 = (Menu.FIRST + 1);
    private ProgressDialog _progressDialog = null;

    SimpleDateFormat sdfforButton = new SimpleDateFormat("yyyy年MM月dd日");
    SimpleDateFormat sdfforSend = new SimpleDateFormat("yyyy/MM/dd");

    CheckBox isStaffCanteenCheckBox;

    // オンラインモードかどうか
    private static boolean isOnlineMode = false;

    private Context mContext = null;

    // コンポーネント
    private Button expenditureSendButton;
    private Button expenditureDateButton;
    private EditText expenditureAmountText;
    private EditText expenditureMemoText;
    private Spinner categorySpinner;
    private Spinner howToPaySpinner;

    private Button incomeSendButton;
    private Button incomeDateButton;
    private EditText incomeMemoText;
    private EditText incomeAmountText;
    private Spinner receiveSpinner;

    private Button moveDateButton;
    private Button moveSendButton;
    private EditText moveAmountText;
    private Spinner moveFromSpinner;
    private Spinner moveToSpinner;
    private EditText moveMemoText;

    // 直接入力カテゴリ用のID
    private final int DIRECT_INPUT_CATEGORY_ID = -1;
    private final int INSTANCE_CATEGORY = -2;
    private ArrayAdapter<Category> categoryAdaper;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = this;
        DBManager.getInstance().initialize(this, "householdbudget");

        // ここで，データベース内にアカウントが登録されていなかったら，初期設定画面を開く
        if (!isAccountEnabled()) {
            showInitializeForm();
        }

        // タブ設定
        TabHost tabs = (TabHost) findViewById(R.id.tabhost);
        tabs.setup();

        TabSpec tab01 = tabs.newTabSpec(getText(R.string.tab_id_expenditure).toString());
        tab01.setIndicator(getText(R.string.expenditure),
                getResources().getDrawable(R.drawable.ic_tab_expenditure));
        tab01.setContent(R.id.expenditure_tab);
        tabs.addTab(tab01);

        TabSpec tab02 = tabs.newTabSpec(getText(R.string.tab_id_income).toString());
        tab02.setIndicator(getText(R.string.income),
                getResources().getDrawable(R.drawable.ic_tab_income));
        tab02.setContent(R.id.income_tab);
        tabs.addTab(tab02);

        TabSpec tab03 = tabs.newTabSpec(getText(R.string.tab_id_move).toString());
        tab03.setIndicator(getText(R.string.move),
                getResources().getDrawable(R.drawable.ic_tab_move));
        tab03.setContent(R.id.move_tab);
        tabs.addTab(tab03);

        // データ初期化
        DBManager.getInstance().buildHouseholdData();

        // 支出タブの設定
        expenditureDateButton = (Button) findViewById(R.id.expenditure_datebutton);
        expenditureDateButton.setText(sdfforButton.format(new Date()));
        expenditureDateButton.setOnClickListener(new ShowCalendarButtonEvent());
        expenditureSendButton = (Button) findViewById(R.id.expenditure_sendbutton);
        expenditureSendButton.setEnabled(false); // ログイン処理完了までは無効化
        expenditureSendButton.setOnClickListener(this);
        expenditureAmountText = (EditText) findViewById(R.id.expenditure_amount);
        expenditureMemoText = (EditText) findViewById(R.id.expenditure_memo);
        howToPaySpinner = (Spinner) findViewById(R.id.expenditure_paymentmethodspinner);
        categorySpinner = (Spinner) findViewById(R.id.expenditure_categoryspinner);
        categorySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Category category = (Category) parent.getItemAtPosition(position);

                if (category.getID() == DIRECT_INPUT_CATEGORY_ID) {
                    final EditText editView = new EditText(mContext);
                    final AlertDialog alartDialog = new AlertDialog.Builder(mContext)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle(R.string.category)
                            .setView(editView)
                            .setPositiveButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String instantCategoryStr = editView.getText()
                                                    .toString();
                                            Category instantCategory = new Category(
                                                    INSTANCE_CATEGORY, instantCategoryStr,
                                                    HouseholdData.getInstance().getPaymentMethod()
                                                            .getMethodByName("現金").getId(), false);
                                            categoryAdaper = createCategoryArrayAdapter();
                                            categoryAdaper.add(instantCategory);
                                            categorySpinner.setAdapter(categoryAdaper);

                                            for (int i = 0; i < categorySpinner.getCount(); i++) {
                                                Category target = (Category) categorySpinner
                                                        .getItemAtPosition(i);
                                                if (target.getID() == instantCategory.getID()) {
                                                    categorySpinner.setSelection(i);
                                                    break;
                                                }
                                            }
                                        }
                                    }).create();
                    editView.setOnFocusChangeListener(new OnFocusChangeListener() {

                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus)
                                alartDialog.getWindow().setSoftInputMode(
                                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        }
                    });
                    alartDialog.show();

                    return;
                }

                // ソフトウェアキーボードを閉じる
                closeSoftwareKeyBoard(view);

                PaymentMethod payMethod = category.getDefaultPaymentMethod();

                // 関連付けてある支払方法に切り替える
                for (int i = 0; i < howToPaySpinner.getCount(); i++) {
                    PaymentMethod target = (PaymentMethod) howToPaySpinner.getItemAtPosition(i);
                    if (target.getId() == payMethod.getId()) {
                        howToPaySpinner.setSelection(i);
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        // 収入タブの設定
        incomeDateButton = (Button) findViewById(R.id.income_datebutton);
        incomeDateButton.setText(sdfforButton.format(new Date()));
        incomeDateButton.setOnClickListener(new ShowCalendarButtonEvent());
        incomeSendButton = (Button) findViewById(R.id.income_sendbutton);
        incomeSendButton.setEnabled(false); // ログイン処理完了までは無効化
        incomeSendButton.setOnClickListener(this);
        incomeAmountText = (EditText) findViewById(R.id.income_amount);
        incomeMemoText = (EditText) findViewById(R.id.income_memo);
        receiveSpinner = (Spinner) findViewById(R.id.income_receivespinner);

        // 移動タブの設定
        moveDateButton = (Button) findViewById(R.id.move_datebutton);
        moveDateButton.setText(sdfforButton.format(new Date()));
        moveDateButton.setOnClickListener(new ShowCalendarButtonEvent());
        moveSendButton = (Button) findViewById(R.id.move_sendbutton);
        moveSendButton.setEnabled(false); // ログイン処理完了までは無効化
        moveSendButton.setOnClickListener(this);
        moveAmountText = (EditText) findViewById(R.id.move_amount);
        moveMemoText = (EditText) findViewById(R.id.move_memo);
        moveFromSpinner = (Spinner) findViewById(R.id.move_movefromspinner);
        moveToSpinner = (Spinner) findViewById(R.id.move_movetospinner);

        // 個人用カスタマイズ部分
        customize();
        // ここまで

        // 非同期でログイン
        Toast.makeText(mContext, R.string.login_now, Toast.LENGTH_SHORT).show();
        new AsyncTask<Boolean, Object, Boolean>() {

            @Override
            protected Boolean doInBackground(Boolean... params) {
                boolean finished = GoogleGateWay.getInstace().createInstance();
                if (finished) {
                    isOnlineMode = true;
                    // オフラインモード時に蓄積したデータがあれば、それをまとめてサーバに送信する
                    sendStock();
                } else {
                    isOnlineMode = false;
                }
                return finished;
            }

            protected void onPostExecute(Boolean result) {
                int resId = result == true ? R.string.login_success : R.string.login_failed;
                Toast.makeText(mContext, resId, Toast.LENGTH_LONG).show();

                // 送信ボタンを有効化
                expenditureSendButton.setEnabled(true);
                incomeSendButton.setEnabled(true);
                moveSendButton.setEnabled(true);
            };

        }.execute(false);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // カテゴリスピナー設定
        categoryAdaper = createCategoryArrayAdapter();
        categorySpinner.setAdapter(categoryAdaper);

        // 支払方法スピナー設定
        ArrayAdapter<PaymentMethod> expenditureAdapter = new ArrayAdapter<PaymentMethod>(this,
                android.R.layout.simple_spinner_item);
        expenditureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (PaymentMethod method : HouseholdData.getInstance().getPaymentMethod()) {
            expenditureAdapter.add(method);
        }
        howToPaySpinner.setAdapter(expenditureAdapter);

        // 入金先スピナー設定
        ArrayAdapter<CashStorage> recieveAdapter = new ArrayAdapter<CashStorage>(this,
                android.R.layout.simple_spinner_item);
        recieveAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (CashStorage cash : HouseholdData.getInstance().getCashStorage()) {
            recieveAdapter.add(cash);
        }
        receiveSpinner.setAdapter(recieveAdapter);

        // 移動元,移動先スピナー設定
        ArrayAdapter<CashStorage> movePaymentAdapter = new ArrayAdapter<CashStorage>(this,
                android.R.layout.simple_spinner_item);
        movePaymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (CashStorage cash : HouseholdData.getInstance().getCashStorage()) {
            movePaymentAdapter.add(cash);
        }
        moveFromSpinner.setAdapter(movePaymentAdapter);
        moveToSpinner.setAdapter(movePaymentAdapter);
    }

    private ArrayAdapter<Category> createCategoryArrayAdapter() {
        ArrayAdapter<Category> categoryAdaper = new ArrayAdapter<Category>(this,
                android.R.layout.simple_spinner_item);
        categoryAdaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (Category category : HouseholdData.getInstance().getCategory()) {
            categoryAdaper.add(category);
        }
        Category instanceCategory = new Category(DIRECT_INPUT_CATEGORY_ID, "直接入力...", HouseholdData
                .getInstance().getPaymentMethod().getMethodByName("現金").getId(), false);
        categoryAdaper.add(instanceCategory);
        return categoryAdaper;
    }

    private class ShowCalendarButtonEvent implements OnClickListener {

        private Button dateButton = null;
        OnDateSetListener onDateSetListener = new OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datepicker, int i, int j, int k) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, i);
                cal.set(Calendar.MONTH, j);
                cal.set(Calendar.DAY_OF_MONTH, k);
                dateButton.setText(sdfforButton.format(cal.getTime()));
            }
        };

        @Override
        public void onClick(View view) {
            dateButton = (Button) view;
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTime(sdfforButton.parse(dateButton.getText().toString()));
                new DatePickerDialog(mContext, onDateSetListener, cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    private void customize() {

    }

    private ProgressDialog getProgressDialog() {
        if (_progressDialog == null) {
            _progressDialog = new ProgressDialog(HouseholdBudget.this);
            _progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            _progressDialog.setCancelable(false);
        }
        return _progressDialog;
    }

    /**
     * アカウントが設定されているか
     * 
     * @return
     */
    private boolean isAccountEnabled() {
        Profile profile = DBManager.getInstance().readProfile();
        if (profile.getAccountName().equals(""))
            return false;

        return true;
    }

    /**
     * データベースの作成
     */
    private void showInitializeForm() {
        Intent intent = new Intent(HouseholdBudget.this, InitializationForm.class);
        startActivity(intent);
    }

    /**
     * 最初にメニューボタンを押された時の処理
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ID_MENU1, Menu.NONE, getText(R.string.setting)).setIcon(
                android.R.drawable.ic_menu_manage);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * メニューが選択された場合の処理
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret = true;
        Intent intent = new Intent(HouseholdBudget.this, MenuForm.class);
        startActivity(intent);

        return ret;
    }

    /**
     * 未送信データが存在した場合は、サーバに送信する
     */
    private void sendStock() {
        boolean stockExist = DBManager.getInstance().hasStock();
        if (stockExist) {
            List<RowElement> rElements = DBManager.getInstance().getStocks();
            for (RowElement r : rElements) {
                // TODO : 送信できなかった時のフェイルセーフを実装
                GoogleGateWay.getInstace().insertElement(r);
            }
            DBManager.getInstance().removeAllStock();

            // !< 未送信データの送信完了通知を表示する
            sendLocalToastHandler.sendEmptyMessage(0);
        }
    }

    /**
     * 未送信データの送信完了通知を表示する
     */
    private Handler sendLocalToastHandler = new Handler() {
        public void handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), getText(R.string.send_local),
                    Toast.LENGTH_SHORT).show();
        }
    };

    private void showDialog(CharSequence title, CharSequence message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        // アラートダイアログの表示
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    /**
     * 送信ボタンを押された時の挙動
     */
    /*
     * (非 Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {

        closeSoftwareKeyBoard(v);

        _progressDialog = null;
        getProgressDialog().setMessage(getText(R.string.waiting_message).toString());
        getProgressDialog().show();

        if (v == expenditureSendButton) {
            sendExpenditureData();

        } else if (v == incomeSendButton) {
            sendIncomeData();

        } else if (v == moveSendButton) {
            sendMoveData();

        }
    }

    /**
     * データをバックグラウンドで送信orDB保存する
     * 
     * @param rowElements
     */
    private void sendDataInBackground(RowElement... rowElements) {
        if (isOnlineMode) {
            Toast.makeText(mContext, R.string.sending, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, R.string.saving, Toast.LENGTH_SHORT).show();
        }

        new AsyncTask<RowElement, Object, Integer>() {

            @Override
            protected Integer doInBackground(RowElement... params) {
                if (isOnlineMode()) {
                    boolean success = true;
                    for (int i = 0; i < params.length; i++) {
                        boolean temp = GoogleGateWay.getInstace().insertElement(params[i]);
                        if (temp == false) {
                            success = false;
                        }
                    }

                    // !< 送信成功した場合、フォーム上の入力データを削除する
                    if (success == true) {
                        resetInputDataHandler.sendEmptyMessage(0);
                    }

                    return success == true ? 1 : -1;
                } else {
                    boolean success = true;
                    for (int i = 0; i < params.length; i++) {
                        boolean temp = DBManager.getInstance().writeStock(params[i]);
                        if (temp == false) {
                            success = false;
                        }
                    }

                    // !< 送信成功した場合、フォーム上の入力データを削除する
                    if (success == true) {
                        resetInputDataHandler.sendEmptyMessage(0);
                    }

                    return success == true ? 2 : -2;
                }
            }

            protected void onPostExecute(Integer result) {
                switch (result) {
                case 1:
                    Toast.makeText(mContext, R.string.send_successed, Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(mContext, R.string.saved_local, Toast.LENGTH_LONG).show();
                    break;
                case -1:
                    showDialog(getText(R.string.send_failed), getText(R.string.no_setting_account));
                    break;
                case -2:
                    showDialog(getText(R.string.save_faild), getText(R.string.no_setting_account));
                    break;
                }
                getProgressDialog().dismiss();
            };
        }.execute(rowElements);
    }

    /**
     * 入力情報をリセットする
     */
    private void resetInputData() {
        expenditureAmountText.setText("");
        categorySpinner.setSelection(0);
        howToPaySpinner.setSelection(0);
        expenditureMemoText.setText("");

        incomeAmountText.setText("");
        receiveSpinner.setSelection(0);
        incomeMemoText.setText("");

        moveAmountText.setText("");
        moveFromSpinner.setSelection(0);
        moveToSpinner.setSelection(0);
        moveMemoText.setText("");
    }

    /**
     * 未送信データの送信完了通知を表示する
     */
    private Handler resetInputDataHandler = new Handler() {
        public void handleMessage(Message msg) {
            resetInputData();
        }
    };

    /**
     * 支出情報を送信
     */
    private void sendExpenditureData() {
        if (hasErrorInMoney(expenditureAmountText.getText().toString())) {
            getProgressDialog().dismiss();
            Toast.makeText(mContext, R.string.no_input_price, Toast.LENGTH_LONG).show();
            return;
        }

        String money = expenditureAmountText.getText().toString();
        String category = categorySpinner.getSelectedItem().toString();
        String howToPay = howToPaySpinner.getSelectedItem().toString();
        String memo = expenditureMemoText.getText().toString();

        String cashStorage = HouseholdData.getInstance().getPaymentMethod()
                .getMethodByName(howToPay).getPaymentCashStorage().getCashStorageName();

        RowElement rowElement = RowElementFactory.createPayRowElement(
                parseButtonText(expenditureDateButton), Integer.parseInt(money), cashStorage,
                category, howToPay, memo);

        sendDataInBackground(rowElement);
    }

    /**
     * 収入情報を送信
     */
    private void sendIncomeData() {
        if (hasErrorInMoney(incomeAmountText.getText().toString())) {
            getProgressDialog().dismiss();
            Toast.makeText(mContext, R.string.no_input_price, Toast.LENGTH_LONG).show();
            return;
        }

        String money = incomeAmountText.getText().toString();
        String receive = receiveSpinner.getSelectedItem().toString();
        String memo = incomeMemoText.getText().toString();

        RowElement rowElement = RowElementFactory.createIncomeRowElement(
                parseButtonText(incomeDateButton), Integer.parseInt(money), receive, memo);

        sendDataInBackground(rowElement);
    }

    /**
     * 移動情報を送信
     */
    private void sendMoveData() {
        if (hasErrorInMoney(moveAmountText.getText().toString())) {
            getProgressDialog().dismiss();
            Toast.makeText(mContext, R.string.no_input_price, Toast.LENGTH_LONG).show();
            return;
        }

        String money = moveAmountText.getText().toString();
        String moveFrom = moveFromSpinner.getSelectedItem().toString();
        String moveTo = moveToSpinner.getSelectedItem().toString();
        String memo = moveMemoText.getText().toString();

        RowElement moveFromRowElement = RowElementFactory.createMoveFromRowElement(
                parseButtonText(moveDateButton), Integer.parseInt(money), moveFrom, memo);
        RowElement moveToRowElement = RowElementFactory.createMoveToRowElement(
                parseButtonText(moveDateButton), Integer.parseInt(money), moveTo, memo);

        RowElement[] rowElements = { moveFromRowElement, moveToRowElement };
        sendDataInBackground(rowElements);
    }

    /**
     * 日付をパーズして表計算に挿入する形式にする
     * 
     * @param dateButton
     * @return
     */
    private String parseButtonText(Button dateButton) {
        // in : YYYY年MM月DD日
        // out : YYYY/MM/DD

        String date = dateButton.getText().toString();

        try {
            return sdfforSend.format(sdfforButton.parse(date));
        } catch (ParseException e) {
            return sdfforButton.format(new Date());
        }
    }

    /**
     * 金額が設定されていないかをチェック
     * 
     * @param textrInMone
     * @return
     */
    private boolean hasErrorInMoney(String text) {
        if (text.equals(""))
            return true;

        return false;
    }

    /**
     * オンラインモードかどうかを取得
     * 
     * @return オンラインモードかどうか
     */
    public static boolean isOnlineMode() {
        return isOnlineMode;
    }

    /**
     * ソフトウェアキーボードを閉じる
     * 
     * @param view
     */
    private void closeSoftwareKeyBoard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showSoftwareKeyBoard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, 0);
    }
}