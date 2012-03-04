package net.metamike.paymentreminder.test;

import java.util.Date;

import net.metamike.paymentreminder.data.PaymentDBAdapter;
import net.metamike.paymentreminder.data.Payments;
import android.database.Cursor;
import android.test.AndroidTestCase;

public class PaymentsTest extends AndroidTestCase {
	Cursor nullCursor;

	protected void setUp() throws Exception {
		super.setUp();
		nullCursor = new NullCursor();
	}

	
	public void testGetAccount() {
		String ex = "account";
		Cursor knownCursor = new KnownCursor(ex);
		assertEquals(ex, Payments.getAccount(knownCursor));
	}
	

	public void testGetAmountDue() {
		assertEquals((Long)0L, Payments.getAmountDue(nullCursor));
		
		Long ex = 50L;
		Cursor knownCursor = new KnownCursor(ex);
		assertEquals(ex, Payments.getAmountDue(knownCursor));
	}

	public void testGetAmountPaid() {
		assertEquals((Long)0L, Payments.getAmountPaid(nullCursor));
		
		Long ex = 100L;
		Cursor knownCursor = new KnownCursor(ex);
		assertEquals(ex, Payments.getAmountPaid(knownCursor));
	}

	public void testGetDueDate() {
		assertNull(Payments.getDueDate(nullCursor));
		
		Date ex = new Date();
		Cursor knownCursor = new KnownCursor(ex.getTime());
		assertEquals(ex, Payments.getDueDate(knownCursor));
	}

	public void testGetDueDateAsLong() {
		assertNull(Payments.getDueDateAsLong(nullCursor));
		
		Date ex = new Date();
		Cursor knownCursor = new KnownCursor(ex.getTime());
		assertEquals((Long)ex.getTime(), Payments.getDueDateAsLong(knownCursor));

	}

	public void testGetTransferDate() {
		assertNull(Payments.getTransferDate(nullCursor));
		
		Date ex = new Date();
		Cursor knownCursor = new KnownCursor(ex.getTime());
		assertEquals(ex, Payments.getTransferDate(knownCursor));

	}

	public void testGetTransferDateAsLong() {
		assertNull(Payments.getTransferDateAsLong(nullCursor));

		Date ex = new Date();
		Cursor knownCursor = new KnownCursor(ex.getTime());
		assertEquals((Long)ex.getTime(), Payments.getTransferDateAsLong(knownCursor));
		
	}

	public void testGetConfirmation() {
		assertEquals("", Payments.getConfirmation(nullCursor));
		
		String ex = "conf";
		Cursor knownCursor = new KnownCursor(ex);
		assertEquals(ex, Payments.getConfirmation(knownCursor));
	}

	
	private class KnownCursor extends MockCursor {
		private final int ACCOUNT = 1;
		private final int AMOUNT_DUE = 2;
		private final int AMOUNT_PAID = 3;
		private final int DATE_DUE = 4;
		private final int DATE_XFER = 5;
		private final int CONF = 6;

		private long longValue = -1;
		private String stringValue = "";

		public KnownCursor(long longValue) {
			this.longValue = longValue;
		}

		public KnownCursor(String stringValue) {
			this.stringValue = stringValue;
		}

		@Override
		public int getColumnIndex(String columnName) {
			if (PaymentDBAdapter.KEY_ACCOUNT.equals(columnName)) {
				return ACCOUNT;
			} else if (PaymentDBAdapter.KEY_AMOUNT_DUE.equals(columnName)) {
				return AMOUNT_DUE;
			} else if (PaymentDBAdapter.KEY_AMOUNT_PAID.equals(columnName)) {
				return AMOUNT_PAID;
			} else if (PaymentDBAdapter.KEY_DATE_DUE.equals(columnName)) {
				return DATE_DUE;
			} else if (PaymentDBAdapter.KEY_DATE_TRANSFER.equals(columnName)) {
				return DATE_XFER;
			} else if (PaymentDBAdapter.KEY_CONFIRMATION.equals(columnName)) {
				return CONF;
			} else {
				return super.getColumnIndex(columnName);
			}
		}

		@Override
		public boolean isNull(int columnIndex) {
			return false;
		}

		@Override
		public long getLong(int columnIndex) {
			switch (columnIndex) {
				case AMOUNT_DUE:
				case AMOUNT_PAID:
				case DATE_DUE:
				case DATE_XFER:
					return longValue;
				default:
					return super.getLong(columnIndex);
			}
		}

		@Override
		public String getString(int columnIndex) {
			switch (columnIndex) {
				case ACCOUNT:
				case CONF:
					return stringValue;
				default:
					return super.getString(columnIndex);
			}
		}

	}
}
