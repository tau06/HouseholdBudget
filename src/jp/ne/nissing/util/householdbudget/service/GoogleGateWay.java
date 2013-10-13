package jp.ne.nissing.util.householdbudget.service;

import java.util.ArrayList;

import jp.ne.nissing.util.householdbudget.data.HouseholdData;
import jp.ne.nissing.util.householdbudget.service.element.*;

import android.app.ProgressDialog;
import android.widget.Toast;

import com.pras.*;

/**
 * Googleのサービスと通信を行うクラス．
 * SpreadSheetManager,およびDocumentManagerに対するアクセスはこのクラスを経由して行う．
 *
 * @author celsius
 *
 */
public class GoogleGateWay {
	private static GoogleGateWay _instance = new GoogleGateWay();
	private String _id = null;
	private String _pass = null;
	private SpreadSheet _spreadSheet = null;
	private WorkSheet _workSheet = null;

	private SpreadSheetFactory _factory = null;

	private boolean _isActive = false;

	/**
	 * コンストラクタ
	 */
	private GoogleGateWay() {
	}

	public static GoogleGateWay getInstace() {
		return _instance;
	}

	/**
	 * IDを取得する
	 *
	 * @return
	 */
	private String getId() {
		if (_id == null) {
			_id = HouseholdData.getInstance().getProfile().getAccountName();
		}
		return _id;
	}

	/**
	 * Passを取得する
	 *
	 * @return
	 */
	private String getPass() {
		if (_pass == null) {
			_pass = HouseholdData.getInstance().getProfile().getPassword();
		}
		return _pass;
	}

	/**
	 * SpreadSheetを取得する
	 *
	 * @return
	 */
	private SpreadSheet getSpreadSheet() {
		if (_spreadSheet == null) {
			String title = HouseholdData.getInstance().getProfile()
					.getSheetName();
			ArrayList<SpreadSheet> spreadSheets = getFactory().getSpreadSheet(
					title, true);
			if (spreadSheets != null && spreadSheets.size() == 1)
				_spreadSheet = spreadSheets.get(0);
		}
		return _spreadSheet;
	}

	/**
	 * WorkSheetを取得する
	 *
	 * @return
	 */
	private WorkSheet getWorkSheet() {
		if (_workSheet == null) {
			ArrayList<WorkSheet> workSheets = getSpreadSheet().getWorkSheet(
					getSpreadSheet().getTitle(), true);
			if (workSheets != null && workSheets.size() == 1) {
				_workSheet = workSheets.get(0);
			}
		}
		return _workSheet;
	}

	/**
	 * SpreadSheetFactoryを取得する
	 *
	 * @return
	 */
	private SpreadSheetFactory getFactory() {
		try {
			if (_factory == null) {
				_factory = SpreadSheetFactory.getInstance(getId(), getPass());
				_factory.getAllSpreadSheets();
			}
		} catch (Exception e) {
			_factory = null;
		}
		return _factory;
	}

	/**
	 * 保存されているID,Pass,spreadsheetの情報をクリア
	 */
	public void clear() {
		_id = null;
		_pass = null;
		_spreadSheet = null;
		_workSheet = null;
		_isActive = false;
	}

	/**
	 * ID,Pass,SpreadSheetを有効にする．失敗した場合はFalse.
	 *
	 * @return
	 */
	public boolean activate() {
		clear();
		String id = getId();
		String pass = getPass();
		String title = HouseholdData.getInstance().getProfile().getSheetName();
		if (id == null || pass == null)
			return false;
		SpreadSheetFactory factory = getFactory();
		if (factory == null)
			return false;
		_isActive = existSpreadSheet(title);
		return _isActive;
	}

	/**
	 * SpreadSheet,WorkSheetのインスタンスを生成する．
	 *
	 * @return
	 */
	public boolean createInstance() {
		if (activate() == false)
			return false;
		else {
			getSpreadSheet();
			getWorkSheet();
			return true;
		}
	}

	/**
	 * ID,Pass,SpreadSheetが有効かどうかを取得する.
	 *
	 * @return
	 */
	public boolean isActive() {
		return _isActive;
	}

	/**
	 * 新たにSpreadSheetを作成する．成功した場合はTrue
	 */
	public boolean cleanupSpreadSheet() {
		String title = HouseholdData.getInstance().getProfile().getSheetName();
		if (title == null)
			return false;
		return createSpreadSheet(title);
	}

	/**
	 * Profileをリセットする.成功した場合はTrue.
	 */
	public boolean cleanupProfile() {
		String id = getId();
		String pass = getPass();
		if (id == null || pass == null)
			return false;
		SpreadSheetFactory factory = getFactory();
		if (factory == null)
			return false;
		return true;

	}

	/**
	 * SpreadSheetを作成する
	 *
	 * @param title
	 * @return
	 */
	private boolean createSpreadSheet(String title) {
		if (existSpreadSheet(title))
			return false;

		getFactory().createSpreadSheet(title);

		ArrayList<SpreadSheet> spreadSheets = getFactory().getSpreadSheet(
				title, true);
		if (spreadSheets != null && spreadSheets.size() == 1) {
			SpreadSheet sheet = spreadSheets.get(0);

			String[] cols = new String[ColumnName.values().length];
			int columnIndex = 0;
			for (ColumnName columnName : ColumnName.values()) {
				cols[columnIndex] = columnName.toString();
				columnIndex++;
			}

			sheet.addWorkSheet(title, cols);
			ArrayList<WorkSheet> workSheets = sheet.getAllWorkSheets();
			for (WorkSheet ws : workSheets) {
				if (ws.getTitle().equals(title) == false) {
					ws.delete();
				}
			}
		}

		return true;
	}

	/**
	 * 引数に指定したタイトルのSpreadSheetが存在するかどうかを取得する．
	 *
	 * @param title
	 * @return
	 */
	public boolean existSpreadSheet(String title) {
		ArrayList<SpreadSheet> spreadSheets = getFactory().getSpreadSheet(
				title, true);
		if (spreadSheets == null || spreadSheets.size() == 0)
			return false;
		else
			return true;
	}

	/**
	 * 要素を追加する(追加するWorksheetは先頭のもの)
	 *
	 * @param element
	 * @return
	 */
	public boolean insertElement(RowElement element) {
		try {

			getWorkSheet().addRecord(getSpreadSheet().getKey(),
					element.getInsertHashMap());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}