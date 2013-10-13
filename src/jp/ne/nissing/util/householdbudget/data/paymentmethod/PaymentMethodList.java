package jp.ne.nissing.util.householdbudget.data.paymentmethod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PaymentMethodList implements Iterable<PaymentMethod>{
	private List<PaymentMethod> _list = new ArrayList<PaymentMethod>();

	/**
	 * コンストラクタ
	 * @param paymentList
	 */
	public PaymentMethodList(List<PaymentMethod> paymentList){
		this._list = paymentList;
	}

	/**
	 * 支払種別のリストを取得する
	 * @return
	 */
	public List<PaymentMethod> getPaymentMethod(){
		return this._list;
	}

	/**
	 * 指定したインデックスの要素を取得
	 * @param index インデックス
	 * @return
	 */
	public PaymentMethod get(int index){
		return _list.get(index);
	}

	/**
	 * IDを指定して支払方法を取得
	 * @param id
	 * @return
	 */
	public PaymentMethod getMethodById(int id){
		for(PaymentMethod method : _list){
			if(method.getId() == id)
				return method;
		}

		return null;
	}

	public PaymentMethod getMethodByName(String name){
		for(PaymentMethod method : _list){
			if(method.getPaymentMethodName().equals(name))
				return method;
		}
		return null;
	}

	/**
	 * リストの長さを取得
	 * @return
	 */
	public int size(){
		return _list.size();
	}

	@Override
	public Iterator<PaymentMethod> iterator() {
		return new PaymentMethodListIterator(_list);
	}
}
