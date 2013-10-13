package jp.ne.nissing.util.householdbudget.data.cashstorage;

public class CashStorage
{
	private int _id;
	private String _cashStorageName;
	private boolean _isPreset;

	/**
	 * コンストラクタ
	 * @param cashStorageName
	 */
	public CashStorage(int id, String cashStorageName,boolean isPreset){
		this._id = id;
		this._cashStorageName = cashStorageName;
		this._isPreset = isPreset;
	}

	/**
	 * IDを取得する
	 * @return
	 */
	public int getId(){
		return this._id;
	}

	/**
	 * 支払金保管場所名を取得
	 * @return
	 */
	public String getCashStorageName(){
		return this._cashStorageName;
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
		return getCashStorageName();
	}
}
