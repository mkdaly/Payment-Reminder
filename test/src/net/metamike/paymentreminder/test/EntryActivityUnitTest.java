package net.metamike.paymentreminder.test;

import net.metamike.paymentreminder.EntryActivity;
import net.metamike.paymentreminder.data.PaymentDBAdapter;
import net.metamike.paymentreminder.data.Payments;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.test.ActivityUnitTestCase;
import android.test.RenamingDelegatingContext;
import android.test.UiThreadTest;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;

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
		String exAccount = "Test Account";
		
		
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

		account.setText(exAccount);
		assertTrue(save.performClick());
		
		c = db.rawQuery("SELECT * FROM payments", null);
		assertEquals(1, c.getCount());
		
		assertTrue(c.moveToFirst());
		assertEquals(exAccount, Payments.getAccount(c));
		assertEquals((Long)0L, Payments.getAmountDue(c));
		assertNull(Payments.getDueDateAsLong(c));
		assertNull(Payments.getDueDate(c));
		assertEquals((Long)0L, Payments.getAmountPaid(c));
		assertNull(Payments.getTransferDateAsLong(c));
		assertNull(Payments.getTransferDate(c));
		assertEquals("", Payments.getConfirmation(c));
		
		c.close();
	}


}
