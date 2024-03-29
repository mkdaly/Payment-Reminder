package net.metamike.paymentreminder.test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.metamike.paymentreminder.data.PaymentDBAdapter;
import net.metamike.paymentreminder.data.Payment;
import net.metamike.paymentreminder.test.PaymentsTest.KnownCursor;
import net.metamike.paymentreminder.test.mocks.NullCursor;
import net.metamike.paymentreminder.test.mocks.MockCursor;
import android.database.Cursor;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.text.format.Time;
import android.util.Pair;

public class PaymentsTest extends AndroidTestCase {
	private Pair<Object, String> exAccount;
	private Pair<Object, String> exAmountDue;
	private Pair<Object, String> exDateDue;
	private Pair<Object, String> exAmountPaid;
	private Pair<Object, String> exDatePaid;
	private Pair<Object, String> exConf;
	private Pair<Object, String> exID;
	private Map<Integer, Object> values = new HashMap<Integer, Object>();
	
	private Payment p;


	protected void setUp() throws Exception {
		super.setUp();
	}

	
	public void testStringConstructor() {

		/** Test bad account name */
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
		
		/** Test string constructor */
		exAccount = new Pair<Object, String>("Test account.", "Test account.");
		exAmountDue = new Pair<Object, String>(null, "");
		exDateDue = new Pair<Object, String>(null, "");
		exAmountPaid = new Pair<Object, String>(null, "");
		exDatePaid = new Pair<Object, String>(null, "");
		exConf = new Pair<Object, String>("", "");
		exID = new Pair<Object, String>(Payment.NO_ID.toString(), Payment.NO_ID.toString());
		
		p = new Payment((String)exAccount.first);
		Bundle b = p.generateBundle();
		assertEquals(exAccount.second, b.getString(PaymentDBAdapter.KEY_ACCOUNT));
		assertEquals(exAmountDue.second, b.getString(PaymentDBAdapter.KEY_AMOUNT_DUE));
		assertEquals(exDateDue.second, b.getString(PaymentDBAdapter.KEY_DATE_DUE));
		assertEquals(exAmountPaid.second, b.getString(PaymentDBAdapter.KEY_AMOUNT_PAID));
		assertEquals(exDatePaid.second, b.getString(PaymentDBAdapter.KEY_DATE_TRANSFER));
		assertEquals(exConf.second, b.getString(PaymentDBAdapter.KEY_CONFIRMATION));
		assertEquals(exID.second, b.getString(PaymentDBAdapter.KEY_ID));
		/** finished string constructors */
	}
	
	public void testCursorConstructor() {
		values.put(KnownCursor.ACCOUNT, null);
		values.put(KnownCursor.AMOUNT_DUE, null);
		values.put(KnownCursor.DATE_DUE, null);
		values.put(KnownCursor.AMOUNT_PAID, null);
		values.put(KnownCursor.DATE_XFER, null);
		values.put(KnownCursor.CONF, null);
		values.put(KnownCursor.ID, null);

		/* Test null account */
		try {
			p = new Payment(new KnownCursor(values));
			fail("No exception thrown for null account.");
		} catch (IllegalArgumentException e) {
			//pass
		}
		
		/* Good values */
		exAccount = new Pair<Object, String>("Test account.", "Test account.");
		exAmountDue = new Pair<Object, String>(3L, "3");
		exDateDue = new Pair<Object, String>("2010-01-01", "2010-01-01");
		exAmountPaid = new Pair<Object, String>(2L, "2");
		exDatePaid = new Pair<Object, String>("2011-02-01", "2011-02-01");
		exConf = new Pair<Object, String>("yes", "yes");
		exID = new Pair<Object, String>(Long.valueOf(1), "1");

		values.clear();
		values.put(KnownCursor.ACCOUNT, exAccount.first);
		values.put(KnownCursor.AMOUNT_DUE, exAmountDue.first);
		values.put(KnownCursor.DATE_DUE, exDateDue.first);
		values.put(KnownCursor.AMOUNT_PAID, exAmountPaid.first);
		values.put(KnownCursor.DATE_XFER, exDatePaid.first);
		values.put(KnownCursor.CONF, exConf.first);
		values.put(KnownCursor.ID, exID.first);
		
		p = new Payment(new KnownCursor(values));
		Bundle b = p.generateBundle();		
		assertEquals(exAccount.second, b.getString(PaymentDBAdapter.KEY_ACCOUNT));
		assertEquals(exAmountDue.second, b.getString(PaymentDBAdapter.KEY_AMOUNT_DUE));
		assertEquals(exDateDue.second, b.getString(PaymentDBAdapter.KEY_DATE_DUE));
		assertEquals(exAmountPaid.second, b.getString(PaymentDBAdapter.KEY_AMOUNT_PAID));
		assertEquals(exDatePaid.second, b.getString(PaymentDBAdapter.KEY_DATE_TRANSFER));
		assertEquals(exConf.second, b.getString(PaymentDBAdapter.KEY_CONFIRMATION));
		assertEquals(exID.second, b.getString(PaymentDBAdapter.KEY_ID));
		
		
		/** Test bad date form */
		exAccount = new Pair<Object, String>("Test account.", "Test account.");
		exAmountDue = new Pair<Object, String>(3L, "3");
		exDateDue = new Pair<Object, String>("200-01-01", "");
		exAmountPaid = new Pair<Object, String>(2L, "2");
		exDatePaid = new Pair<Object, String>("2011-02-01", "2011-02-01");
		exConf = new Pair<Object, String>("yes", "yes");
		exID = new Pair<Object, String>(Long.valueOf(1), "1");

		values.clear();
		values.put(KnownCursor.ACCOUNT, exAccount.first);
		values.put(KnownCursor.AMOUNT_DUE, exAmountDue.first);
		values.put(KnownCursor.DATE_DUE, exDateDue.first);
		values.put(KnownCursor.AMOUNT_PAID, exAmountPaid.first);
		values.put(KnownCursor.DATE_XFER, exDatePaid.first);
		values.put(KnownCursor.CONF, exConf.first);
		values.put(KnownCursor.ID, exID.first);
		p = new Payment(new KnownCursor(values));
		b = p.generateBundle();
		assertEquals(exAccount.second, b.getString(PaymentDBAdapter.KEY_ACCOUNT));
		assertEquals(exAmountDue.second, b.getString(PaymentDBAdapter.KEY_AMOUNT_DUE));
		assertEquals(exDateDue.second, b.getString(PaymentDBAdapter.KEY_DATE_DUE));
		assertEquals(exAmountPaid.second, b.getString(PaymentDBAdapter.KEY_AMOUNT_PAID));
		assertEquals(exDatePaid.second, b.getString(PaymentDBAdapter.KEY_DATE_TRANSFER));
		assertEquals(exConf.second, b.getString(PaymentDBAdapter.KEY_CONFIRMATION));
		assertEquals(exID.second, b.getString(PaymentDBAdapter.KEY_ID));
		
		p = new Payment(new KnownCursor("Test", 3L, "2010-01-02",
				2L, "2010-01-01", "yes", 1L));
		b = p.generateBundle();
		assertEquals("Test", b.getString(PaymentDBAdapter.KEY_ACCOUNT));
		assertEquals("3", b.getString(PaymentDBAdapter.KEY_AMOUNT_DUE));
		assertEquals("2010-01-02", b.getString(PaymentDBAdapter.KEY_DATE_DUE));
		assertEquals("2", b.getString(PaymentDBAdapter.KEY_AMOUNT_PAID));
		assertEquals("2010-01-01", b.getString(PaymentDBAdapter.KEY_DATE_TRANSFER));
		assertEquals("yes", b.getString(PaymentDBAdapter.KEY_CONFIRMATION));
		assertEquals("1", b.getString(PaymentDBAdapter.KEY_ID));		
	}
	
	public void testBundleConstructor() {
		Bundle  b = new Bundle();
		b.putString(PaymentDBAdapter.KEY_ACCOUNT, null);
		b.putString(PaymentDBAdapter.KEY_AMOUNT_DUE, null);
		b.putString(PaymentDBAdapter.KEY_DATE_DUE, null);
		b.putString(PaymentDBAdapter.KEY_AMOUNT_PAID, null);
		b.putString(PaymentDBAdapter.KEY_DATE_TRANSFER, null);
		b.putString(PaymentDBAdapter.KEY_CONFIRMATION, null);
		b.putString(PaymentDBAdapter.KEY_ID, null);

		/* Test null account */
		try {
			p = new Payment(b);
			fail("No exception thrown for null account.");
		} catch (IllegalArgumentException e) {
			//pass
		}
		
		/* Good values */
		exAccount = new Pair<Object, String>("Test account.", "Test account.");
		exAmountDue = new Pair<Object, String>("3", "3");
		exDateDue = new Pair<Object, String>("2010-01-01", "2010-01-01");
		exAmountPaid = new Pair<Object, String>("2", "2");
		exDatePaid = new Pair<Object, String>("2011-02-01", "2011-02-01");
		exConf = new Pair<Object, String>("yes", "yes");
		exID = new Pair<Object, String>("2", "2");

		b = new Bundle();
		b.putString(PaymentDBAdapter.KEY_ACCOUNT, (String)exAccount.first);
		b.putString(PaymentDBAdapter.KEY_AMOUNT_DUE, (String)exAmountDue.first);
		b.putString(PaymentDBAdapter.KEY_DATE_DUE, (String)exDateDue.first);
		b.putString(PaymentDBAdapter.KEY_AMOUNT_PAID, (String)exAmountPaid.first);
		b.putString(PaymentDBAdapter.KEY_DATE_TRANSFER, (String)exDatePaid.first);
		b.putString(PaymentDBAdapter.KEY_CONFIRMATION, (String)exConf.first);
		b.putString(PaymentDBAdapter.KEY_ID, (String)exID.first);
		
		p = new Payment(b);
		b = p.generateBundle();
		assertEquals(exAccount.second, b.getString(PaymentDBAdapter.KEY_ACCOUNT));
		assertEquals(exAmountDue.second, b.getString(PaymentDBAdapter.KEY_AMOUNT_DUE));
		assertEquals(exDateDue.second, b.getString(PaymentDBAdapter.KEY_DATE_DUE));
		assertEquals(exAmountPaid.second, b.getString(PaymentDBAdapter.KEY_AMOUNT_PAID));
		assertEquals(exDatePaid.second, b.getString(PaymentDBAdapter.KEY_DATE_TRANSFER));
		assertEquals(exConf.second, b.getString(PaymentDBAdapter.KEY_CONFIRMATION));
		assertEquals(exID.second, b.getString(PaymentDBAdapter.KEY_ID));		
		
		/** Test bad date form */
		exAccount = new Pair<Object, String>("Test account.", "Test account.");
		exAmountDue = new Pair<Object, String>("3", "3");
		exDateDue = new Pair<Object, String>("200-01-01", "");
		exAmountPaid = new Pair<Object, String>("2", "2");
		exDatePaid = new Pair<Object, String>("2011-02-01", "2011-02-01");
		exConf = new Pair<Object, String>("yes", "yes");
		exID = new Pair<Object, String>("2", "2");

		b = new Bundle();
		b.putString(PaymentDBAdapter.KEY_ACCOUNT, (String)exAccount.first);
		b.putString(PaymentDBAdapter.KEY_AMOUNT_DUE, (String)exAmountDue.first);
		b.putString(PaymentDBAdapter.KEY_DATE_DUE, (String)exDateDue.first);
		b.putString(PaymentDBAdapter.KEY_AMOUNT_PAID, (String)exAmountPaid.first);
		b.putString(PaymentDBAdapter.KEY_DATE_TRANSFER, (String)exDatePaid.first);
		b.putString(PaymentDBAdapter.KEY_CONFIRMATION, (String)exConf.first);
		b.putString(PaymentDBAdapter.KEY_ID, (String)exID.first);
		
		p = new Payment(b);
		b = p.generateBundle();
		assertEquals(exAccount.second, b.getString(PaymentDBAdapter.KEY_ACCOUNT));
		assertEquals(exAmountDue.second, b.getString(PaymentDBAdapter.KEY_AMOUNT_DUE));
		assertEquals(exDateDue.second, b.getString(PaymentDBAdapter.KEY_DATE_DUE));
		assertEquals(exAmountPaid.second, b.getString(PaymentDBAdapter.KEY_AMOUNT_PAID));
		assertEquals(exDatePaid.second, b.getString(PaymentDBAdapter.KEY_DATE_TRANSFER));
		assertEquals(exConf.second, b.getString(PaymentDBAdapter.KEY_CONFIRMATION));
		assertEquals(exID.second, b.getString(PaymentDBAdapter.KEY_ID));
		
		/** Test bad Long values */
		exAccount = new Pair<Object, String>("Test account.", "Test account.");
		exAmountDue = new Pair<Object, String>("", "");
		exDateDue = new Pair<Object, String>("200-01-01", "");
		exAmountPaid = new Pair<Object, String>(null, "");
		exDatePaid = new Pair<Object, String>("2011-02-01", "2011-02-01");
		exConf = new Pair<Object, String>("yes", "yes");
		exID = new Pair<Object, String>("2", "2");

		b = new Bundle();
		b.putString(PaymentDBAdapter.KEY_ACCOUNT, (String)exAccount.first);
		b.putString(PaymentDBAdapter.KEY_AMOUNT_DUE, (String)exAmountDue.first);
		b.putString(PaymentDBAdapter.KEY_DATE_DUE, (String)exDateDue.first);
		b.putString(PaymentDBAdapter.KEY_AMOUNT_PAID, (String)exAmountPaid.first);
		b.putString(PaymentDBAdapter.KEY_DATE_TRANSFER, (String)exDatePaid.first);
		b.putString(PaymentDBAdapter.KEY_CONFIRMATION, (String)exConf.first);
		b.putString(PaymentDBAdapter.KEY_ID, (String)exID.first);
		
		p = new Payment(b);
		b = p.generateBundle();
		assertEquals(exAccount.second, b.getString(PaymentDBAdapter.KEY_ACCOUNT));
		assertEquals(exAmountDue.second, b.getString(PaymentDBAdapter.KEY_AMOUNT_DUE));
		assertEquals(exDateDue.second, b.getString(PaymentDBAdapter.KEY_DATE_DUE));
		assertEquals(exAmountPaid.second, b.getString(PaymentDBAdapter.KEY_AMOUNT_PAID));
		assertEquals(exDatePaid.second, b.getString(PaymentDBAdapter.KEY_DATE_TRANSFER));
		assertEquals(exConf.second, b.getString(PaymentDBAdapter.KEY_CONFIRMATION));
		assertEquals(exID.second, b.getString(PaymentDBAdapter.KEY_ID));

		
		/** Test record ID */
		exAccount = new Pair<Object, String>("Test account.", "Test account.");
		exID = new Pair<Object, String>("", Payment.NO_ID.toString());

		b = new Bundle();
		b.putString(PaymentDBAdapter.KEY_ACCOUNT, (String)exAccount.first);
		b.putString(PaymentDBAdapter.KEY_ID, (String)exID.first);
		
		p = new Payment(b);
		b = p.generateBundle();
		assertEquals(exAccount.second, b.getString(PaymentDBAdapter.KEY_ACCOUNT));
		assertEquals(exID.second, b.getString(PaymentDBAdapter.KEY_ID));

		
		exAccount = new Pair<Object, String>("Test account.", "Test account.");
		exID = new Pair<Object, String>("5", "5");

		b = new Bundle();
		b.putString(PaymentDBAdapter.KEY_ACCOUNT, (String)exAccount.first);
		b.putString(PaymentDBAdapter.KEY_ID, (String)exID.first);
		
		p = new Payment(b);
		b = p.generateBundle();
		assertEquals(exAccount.second, b.getString(PaymentDBAdapter.KEY_ACCOUNT));
		assertEquals(exID.second, b.getString(PaymentDBAdapter.KEY_ID));

		
	}
	
	public void testGenerateBundle() {
		exAccount = new Pair<Object, String>("Test account.", "Test account.");
		exAmountDue = new Pair<Object, String>(3L, "3");
		exDateDue = new Pair<Object, String>("2010-01-01", "2010-01-01");
		exAmountPaid = new Pair<Object, String>(2L, "2");
		exDatePaid = new Pair<Object, String>("2011-02-01", "2011-02-01");
		exConf = new Pair<Object, String>("yes", "yes");
		exID = new Pair<Object, String>(Long.valueOf(1), "1");

		values.clear();
		values.put(KnownCursor.ACCOUNT, exAccount.first);
		values.put(KnownCursor.AMOUNT_DUE, exAmountDue.first);
		values.put(KnownCursor.DATE_DUE, exDateDue.first);
		values.put(KnownCursor.AMOUNT_PAID, exAmountPaid.first);
		values.put(KnownCursor.DATE_XFER, exDatePaid.first);
		values.put(KnownCursor.CONF, exConf.first);
		values.put(KnownCursor.ID, exID.first);
		
		Payment p = new Payment(new KnownCursor(values));
		Bundle b = p.generateBundle();
		assertEquals(b.getString(PaymentDBAdapter.KEY_ACCOUNT), exAccount.second);
		assertEquals(b.getString(PaymentDBAdapter.KEY_AMOUNT_DUE), exAmountDue.second);
		assertEquals(b.getString(PaymentDBAdapter.KEY_DATE_DUE), exDateDue.second);
		assertEquals(b.getString(PaymentDBAdapter.KEY_AMOUNT_PAID), exAmountPaid.second);
		assertEquals(b.getString(PaymentDBAdapter.KEY_DATE_TRANSFER), exDatePaid.second);
		assertEquals(b.getString(PaymentDBAdapter.KEY_CONFIRMATION), exConf.second);
		assertEquals(b.getString(PaymentDBAdapter.KEY_ID), exID.second);
	}
	
	
	public static class KnownCursor extends MockCursor {
		static final int ACCOUNT = 1;
		static final int AMOUNT_DUE = 2;
		static final int DATE_DUE = 3;
		static final int AMOUNT_PAID = 4;
		static final int DATE_XFER = 5;
		static final int CONF = 6;
		static final int ID = 7;
		
		private Object account;
		private Object amountDue;
		private Object dateDue;
		private Object amountPaid;
		private Object dateXfer;
		private Object conf;
		private Object id;

		public KnownCursor(Object account, Object amountDue, Object dateDue, Object amountPaid, Object dateXfer, Object conf, Object id) {
			this.account = account;
			this.amountDue = amountDue;
			this.dateDue = dateDue;
			this.amountPaid = amountPaid;
			this.dateXfer = dateXfer;
			this.conf = conf;
			this.id = id;
		}
		
		public KnownCursor(Map<Integer, Object> vals) {
			this.account = vals.get(ACCOUNT);
			this.amountDue = vals.get(AMOUNT_DUE);
			this.dateDue = vals.get(DATE_DUE);
			this.amountPaid = vals.get(AMOUNT_PAID);
			this.dateXfer = vals.get(DATE_XFER);
			this.conf = vals.get(CONF);
			this.id = vals.get(ID);
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
			} else if (PaymentDBAdapter.KEY_ID.equals(columnName)) {
				return ID;
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
				case ID:
					return (Long)id;
				default:
					return super.getLong(columnIndex);
			}
		}
	}
}
