package jp.ne.nissing.util.householdbudget.data.category;

import jp.ne.nissing.util.householdbudget.data.HouseholdData;
import jp.ne.nissing.util.householdbudget.data.paymentmethod.PaymentMethod;

public class Category {
	private int _id;
	private String _categoryName;
	private int _defaultPay;
	private boolean _isPreset;

	/**
	 * コンストラクタ
	 * @param id ID
	 * @param category カテゴリ
	 * @param defaultPay デフォルトの支払方法
	 * @param isPreset プリセットかどうか
	 */
	public Category(int id, String category, int defaultPay, boolean isPreset){
		this._id = id;
		this._categoryName = category;
		this._defaultPay = defaultPay;
		this._isPreset = isPreset;
	}

	/**
	 * IDを取得
	 * @return
	 */
	public int getID(){
		return this._id;
	}

	/**
	 * カテゴリ名を取得
	 * @return
	 */
	public String getCategoryName(){
		return this._categoryName;
	}

	/**
	 * デフォルトの支払方法を取得
	 * @param id
	 * @return
	 */
	public PaymentMethod getDefaultPaymentMethod(){
		return HouseholdData.getInstance().getPaymentMethod().getMethodById(_defaultPay);
	}

	/**
	 * プリセットかどうか
	 * @return
	 */
	public boolean isPreset(){
		return this._isPreset;
	}

	@Override
	public String toString() {
		return getCategoryName();
	}
}
