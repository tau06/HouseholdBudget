package jp.ne.nissing.util.householdbudget.forms;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import jp.ne.nissing.util.householdbudget.DBManager;
import jp.ne.nissing.util.householdbudget.R;
import jp.ne.nissing.util.householdbudget.data.cashstorage.CashStorage;

import java.util.List;

public class CashStorageSettingForm extends Activity implements OnClickListener {
    private static final int MENU_ID_MENU1 = (Menu.FIRST + 1);
    private static final int MENU_ID_MENU2 = (Menu.FIRST + 2);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cashstoragesetting);

        Button setCashStrageButton = (Button) findViewById(R.id.cashstorage_okbutton);
        setCashStrageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        EditText name = (EditText) findViewById(R.id.cashstorage_name);

        // 空欄なら警告を出してその後無視
        if (name.getText().toString().equals("")) {
            showToastMessage("支払金保管場所を入力してください");
            return;
        }

        // 指定した場所が既に存在するなら、警告を出してその後無視
        if (DBManager.getInstance().cashStorageExists(name.getText().toString())) {
            showToastMessage("その場所は既に存在します");
            return;
        }

        DBManager.getInstance().addCashStorage(name.getText().toString());

        showToastMessage("支払金保管場所：" + name.getText().toString() + "を設定しました");

        name.setText("");

        DBManager.getInstance().buildHouseholdData();
    }

    private void showToastMessage(String message) {
        Toast.makeText(CashStorageSettingForm.this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * メニューボタンが押された時
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ID_MENU1, Menu.NONE, getText(R.string.delete)).setIcon(
                android.R.drawable.ic_menu_close_clear_cancel);
        menu.add(Menu.NONE, MENU_ID_MENU2, Menu.NONE, getText(R.string.sort_item)).setIcon(
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
            // !< カテゴリ一覧を取得
            final String[] itemList = createCashStorageList();

            // !< 削除選択ダイアログを表示する
            new AlertDialog.Builder(CashStorageSettingForm.this).setTitle("削除する項目を選択してください")
                    .setItems(itemList, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final int index = which;
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    CashStorageSettingForm.this);
                            builder.setTitle("確認");
                            builder.setMessage("本当に削除しますか？");
                            builder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // !<
                                    // 削除する支払金保管場所と関連付けられている支払金保管場所があれば、財布に変換する
                                    DBManager.getInstance()
                                            .replacePaymentMethodCashStorageRelation(
                                                    itemList[index], "財布");
                                    // !< データ更新
                                    DBManager.getInstance().buildHouseholdData();
                                    // !< データベースから選択した支払金保管場所を削除する
                                    DBManager.getInstance().removeCashStorage(itemList[index]);
                                    // !< データ更新
                                    DBManager.getInstance().buildHouseholdData();
                                }
                            });
                            builder.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }).show();
            break;
        case MENU_ID_MENU2:
            // !< 並び替えフォームを表示する
            break;
        }

        return ret;
    }

    /**
     * 支払金保管場所の一覧を作成
     */
    private String[] createCashStorageList() {
        List<CashStorage> list = DBManager.getInstance().readCashStorage();
        String[] itemList = new String[list.size()];

        // !< カテゴリを文字列配列に変換
        for (int i = 0; i < list.size(); i++) {
            itemList[i] = list.get(i).getCashStorageName();
        }

        return itemList;
    }
}
