package jp.ne.nissing.util.householdbudget.data.category;

import java.util.*;

public class CategoryList implements Iterable<Category>{
	private List<Category> _list = new ArrayList<Category>();
	
	/**
	 * コンストラクタ
	 * @param categoryList
	 */
	public CategoryList(List<Category> categoryList){
		this._list = categoryList;
	}
	
	/**
	 * カテゴリのリストを取得する
	 * @return
	 */
	public List<Category> getCategory(){
		return this._list;
	}
	
	/**
	 * 指定したインデックスの要素を取得
	 * @param index インデックス
	 * @return
	 */
	public Category get(int index){
		return _list.get(index);
	}
	
	/**
	 * IDを指定して支払方法を取得
	 * @param id
	 * @return
	 */
	public Category getCategoryFromID(int id){
		for(Category category : _list){
			if(category.getID() == id)
				return category;
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
	public Iterator<Category> iterator(){
		return new CategoryListIterator(_list);
	}
}
