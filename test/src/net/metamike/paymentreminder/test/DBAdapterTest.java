package net.metamike.paymentreminder.test;

import java.math.BigDecimal;
import java.util.Date;

import net.metamike.paymentreminder.PaymentDBAdapter;
import net.metamike.paymentreminder.PaymentDBAdapter.ReminderType;
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
		assertEquals(account, c.getString(c.getColumnIndex(PaymentDBAdapter.KEY_ACCOUNT)));
		assertEquals(40000, c.getLong(c.getColumnIndex(PaymentDBAdapter.KEY_AMOUNT_DUE)));
		assertEquals(dt_due.getTime(), c.getLong(c.getColumnIndex(PaymentDBAdapter.KEY_DATE_DUE)));
		assertEquals(430430, c.getLong(c.getColumnIndex(PaymentDBAdapter.KEY_AMOUNT_PAID)));
		assertEquals(dt_xfer.getTime(), c.getLong(c.getColumnIndex(PaymentDBAdapter.KEY_DATE_TRANSFER)));
		assertEquals(conf, c.getString(c.getColumnIndex(PaymentDBAdapter.KEY_CONFIRMATION)));
		
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
		
	}

}
