package jp.ne.nissing.util.householdbudget;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class DBConnection extends SQLiteOpenHelper {
    private SQLiteDatabase _db;

    /**
     * コンストラクタ
     * 
     * @param c
     *            自身のアプリを示す場合は getApplicationContet()
     * @param dbname
     *            データベースファイル名
     * @param version
     *            データベースのバージョン番号．作成するときに指定可能５
     */
    public DBConnection(Context c, String dbname, int version) {
        super(c, dbname, null, version);
    }

    /**
     * データベースが存在しないときはテーブルを生成
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // テーブル作成
        String createProfileTable = "CREATE TABLE profile( account_name TEXT NOT NULL, password TEXT NOT NULL, spreadsheet_name TEXT NOT NULL );";
        String createCategoryTable = "CREATE TABLE category( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, paymethod_id INTEGER, is_preset BOOLEAN, FOREIGN KEY(paymethod_id) REFERENCES payment(_id));";
        String createPayMethodTable = "CREATE TABLE paymethod(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, cash_storage_id INTEGER, is_preset BOOLEAN, FOREIGN KEY(cash_storage_id) REFERENCES cash_storage(_id));";
        String createCashStorageTable = "CREATE TABLE cash_storage( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, is_preset BOOLEAN);";
        String createStockTable = "CREATE TABLE stock(_id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT NOT NULL, type TEXT NOT NULL, money INTEGER, storage TEXT NOT NULL, paymethod TEXT NOT NULL, category TEXT NOT NULL, memo TEXT NOT NULL);";

        db.execSQL(createProfileTable);
        db.execSQL(createCashStorageTable);
        db.execSQL(createPayMethodTable);
        db.execSQL(createCategoryTable);
        db.execSQL(createStockTable);

        // 現金保管場所のプリセットを設定
        db.beginTransaction();
        try {
            String[] attributes = new String[] { "財布" };
            SQLiteStatement stmt = db
                    .compileStatement("INSERT INTO cash_storage VALUES(?, ?,'true');");
            for (int i = 0; i < attributes.length; i++) {
                stmt.bindLong(1, i);
                stmt.bindString(2, attributes[i]);

                stmt.executeInsert();
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            String s = e.toString();
        } finally {
            db.endTransaction();
        }

        // 支払方法のプリセットを設定
        db.beginTransaction();
        try {
            String[] pays = new String[] { "現金" };
            long[] payl = new long[] { 0 };
            SQLiteStatement stmt = db
                    .compileStatement("INSERT INTO paymethod VALUES(?, ?, ?, 'true');");
            for (int i = 0; i < pays.length; i++) {
                stmt.bindLong(1, i);
                stmt.bindString(2, pays[i]);
                stmt.bindLong(3, payl[i]);
                stmt.executeInsert();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        // カテゴリのプリセットを設定
        db.beginTransaction();
        try {
            String[] categorys = new String[] { "食費", "交通費", "医療費" };
            SQLiteStatement stmt = db
                    .compileStatement("INSERT INTO category VALUES(?, ?, '0', 'true');");
            for (int i = 0; i < categorys.length; i++) {
                stmt.bindLong(1, i);
                stmt.bindString(2, categorys[i]);
                stmt.executeInsert();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        this._db = db;
    }

    /**
     * データベースを取得
     * 
     * @return
     */
    public SQLiteDatabase getDatabase() {
        return this._db;
    }

    /**
     * テーブルのバージョンアップがあった場合
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 実装例
        // 現在のレコードを取得して，いったんメモリへ退避
        // テーブルの削除
        // 新しくテーブルを作り直して
        // メモリへ退避させたレコードを挿入する
    }
}
