package jp.ne.nissing.util.householdbudget.data.cashstorage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CashStorageList implements Iterable<CashStorage> {
    private List<CashStorage> _list = new ArrayList<CashStorage>();

    /**
     * コンストラクタ
     * 
     * @param cashStorageList
     */
    public CashStorageList(List<CashStorage> cashStorageList) {
        this._list = cashStorageList;
    }

    /**
     * 支払金保管場所のリストを取得する
     * 
     * @return
     */
    public List<CashStorage> getCashStorage() {
        return this._list;
    }

    /**
     * 指定したインデックスの要素を取得
     * 
     * @param index
     *            インデックス
     * @return
     */
    public CashStorage get(int index) {
        return this._list.get(index);
    }

    /**
     * IDを指定して支払金保管場所を取得する
     * 
     * @param id
     * @return
     */
    public CashStorage getCashStorageById(int id) {
        for (CashStorage cashStorage : _list) {
            if (cashStorage.getId() == id)
                return cashStorage;
        }

        return null;
    }

    /**
     * 名前を指定して支払金保管場所を取得する
     * 
     * @param name
     * @return
     */
    public CashStorage getCashStorageByName(String name) {
        for (CashStorage cashStorage : _list) {
            if (cashStorage.getCashStorageName() == name)
                return cashStorage;
        }
        return null;
    }

    /**
     * リストの長さを取得
     * 
     * @return
     */
    public int size() {
        return _list.size();
    }

    @Override
    public Iterator<CashStorage> iterator() {
        return new CashStorageListIterator(_list);
    }
}
