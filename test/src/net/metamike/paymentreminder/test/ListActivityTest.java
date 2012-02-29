package net.metamike.paymentreminder.test;

import net.metamike.paymentreminder.ListActivity;
import android.test.ActivityInstrumentationTestCase2;

public class ListActivityTest extends ActivityInstrumentationTestCase2<ListActivity> {
	ListActivity activity;
	
	public ListActivityTest() {
		super("net.metamike.paymentreminder", ListActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		activity = this.getActivity();
		
	}
	
	public void testPreconditions() {
		assertNotNull("ListActivity is null", activity.findViewById(net.metamike.paymentreminder.R.id.listView));
	}

}
