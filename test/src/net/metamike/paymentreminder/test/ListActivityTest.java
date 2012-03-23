package net.metamike.paymentreminder.test;

import net.metamike.paymentreminder.ListActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

public class ListActivityTest extends ActivityInstrumentationTestCase2<ListActivity> {
	ListActivity activity;
	
	public ListActivityTest() {
		super("net.metamike.paymentreminder", ListActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(false);
		activity = this.getActivity();
		
	}
	
	public void testPreconditions() {
		assertNotNull("ListView is null", activity.findViewById(net.metamike.paymentreminder.R.id.listView));
	}
	
	public void testLayout() {
		ListView view = (ListView)activity.findViewById(net.metamike.paymentreminder.R.id.listView);
		assertNotNull(view.getAdapter());
	}

}
