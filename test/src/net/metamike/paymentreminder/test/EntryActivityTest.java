package net.metamike.paymentreminder.test;

import net.metamike.paymentreminder.EntryActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.RelativeLayout;

public class EntryActivityTest extends ActivityInstrumentationTestCase2<EntryActivity> {
	private EntryActivity activity;
	
	
	public EntryActivityTest() {
		super("net.metamike.paymentreminder", EntryActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(false);
		activity = this.getActivity();

	}
	
	public void testPreconditions() {
		assertNotNull("Entry view is null", activity.findViewById(net.metamike.paymentreminder.R.id.entryView));
		
	}

	public void testLayout() {
		RelativeLayout entryView = (RelativeLayout) activity.findViewById(net.metamike.paymentreminder.R.id.entryView);
		
		
		
	}
	
	

}
