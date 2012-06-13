package net.metamike.paymentreminder.data;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;

import net.metamike.paymentreminder.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.util.TimeFormatException;

public final class PaymentDBAdapter {
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

	public static final String dateFormatString = "%Y%m%d";
	
	private static final String PAYMENTS_CREATE = 
			"CREATE TABLE " + PAYMENTS_TABLE + " (" +
			KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			KEY_ACCOUNT + " TEXT NOT NULL, " +
			KEY_AMOUNT_DUE + " INTEGER, " +
			KEY_AMOUNT_PAID + " INTEGER, " +
			KEY_DATE_DUE + " TEXT DEFAULT '', " +
			KEY_DATE_TRANSFER + " TEXT DEFAULT '', " +
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
	private NumberFormat formatter;
	private Context context;
	
	public PaymentDBAdapter(Context context) {
		dbHelper = new Helper(context);
		formatter = NumberFormat.getInstance();
		this.context = context;
		if (formatter instanceof DecimalFormat) {
			((DecimalFormat)formatter).setParseBigDecimal(true);
		}
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
	/*
	public boolean insertPayment(String account, Long amt_due, Long dt_due, Long amt_paid, Long dt_xfer, String conf) {
		if (TextUtils.isEmpty(account))
			return false; //Account needs to be *something*
		ContentValues values = new ContentValues();
		values.put(KEY_ACCOUNT, account);
		if (amt_due != null)
				values.put(KEY_AMOUNT_DUE, amt_due);
		if (dt_due != null) 
			values.put(KEY_DATE_DUE, dt_due);
		if (amt_paid != null)
			values.put(KEY_AMOUNT_PAID, amt_paid);
		if (dt_xfer != null)
			values.put(KEY_DATE_TRANSFER, dt_xfer);
		if (conf != null)
			values.put(KEY_CONFIRMATION, conf);
		return database.insert(PAYMENTS_TABLE, null, values) > 0;
	}
	*/
	
	public boolean deletePayment(long id) {
		return this.deletePayment(Long.toString(id));
		
	}
	
	public boolean deletePayment(String id) {
		return (database.delete(PAYMENTS_TABLE, KEY_ID + " = ?", new String[]{id})) == 1;
	}

	public boolean insertPayment(Payment p) {
		return insertPayment(p.getRecordID(), p.getAccount(), p.getAmountDueForDB(), p.getDueDateForDB(),
				p.getAmountPaidForDB(), p.getTransferDateForDB(), p.getConfirmation());		
	}

	boolean insertPayment(Long _id, String account, Long amt_due, String dt_due, Long amt_paid, String dt_xfer, String conf) {
		if (TextUtils.isEmpty(account))
			return false; //Account needs to be *something*
		ContentValues values = new ContentValues();
		values.put(KEY_ACCOUNT, account);
		if (amt_due != null)
			values.put(KEY_AMOUNT_DUE, amt_due);
		if (dt_due != null) 
			values.put(KEY_DATE_DUE, dt_due);
		if (amt_paid != null)
			values.put(KEY_AMOUNT_PAID, amt_paid);
		if (dt_xfer != null)
			values.put(KEY_DATE_TRANSFER, dt_xfer);
		if (conf != null)
			values.put(KEY_CONFIRMATION, conf);
		if (_id == Payment.NO_ID) {
			return database.insert(PAYMENTS_TABLE, null, values) > 0;
		} else {
			return database.update(PAYMENTS_TABLE, values, KEY_ID + "= ?", new String[]{_id.toString()}) > 0;
		}
	}

	public boolean insertPayment(String account) {
		if (TextUtils.isEmpty(account))
			return false; //Account needs to be *something*
		ContentValues values = new ContentValues();
		values.put(KEY_ACCOUNT, account);
		return database.insert(PAYMENTS_TABLE, null, values) > 0;
	}

	//TODO: Provide feed back to user if nulls are sent
	public boolean insertReminder(Integer _id, ReminderType type, String time) {
		ContentValues values = new ContentValues();
		values.put(KEY_PAYMENT_ID, _id);
		if (type != null)
			values.put(KEY_TYPE, type.ordinal());
		if (time != null)
			values.put(KEY_TIME, time);
		return database.insert(REMINDERS_TABLE, null, values) > 0;
	}
	
	public Cursor getAllPayments() {
		return database.query(PAYMENTS_TABLE, null, null, null, null, null, KEY_DATE_DUE);
	}

	public Long convertStringToLongMill(String src) throws NumberFormatException {
		if (TextUtils.isEmpty(src))
			return null;
		try {
			Number val = formatter.parse(src);
			if (val instanceof BigDecimal) {
				/*TODO: Probably should set the decimal places
				 * as a setting.
				 */
				if (((BigDecimal) val).scale() > 2) {
					//Not dealing with mills as input.
					throw new NumberFormatException(context.getString(R.string.more_than_cents));
				}
				return ((BigDecimal)val).movePointRight(3).longValue();
			}
		} catch (java.text.ParseException p) {
			throw new NumberFormatException(p.getLocalizedMessage());
		}
		//TODO: Handle cases when src isn't a nice number
		return null;
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
