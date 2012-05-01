package net.metamike.paymentreminder.test;

import java.util.HashMap;
import java.util.Map;

import net.metamike.paymentreminder.EntryActivity;
import net.metamike.paymentreminder.ListActivity;
import net.metamike.paymentreminder.data.PaymentDBAdapter;
import net.metamike.paymentreminder.data.Payment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityUnitTestCase;
import android.test.RenamingDelegatingContext;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
		TextView entryID = (TextView)activity.findViewById(net.metamike.paymentreminder.R.id.entry_id);
		long exID = 0;

		Map<TextView, String> textMap = new HashMap<TextView, String>(7);
		
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

		String exAccount = "test";
		String exAmountDue = "";
		String exDueDate = "";
		String exXferDate = "";
		String exAmountPaid = "";
		String exConf = "";
		exID++;
		
		/** Just the account name */
		textMap.put(account, (String)exAccount);
		fieldSetUp(textMap);
		assertTrue(save.performClick());
		
		c = db.rawQuery("SELECT * FROM payments", null);
		assertEquals(1, c.getCount());
		/**
		 * Six values: account, amount, due date as string,
		 * 				 amount paid, xfer date as string, conf, ID
		 */
		String[] expectedValues = new String[]{
				exAccount, exAmountDue, exDueDate,
				exAmountPaid, exXferDate, exConf, Long.toString(exID) };
		verifyData(c, expectedValues);
		c.close();
		
		
		exAccount = "test3";
		exConf = "confirmation";
		exAmountDue = "2";
		exDueDate = "2010-01-03";
		exXferDate = "2010-01-02";
		exAmountPaid = "2";
		exID++;

		textMap.clear();
		textMap.put(account, exAccount);
		textMap.put(amountDue, exAmountDue);
		textMap.put(amountPaid, exAmountPaid);
		textMap.put(amt_date, exDueDate);
		textMap.put(xfer_date, exXferDate);
		textMap.put(conf, exConf);
		fieldSetUp(textMap);
		assertTrue(save.performClick());
		c = db.rawQuery("SELECT * FROM payments WHERE account = ?", new String[]{(String)exAccount});
		assertEquals(1, c.getCount());

		expectedValues = new String[]{
				exAccount, exAmountDue, exDueDate,
				exAmountPaid, exXferDate, exConf, Long.toString(exID) };
		verifyData(c, expectedValues);
		c.close();
		/** Check that fields are cleared after a save */
		assertTrue(TextUtils.isEmpty(account.getText()));
		assertTrue(TextUtils.isEmpty(amountDue.getText() ));
		assertTrue(TextUtils.isEmpty(amt_date.getText()));
		assertTrue(TextUtils.isEmpty(amountPaid.getText()));
		assertTrue(TextUtils.isEmpty(xfer_date.getText()));
		assertTrue(TextUtils.isEmpty(conf.getText()));
		assertTrue(TextUtils.isEmpty(entryID.getText()));

		textMap.clear();
		textMap.put(account, exAccount);
		textMap.put(amountDue, exAmountDue);
		textMap.put(amountPaid, exAmountPaid);
		textMap.put(amt_date, exDueDate);
		textMap.put(xfer_date, exXferDate);
		textMap.put(conf, exConf);
		textMap.put(entryID, Long.toString(exID));
		fieldSetUp(textMap);
		assertTrue(save.performClick());
		c = db.rawQuery("SELECT * FROM payments WHERE account = ?", new String[]{(String)exAccount});
		assertEquals(1, c.getCount());
		
 		assertTrue(TextUtils.isEmpty(account.getText().toString() ));
		assertTrue(TextUtils.isEmpty(amountDue.getText().toString() ));
		assertTrue(TextUtils.isEmpty(amt_date.getText().toString() ));
		assertTrue(TextUtils.isEmpty(amountPaid.getText().toString() ));
		assertTrue(TextUtils.isEmpty(xfer_date.getText().toString() ));
		assertTrue(TextUtils.isEmpty(conf.getText().toString() ));
		assertTrue(TextUtils.isEmpty(entryID.getText()));

		expectedValues = new String[]{
				exAccount, exAmountDue, exDueDate,
				exAmountPaid, exXferDate, exConf, Long.toString(exID) };

		verifyData(c, expectedValues);

		c.close();		
		//TODO: need to create the dialog and test for its exsistence	
		db.close();
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
		TextView entryID = (TextView)activity.findViewById(net.metamike.paymentreminder.R.id.entry_id);
		
		Map<TextView, String> textMap = new HashMap<TextView, String>(7);
		
		Button cancel = (Button)activity.findViewById(net.metamike.paymentreminder.R.id.button_cancel);

		String exAccount = "Test cancel";
		textMap.put(account, exAccount);
		textMap.put(amountDue, "2");
		textMap.put(amt_date, "2012-03-01");
		textMap.put(amountPaid, "3");
		textMap.put(xfer_date, "2012-03-01");
		textMap.put(conf, "confirmed");
		textMap.put(entryID, "1");
		fieldSetUp(textMap);
		
		assertTrue(cancel.requestFocus());
		assertTrue(cancel.performClick());
		assertTrue(TextUtils.isEmpty(account.getText().toString() ));
		assertTrue(TextUtils.isEmpty(amountDue.getText().toString() ));
		assertTrue(TextUtils.isEmpty(amt_date.getText().toString() ));
		assertTrue(TextUtils.isEmpty(amountPaid.getText().toString() ));
		assertTrue(TextUtils.isEmpty(xfer_date.getText().toString() ));
		assertTrue(TextUtils.isEmpty(conf.getText().toString() ));
		assertTrue(TextUtils.isEmpty(entryID.getText()));
		assertTrue(account.isFocused());
	}
	
	public void testViewEntry() {
		/** Setup */
		String exAccount = "Test cancel";
		String exAmountDue = "2";
		String exDateDue = "2012-03-01";
		String exAmountPaid = "3";
		String exTransferferDate = "2012-03-01";
		String exConf = "confirmed";
		String exID = "1";

		Intent viewIntent = new Intent(testContext, EntryActivity.class);
		viewIntent.setAction(ListActivity.LOAD_INTENT);
		viewIntent.putExtra(PaymentDBAdapter.KEY_ACCOUNT, exAccount);
		viewIntent.putExtra(PaymentDBAdapter.KEY_AMOUNT_DUE, exAmountDue);
		viewIntent.putExtra(PaymentDBAdapter.KEY_DATE_DUE, exDateDue);
		viewIntent.putExtra(PaymentDBAdapter.KEY_AMOUNT_PAID, exAmountPaid);
		viewIntent.putExtra(PaymentDBAdapter.KEY_DATE_TRANSFER, exTransferferDate);
		viewIntent.putExtra(PaymentDBAdapter.KEY_CONFIRMATION, exConf);
		viewIntent.putExtra(PaymentDBAdapter.KEY_ID, exID);
		
		/** Test */
		startActivity(viewIntent, null, null);
		EntryActivity activity = this.getActivity();
		EditText account = (EditText)activity.findViewById(net.metamike.paymentreminder.R.id.field_account);
		EditText amountDue =  (EditText)activity.findViewById(net.metamike.paymentreminder.R.id.field_amount_due);
		EditText amt_date =  (EditText)activity.findViewById(net.metamike.paymentreminder.R.id.field_due_date);
		EditText amountPaid =  (EditText)activity.findViewById(net.metamike.paymentreminder.R.id.field_amount_paid);
		EditText xfer_date =  (EditText)activity.findViewById(net.metamike.paymentreminder.R.id.field_transfer_date);
		EditText conf =  (EditText)activity.findViewById(net.metamike.paymentreminder.R.id.field_confirmation);
		TextView entryID = (TextView)activity.findViewById(net.metamike.paymentreminder.R.id.entry_id);
		
		assertEquals(exAccount, account.getText().toString() );
		assertEquals(exAmountDue, amountDue.getText().toString() );
		assertEquals(exDateDue, amt_date.getText().toString() );
		assertEquals(exAmountPaid, amountPaid.getText().toString() );
		assertEquals(exTransferferDate, xfer_date.getText().toString() );
		assertEquals(exConf, conf.getText().toString() );
		assertEquals(exID, entryID.getText().toString());
	}
	
	public void testViewEntryNoID() {
		Intent viewIntent = new Intent(testContext, EntryActivity.class);
		viewIntent.setAction(ListActivity.LOAD_INTENT);
		startActivity(viewIntent, null, null);
		assertTrue(isFinishCalled());
	}
	
	public void testViewEntryBadID1() {
		/** Setup */
		String exID = Payment.NO_ID.toString();

		Intent viewIntent = new Intent(testContext, EntryActivity.class);
		viewIntent.setAction(ListActivity.LOAD_INTENT);
		viewIntent.putExtra(PaymentDBAdapter.KEY_ID, exID);

		startActivity(viewIntent, null, null);
		assertTrue(isFinishCalled());
	}

	public void testViewEntryBadID2() {
		/** Setup */
		String exID = "-1";

		Intent viewIntent = new Intent(testContext, EntryActivity.class);
		viewIntent.setAction(ListActivity.LOAD_INTENT);
		viewIntent.putExtra(PaymentDBAdapter.KEY_ID, exID);

		startActivity(viewIntent, null, null);
		assertTrue(isFinishCalled());
	}

	public void testViewEntryBadID3() {
		/** Setup */
		String exID = "one";

		Intent viewIntent = new Intent(testContext, EntryActivity.class);
		viewIntent.setAction(ListActivity.LOAD_INTENT);
		viewIntent.putExtra(PaymentDBAdapter.KEY_ID, exID);

		startActivity(viewIntent, null, null);
		assertTrue(isFinishCalled());
	}

	
	
	private void fieldSetUp(Map<TextView, String> map) {
		try {
			for(TextView view: map.keySet()) {
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
		assertEquals(expectedValues[6], Long.toString(c.getLong(c.getColumnIndex(PaymentDBAdapter.KEY_ID))));
	}
}
