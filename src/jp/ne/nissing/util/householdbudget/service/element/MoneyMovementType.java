package jp.ne.nissing.util.householdbudget.service.element;

/**
 * 金銭移動のタイプ
 * @author kiuchi
 *
 */
public enum MoneyMovementType {
	/**
	 * 支出
	 */
	Expenditure("支出"), /**
	 *
	 * 収入
	 */
	income("収入"),
	/**
	 * 移動元
	 */
	moveFrom("移動元"),
	/**
	 * 移動先
	 */
	moveTo("移動先");

	private final String moneyMovementTypeStr;

	private MoneyMovementType(String moneyMovementTypeString) {
		this.moneyMovementTypeStr = moneyMovementTypeString;
	}

	@Override
	public String toString() {
		return moneyMovementTypeStr;
	}

}
