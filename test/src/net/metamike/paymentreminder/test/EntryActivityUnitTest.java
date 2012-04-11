package net.metamike.paymentreminder.test;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.metamike.paymentreminder.EntryActivity;
import net.metamike.paymentreminder.data.PaymentDBAdapter;
import net.metamike.paymentreminder.data.Payment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityUnitTestCase;
import android.test.RenamingDelegatingContext;
import android.text.TextUtils;
import android.text.format.Time;
import android.widget.Button;
import android.widget.EditText;

public class EntryActivityUnitTest extends ActivityUnitTestCase<EntryActivity> {
	private Context testContext;

	public EntryActivityUnitTest() {
		super(EntryActivity.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		testContext = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "test_");
		setActivityContext(testContext);
	}

	public void testSaveEntry() {
		startActivity(new Intent(), null, null);
		EntryActivity activity = this.getActivity();
		EditText account = (EditText)activity.findViewById(net.metamike.paymentreminder.R.id.field_account);
		EditText amountDue =  (EditText)activity.findViewById(net.metamike.paymentreminder.R.id.field_amount_due);
		EditText amt_date =  (EditText)activity.findViewById(net.metamike.paymentreminder.R.id.field_due_date);
		EditText amountPaid =  (EditText)activity.findViewById(net.metamike.paymentreminder.R.id.field_amount_paid);
		EditText xfer_date =  (EditText)activity.findViewById(net.metamike.paymentreminder.R.id.field_transfer_date);
		EditText conf =  (EditText)activity.findViewById(net.metamike.paymentreminder.R.id.field_confirmation);		

		Map<EditText, String> textMap = new HashMap<EditText, String>(6);
		
		Button save = (Button)activity.findViewById(net.metamike.paymentreminder.R.id.button_save);
		PaymentDBAdapter adapter = new PaymentDBAdapter(testContext);
		adapter.open();
		SQLiteDatabase db = adapter.getDatabase();
		Cursor c;
		
		//Test empty fields
		assertTrue(save.performClick());
		c = db.rawQuery("SELECT * FROM payments", null);
		assertEquals(0, c.getCount());
		c.close();

		Object exAccount = "test";
		Object exAmountDue = Long.valueOf(0);
		Object exDueDate = null;
		Object exXferDate = null;
		Object exAmountPaid = Long.valueOf(0);
		Object exConf = "";
		
		textMap.put(account, (String)exAccount);
		fieldSetUp(textMap);
		assertTrue(save.performClick());
		
		c = db.rawQuery("SELECT * FROM payments", null);
		assertEquals(1, c.getCount());
		/**
		 * Six values: account, amount, due date as string,
		 * 				 amount paid, xfer date as string, conf
		 */
		Object[] expectedValues = new Object[]{
				exAccount, exAmountDue, exDueDate,
				exAmountPaid, exXferDate, exConf };
		verifyData(c, expectedValues);
		c.close();
		
		//Test bad amount
		exAccount = "test2";
		exAmountDue = "two";
		textMap.clear();
		textMap.put(account, (String)exAccount);
		textMap.put(amountDue, (String)exAmountDue);
		fieldSetUp(textMap);
		assertTrue(save.performClick());
		c = db.rawQuery("SELECT * FROM payments", null);
		/** A bad amount should keep the entry from being saved: */
		assertEquals(1, c.getCount());

		//Test bad due date
		exAmountDue = Long.valueOf(2);
		exDueDate = "today";
		textMap.clear();
		textMap.put(account, (String)exAccount);
		textMap.put(amountDue, (String)exAmountDue.toString());
		textMap.put(amt_date, (String)exDueDate);
		fieldSetUp(textMap);
		assertTrue(save.performClick());
		c = db.rawQuery("SELECT * FROM payments", null);
		/** A bad date should keep the entry from being saved: */
		assertEquals(1, c.getCount());

		//Test bad paid date
		exDueDate = new Time();
		((Time)exDueDate).set(1, Calendar.MARCH, 2012);
		exXferDate = "now";
		textMap.clear();
		textMap.put(account, (String)exAccount);
		textMap.put(amountDue, (String)exAmountDue.toString());
		textMap.put(amt_date, ((Time)exDueDate).format3339(true));
		textMap.put(xfer_date, (String)exXferDate);
		fieldSetUp(textMap);
		assertTrue(save.performClick());
		c = db.rawQuery("SELECT * FROM payments", null);
		/** A bad date should keep the entry from being saved: */
		assertEquals(1, c.getCount());
		
		exXferDate = new Time((Time)exDueDate);
		textMap.clear();
		textMap.put(account, (String)exAccount);
		textMap.put(amountDue, (String)exAmountDue.toString());
		textMap.put(amt_date, ((Time)exDueDate).format3339(true));
		textMap.put(xfer_date, ((Time)exXferDate).format3339(true));
		fieldSetUp(textMap);
		assertTrue(save.performClick());
		c = db.rawQuery("SELECT * FROM payments WHERE account = ?", new String[]{(String)exAccount});
		assertEquals(1, c.getCount());

		/*expectedValues = new Object[]{
				exAccount, ((Long)exAmount*1000), new Date(((Time)exDueDate).toMillis(false)),
				((Long)exPaid*1000), ((Time)exXferDate).toMillis(false), new Date(((Time)exXferDate).toMillis(false)), exConf };

		verifyData(c, expectedValues);
		
		exAccount = "test3";
		exConf = "confirmation";
		textMap.clear();
		textMap.put(account, (String)exAccount);
		textMap.put(amount, (String)exAmount.toString());
		textMap.put(amt_date, ((Time)exDueDate).format3339(true));
		textMap.put(xfer_date, ((Time)exXferDate).format3339(true));
		textMap.put(conf, (String)exConf);
		fieldSetUp(textMap);
		assertTrue(save.performClick());
		c = db.rawQuery("SELECT * FROM payments WHERE account = ?", new String[]{(String)exAccount});
		assertEquals(1, c.getCount());

		expectedValues = new Object[]{
				exAccount, ((Long)exAmount*1000), ((Time)exDueDate).toMillis(false), new Date(((Time)exDueDate).toMillis(false)),
				((Long)exPaid*1000), ((Time)exXferDate).toMillis(false), new Date(((Time)exXferDate).toMillis(false)), exConf };

		verifyData(c, expectedValues);
		
		
		//TODO: need to create the dialog and test for its exsistence
		*/
		
		adapter.close();
	}
	
	public void testCancel() {
		startActivity(new Intent(), null, null);
		EntryActivity activity = this.getActivity();
		EditText account = (EditText)activity.findViewById(net.metamike.paymentreminder.R.id.field_account);
		EditText amountDue =  (EditText)activity.findViewById(net.metamike.paymentreminder.R.id.field_amount_due);
		EditText amt_date =  (EditText)activity.findViewById(net.metamike.paymentreminder.R.id.field_due_date);
		EditText amountPaid =  (EditText)activity.findViewById(net.metamike.paymentreminder.R.id.field_amount_paid);
		EditText xfer_date =  (EditText)activity.findViewById(net.metamike.paymentreminder.R.id.field_transfer_date);
		EditText conf =  (EditText)activity.findViewById(net.metamike.paymentreminder.R.id.field_confirmation);
		
		Map<EditText, String> textMap = new HashMap<EditText, String>(6);
		
		Button cancel = (Button)activity.findViewById(net.metamike.paymentreminder.R.id.button_cancel);

		String exAccount = "Test cancel";
		textMap.put(account, exAccount);
		textMap.put(amountDue, "2");
		textMap.put(amt_date, "2012-03-01");
		textMap.put(amountPaid, "3");
		textMap.put(xfer_date, "2012-03-01");
		textMap.put(conf, "confirmed");
		fieldSetUp(textMap);
		
		assertTrue(cancel.requestFocus());
		assertTrue(cancel.performClick());
		assertTrue(TextUtils.isEmpty(account.getText().toString() ));
		assertTrue(TextUtils.isEmpty(amountDue.getText().toString() ));
		assertTrue(TextUtils.isEmpty(amt_date.getText().toString() ));
		assertTrue(TextUtils.isEmpty(amountPaid.getText().toString() ));
		assertTrue(TextUtils.isEmpty(xfer_date.getText().toString() ));
		assertTrue(TextUtils.isEmpty(conf.getText().toString() ));
		assertTrue(account.isFocused());
	}
	
	private void fieldSetUp(Map<EditText, String> map) {
		try {
			for(EditText view: map.keySet()) {
				view.setText(map.get(view));
			}
		} catch (Exception e) {
			fail("Exception thrown when trying to set up fields:" + e.getMessage());
		}
	}
	
	private void verifyData(Cursor c, Object[] expectedValues) {
		assertTrue(c.moveToFirst());
		Payment p = new Payment(c);
		assertEquals(expectedValues[0], p.getAccount());
		assertEquals(expectedValues[1], p.getAmountDue());
		assertEquals(expectedValues[2], p.getDueDate());
		assertEquals(expectedValues[3], p.getAmountPaid());
		assertEquals(expectedValues[4], p.getTransferDate());
		assertEquals(expectedValues[5], p.getConfirmation());
	}
}
