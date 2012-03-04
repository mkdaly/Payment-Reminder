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
		BigDecimal amt_due = new BigDecimal("40.00");
		Date dt_due = new Date();
		BigDecimal amt_paid = new BigDecimal("430.43");
		Date dt_xfer = new Date();
		String conf = "confirmed";
		
		PaymentDBAdapter adapter = new PaymentDBAdapter(testContext);
		adapter.open();
		SQLiteDatabase db = adapter.getDatabase();
		
		assertTrue("Failed inserting payment.", adapter.insertPayment(
				account, amt_due, dt_due, amt_paid, dt_xfer, conf));
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
		BigDecimal amt_due = new BigDecimal("40.00");
		Date dt_due = new Date();
		BigDecimal amt_paid = new BigDecimal("430.43");
		Date dt_xfer = new Date();
		String conf = "confirmed";
		
		PaymentDBAdapter adapter = new PaymentDBAdapter(testContext);
		adapter.open();
		SQLiteDatabase db = adapter.getDatabase();
		
		//Account needs to be specified
		assertFalse(adapter.insertPayment(null, amt_due, dt_due, amt_paid, dt_xfer, conf));
		
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

	public void testConvertBigDecimal() {
		PaymentDBAdapter adapter = new PaymentDBAdapter(testContext);
		BigDecimal src = new BigDecimal(1);
		assertEquals((Long)(1L * 1000), adapter.convertBigDecimal(src));
		
		src = new BigDecimal("1.01");
		assertEquals((Long)(1010L), adapter.convertBigDecimal(src));
		
		src = new BigDecimal("34.01");
		assertEquals((Long)(34010L), adapter.convertBigDecimal(src));
		
		src = new BigDecimal(".01");
		assertEquals((Long)(10L), adapter.convertBigDecimal(src));
		
		src = new BigDecimal("-.01");
		assertEquals((Long)(-10L), adapter.convertBigDecimal(src));
		
		assertEquals(null, adapter.convertBigDecimal(null));
		
	}

}
