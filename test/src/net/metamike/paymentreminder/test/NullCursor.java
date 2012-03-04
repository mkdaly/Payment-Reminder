package net.metamike.paymentreminder.test;

class NullCursor extends MockCursor {

	@Override
	public int getColumnIndex(String columnName) {
		return 0;
	}

	@Override
	public boolean isNull(int columnIndex) {
		return true;
	}
	
	
}