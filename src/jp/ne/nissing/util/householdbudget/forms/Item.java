package jp.ne.nissing.util.householdbudget.forms;

public class Item {
    private String _title;
    private String _value;

    /**
     * コンストラクタ
     */
    public Item() {
        this._title = "";
        this._value = "";
    }

    public String getValue() {
        return this._value;
    }

    public void setValue(String value) {
        this._value = value;
    }

    public String getTitle() {
        return this._title;
    }

    public void setItem(String title) {
        this._title = title;
    }
}
