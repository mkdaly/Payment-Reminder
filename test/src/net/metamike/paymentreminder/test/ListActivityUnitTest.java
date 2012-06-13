package net.metamike.paymentreminder.test;

import java.util.Arrays;
import java.util.Calendar;

import net.metamike.paymentreminder.ListActivity;
import net.metamike.paymentreminder.data.PaymentDBAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.test.ActivityUnitTestCase;
import android.test.RenamingDelegatingContext;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.Suppress;
import android.text.format.Time;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListActivityUnitTest extends ActivityUnitTestCase<ListActivity> {
	private Context testContext;
	private String[] expectedAccounts;
	private String[] expectedDates;
	private Long[] expectedIDs;
	private Integer[] expectedOrder;
	private PaymentDBAdapter dbAdapter;

	
	
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
		
		/** Order should be by date, asc. */
		expectedOrder = new Integer[]{0, 3, 1, 2};
		
		dbAdapter = new PaymentDBAdapter(testContext);
		dbAdapter.open();		
	}
	
	@Override
	public void tearDown() {
		dbAdapter.close();
	}
	
	public void testAdapter() {
		loadData(dbAdapter);

		startActivity(new Intent(), null, null);
		ListActivity activity = this.getActivity();
		ListView listView = (ListView)activity.findViewById(net.metamike.paymentreminder.R.id.listView);
		ListAdapter listAdapter = listView.getAdapter();
		assertEquals(expectedAccounts.length, listAdapter.getCount());
	}
	
	public void testOnClickListener() {
		//PaymentDBAdapter dbAdapter = new PaymentDBAdapter(testContext);
		//dbAdapter.open();		
		loadData(dbAdapter);
		startActivity(new Intent(), null, null);
		ListActivity activity = this.getActivity();
		ListView listView = (ListView)activity.findViewById(net.metamike.paymentreminder.R.id.listView);
		
		for (int idx = 0; idx < expectedOrder.length; idx++) {
			listView.performItemClick(null, idx, listView.getAdapter().getItemId(idx));
			Intent i = getStartedActivityIntent();
			assertEquals(ListActivity.VIEW_INTENT, i.getAction());
			assertEquals(expectedAccounts[expectedOrder[idx]], i.getStringExtra(PaymentDBAdapter.KEY_ACCOUNT));
			assertEquals(expectedDates[expectedOrder[idx]], i.getStringExtra(PaymentDBAdapter.KEY_DATE_DUE));
			assertEquals(expectedIDs[expectedOrder[idx]].toString(), i.getStringExtra(PaymentDBAdapter.KEY_ID));
			assertEquals("", i.getStringExtra(PaymentDBAdapter.KEY_AMOUNT_DUE));
			assertEquals("", i.getStringExtra(PaymentDBAdapter.KEY_AMOUNT_PAID));
			assertEquals("", i.getStringExtra(PaymentDBAdapter.KEY_DATE_TRANSFER));
			assertEquals("", i.getStringExtra(PaymentDBAdapter.KEY_CONFIRMATION));
		}
	}
	
	@Suppress
	public void testOnLongClickListeners() {
		//PaymentDBAdapter dbAdapter = new PaymentDBAdapter(testContext);
		//dbAdapter.open();		
		loadData(dbAdapter);
		startActivity(new Intent(), null, null);
		ListActivity activity = this.getActivity();
		ListView listView = (ListView)activity.findViewById(net.metamike.paymentreminder.R.id.listView);

		/** long click listeners should display context menu */
		for (int idx = 0; idx < expectedOrder.length; idx++) {
			listView.showContextMenuForChild(listView.getChildAt(idx));
			//listView.getSelectedView().performLongClick();
			//listView.performItemClick(null, idx, listView.getAdapter().getItemId(idx));
			Intent i = getStartedActivityIntent();
			assertEquals(ListActivity.EDIT_INTENT, i.getAction());
			assertEquals(expectedAccounts[expectedOrder[idx]], i.getStringExtra(PaymentDBAdapter.KEY_ACCOUNT));
			assertEquals(expectedDates[expectedOrder[idx]], i.getStringExtra(PaymentDBAdapter.KEY_DATE_DUE));
			assertEquals(expectedIDs[expectedOrder[idx]].toString(), i.getStringExtra(PaymentDBAdapter.KEY_ID));
			assertEquals("", i.getStringExtra(PaymentDBAdapter.KEY_AMOUNT_DUE));
			assertEquals("", i.getStringExtra(PaymentDBAdapter.KEY_AMOUNT_PAID));
			assertEquals("", i.getStringExtra(PaymentDBAdapter.KEY_DATE_TRANSFER));
			assertEquals("", i.getStringExtra(PaymentDBAdapter.KEY_CONFIRMATION));
		}
	}
	
	@Suppress
	public void testDelete() {
		//PaymentDBAdapter dbAdapter = new PaymentDBAdapter(testContext);
		//dbAdapter.open();		
		loadData(dbAdapter);
		Intent delIntent = new Intent(testContext, ListActivity.class);
		delIntent.setAction(ListActivity.DELETE_INTENT);
		delIntent.putExtra(PaymentDBAdapter.KEY_ID, expectedIDs[1].toString());
		startActivity(delIntent, null, null);
		ListActivity activity = this.getActivity();
		ListView listView = (ListView)activity.findViewById(net.metamike.paymentreminder.R.id.listView);
		ListAdapter adapter = listView.getAdapter();
		assertEquals(expectedIDs.length -1, adapter.getCount());
		Long[] cursorIDs = new Long[adapter.getCount()];
		for (int i = 0; i < adapter.getCount(); i++) {
			Cursor c = (Cursor)adapter.getItem(i);
			cursorIDs[i] = c.getLong(c.getColumnIndex(PaymentDBAdapter.KEY_ID));
		}
		assertTrue(Arrays.binarySearch(cursorIDs, expectedIDs[1]) < 0);
		
		
		
	}
	
	public void testMenu() {
		startActivity(new Intent(), null, null);
		ListActivity activity = getActivity();
		assertTrue(getInstrumentation().invokeMenuActionSync(activity, net.metamike.paymentreminder.R.id.list_new_payment, 0));
		Intent i = getStartedActivityIntent();
		assertNotNull(i);
		assertEquals(ListActivity.NEW_ACTION, i.getAction());
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
