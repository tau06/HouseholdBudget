package jp.ne.nissing.util.householdbudget.data.paymentmethod;

import java.util.Iterator;
import java.util.List;

public class PaymentMethodListIterator implements Iterator<PaymentMethod> {
    private List<PaymentMethod> _list;
    private int _index = 0;

    /**
     * コンストラクタ
     * 
     * @param paymentDataList
     */
    public PaymentMethodListIterator(List<PaymentMethod> paymentDataList) {
        this._list = paymentDataList;
        this._index = 0;
    }

    @Override
    public boolean hasNext() {
        return _index < _list.size();
    }

    @Override
    public PaymentMethod next() {
        return _list.get(_index++);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
