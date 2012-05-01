package net.metamike.paymentreminder.test;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import net.metamike.paymentreminder.ListActivity;
import net.metamike.paymentreminder.data.Payment;
import net.metamike.paymentreminder.data.PaymentDBAdapter;
import net.metamike.paymentreminder.test.PaymentsTest.KnownCursor;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.RenamingDelegatingContext;
import android.text.format.Time;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListActivityUnitTest extends ActivityUnitTestCase<ListActivity> {
	private Context testContext;
	private String[] expectedAccounts;
	private String[] expectedDates;
	private Long[] expectedIDs;
	
	
	public ListActivityUnitTest() {
		super(ListActivity.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		testContext = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "test_");
		setActivityContext(testContext);
		
		expectedAccounts = new String[]{"Test 1", "Test 2", 
				"Test 4", "Test 3"};
		expectedIDs = new Long[4];
		
		expectedDates = new String[4];
		Time t = new Time();
		t.set(1, Calendar.JANUARY, 2012);
		expectedDates[0] = (new Time(t)).format3339(true);
		t.set(1, Calendar.FEBRUARY, 2012);
		expectedDates[1] = (new Time(t)).format3339(true);
		t.set(1, Calendar.JUNE, 2012);
		expectedDates[2] = (new Time(t)).format3339(true);		
		t.set(2, Calendar.JANUARY, 2012);
		expectedDates[3] = (new Time(t)).format3339(true);		
	}

	public void testAdapter() {
		PaymentDBAdapter dbAdapter = new PaymentDBAdapter(testContext);
		dbAdapter.open();		
		loadData(dbAdapter);

		startActivity(new Intent(), null, null);
		ListActivity activity = this.getActivity();
		ListView listView = (ListView)activity.findViewById(net.metamike.paymentreminder.R.id.listView);
		ListAdapter listAdapter = listView.getAdapter();
		assertEquals(expectedAccounts.length, listAdapter.getCount());
	}
	
	public void testClickListeners() {
		PaymentDBAdapter dbAdapter = new PaymentDBAdapter(testContext);
		dbAdapter.open();		
		loadData(dbAdapter);
		startActivity(new Intent(), null, null);
		ListActivity activity = this.getActivity();
		ListView listView = (ListView)activity.findViewById(net.metamike.paymentreminder.R.id.listView);
		
		/** Order should be by date, asc. */
		Integer[] expectedOrder = {0, 3, 1, 2};

		for (int idx = 0; idx < expectedOrder.length; idx++) {
			listView.performItemClick(null, idx, listView.getAdapter().getItemId(idx));
			Intent i = getStartedActivityIntent();
			assertEquals(ListActivity.LOAD_INTENT, i.getAction());
			assertEquals(expectedAccounts[expectedOrder[idx]], i.getStringExtra(PaymentDBAdapter.KEY_ACCOUNT));
			assertEquals(expectedDates[expectedOrder[idx]], i.getStringExtra(PaymentDBAdapter.KEY_DATE_DUE));
			assertEquals(expectedIDs[expectedOrder[idx]].toString(), i.getStringExtra(PaymentDBAdapter.KEY_ID));
			assertEquals("", i.getStringExtra(PaymentDBAdapter.KEY_AMOUNT_DUE));
			assertEquals("", i.getStringExtra(PaymentDBAdapter.KEY_AMOUNT_PAID));
			assertEquals("", i.getStringExtra(PaymentDBAdapter.KEY_DATE_TRANSFER));
			assertEquals("", i.getStringExtra(PaymentDBAdapter.KEY_CONFIRMATION));
		}
		
		/*
		listView.performItemClick(null, 0, listView.getAdapter().getItemId(0));
		i = getStartedActivityIntent();
		assertEquals(ListActivity.LOAD_INTENT, i.getAction());
		assertEquals(expectedAccounts[0], i.getStringExtra(PaymentDBAdapter.KEY_ACCOUNT));
		assertEquals(expectedDates[0], i.getStringExtra(PaymentDBAdapter.KEY_DATE_DUE));
		assertEquals("", i.getStringExtra(PaymentDBAdapter.KEY_AMOUNT_PAID));
		assertEquals("", i.getStringExtra(PaymentDBAdapter.KEY_DATE_TRANSFER));
		assertEquals("", i.getStringExtra(PaymentDBAdapter.KEY_CONFIRMATION));
		 */
	}
	
	private void loadData(PaymentDBAdapter dbAdapter) {		
		ContentValues values = new ContentValues();
		
		for (int i = 0; i < expectedAccounts.length; i++) {
			values.put(PaymentDBAdapter.KEY_ACCOUNT, expectedAccounts[i]);
			values.put(PaymentDBAdapter.KEY_DATE_DUE, expectedDates[i]);
			
			long id = dbAdapter.getDatabase().insert("payments", null, values);
			assertTrue( id > 0);
			expectedIDs[i] = Long.valueOf(id);
		}
	}

}
