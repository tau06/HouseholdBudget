package jp.ne.nissing.util.householdbudget.data.category;

import java.util.*;

public class CategoryListIterator implements Iterator<Category>{
	private List<Category> _categoryList;
	private int _index = 0;
	
	/**
	 * コンストラクタ
	 * @param categoryList
	 */
	public CategoryListIterator(List<Category> categoryList){
		this._categoryList = categoryList;
		this._index = 0;
	}

	@Override
	public boolean hasNext() {
		return _index < _categoryList.size();
	}

	@Override
	public Category next() {
		return _categoryList.get(_index++);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
}
