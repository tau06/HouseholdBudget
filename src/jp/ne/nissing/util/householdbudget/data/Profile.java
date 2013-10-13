package jp.ne.nissing.util.householdbudget.data;

public class Profile {
	private String _accountName;
	private String _password;
	private String _sheetName;
	private final String _APP_NAME = "householdBudget";

	/**
	 * コンストラクタ
	 * 
	 * @param accountName
	 *            アカウント名（メールアドレス）
	 * @param pass
	 *            パスワード
	 * @param sheetName
	 *            SpreadSheet名
	 */
	public Profile(String accountName, String pass, String sheetName) {
		this._accountName = accountName;
		this._password = pass;
		this._sheetName = sheetName;
	}

	/**
	 * アカウント名（メールアドレス）を取得する
	 * 
	 * @return
	 */
	public String getAccountName() {
		return _accountName;
	}

	/**
	 * パスワードを取得する
	 * 
	 * @return
	 */
	public String getPassword() {
		return _password;
	}

	/**
	 * SpraedSheet名を取得する
	 * 
	 * @return
	 */
	public String getSheetName() {
		return _sheetName;
	}

	/**アプリケーション名を取得する
	 * @return
	 */
	public String getApplicationName() {
		return _APP_NAME;
	}
}
