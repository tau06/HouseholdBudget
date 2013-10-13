package jp.ne.nissing.util.householdbudget.data.paymentmethod;

import jp.ne.nissing.util.householdbudget.data.HouseholdData;
import jp.ne.nissing.util.householdbudget.data.cashstorage.CashStorage;

public class PaymentMethod {
	private int _id;
	private String _paymentMethodName;
	private int _paymentCashStorageId;
	private boolean _isPreset;

	/**
	 * コンストラクタ
	 * @param id ID
	 * @param paymentMethod 支払方法名
	 * @param paymentCategory 属性
	 * @param isPreset プリセットかどうか
	 */
	public PaymentMethod(int id, String paymentMethod, int paymentCashStorage, boolean isPreset){
		this._id = id;
		this._paymentMethodName = paymentMethod;
		this._paymentCashStorageId = paymentCashStorage;
		this._isPreset = isPreset;
	}

	/**
	 * IDを取得
	 * @return
	 */
	public int getId(){
		return this._id;
	}

	/**
	 * 支払種別名を取得
	 * @return
	 */
	public String getPaymentMethodName(){
		return this._paymentMethodName;
	}
	/**
	 * 現金保管場所のIDを取得
	 * @return
	 */
	public int getPaymentCashStorageId(){
		return this._paymentCashStorageId;
	}

	/**
	 * 現金保管場所のインスタンスを取得
	 * @return
	 */
	public CashStorage getPaymentCashStorage(){
		return HouseholdData.getInstance().getCashStorage().getCashStorageById(_paymentCashStorageId);
	}

	/**
	 * プリセットの支払方法かどうか
	 * @return
	 */
	public boolean isPreset(){
		return this._isPreset;
	}

	@Override
	public String toString() {
		return getPaymentMethodName();
	}
}
