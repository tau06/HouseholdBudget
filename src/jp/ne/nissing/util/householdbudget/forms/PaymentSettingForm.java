package jp.ne.nissing.util.householdbudget.forms;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import jp.ne.nissing.util.householdbudget.DBManager;
import jp.ne.nissing.util.householdbudget.R;
import jp.ne.nissing.util.householdbudget.data.HouseholdData;
import jp.ne.nissing.util.householdbudget.data.cashstorage.CashStorage;
import jp.ne.nissing.util.householdbudget.data.paymentmethod.PaymentMethod;

import java.util.List;

public class PaymentSettingForm extends Activity implements OnClickListener {
    private static final int MENU_ID_MENU1 = (Menu.FIRST + 1);
    private static final int MENU_ID_MENU2 = (Menu.FIRST + 2);
    private static final int MENU_ID_MENU3 = (Menu.FIRST + 3);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paymentsetting);

        Spinner attributeSpinner = (Spinner) findViewById(R.id.paymentsetting_cashstrage);
        attributeSpinner.removeAllViewsInLayout();
        ArrayAdapter<String> attributeAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item);
        attributeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (CashStorage attribute : HouseholdData.getInstance().getCashStorage()) {
            attributeAdapter.add(attribute.getCashStorageName());
        }
        attributeSpinner.setAdapter(attributeAdapter);

        Button setPaymentSettingButton = (Button) findViewById(R.id.paymentsetting_sendbutton);
        setPaymentSettingButton.setOnClickListener(this);
    }

    /**
     * データベースに関連情報を格納
     */
    @Override
    public void onClick(View v) {
        EditText payMethod = (EditText) findViewById(R.id.paymentsetting_nametext);
        Spinner cashStorage = (Spinner) findViewById(R.id.paymentsetting_cashstrage);

        // 支払方法の名称がすでに存在していた場合，
        // 存在する旨をダイアログで表示
        List<PaymentMethod> paymentList = DBManager.getInstance().readPaymentMethod();

        if (payMethod.getText().toString().equals("")) {
            showToastMessage("項目名が空欄です");
            return;
        }

        for (PaymentMethod pay : paymentList) {
            if (pay.getPaymentMethodName().equals(payMethod.getText().toString())) {
                showToastMessage("すでに同名の支払方法が存在します");
                return;
            }
        }

        // データベースに支払方法と現金保管場所の組を格納
        DBManager.getInstance().writePaymentMethod(payMethod.getText().toString(),
                Long.toString(cashStorage.getSelectedItemId()));

        showToastMessage("支払方法：" + payMethod.getText().toString() + "を設定しました");

        payMethod.setText("");
        cashStorage.setSelection(0);

        DBManager.getInstance().buildHouseholdData();
    }

    private void showToastMessage(String message) {
        Toast.makeText(PaymentSettingForm.this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * メニューボタンが押された時
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, Menu.NONE, Menu.NONE,
                getText(R.string.change_relation_paymethod_storage)).setIcon(
                android.R.drawable.ic_menu_edit);
        menu.add(Menu.NONE, Menu.NONE, Menu.NONE, getText(R.string.delete)).setIcon(
                android.R.drawable.ic_menu_close_clear_cancel);
        menu.add(Menu.NONE, MENU_ID_MENU3, Menu.NONE, getText(R.string.sort_item)).setIcon(
                android.R.drawable.ic_menu_sort_alphabetically);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * メニューが選択された時
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret = false;

        switch (item.getItemId()) {
        case MENU_ID_MENU1:
            // !< 画面遷移
            break;
        case MENU_ID_MENU2:
            // !< カテゴリ一覧を取得
            final String[] itemList = createPaymentMethodList();

            // !< 削除選択ダイアログを表示する
            new AlertDialog.Builder(PaymentSettingForm.this).setTitle("削除する項目を選択してください")
                    .setItems(itemList, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final int index = which;
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    PaymentSettingForm.this);
                            builder.setTitle("確認");
                            builder.setMessage("本当に削除しますか？");
                            builder.setPositiveButton(getText(R.string.yes),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // !<
                                            // 削除する支払い方法と関連付けられているカテゴリがあれば、現金に変更する
                                            DBManager.getInstance()
                                                    .replaceCategoryPaymentMethodRelation(
                                                            itemList[index], "現金");
                                            // !< データ更新
                                            DBManager.getInstance().buildHouseholdData();
                                            // !< データベースから選択した支払い方法を削除する
                                            DBManager.getInstance().removePaymentMethod(
                                                    itemList[index]);
                                            // !< データ更新
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
                    }).show();
            break;
        case MENU_ID_MENU3:
            // !< 並び替えフォームを表示する
            break;
        }

        return ret;
    }

    /**
     * 支払い方法の一覧を作成
     */
    private String[] createPaymentMethodList() {
        List<PaymentMethod> list = DBManager.getInstance().readPaymentMethod();
        String[] itemList = new String[list.size()];

        // !< カテゴリを文字列配列に変換
        for (int i = 0; i < list.size(); i++) {
            itemList[i] = list.get(i).getPaymentMethodName();
        }

        return itemList;
    }
}
