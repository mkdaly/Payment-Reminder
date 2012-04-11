package net.metamike.paymentreminder.test;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import net.metamike.paymentreminder.data.Payment;
import net.metamike.paymentreminder.data.PaymentDBAdapter;
import net.metamike.paymentreminder.data.PaymentDBAdapter.ReminderType;
import net.metamike.paymentreminder.test.PaymentsTest.KnownCursor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.MoreAsserts;
import android.test.RenamingDelegatingContext;
import android.text.format.Time;

public class PaymentDBAdapterTest extends AndroidTestCase {
	private RenamingDelegatingContext testContext;
	private Time now;

	protected void setUp() throws Exception {
		super.setUp();
		testContext = new RenamingDelegatingContext(getContext(), "test_");
		now = new Time();
		now.setToNow();
	}
	
	
	public void testOpen() {
		MoreAsserts.assertEquals("Testing for no databases", new String[]{}, testContext.databaseList());
		PaymentDBAdapter adapter = new PaymentDBAdapter(testContext);
		adapter.open();
		MoreAsserts.assertEquals("Testing for one database", new String[]{"paymentreminder.db"}, testContext.databaseList());
		adapter.close();
	}
	
	public void testInserts() {
		String exAccount = "Test Account";
		Long exAmountDue = 40000L;
		String exDateDue = now.format3339(true);
		Long exAmountPaid = 430430L;
		String exDatePaid = now.format3339(true);
		String exConf = "confirmed";
		Map<Integer, Object> values = new HashMap<Integer, Object>();
		values.put(KnownCursor.ACCOUNT, exAccount);
		values.put(KnownCursor.AMOUNT_DUE, exAmountDue);
		values.put(KnownCursor.DATE_DUE, exDateDue);
		values.put(KnownCursor.AMOUNT_PAID, exAmountPaid);
		values.put(KnownCursor.DATE_XFER, exDatePaid);
		values.put(KnownCursor.CONF, exConf);
		values.put(KnownCursor.ID, Integer.valueOf(1));

		Payment p = new Payment(new KnownCursor(values));

		
		PaymentDBAdapter adapter = new PaymentDBAdapter(testContext);
		adapter.open();
		SQLiteDatabase db = adapter.getDatabase();
		
		try {
			assertTrue("Failed inserting payment.", adapter.insertPayment(p));
		} catch  (Exception e) {
			//TODO: Fix type when new ex type is written
			fail(e.getMessage());
		}
		Cursor c = db.rawQuery("SELECT * FROM payments", null);
		assertEquals(1, c.getCount());
		assertTrue(c.moveToFirst());
		assertEquals(exAccount, c.getString(c.getColumnIndex(PaymentDBAdapter.KEY_ACCOUNT)));
		assertEquals(exAmountDue.longValue(), c.getLong(c.getColumnIndex(PaymentDBAdapter.KEY_AMOUNT_DUE)));
		assertEquals(exDateDue, c.getString(c.getColumnIndex(PaymentDBAdapter.KEY_DATE_DUE)));
		assertEquals(exAmountPaid.longValue(), c.getLong(c.getColumnIndex(PaymentDBAdapter.KEY_AMOUNT_PAID)));
		assertEquals(exDatePaid, c.getString(c.getColumnIndex(PaymentDBAdapter.KEY_DATE_TRANSFER)));
		assertEquals(exConf, c.getString(c.getColumnIndex(PaymentDBAdapter.KEY_CONFIRMATION)));
		
		int _id = c.getInt(c.getColumnIndex(PaymentDBAdapter.KEY_ID));
		ReminderType type = ReminderType.PAY;
		String time = now.format3339(true);

		c.close();
		assertTrue("Failed inserting reminder.", adapter.insertReminder(_id, type, time));
		c = db.rawQuery("SELECT * FROM reminders", null);
		assertEquals(1, c.getCount());
		assertTrue(c.moveToFirst());
		assertEquals(_id, c.getInt(c.getColumnIndex(PaymentDBAdapter.KEY_PAYMENT_ID)));
		assertEquals(type.ordinal(), c.getInt(c.getColumnIndex(PaymentDBAdapter.KEY_TYPE)));
		assertEquals(time, c.getString(c.getColumnIndex(PaymentDBAdapter.KEY_TIME)));
		c.close();
		
		//assertTrue(adapter.insertPayment(account));

	}

	public void testInsertsConstraintsPayments() {
		Map<Integer, Object> values = new HashMap<Integer, Object>();
		values.put(KnownCursor.ACCOUNT, null);
		values.put(KnownCursor.AMOUNT_DUE, null);
		values.put(KnownCursor.DATE_DUE, null);
		values.put(KnownCursor.AMOUNT_PAID, null);
		values.put(KnownCursor.DATE_XFER, null);
		values.put(KnownCursor.CONF, null);
		values.put(KnownCursor.ID, null);
		Payment p;
		
		PaymentDBAdapter adapter = new PaymentDBAdapter(testContext);
		adapter.open();
		SQLiteDatabase db = adapter.getDatabase();
		
		//Account needs to be specified
		try {
			assertFalse(adapter.insertPayment((String)null));
		} catch (Exception e) {
			//TODO: Fix type when new ex type is written
			fail(e.getMessage());
		}
		
		values.put(KnownCursor.ACCOUNT, "Test Account");
		values.put(KnownCursor.ID, Integer.valueOf(1));
		p = new Payment(new KnownCursor(values));

		//Nulls for these should use the DEFAULTs 
		assertTrue("Succeeded inserting payment with null account.", adapter.insertPayment(p));
		
		Cursor c = db.rawQuery("SELECT * FROM payments WHERE account = ?", new String[]{"Test Account"});
		assertEquals(1, c.getCount());
		assertTrue(c.moveToFirst());
		assertEquals("Test Account", c.getString(c.getColumnIndex(PaymentDBAdapter.KEY_ACCOUNT)));
		assertEquals(0, c.getLong(c.getColumnIndex(PaymentDBAdapter.KEY_AMOUNT_DUE)));
		assertEquals("", c.getString(c.getColumnIndex(PaymentDBAdapter.KEY_DATE_DUE)));
		assertEquals(0, c.getLong(c.getColumnIndex(PaymentDBAdapter.KEY_AMOUNT_PAID)));
		assertEquals("", c.getString(c.getColumnIndex(PaymentDBAdapter.KEY_DATE_TRANSFER)));
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
	
	/*
	public void testConvertStringToTime() {
		PaymentDBAdapter adapter = new PaymentDBAdapter(testContext);
		Time date = new Time();
		date.set(1, Calendar.JANUARY, 2012); //Jan 1 2012
		String exDateString = "20120101";
		String exDatetimeString = "20120101T160000";
		String badFormat = "2012-01-01";
		String badFormat2 = "Jan 01, 2012";
		
		try {
			assertTrue(0 == Time.compare(date, adapter.convertStringToTime(exDateString)));
			assertTrue(0 == Time.compare(date, adapter.convertStringToTime(exDatetimeString)));
		} catch (Exception e) {
			fail(e.getMessage());
		}
		try {
			adapter.convertStringToTime(badFormat);
			fail("Exception not thrown.");
		} catch (TimeFormatException tfe) {
			//ok.
		}
		try {
			adapter.convertStringToTime(badFormat2);
			fail("Exception not thrown.");
		} catch (TimeFormatException tfe) {
			//ok.
		}

	}
	*/
	
	public void testGetAllPayments() {
		PaymentDBAdapter adapter = new PaymentDBAdapter(testContext);
		adapter.open();
		Cursor c = adapter.getAllPayments();
		assertEquals(0, c.getCount());
				
		String[] expectedAccounts = {"Test 1", "Test 2", 
				"Test 4", "Test 3"};
		
		String[] expectedDate = new String[4];
		Time t = new Time();
		t.set(1, Calendar.JANUARY, 2012);
		expectedDate[0] = (new Time(t)).format3339(true);
		t.set(1, Calendar.FEBRUARY, 2012);
		expectedDate[1] = (new Time(t)).format3339(true);
		t.set(1, Calendar.JUNE, 2012);
		expectedDate[2] = (new Time(t)).format3339(true);		
		t.set(2, Calendar.JANUARY, 2012);
		expectedDate[3] = (new Time(t)).format3339(true);
		
		Map<Integer, Object> values = new HashMap<Integer, Object>();
		
		for (int i = 0; i < expectedAccounts.length; i++) {
			values.put(KnownCursor.ACCOUNT, expectedAccounts[i]);
			values.put(KnownCursor.DATE_DUE, expectedDate[i]);
			values.put(KnownCursor.ID, Integer.valueOf(i));

			Payment p = new Payment(new KnownCursor(values));
			assertTrue(adapter.insertPayment(p));
		}
		
		c = adapter.getAllPayments();
		assertEquals(4, c.getCount());
		int[] expectedOrder = {0, 3, 1, 2}; //Jan 1; Jan 2; Feb 1; Jun 1
		c.moveToFirst();
		for(Integer i: expectedOrder) {
			assertEquals(expectedAccounts[i], c.getString(c.getColumnIndex(PaymentDBAdapter.KEY_ACCOUNT)));
			assertEquals(expectedDate[i], c.getString(c.getColumnIndex(PaymentDBAdapter.KEY_DATE_DUE)));
			if (!c.isLast())
				assertTrue(c.moveToNext());
		}
		c.close();
		adapter.close();
		
	}
}
