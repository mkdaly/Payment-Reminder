package net.metamike.paymentreminder.test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.metamike.paymentreminder.data.PaymentDBAdapter;
import net.metamike.paymentreminder.data.Payment;
import net.metamike.paymentreminder.test.mocks.NullCursor;
import net.metamike.paymentreminder.test.mocks.MockCursor;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.text.format.Time;
import android.util.Pair;

public class PaymentsTest extends AndroidTestCase {
	Cursor nullCursor;

	protected void setUp() throws Exception {
		super.setUp();
		nullCursor = new NullCursor();
	}

	
	public void testConstructors() {
		Pair<Object, String> exAccount;
		Pair<Object, String> exAmountDue;
		Pair<Object, String> exDateDue;
		Pair<Object, String> exAmountPaid;
		Pair<Object, String> exDatePaid;
		Pair<Object, String> exConf;
		Map<Integer, Object> values = new HashMap<Integer, Object>();
		
		Payment p;
		
		/** Test string constructors */
		try {
			p = new Payment((String)null);
			fail("No exception thrown for null account.");
		} catch (IllegalArgumentException e) {
			//pass
		}

		try {
			exAccount = new Pair<Object, String>("", "");
			p = new Payment(exAccount.second);
			fail("No exception thrown for empty account.");
		} catch (IllegalArgumentException e) {
			//pass
		}
		
		exAccount = new Pair<Object, String>("Test account.", "Test account.");
		exAmountDue = new Pair<Object, String>(null, "");
		exDateDue = new Pair<Object, String>(null, "");
		exAmountPaid = new Pair<Object, String>(null, "");
		exDatePaid = new Pair<Object, String>(null, "");
		exConf = new Pair<Object, String>("", "");
		
		p = new Payment((String)exAccount.first);
		assertEquals(exAccount.second, p.getAccount());
		assertEquals(exAmountDue.second, p.getAmountDue());
		assertEquals(exDateDue.second, p.getDueDate());
		assertEquals(exAmountPaid.second, p.getAmountPaid());
		assertEquals(exDatePaid.second, p.getTransferDate());
		assertEquals(exConf.second, p.getConfirmation());
		/** finished string constructors */
		
		exAccount = new Pair<Object, String>("Test account.", "Test account.");
		exAmountDue = new Pair<Object, String>(3L, "3");
		exDateDue = new Pair<Object, String>("2010-01-01", "Jan 1, 2010");
		exAmountPaid = new Pair<Object, String>(2L, "2");
		exDatePaid = new Pair<Object, String>("2011-02-01", "Feb 1, 2011");
		exConf = new Pair<Object, String>("yes", "yes");

		values.clear();
		values.put(KnownCursor.ACCOUNT, exAccount.first);
		values.put(KnownCursor.AMOUNT_DUE, exAmountDue.first);
		values.put(KnownCursor.DATE_DUE, exDateDue.first);
		values.put(KnownCursor.AMOUNT_PAID, exAmountPaid.first);
		values.put(KnownCursor.DATE_XFER, exDatePaid.first);
		values.put(KnownCursor.CONF, exConf.first);
		
		p = new Payment(new KnownCursor(values));
		assertEquals(exAccount.second, p.getAccount());
		assertEquals(exAmountDue.second, p.getAmountDue());
		assertEquals(exDateDue.second, p.getDueDate());
		assertEquals(exAmountPaid.second, p.getAmountPaid());
		assertEquals(exDatePaid.second, p.getTransferDate());
		assertEquals(exConf.second, p.getConfirmation());
		
		/** Test bad date form */
		exAccount = new Pair<Object, String>("Test account.", "Test account.");
		exAmountDue = new Pair<Object, String>(3L, "3");
		exDateDue = new Pair<Object, String>("200-01-01", "");
		exAmountPaid = new Pair<Object, String>(2L, "2");
		exDatePaid = new Pair<Object, String>("2011-02-01", "Feb 1, 2011");
		exConf = new Pair<Object, String>("yes", "yes");

		values.clear();
		values.put(KnownCursor.ACCOUNT, exAccount.first);
		values.put(KnownCursor.AMOUNT_DUE, exAmountDue.first);
		values.put(KnownCursor.DATE_DUE, exDateDue.first);
		values.put(KnownCursor.AMOUNT_PAID, exAmountPaid.first);
		values.put(KnownCursor.DATE_XFER, exDatePaid.first);
		values.put(KnownCursor.CONF, exConf.first);

		
		p = new Payment(new KnownCursor("Test", 3L, "2010-01-02",
				2L, "2010-01-01", "yes"));
		assertEquals("Test", p.getAccount());
		assertEquals("3", p.getAmountDue());
		assertEquals("Jan 2, 2010", p.getDueDate());
		assertEquals("2", p.getAmountPaid());
		assertEquals("Jan 1, 2010", p.getTransferDate());
		assertEquals("yes", p.getConfirmation());		
		
	}
	
	
	private class KnownCursor extends MockCursor {
		static final int ACCOUNT = 1;
		static final int AMOUNT_DUE = 2;
		static final int DATE_DUE = 3;
		static final int AMOUNT_PAID = 4;
		static final int DATE_XFER = 5;
		static final int CONF = 6;
		
		private Object account;
		private Object amountDue;
		private Object dateDue;
		private Object amountPaid;
		private Object dateXfer;
		private Object conf;

		public KnownCursor(Object account, Object amountDue, Object dateDue, Object amountPaid, Object dateXfer, Object conf) {
			this.account = account;
			this.amountDue = amountDue;
			this.dateDue = dateDue;
			this.amountPaid = amountPaid;
			this.dateXfer = dateXfer;
			this.conf = conf;
		}
		
		public KnownCursor(Map<Integer, Object> vals) {
			this.account = vals.get(ACCOUNT);
			this.amountDue = vals.get(AMOUNT_DUE);
			this.dateDue = vals.get(DATE_DUE);
			this.amountPaid = vals.get(AMOUNT_PAID);
			this.dateXfer = vals.get(DATE_XFER);
			this.conf = vals.get(CONF);
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
			switch(columnIndex) {
			case ACCOUNT:
				return account == null;
			case AMOUNT_DUE:
				return amountDue == null;
			case AMOUNT_PAID:
				return amountDue == null;
			case DATE_DUE:
				return dateDue == null;
			case DATE_XFER:
				return dateXfer == null;
			case CONF:
				return conf == null;
			default:
				return super.isNull(columnIndex);
			}
		}

		@Override
		public String getString(int columnIndex) {
			switch(columnIndex) {
			case ACCOUNT:
				return (String)account;
			case AMOUNT_DUE:
				return (String)amountDue;
			case AMOUNT_PAID:
				return (String)amountDue;
			case DATE_DUE:
				return (String)dateDue;
			case DATE_XFER:
				return (String)dateXfer;
			case CONF:
				return (String)conf;
			default:
				return super.getString(columnIndex);
			}
		}
		
		@Override
		public long getLong(int columnIndex) {
			switch (columnIndex) {
				case AMOUNT_DUE:
					return (Long)amountDue;
				case AMOUNT_PAID:
					return (Long)amountPaid;
				default:
					return super.getLong(columnIndex);
			}
		}
	}
}
