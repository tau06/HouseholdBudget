package jp.ne.nissing.util.householdbudget.data.cashstorage;

import java.util.Iterator;
import java.util.List;

public class CashStorageListIterator implements Iterator<CashStorage> {
    private List<CashStorage> _cashStorageList;
    private int _index = 0;

    /**
     * コンストラクタ
     * 
     * @param cashStorageList
     */
    public CashStorageListIterator(List<CashStorage> cashStorageList) {
        this._cashStorageList = cashStorageList;
        this._index = 0;
    }

    @Override
    public boolean hasNext() {
        return _index < _cashStorageList.size();
    }

    @Override
    public CashStorage next() {
        return _cashStorageList.get(_index++);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
