package net.metamike.paymentreminder.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(PaymentDBAdapterTest.class);
		suite.addTestSuite(EntryActivityTest.class);
		suite.addTestSuite(EntryActivityUnitTest.class);
		suite.addTestSuite(ListActivityTest.class);
		suite.addTestSuite(ListActivityUnitTest.class);
		suite.addTestSuite(PaymentsTest.class);
		suite.addTestSuite(ViewActivityUnitTest.class);
		//suite.addTestSuite(RemindersTest.class);
		//$JUnit-END$
		return suite;
	}

}
