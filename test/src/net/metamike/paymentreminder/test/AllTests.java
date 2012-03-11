package net.metamike.paymentreminder.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(DBAdapterTest.class);
		suite.addTestSuite(EntryActivityTest.class);
		suite.addTestSuite(ListActivityTest.class);
		suite.addTestSuite(PaymentsTest.class);
		suite.addTestSuite(RemindersTest.class);
		//$JUnit-END$
		return suite;
	}

}
