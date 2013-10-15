package jp.ne.nissing.util.householdbudget.service.element;

import java.util.HashMap;

/**
 * 1つ列のデータを表すモデルクラス
 * 
 * @author celsius
 */
public class RowElement {
    /**
     * 挿入するカラムと値を持つハッシュマップ
     */
    private HashMap<ColumnName, Object> _insertHashMap = new HashMap<ColumnName, Object>();

    /**
     * 値を挿入
     * 
     * @param columnName
     * @param value
     */
    public void inputValue(ColumnName columnName, Object value) {
        _insertHashMap.put(columnName, value);
    }

    /**
     * 挿入する列のデータのハッシュマップを取得する
     * 
     * @return
     */
    public HashMap<String, String> getInsertHashMap() {
        HashMap<String, String> retval = new HashMap<String, String>();
        for (ColumnName key : _insertHashMap.keySet()) {
            retval.put(key.toString(), _insertHashMap.get(key).toString());
        }
        return retval;
    }
}
