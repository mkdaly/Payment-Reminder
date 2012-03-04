/**
 * 
 */
package net.metamike.paymentreminder.data;

import java.util.Date;

import android.database.Cursor;

/**
 * @author mdaly
 *
 */
public final class Payments {
	
	/**
	 * 
	 * All these methods are static to keep from creating
	 * a bunch of otherwise-unneeded objects since this class
	 * is only used to get data from the cursor.
	 */

	
	public static String getAccount(Cursor c) {
		return c.getString(c.getColumnIndex(PaymentDBAdapter.KEY_ACCOUNT));
	}
	
	public static Long getAmountDue(Cursor c) {
		int colIndex = c.getColumnIndex(PaymentDBAdapter.KEY_AMOUNT_DUE);
		//Default value is 0.
		return c.isNull(colIndex) ? 0L : c.getLong(colIndex); 
	}
	
	public static Long getAmountPaid(Cursor c) {
		int colIndex = c.getColumnIndex(PaymentDBAdapter.KEY_AMOUNT_PAID);
		//Default value is 0.
		return c.isNull(colIndex) ? 0L : c.getLong(colIndex); 
	}
	
	public static Date getDueDate(Cursor c) {
		int colIndex = c.getColumnIndex(PaymentDBAdapter.KEY_DATE_DUE);
		return c.isNull(colIndex) ? null : new Date( c.getLong(colIndex));
	}
	
	public static Long getDueDateAsLong(Cursor c) {
		int colIndex = c.getColumnIndex(PaymentDBAdapter.KEY_DATE_DUE);
		return c.isNull(colIndex) ? null : c.getLong(colIndex);
	}

	
	public static Date getTransferDate(Cursor c) {
		int colIndex = c.getColumnIndex(PaymentDBAdapter.KEY_DATE_TRANSFER);
		return c.isNull(colIndex) ? null : new Date( c.getLong(colIndex));
	}

	public static Long getTransferDateAsLong(Cursor c) {
		int colIndex = c.getColumnIndex(PaymentDBAdapter.KEY_DATE_TRANSFER);
		return c.isNull(colIndex) ? null : c.getLong(colIndex);
	}
	
	public static String getConfirmation(Cursor c) {
		int colIndex = c.getColumnIndex(PaymentDBAdapter.KEY_CONFIRMATION);
		return c.isNull(colIndex) ? "" : c.getString(colIndex);
	}
}
