package net.metamike.paymentreminder.test;

import java.util.Calendar;

import net.metamike.paymentreminder.ListActivity;
import net.metamike.paymentreminder.data.PaymentDBAdapter;
import net.metamike.paymentreminder.data.Payments;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.test.ActivityUnitTestCase;
import android.test.RenamingDelegatingContext;
import android.text.format.Time;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListActivityUnitTest extends ActivityUnitTestCase<ListActivity> {
	private Context testContext;
	
	private String[] expectedAccounts = {"Test 1", "Test 2", "Test 3"};
	private Long[] expectedLongs = new Long[3];

	public ListActivityUnitTest() {
		super(ListActivity.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		testContext = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "test_");
		setActivityContext(testContext);
		
		Time t = new Time();
		t.set(1, Calendar.JANUARY, 2012);
		expectedLongs[0] = Long.valueOf(t.toMillis(false));
		t.set(1, Calendar.FEBRUARY, 2012);
		expectedLongs[1] = Long.valueOf(t.toMillis(false));
		t.set(1, Calendar.JUNE, 2012);
		expectedLongs[2] = Long.valueOf(t.toMillis(false));		
	}

	public void testAdapter() {
		PaymentDBAdapter dbAdapter = new PaymentDBAdapter(testContext);
		dbAdapter.open();		
		loadData(dbAdapter);

		startActivity(new Intent(), null, null);
		ListActivity activity = this.getActivity();
		ListView listView = (ListView)activity.findViewById(net.metamike.paymentreminder.R.id.listView);
		ListAdapter listAdapter = listView.getAdapter();
		assertEquals(3, listAdapter.getCount());
	}
	
	public void testClickListener() {
		PaymentDBAdapter dbAdapter = new PaymentDBAdapter(testContext);
		dbAdapter.open();		
		loadData(dbAdapter);
		startActivity(new Intent(), null, null);
		ListActivity activity = this.getActivity();
		ListView listView = (ListView)activity.findViewById(net.metamike.paymentreminder.R.id.listView);
		listView.performItemClick(null, 0, listView.getAdapter().getItemId(0));
		Intent i = getStartedActivityIntent();
		assertEquals(ListActivity.LOAD_INTENT, i.getAction());
		assertEquals(expectedAccounts[0], i.getStringExtra(PaymentDBAdapter.KEY_ACCOUNT));
		assertEquals(expectedLongs[0], Long.valueOf(i.getLongExtra(PaymentDBAdapter.KEY_DATE_DUE, Long.valueOf(-1))));
		assertEquals(0L, i.getLongExtra(PaymentDBAdapter.KEY_AMOUNT_PAID, Long.valueOf(-1)));
		assertNull(i.getLongExtra(PaymentDBAdapter.KEY_DATE_TRANSFER, Long.valueOf(-1)));
		assertEquals("", i.getStringExtra(PaymentDBAdapter.KEY_CONFIRMATION));
		
		
		
	}
	
	private void loadData(PaymentDBAdapter dbAdapter) {
		for (int i = 0; i < expectedAccounts.length; i++) {
			assertTrue(dbAdapter.insertPayment(expectedAccounts[i], null, expectedLongs[i], null, null, null));
		}
	}

}
