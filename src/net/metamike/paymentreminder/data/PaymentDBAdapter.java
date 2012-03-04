package net.metamike.paymentreminder.data;

import java.math.BigDecimal;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PaymentDBAdapter {
	private static final String TAG = "PaymentDBAdaper";
	
	private static final String DATABASE_NAME = "paymentreminder.db";
	static final String PAYMENTS_TABLE = "payments";
	static final String REMINDERS_TABLE = "reminders";
	private static final int DATABASE_VERSION = 1;
	
	//Columns
	public static final String KEY_ID = "_id";
	public static final String KEY_ACCOUNT = "account";
	public static final String KEY_AMOUNT_DUE = "amount_due";
	public static final String KEY_AMOUNT_PAID = "amount_paid";
	public static final String KEY_DATE_DUE = "due_date";
	public static final String KEY_DATE_TRANSFER = "xfer_date";
	public static final String KEY_CONFIRMATION = "confirmation";
	public static final String KEY_PAYMENT_ID = "payment_id";
	public static final String KEY_TYPE = "type";
	public static final String KEY_TIME = "time";
	
	private static final String PAYMENTS_CREATE = 
			"CREATE TABLE " + PAYMENTS_TABLE + " (" +
			KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			KEY_ACCOUNT + " TEXT NOT NULL, " +
			KEY_AMOUNT_DUE + " INTEGER DEFAULT 0, " +
			KEY_AMOUNT_PAID + " INTEGER DEFAULT 0, " +
			KEY_DATE_DUE + " DATE, " +
			KEY_DATE_TRANSFER + " DATE, " +
			KEY_CONFIRMATION + " TEXT NOT NULL DEFAULT '');";

	private static final String REMINDERS_CREATE = 
			"CREATE TABLE " + REMINDERS_TABLE + "(" +
			KEY_ID + " INTEGER PRIMARY KEY, " +
			KEY_PAYMENT_ID + " INTEGER REFERENCES " + 
				PAYMENTS_TABLE + ", " +
			KEY_TYPE + " TEXT NOT NULL, " +
			KEY_TIME + " DATE NOT NULL);";
	
	private SQLiteDatabase database;
	private Helper dbHelper;
	
	public PaymentDBAdapter(Context context) {
		dbHelper = new Helper(context);
	}
	
	//For testing-purposes only!
	public SQLiteDatabase getDatabase() {
		return database;
	}
	
	public PaymentDBAdapter open() throws SQLException  {
		try {
			database = dbHelper.getWritableDatabase();
		} catch (SQLiteException sle) {
			Log.w(TAG, sle.getMessage());
			database = dbHelper.getReadableDatabase();
		}
		return this;
	}
	
	public void close() {
		database.close();
	}
	
	//TODO: Provide feed back if account is null
	public boolean insertPayment(String account, BigDecimal amt_due, Date dt_due, BigDecimal amt_paid, Date dt_xfer, String conf) {
		ContentValues values = new ContentValues();
		values.put(KEY_ACCOUNT, account);
		if (amt_due != null)
				values.put(KEY_AMOUNT_DUE, convertBigDecimal(amt_due));
		if (dt_due != null) 
			values.put(KEY_DATE_DUE, dt_due.getTime());
		if (amt_paid != null)
			values.put(KEY_AMOUNT_PAID, convertBigDecimal(amt_paid));
		if (dt_xfer != null)
			values.put(KEY_DATE_TRANSFER, dt_xfer.getTime());
		if (conf != null)
			values.put(KEY_CONFIRMATION, conf);
		return database.insert(PAYMENTS_TABLE, null, values) > 0;
	}

	//TODO: Test
	public boolean insertPayment(String account) {
		ContentValues values = new ContentValues();
		values.put(KEY_ACCOUNT, account);
		return database.insert(PAYMENTS_TABLE, null, values) > 0;
	}

	//TODO: Provide feed back to user if nulls are sent
	public boolean insertReminder(Integer _id, ReminderType type, Date time) {
		ContentValues values = new ContentValues();
		values.put(KEY_PAYMENT_ID, _id);
		if (type != null)
			values.put(KEY_TYPE, type.ordinal());
		if (time != null)
			values.put(KEY_TIME, time.getTime());
		return database.insert(REMINDERS_TABLE, null, values) > 0;
	}

	public Long convertBigDecimal(BigDecimal src) {
		return src == null ? null : src.movePointRight(3).longValueExact();
	}
	
	private static class Helper extends SQLiteOpenHelper {
		public Helper(Context c) {
			super(c,DATABASE_NAME,null,DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(PAYMENTS_CREATE);
			db.execSQL(REMINDERS_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public enum ReminderType {
		PAY,
		TRANSFER;
	}
}
