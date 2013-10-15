package jp.ne.nissing.util.householdbudget.forms;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import jp.ne.nissing.util.householdbudget.DBManager;
import jp.ne.nissing.util.householdbudget.R;
import jp.ne.nissing.util.householdbudget.data.HouseholdData;
import jp.ne.nissing.util.householdbudget.data.category.Category;
import jp.ne.nissing.util.householdbudget.data.paymentmethod.PaymentMethod;

public class CategoryRelationForm extends Activity implements OnClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categoryrelation);

        Spinner categorySpinner = (Spinner) findViewById(R.id.categoryrelation_category);
        Spinner paymethodSpinner = (Spinner) findViewById(R.id.categoryrelation_payment);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * カテゴリSpinnerのリスト要素が選択された時に、関連付けられている支払方法を支払方法Spinnerに表示
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // カテゴリ以外のSpinnerの場合は無視
                if (parent != (Spinner) findViewById(R.id.categoryrelation_category))
                    return;

                // カテゴリのSpinnerを取得
                Spinner categorySpinner = (Spinner) parent;
                String category = categorySpinner.getSelectedItem().toString();

                // カテゴリと関連付けられている支払方法を取得
                int paymethodId = DBManager.getInstance().readPaymethodIdFromCategory(category);

                Spinner paymethodSpinner = (Spinner) findViewById(R.id.categoryrelation_payment);
                paymethodSpinner.setSelection(paymethodId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        categorySpinner.removeAllViewsInLayout();
        paymethodSpinner.removeAllViewsInLayout();

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (Category category : HouseholdData.getInstance().getCategory()) {
            categoryAdapter.add(category.getCategoryName());
        }
        categorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter<String> attributeAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item);
        attributeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (PaymentMethod payment : HouseholdData.getInstance().getPaymentMethod()) {
            attributeAdapter.add(payment.getPaymentMethodName());
        }
        paymethodSpinner.setAdapter(attributeAdapter);

        Button setRelationButton = (Button) findViewById(R.id.categoryrelation_sendbutton);
        setRelationButton.setOnClickListener(this);
    }

    /**
     * 決定
     */
    @Override
    public void onClick(View v) {
        // データベースにカテゴリと支払方法の組を格納
        Spinner category = (Spinner) findViewById(R.id.categoryrelation_category);
        Spinner payMethod = (Spinner) findViewById(R.id.categoryrelation_payment);

        // 未入力個所があればキャンセル
        if (category.getSelectedItem().toString().equals("")
                || payMethod.getSelectedItem().toString().equals("")) {
            showToastMessage("テキストエリアに入力してください");
            return;
        }

        DBManager.getInstance().updateCategoryPayMethodRelation(
                category.getSelectedItem().toString(), payMethod.getSelectedItem().toString());

        showToastMessage(category.getSelectedItem().toString() + "のデフォルトを"
                + payMethod.getSelectedItem().toString() + "に設定しました");

        category.setSelection(0);
        payMethod.setSelection(0);

        DBManager.getInstance().buildHouseholdData();
    }

    private void showToastMessage(String message) {
        Toast.makeText(CategoryRelationForm.this, message, Toast.LENGTH_SHORT).show();
    }
}
