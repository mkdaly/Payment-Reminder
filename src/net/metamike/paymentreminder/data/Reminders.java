package net.metamike.paymentreminder.data;

import java.util.Date;

import net.metamike.paymentreminder.data.PaymentDBAdapter.ReminderType;
import android.database.Cursor;

public final class Reminders {

	/**
	 * 
	 * All these methods are static to keep from creating
	 * a bunch of otherwise-unneeded objects since this class
	 * is only used to get data from the cursor.
	 */

	public static Integer getPaymentID(Cursor c) {
		return c.getInt(c.getColumnIndex(PaymentDBAdapter.KEY_PAYMENT_ID));
	}
	
	public static ReminderType getType(Cursor c) {
		int val = c.getInt(c.getColumnIndex(PaymentDBAdapter.KEY_TYPE));
		return ReminderType.values()[val];
	}
	
	public static Date getTime(Cursor c) {
		return new Date(c.getLong(c.getColumnIndex(PaymentDBAdapter.KEY_TIME)));
	}

	public static Long getTimeAsLong(Cursor c) {
		return c.getLong(c.getColumnIndex(PaymentDBAdapter.KEY_TIME));
	}
}
