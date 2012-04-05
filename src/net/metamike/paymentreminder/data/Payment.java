/**
 * 
 */
package net.metamike.paymentreminder.data;

import android.database.Cursor;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.TimeFormatException;

/**
 * @author mdaly
 *
 */
public final class Payment {
	// context.getResources().getConfiguration().locale
	private String account;
	private String conf;

	/* If the following fields are null, it is assumed that
	 * in the database, they are not set. 
	 */
	private Long amountDue;
	private Long amountPaid;
	
	private Time dayDue;
	private Time dayXfer;

	
	private static final int formatFlags = DateUtils.FORMAT_ABBREV_ALL;
		
	public Payment(String account) {
		if (TextUtils.isEmpty(account))
			throw new IllegalArgumentException("The value for account is empty.");
		this.account = account;
		conf = "";
		amountDue = null;
		amountPaid = null;
		dayDue = null;		
		dayXfer = null;
	}
	
	public Payment(Cursor c) {
		account = c.getString(c.getColumnIndex(PaymentDBAdapter.KEY_ACCOUNT));
		if (TextUtils.isEmpty(account))
			throw new IllegalArgumentException("The value for account is empty.");
		conf = c.getString(c.getColumnIndex(PaymentDBAdapter.KEY_CONFIRMATION));
		
		amountDue = Payment.getLong(c, c.getColumnIndex(PaymentDBAdapter.KEY_AMOUNT_DUE));
		amountPaid = Payment.getLong(c, c.getColumnIndex(PaymentDBAdapter.KEY_AMOUNT_PAID));

		try {
			dayDue = new Time();  
			dayDue.parse3339(Payment.getString(c, c.getColumnIndex(PaymentDBAdapter.KEY_DATE_DUE)));
		} catch (TimeFormatException tfe) {
			dayDue = null;
		}
		try {
			dayXfer = new Time();
			dayXfer.parse3339(Payment.getString(c, c.getColumnIndex(PaymentDBAdapter.KEY_DATE_TRANSFER)));
		} catch (TimeFormatException tfe) {
			dayXfer = null;
		}
	}
	
	private static Long getLong(Cursor c, int column) {
		return (c.isNull(column)) ? null : Long.valueOf(c.getLong(column));
	}
		
	private static String getString(Cursor c, int column) {		
		return c.isNull(column) ? "" : c.getString(column);
	}
	
	public String getAccount() {
		return account;
	}
	
	public String getAmountDue() {
		return amountDue == null ? "" : amountDue.toString();
	}
	
	/**
	 * A package-private method for use in {@link PaymentDBAdapter}.
	 * 
	 * @return the amount due
	 */
	Long getAmountDueForDB() {
		return amountDue;
	}
	
	public String getAmountPaid() {
		return amountPaid == null ? "" : amountPaid.toString();
	}

	/**
	 * A package-private method for use in {@link PaymentDBAdapter}.
	 * 
	 * @return the amount to transfer
	 */
	Long getAmountPaidForDB() {
		return amountPaid;
	}

	/**
	 *  A string suitable for display to the user.
	 *  
	 * @return if the due date is not null, a string representation of the
	 *  date formatted to the locale's abbreviated
	 *  {@link DateUtils.FORMAT_ABBREV_ALL} format, else the emppty string.  
	 */
	public String getDueDate() {
		return dayDue == null ? "" : DateUtils.formatDateTime(null, dayDue.toMillis(false), formatFlags);
	}
	
	String getDueDateForDB() {
		return dayDue == null ? null : dayDue.format3339(true);
	}

	/**
	 *  A string suitable for display to the user.
	 *  
	 * @return if the due date is not null, a string representation of the
	 *  date formatted to the locale's abbreviated
	 *  {@link DateUtils.FORMAT_ABBREV_ALL} format, else the empty string.  
	 */
	public String getTransferDate() {
		return dayXfer == null ? "" : DateUtils.formatDateTime(null, dayXfer.toMillis(false), formatFlags);
	}
	
	String getTransferDateForDB() {
		return dayXfer == null ? null : dayXfer.format3339(true);
	}
	
	public String getConfirmation() {
		return conf;
	}
	
}
