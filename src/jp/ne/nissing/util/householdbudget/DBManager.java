package jp.ne.nissing.util.householdbudget;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import jp.ne.nissing.util.householdbudget.data.HouseholdData;
import jp.ne.nissing.util.householdbudget.data.Profile;
import jp.ne.nissing.util.householdbudget.data.cashstorage.CashStorage;
import jp.ne.nissing.util.householdbudget.data.category.Category;
import jp.ne.nissing.util.householdbudget.data.paymentmethod.PaymentMethod;
import jp.ne.nissing.util.householdbudget.service.element.ColumnName;
import jp.ne.nissing.util.householdbudget.service.element.RowElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DBManager {
    private String _dbname;
    private static DBManager _instance = new DBManager();
    private SQLiteDatabase database;
    private DBConnection connection;
    private Context _context;

    /**
     * DBManagerのインスタンスを取得した
     * 
     * @return
     */
    public static DBManager getInstance() {
        return _instance;
    }

    /**
     * コンストラクタ
     */
    private DBManager() {
    }

    /**
     * コンテキストを設定
     * 
     * @param c
     */
    public void initialize(Context c, String dbName) {
        this._context = c;
        this._dbname = dbName;

        connection = new DBConnection(_context, _dbname, 1);
    }

    /**
     * データベースを開く
     */
    public void open(String dbName) {
        this._dbname = dbName;
    }

    /**
     * データベースを閉じる
     */
    public void close() {
        this.connection.close();
        database.close();
    }

    /**
     * データベースから情報を読み取り，モデルクラスに格納する
     */
    public void buildHouseholdData() {
        Profile profile = readProfile();
        List<CashStorage> atList = readCashStorage();
        List<PaymentMethod> payList = readPaymentMethod();
        List<Category> catList = readCategory();

        HouseholdData.getInstance().setProfile(profile.getAccountName(), profile.getPassword(),
                profile.getSheetName());
        HouseholdData.getInstance().setCashStorage(atList);
        HouseholdData.getInstance().setCategory(catList);
        HouseholdData.getInstance().setPaymentMethod(payList);
    }

    /**
     * アカウント情報を読み取る
     * 
     * @return
     */
    public Profile readProfile() {
        String sql = "SELECT * FROM profile;";
        database = this.connection.getReadableDatabase();
        Cursor c = database.rawQuery(sql, null);
        String[] list = new String[3];
        list[0] = "";
        list[1] = "";
        list[2] = "";
        while (c.moveToNext()) {
            list[0] = c.getString(0);
            list[1] = c.getString(1);
            list[2] = c.getString(2);
        }
        c.close();
        database.close();

        return new Profile(list[0], list[1], list[2]);
    }

    /**
     * カテゴリを読み取る
     * 
     * @return
     */
    public List<Category> readCategory() {
        String query = "SELECT * FROM category;";
        database = this.connection.getReadableDatabase();
        Cursor c = database.rawQuery(query, null);
        List<Category> categorylist = new ArrayList<Category>();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            int payid = c.getInt(2);
            boolean isPreset = Boolean.parseBoolean(c.getString(3));
            categorylist.add(new Category(id, name, payid, isPreset));
        }
        c.close();
        database.close();

        return categorylist;
    }

    /**
     * 支払方法を読み取る
     * 
     * @return
     */
    public List<PaymentMethod> readPaymentMethod() {
        this.database = connection.getReadableDatabase();
        String query = "SELECT * FROM paymethod;";
        Cursor c = database.rawQuery(query, null);
        List<PaymentMethod> paylist = new ArrayList<PaymentMethod>();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            int cashStorageId = c.getInt(2);
            boolean isPreset = Boolean.parseBoolean(c.getString(3));
            paylist.add(new PaymentMethod(id, name, cashStorageId, isPreset));
        }
        c.close();

        return paylist;
    }

    /**
     * 現金保管場所を読み取る
     * 
     * @return
     */
    public List<CashStorage> readCashStorage() {
        String query = "SELECT * FROM cash_storage";
        database = this.connection.getReadableDatabase();
        Cursor c = database.rawQuery(query, null);
        List<CashStorage> cashStoragelist = new ArrayList<CashStorage>();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            boolean isPreset = Boolean.parseBoolean(c.getString(2));
            cashStoragelist.add(new CashStorage(id, name, isPreset));
        }
        c.close();
        database.close();

        return cashStoragelist;
    }

    /**
     * 指定した支払金保管場所が存在するかどうか
     * 
     * @param cashStorage
     * @return
     */
    public boolean cashStorageExists(String cashStorage) {
        String query = "SELECT * FROM cash_storage WHERE name='" + cashStorage + "';";
        database = this.connection.getReadableDatabase();
        Cursor c = database.rawQuery(query, null);
        while (c.moveToNext()) {
            return true;
        }

        return false;
    }

    /**
     * 指定した支払方法のIDを取得する
     * 
     * @param paymethod
     * @return
     */
    public int readPaymethodId(String paymethod) {
        String query = "SELECT _id FROM paymethod WHERE name = '" + paymethod + "';";
        database = this.connection.getReadableDatabase();
        Cursor c = database.rawQuery(query, null);
        c.moveToNext();
        int id = c.getInt(0);

        c.close();
        database.close();

        return id;
    }

    /**
     * 指定したカテゴリに関連付けられている支払方法のIDを取得
     * 
     * @param category
     * @return
     */
    public int readPaymethodIdFromCategory(String category) {
        String query = "SELECT paymethod_id FROM category WHERE name='" + category + "';";
        database = this.connection.getReadableDatabase();
        Cursor c = database.rawQuery(query, null);
        c.moveToNext();
        int paymethod = c.getInt(0);

        c.close();
        database.close();

        return paymethod;
    }

    /**
     * 指定したカテゴリに関連付けられている支払方法を取得
     * 
     * @param category
     * @return
     */
    public String readPaymethodFromCategory(String category) {
        String query = "SELECT paymethod_id FROM category WHERE name='" + category + "';";
        database = this.connection.getReadableDatabase();
        Cursor c = database.rawQuery(query, null);
        c.moveToNext();
        int paymethod = c.getInt(0);
        c.close();

        query = "SELECT name FROM paymethod WHERE _id='" + paymethod + "';";
        Cursor c2 = database.rawQuery(query, null);
        c2.moveToNext();
        String payName = c2.getString(0);
        c2.close();

        database.close();

        return payName;
    }

    /**
     * アカウント情報を書き込む
     * 
     * @param accountName
     *            アカウント名（メールアドレス）
     * @param password
     *            パスワード
     * @param spreadSheetName
     *            スプレッドシート名
     */
    public void writeProfile(String accountName, String password, String spreadSheetName) {
        String sql = "INSERT INTO profile VALUES('" + accountName + "','" + password + "','"
                + spreadSheetName + "');";
        database = this.connection.getWritableDatabase();
        database.execSQL(sql);
        database.close();
    }

    /**
     * アカウントをDBに書き込む
     * 
     * @param accountName
     */
    public void writeAccount(String accountName) {
        String sql = "UPDATE profile set account_name='" + accountName + "';";
        database = this.connection.getWritableDatabase();
        database.execSQL(sql);
        database.close();
    }

    /**
     * パスワードをDBに書き込む
     * 
     * @param password
     */
    public void writePassword(String password) {
        String sql = "UPDATE profile set password='" + password + "';";
        database = this.connection.getWritableDatabase();
        database.execSQL(sql);
        database.close();
    }

    /**
     * スプレッドシート名をDBに書き込む
     * 
     * @param name
     */
    public void writeSpreadSheetName(String name) {
        String sql = "UPDATE profile set spreadsheet_name='" + name + "';";
        database = this.connection.getWritableDatabase();
        database.execSQL(sql);
        database.close();
    }

    /**
     * カテゴリを書き込む
     * 
     * @param category
     * @param paymentMethod
     */
    public void writeCategory(String category, String paymentMethod) {
        String sql = "SELECT name FROM category";
        database = this.connection.getWritableDatabase();
        Cursor c = database.rawQuery(sql, null);
        // DB内に存在するカテゴリなら「更新」，無ければ「追加」
        while (c.moveToNext()) {
            if (c.getString(0).equals(category)) {
                updateCategory(category, paymentMethod);
                return;
            }
        }
        c.close();
        database.close();

        updateCategoryPayMethodRelation(category, paymentMethod);
    }

    /**
     * カテゴリに対応する支払方法を更新する
     * 
     * @param category
     * @param paymentMethod
     */
    public void updateCategoryPayMethodRelation(String category, String paymentMethod) {
        String sql = "SELECT _id FROM paymethod WHERE name='" + paymentMethod + "';";
        database = this.connection.getWritableDatabase();
        Cursor c = database.rawQuery(sql, null);
        c.moveToNext();
        int paymethodid = c.getInt(0);
        sql = "UPDATE category SET paymethod_id = " + paymethodid + " WHERE name = '" + category
                + "';";
        try {
            database.execSQL(sql);
        } catch (Exception e) {
            String s = e.toString();
            System.out.println(s);
        }
        c.close();

        database.close();
    }

    /**
     * カテゴリを追加する
     */
    public void addCategory(String category, int paymethodID) {
        // IDの最大値を取得
        String query = "SELECT MAX(_id) FROM category;";
        database = this.connection.getWritableDatabase();
        Cursor c = database.rawQuery(query, null);
        c.moveToNext();
        int id = c.getInt(0) + 1;

        query = "INSERT INTO category VALUES('" + id + "','" + category + "','" + paymethodID
                + "','false');";
        database.execSQL(query);

        database.close();
    }

    /**
     * カテゴリを更新する
     * 
     * @param category
     *            カテゴリ名
     * @param paymentMethod
     *            支払方法
     */
    public void updateCategory(String category, String paymentMethod) {
        String sql = "UPDATE category SET defaultpay = '" + paymentMethod + "' WHERE name = '"
                + category + "'";
        database.execSQL(sql);
    }

    /**
     * すべてのカテゴリに対し、指定した支払い方法から指定した支払い方法に一括変換する
     */
    public void replaceCategoryPaymentMethodRelation(String beforePaymentMethod,
            String afterPaymentMethod) {
        String sql = "SELECT _id FROM paymethod WHERE name = '" + beforePaymentMethod + "'";
        database = this.connection.getWritableDatabase();
        Cursor c = database.rawQuery(sql, null);
        c.moveToNext();
        int beforePaymentId = c.getInt(0);

        sql = "SELECT _id FROM paymethod WHERE name = '" + afterPaymentMethod + "'";
        c = database.rawQuery(sql, null);
        c.moveToNext();
        int afterPaymentId = c.getInt(0);

        sql = "UPDATE category SET paymethod_id = '" + afterPaymentId + "' WHERE paymethod_id = '"
                + beforePaymentId + "'";
        database.execSQL(sql);

        c.close();

        database.close();
    }

    /**
     * 指定したカテゴリ名が存在するかどうか
     * 
     * @param category
     * @return
     */
    public boolean categoryExists(String category) {
        String query = "SELECT * FROM category WHERE name='" + category + "';";
        database = this.connection.getReadableDatabase();
        Cursor c = database.rawQuery(query, null);
        while (c.moveToNext()) {
            return true;
        }

        return false;
    }

    /**
     * 支払方法を書き込む
     * 
     * @param method
     * @param cashStorage
     */
    public void writePaymentMethod(String method, String cashStorage) {
        String sql = "SELECT name FROM category";
        database = this.connection.getWritableDatabase();
        Cursor c = database.rawQuery(sql, null);
        // DB内に存在するカテゴリなら「更新」，無ければ「追加」
        while (c.moveToNext()) {
            if (c.getString(0).equals(method)) {
                updatePaymentMethod(method, cashStorage);
                return;
            }
        }
        c.close();
        database.close();

        addPaymentMethod(method, cashStorage);
    }

    /**
     * 支払方法を追加する
     * 
     * @param method
     * @param cashStorage
     */
    public void addPaymentMethod(String method, String cashStorage) {
        // IDの最大値を取得
        String query = "SELECT MAX(_id) FROM paymethod;";
        database = this.connection.getWritableDatabase();
        Cursor c = database.rawQuery(query, null);
        c.moveToNext();
        int id = c.getInt(0) + 1;
        c.close();

        String sql = "INSERT INTO paymethod VALUES('" + id + "', '" + method + "','" + cashStorage
                + "','false');";
        database.execSQL(sql);
        database.close();
    }

    /**
     * 支払方法を更新する
     * 
     * @param method
     *            支払方法
     * @param cashStorage
     *            現金保管場所
     */
    public void updatePaymentMethod(String method, String cashStorage) {
        String sql = "UPDATE payment SET cash_storage = '" + cashStorage + "' WHERE name ='"
                + method + "'";
        database.execSQL(sql);
    }

    /**
     * すべての支払い方法に対し、指定した支払金保管場所から指定した支払金保管場所に一括変換する
     */
    public void replacePaymentMethodCashStorageRelation(String beforeCashStorage,
            String afterCashStorage) {
        String sql = "SELECT _id FROM cash_storage WHERE name = '" + beforeCashStorage + "'";
        database = this.connection.getWritableDatabase();
        Cursor c = database.rawQuery(sql, null);
        c.moveToNext();
        int beforeCashStorageId = c.getInt(0);

        sql = "SELECT _id FROM cash_storage WHERE name = '" + afterCashStorage + "'";
        c = database.rawQuery(sql, null);
        c.moveToNext();
        int afterCashStorageId = c.getInt(0);

        sql = "UPDATE paymethod SET cash_storage_id = '" + afterCashStorageId
                + "' WHERE cash_storage_id = '" + beforeCashStorageId + "'";
        try {
            database.execSQL(sql);
        } catch (Exception e) {
            String s = e.toString();
            int a = 1;
        }

        c.close();

        database.close();
    }

    /**
     * 指定した支払方法が存在するかどうか
     * 
     * @param method
     * @return
     */
    public boolean paymentExists(String method) {
        String query = "SELECT * FROM paymethod WHERE name = '" + method + "'";
        database = this.connection.getReadableDatabase();
        Cursor c = database.rawQuery(query, null);
        while (c.moveToNext()) {
            return true;
        }
        return false;
    }

    /**
     * 支払金保管場所を追加する
     * 
     * @param cashStorage
     */
    public void addCashStorage(String cashStorage) {
        // IDの最大値を取得
        String query = "SELECT MAX(_id) FROM cash_storage";
        database = this.connection.getWritableDatabase();
        Cursor c = database.rawQuery(query, null);
        c.moveToNext();
        int id = c.getInt(0) + 1;
        c.close();

        String sql = "INSERT INTO cash_storage VALUES('" + id + "', '" + cashStorage
                + "' ,'false');";
        database.execSQL(sql);
        database.close();
    }

    /**
     * 支払金保管場所を書き込む
     * 
     * @param cashStorage
     *            支払金保管場所
     */
    public void writeCashStorage(String cashStorage) {
        addCashStorage(cashStorage);
    }

    /**
     * オフラインモード時に「送信」ボタンを押すと、情報をDBに書き込む
     */
    public boolean writeStock(RowElement rowElement) {
        String q = "SELECT MAX(_id) FROM stock;";
        database = this.connection.getWritableDatabase();
        Cursor c = database.rawQuery(q, null);
        c.moveToNext();
        int id = c.getInt(0) + 1;
        c.close();

        HashMap<String, String> records = rowElement.getInsertHashMap();
        Set keys = records.keySet();
        Iterator<String> it = keys.iterator();
        String[] columns = { "日付", "種別", "金額", "支払金保管場所", "支払方法", "カテゴリ", "備考" };

        // クエリの構築
        String query = "INSERT INTO stock VALUES('" + id + "','";
        int i = 0;
        while (true) {
            String k = columns[i];
            it.next();
            query += records.get(k);
            if (it.hasNext()) {
                query += "','";
                i++;
            } else {
                query += "');";
                break;
            }
        }

        try {
            database.execSQL(query);
            database.close();
        } catch (Exception e) {
            String s = e.toString();
            System.out.println("");
            database.close();
            return false;
        }
        return true;
    }

    /**
     * 未送信のデータを全て取得する
     * 
     * @return
     */
    public List<RowElement> getStocks() {
        List<RowElement> result = new ArrayList<RowElement>();
        String query = "SELECT * FROM stock";
        database = this.connection.getWritableDatabase();
        Cursor c = database.rawQuery(query, null);
        while (c.moveToNext()) {
            String date = c.getString(1);
            String type = c.getString(2);
            String money = c.getString(3);
            String storage = c.getString(4);
            String pay = c.getString(5);
            String category = c.getString(6);
            String memo = c.getString(7);
            RowElement rElement = new RowElement();
            rElement.inputValue(ColumnName.date, date);
            rElement.inputValue(ColumnName.type, type);
            rElement.inputValue(ColumnName.money, money);
            rElement.inputValue(ColumnName.cashStorage, storage);
            rElement.inputValue(ColumnName.howtopay, pay);
            rElement.inputValue(ColumnName.category, category);
            rElement.inputValue(ColumnName.memo, memo);
            result.add(rElement);
        }

        return result;
    }

    /**
     * 指定したカテゴリを削除する
     * 
     * @param category
     *            カテゴリ名
     */
    public void removeCategory(String category) {
        boolean exists = categoryExists(category);
        if (exists == false)
            return;
        String query = "DELETE FROM category WHERE name = '" + category + "';";
        database = this.connection.getWritableDatabase();
        database.execSQL(query);
        database.close();

    }

    /**
     * 指定した支払方法を削除する
     * 
     * @param paymentMethod
     *            支払方法
     */
    public void removePaymentMethod(String paymentMethod) {
        boolean exists = paymentExists(paymentMethod);
        if (exists == false || paymentMethod.equals("現金"))
            return;

        for (Category category : readCategory()) {
            String target = readPaymethodFromCategory(category.getCategoryName());
            if (target.equals(paymentMethod)) {
                updateCategoryPayMethodRelation(category.getCategoryName(), "現金");
            }
        }
        String query = "DELETE FROM paymethod WHERE name = '" + paymentMethod + "';";
        database = this.connection.getWritableDatabase();
        database.execSQL(query);
        database.close();

    }

    /**
     * 指定した支払金保管場所を削除する
     * 
     * @param cashStorage
     */
    public void removeCashStorage(String cashStorage) {
        boolean exists = cashStorageExists(cashStorage);
        if (exists == false || cashStorage.equals("財布"))
            return;

        for (PaymentMethod pay : readPaymentMethod()) {
            if (cashStorage.equals(pay.getPaymentCashStorage())) {
                updatePaymentMethod(pay.getPaymentMethodName(), "財布");
            }
        }

        String query = "DELETE FROM cash_storage WHERE name = '" + cashStorage + "';";
        database = this.connection.getWritableDatabase();
        database.execSQL(query);
        database.close();

    }

    /**
     * stockテーブルが無ければ生成する とりあえず保留
     */
    public void createStockTable() {
        String query = "SELECT * FROM sqlite_sequence WHERE name='stock';";
        database = this.connection.getWritableDatabase();
        Cursor c = database.rawQuery(query, null);
        boolean ex = c.isNull(0);
        // バージョンアップによってstockテーブルが新設された。
        // もしもDB内にstockテーブルが存在しなければ新しく作る
        if (ex) {
            query = "CREATE TABLE stock(_id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT NOT NULL, type TEXT NOT NULL, money INTEGER, storage TEXT NOT NULL, paymethod TEXT NOT NULL, category TEXT NOT NULL, memo TEXT NOT NULL);";
            database.execSQL(query);
        }
    }

    /**
     * オフラインモードによって内部に保存された収支データがあるかどうか
     * 
     * @return
     */
    public boolean hasStock() {
        int count = countStock();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * オフラインモードによって内部に保存された収支データの数を数える
     * 
     * @return
     */
    public int countStock() {
        String query = "SELECT * FROM stock";
        database = this.connection.getWritableDatabase();
        Cursor c = database.rawQuery(query, null);
        int rawNum = c.getCount();
        c.close();
        database.close();

        return rawNum;
    }

    /**
     * オフラインモードによって内部に保存された収支データを全て削除する
     */
    public void removeAllStock() {
        String query = "DELETE FROM stock;";
        database = this.connection.getWritableDatabase();
        database.execSQL(query);
    }
}
