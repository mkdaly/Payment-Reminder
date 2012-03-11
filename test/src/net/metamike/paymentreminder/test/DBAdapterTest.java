package net.metamike.paymentreminder.test;

import java.math.BigDecimal;
import java.util.Date;

import net.metamike.paymentreminder.data.PaymentDBAdapter;
import net.metamike.paymentreminder.data.PaymentDBAdapter.ReminderType;
import net.metamike.paymentreminder.data.Payments;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.MoreAsserts;
import android.test.RenamingDelegatingContext;
import android.text.format.Time;
import android.util.TimeFormatException;

public class DBAdapterTest extends AndroidTestCase {
	private RenamingDelegatingContext testContext;

	protected void setUp() throws Exception {
		super.setUp();
		testContext = new RenamingDelegatingContext(getContext(), "test_"); 
	}
	
	public void testOpen() {
		MoreAsserts.assertEquals("Testing for no databases", new String[]{}, testContext.databaseList());
		PaymentDBAdapter adapter = new PaymentDBAdapter(testContext);
		adapter.open();
		MoreAsserts.assertEquals("Testing for one database", new String[]{"paymentreminder.db"}, testContext.databaseList());
		adapter.close();
	}
	
	public void testInserts() {
		String account = "Test Account";
		String amt_due = "40.00";
		Date dt_due = new Date();
		String amt_paid = "430.43";
		Date dt_xfer = new Date();
		String conf = "confirmed";
		
		PaymentDBAdapter adapter = new PaymentDBAdapter(testContext);
		adapter.open();
		SQLiteDatabase db = adapter.getDatabase();
		
		try {
			assertTrue("Failed inserting payment.", adapter.insertPayment(
				account, adapter.convertStringToLongMill(amt_due), dt_due.getTime(), adapter.convertStringToLongMill(amt_paid), dt_xfer.getTime(), conf));
		} catch  (Exception e) {
			//TODO: Fix type when new ex type is written
			fail(e.getMessage());
		}
		Cursor c = db.rawQuery("SELECT * FROM payments", null);
		assertEquals(1, c.getCount());
		assertTrue(c.moveToFirst());
		assertEquals(account, Payments.getAccount(c));
		assertEquals((Long)40000L, Payments.getAmountDue(c));
		assertEquals((Long)dt_due.getTime(), Payments.getDueDateAsLong(c));
		assertTrue(dt_due.equals(Payments.getDueDate(c)));
		assertEquals((Long)430430L, Payments.getAmountPaid(c));
		assertEquals((Long)dt_xfer.getTime(), Payments.getTransferDateAsLong(c));
		assertTrue(dt_xfer.equals(Payments.getTransferDate(c)));
		assertEquals(conf, Payments.getConfirmation(c));
		
		int _id = c.getInt(c.getColumnIndex(PaymentDBAdapter.KEY_ID));
		ReminderType type = ReminderType.PAY;
		Date time = new Date();

		c.close();
		assertTrue("Failed inserting reminder.", adapter.insertReminder(
				_id, type, time));
		c = db.rawQuery("SELECT * FROM reminders", null);
		assertEquals(1, c.getCount());
		assertTrue(c.moveToFirst());
		assertEquals(_id, c.getInt(c.getColumnIndex(PaymentDBAdapter.KEY_PAYMENT_ID)));
		assertEquals(type.ordinal(), c.getInt(c.getColumnIndex(PaymentDBAdapter.KEY_TYPE)));
		assertEquals(time.getTime(), c.getLong(c.getColumnIndex(PaymentDBAdapter.KEY_TIME)));
		c.close();
		
		assertTrue(adapter.insertPayment(account));

	}

	public void testInsertsConstraintsPayments() {
		String account = "Test Account Constraints";
		String amt_due = "40.00";
		Date dt_due = new Date();
		String amt_paid = "430.43";
		Date dt_xfer = new Date();
		String conf = "confirmed";
		
		PaymentDBAdapter adapter = new PaymentDBAdapter(testContext);
		adapter.open();
		SQLiteDatabase db = adapter.getDatabase();
		
		//Account needs to be specified
		try {
			assertFalse(adapter.insertPayment(null, adapter.convertStringToLongMill(amt_due), dt_due.getTime(), adapter.convertStringToLongMill(amt_paid), dt_xfer.getTime(), conf));
		} catch (Exception e) {
			//TODO: Fix type when new ex type is written
			fail(e.getMessage());
		}
		
		//Nulls for these should use the DEFAULTs 
		assertTrue("Succeeded inserting payment with null account.", adapter.insertPayment(
				account, null, null, null, null, null));
		
		Cursor c = db.rawQuery("SELECT * FROM payments WHERE account = ?", new String[]{account});
		assertEquals(1, c.getCount());
		assertTrue(c.moveToFirst());
		assertEquals(account, c.getString(c.getColumnIndex(PaymentDBAdapter.KEY_ACCOUNT)));
		assertEquals(0, c.getLong(c.getColumnIndex(PaymentDBAdapter.KEY_AMOUNT_DUE)));
		assertTrue( c.isNull(c.getColumnIndex(PaymentDBAdapter.KEY_DATE_DUE)));
		assertEquals(0, c.getLong(c.getColumnIndex(PaymentDBAdapter.KEY_AMOUNT_PAID)));
		assertTrue(c.isNull(c.getColumnIndex(PaymentDBAdapter.KEY_DATE_TRANSFER)));
		assertEquals("", c.getString(c.getColumnIndex(PaymentDBAdapter.KEY_CONFIRMATION)));
		c.close();		
	}

	public void testInsertDefaultsReminders() {
		String account = "Test Account";
		
		PaymentDBAdapter adapter = new PaymentDBAdapter(testContext);
		adapter.open();
		SQLiteDatabase db = adapter.getDatabase();
		
		//We need a payment_id value
		assertTrue(adapter.insertPayment(account));

		Cursor c = db.rawQuery("SELECT * FROM payments WHERE account = ?", new String[]{account});
		assertTrue(c.moveToFirst());
		int _id = c.getInt(c.getColumnIndex(PaymentDBAdapter.KEY_ID));
		c.close();
		
		ReminderType type = ReminderType.PAY;
		Date time = new Date();

		assertFalse(adapter.insertReminder(null, null, null));
		assertFalse(adapter.insertReminder(_id, null, null));
		assertFalse(adapter.insertReminder(_id, type, null));
	}

	public void testConvertStringToLongMill() {
		PaymentDBAdapter adapter = new PaymentDBAdapter(testContext);
		//TODO: Fix type when new ex type is written
		try {
			assertEquals((Long)(1L * 1000), adapter.convertStringToLongMill("1"));
			assertEquals((Long)(1010L), adapter.convertStringToLongMill("1.01"));
			assertEquals((Long)(34010L), adapter.convertStringToLongMill("34.01"));
			assertEquals((Long)(10L), adapter.convertStringToLongMill(".01"));
			assertEquals((Long)(-10L), adapter.convertStringToLongMill("-.01"));
			assertEquals(null, adapter.convertStringToLongMill(null));
		} catch (Exception e) {
			fail(e.getMessage());
		}
		try {
			assertEquals((Long)(0L), adapter.convertStringToLongMill(".001"));
			fail("Exception not thrown.");
		} catch (NumberFormatException nfe) {
			assertEquals(testContext.getString(net.metamike.paymentreminder.R.string.more_than_cents), nfe.getMessage());
		}
	}
	
	public void testConvertDateStringToMilliseconds() {
		PaymentDBAdapter adapter = new PaymentDBAdapter(testContext);
		Time date = new Time();
		date.set(1, 0, 2012); //Jan 1 2012
		String dateString = "2012-01-01";
		String datetimeString = "2012-01-01T16:00:00.000Z";
		String badFormat = "Jan 01, 2012";
		
		try {
			assertEquals((Long)date.toMillis(true), adapter.convertDateStringToMilliseconds(dateString));
			assertNull(adapter.convertDateStringToMilliseconds(null));
			assertNull(adapter.convertDateStringToMilliseconds(datetimeString));
		} catch (Exception e) {
			fail(e.getMessage());
		}
		try {
			assertNull(adapter.convertDateStringToMilliseconds(badFormat));
			fail("Exception not thrown.");
		} catch (TimeFormatException tfe) {
			//ok.
		}
	}

}
