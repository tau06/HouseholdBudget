package jp.ne.nissing.util.householdbudget.service.element;

/**
 * RowElementを生成するファクトリ．
 * 
 * @author celsius
 * 
 */
public class RowElementFactory {

    /**
     * サンプルコード
     * 
     * @param money
     * @return
     */
    public static RowElement createRowElement(int money) {
        RowElement retval = new RowElement();
        retval.inputValue(ColumnName.money, money);

        return retval;
    }

    /**
     * 支出の情報を格納した RowElement の生成
     * 
     * @param date
     * @param money
     * @param cashStorage
     * @param category
     * @param howToPay
     * @return
     */
    public static RowElement createPayRowElement(String date, int money, String cashStorage,
            String category, String howToPay, String memo) {
        RowElement retval = new RowElement();
        retval.inputValue(ColumnName.date, date);
        retval.inputValue(ColumnName.type, MoneyMovementType.Expenditure.toString());
        retval.inputValue(ColumnName.money, money);
        retval.inputValue(ColumnName.cashStorage, cashStorage);
        retval.inputValue(ColumnName.category, category);
        retval.inputValue(ColumnName.howtopay, howToPay);
        if (memo.equals("") == false)
            retval.inputValue(ColumnName.memo, memo);
        else
            retval.inputValue(ColumnName.memo, "_");

        return retval;
    }

    /**
     * 収入の情報を格納した RowElement の生成
     * 
     * @param date
     * @param money
     * @param cashStorage
     * @param memo
     * @return
     */
    public static RowElement createIncomeRowElement(String date, int money, String cashStorage,
            String memo) {
        RowElement retval = new RowElement();
        retval.inputValue(ColumnName.date, date);
        retval.inputValue(ColumnName.type, MoneyMovementType.income.toString());
        retval.inputValue(ColumnName.money, money);
        retval.inputValue(ColumnName.cashStorage, cashStorage);
        retval.inputValue(ColumnName.category, "_");
        retval.inputValue(ColumnName.howtopay, "_");
        if (memo.equals("") == false)
            retval.inputValue(ColumnName.memo, memo);
        else
            retval.inputValue(ColumnName.memo, "_");

        return retval;
    }

    /**
     * 移動元の情報を格納した RowElement の生成
     * 
     * @param date
     * @param money
     * @param cashStorage
     * @param moveTo
     * @param memo
     * @return
     */
    public static RowElement createMoveFromRowElement(String date, int money, String cashStorage,
            String memo) {
        RowElement retval = new RowElement();
        retval.inputValue(ColumnName.date, date);
        retval.inputValue(ColumnName.type, MoneyMovementType.moveFrom.toString());
        retval.inputValue(ColumnName.money, money);
        retval.inputValue(ColumnName.cashStorage, cashStorage);
        retval.inputValue(ColumnName.category, "_");
        retval.inputValue(ColumnName.howtopay, "_");
        if (memo.equals("") == false)
            retval.inputValue(ColumnName.memo, memo);
        else
            retval.inputValue(ColumnName.memo, "_");

        return retval;
    }

    /**
     * 移動先の情報を格納した RowElement の生成
     * 
     * @param date
     * @param money
     * @param cashStorage
     * @param memo
     * @return
     */
    public static RowElement createMoveToRowElement(String date, int money, String cashStorage,
            String memo) {
        RowElement retval = new RowElement();
        retval.inputValue(ColumnName.date, date);
        retval.inputValue(ColumnName.type, MoneyMovementType.moveTo.toString());
        retval.inputValue(ColumnName.money, money);
        retval.inputValue(ColumnName.cashStorage, cashStorage);
        retval.inputValue(ColumnName.category, "_");
        retval.inputValue(ColumnName.howtopay, "_");
        if (memo.equals("") == false)
            retval.inputValue(ColumnName.memo, memo);
        else
            retval.inputValue(ColumnName.memo, "_");

        return retval;
    }
}
