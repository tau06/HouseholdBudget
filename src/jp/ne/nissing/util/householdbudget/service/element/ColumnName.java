package jp.ne.nissing.util.householdbudget.service.element;

/**
 * カラム名
 * @author kiuchi
 */
public enum ColumnName {
	date("日付"),		// 日付
	type("種別"),		// 種別
	money("金額"),		// 金額
	cashStorage("支払金保管場所"),  //支払金保管場所
	howtopay("支払方法"),	// 支払方法
	category("カテゴリ"),	// カテゴリ
//	incometo("入金先"),	// 入金先
//	movefrom("移動元"),	// 移動元
//	moveto("移動前"),		// 移動先
	memo("備考");		// 備考

	private final String columnStr;

	private ColumnName(String columnString){
		this.columnStr = columnString;
	}

	@Override
	public String toString() {
		return columnStr;
	}

}
