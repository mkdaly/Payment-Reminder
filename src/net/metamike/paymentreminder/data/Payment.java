/**
 * 
 */
package net.metamike.paymentreminder.data;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.TimeFormatException;

/**
 * @author mdaly
 *
 */
public final class Payment {
	public static final Long NO_ID = Long.valueOf(0L);
	private Long recordID;
	
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
		recordID = NO_ID;
	}
	
	public Payment(Cursor c) {
		account = c.getString(c.getColumnIndex(PaymentDBAdapter.KEY_ACCOUNT));
		if (TextUtils.isEmpty(account))
			throw new IllegalArgumentException("The value for account is empty.");
		conf = c.getString(c.getColumnIndex(PaymentDBAdapter.KEY_CONFIRMATION));
		recordID = Long.valueOf(c.getLong(c.getColumnIndex(PaymentDBAdapter.KEY_ID)));
		
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
	
	public Payment(Bundle b){
		account = b.getString(PaymentDBAdapter.KEY_ACCOUNT);
		if (TextUtils.isEmpty(account))
			throw new IllegalArgumentException("The value for account is empty.");
		conf = b.getString(PaymentDBAdapter.KEY_CONFIRMATION);
		
		try {
			recordID = Long.valueOf(b.getString(PaymentDBAdapter.KEY_ID));
		} catch (NumberFormatException nfe) {
			recordID = NO_ID;
		}

		try {
			amountDue = Long.valueOf(b.getString(PaymentDBAdapter.KEY_AMOUNT_DUE));
		}  catch (Exception e) {
			//TODO: get more specific exception
			amountDue = null;
		}
		try {
			amountPaid = Long.valueOf(b.getString(PaymentDBAdapter.KEY_AMOUNT_PAID));
		} catch (Exception e) {
			//TODO: get more specific exception
			amountPaid = null;
		}

		try {
			String d = b.getString(PaymentDBAdapter.KEY_DATE_DUE);
			if (d != null) {
				dayDue = new Time();  
				dayDue.parse3339(d);
			}
		} catch (TimeFormatException tfe) {
			dayDue = null;
		}
		try {
			String d = b.getString(PaymentDBAdapter.KEY_DATE_TRANSFER);
			if (d != null) {
				dayXfer = new Time();
				dayXfer.parse3339(d);
			}
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
	
	Long getRecordID() {
		return recordID;
	}
	
	public Bundle generateBundle() {
		Bundle b = new Bundle();
		b.putString(PaymentDBAdapter.KEY_ACCOUNT, this.getAccount());
		b.putString(PaymentDBAdapter.KEY_AMOUNT_DUE, this.getAmountDue());
		b.putString(PaymentDBAdapter.KEY_DATE_DUE, this.getDueDate());
		b.putString(PaymentDBAdapter.KEY_AMOUNT_PAID, this.getAmountPaid());
		b.putString(PaymentDBAdapter.KEY_DATE_TRANSFER, this.getTransferDate());
		b.putString(PaymentDBAdapter.KEY_CONFIRMATION, this.getConfirmation());
		b.putString(PaymentDBAdapter.KEY_ID, this.getRecordID().toString());
		return b;
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
		return dayDue == null ? "" : dayDue.format3339(true);
		//return dayDue == null ? "" : DateUtils.formatDateTime(null, dayDue.toMillis(false), formatFlags);
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
		return dayXfer == null ? "" : dayXfer.format3339(true);
		//return dayXfer == null ? "" : DateUtils.formatDateTime(null, dayXfer.toMillis(false), formatFlags);
	}
	
	String getTransferDateForDB() {
		return dayXfer == null ? null : dayXfer.format3339(true);
	}
	
	public String getConfirmation() {
		return conf;
	}
	
}
