package jp.ne.nissing.util.householdbudget.data;

import jp.ne.nissing.util.householdbudget.data.cashstorage.CashStorage;
import jp.ne.nissing.util.householdbudget.data.cashstorage.CashStorageList;
import jp.ne.nissing.util.householdbudget.data.category.Category;
import jp.ne.nissing.util.householdbudget.data.category.CategoryList;
import jp.ne.nissing.util.householdbudget.data.paymentmethod.PaymentMethod;
import jp.ne.nissing.util.householdbudget.data.paymentmethod.PaymentMethodList;

import java.util.List;

public class HouseholdData {
    private static HouseholdData _instance = new HouseholdData();

    private HouseholdData() {
    }

    /**
     * 家計簿データのインスタンスを取得
     * 
     * @return
     */
    public static HouseholdData getInstance() {
        return _instance;
    }

    private Profile _profile;

    /**
     * アカウント情報を取得
     * 
     * @return
     */
    public Profile getProfile() {
        return this._profile;
    }

    /**
     * アカウント情報を設定
     * 
     * @param accountName
     *            アカウント名（メールアドレス）
     * @param password
     *            パスワード
     * @param sheetName
     *            SpreadSheet名
     */
    public void setProfile(String accountName, String password, String sheetName) {
        this._profile = new Profile(accountName, password, sheetName);
    }

    private CategoryList _category;

    /**
     * カテゴリ情報を取得
     * 
     * @return カテゴリ情報
     */
    public CategoryList getCategory() {
        return this._category;
    }

    /**
     * カテゴリ情報を設定
     * 
     * @param categoryList
     *            カテゴリ情報が格納されたリスト
     */
    public void setCategory(List<Category> categoryList) {
        this._category = new CategoryList(categoryList);
    }

    private PaymentMethodList _paymentMethod;

    /**
     * 支払方法を取得
     * 
     * @return 支払方法
     */
    public PaymentMethodList getPaymentMethod() {
        return this._paymentMethod;
    }

    /**
     * 支払方法を設定
     * 
     * @param paymentMethod
     *            支払方法が格納されたリスト
     */
    public void setPaymentMethod(List<PaymentMethod> paymentList) {
        this._paymentMethod = new PaymentMethodList(paymentList);
    }

    private CashStorageList _cashStorage;

    /**
     * 支払金保管場所を取得
     * 
     * @return 支払金保管場所
     */
    public CashStorageList getCashStorage() {
        return this._cashStorage;
    }

    /**
     * 支払金保管場所を設定
     * 
     * @param cashStorageList
     *            現金保管場所が格納されたリスト
     */
    public void setCashStorage(List<CashStorage> cashStorageList) {
        this._cashStorage = new CashStorageList(cashStorageList);
    }
}
