package jp.ne.nissing.util.householdbudget.forms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import jp.ne.nissing.util.householdbudget.R;

public class MenuForm extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menuform);

        createList();
    }

    /**
     * フォームの内容を作成
     */
    private void createList() {
        String[] items = { getText(R.string.categorysettingtitle).toString(),
                getText(R.string.paymentsettingtitle).toString(),
                getText(R.string.cashstoragesetting).toString(),
                getText(R.string.accountsettingtitle).toString() };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
        // 要素の追加
        for (String item : items)
            adapter.add(item);

        ListView list = (ListView) findViewById(R.id.menulist);
        list.setAdapter(adapter);
        // リストのクリックイベント
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();

                switch (position) {
                case 0: // カテゴリの設定
                    intent = new Intent(MenuForm.this, CategorySettingForm.class);
                    startActivity(intent);
                    break;

                case 1:
                    intent = new Intent(MenuForm.this, PaymentSettingForm.class);
                    startActivity(intent);
                    break;

                case 2:
                    intent = new Intent(MenuForm.this, CashStorageSettingForm.class);
                    startActivity(intent);
                    break;

                case 3:
                    intent = new Intent(MenuForm.this, AccountSettingForm.class);
                    startActivity(intent);
                    break;

                default:
                    break;
                }
            }
        });
    }
}
