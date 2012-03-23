package net.metamike.paymentreminder;

import net.metamike.paymentreminder.data.PaymentDBAdapter;
import net.metamike.paymentreminder.data.Payments;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ListActivity extends Activity {
	public static final String LOAD_INTENT = "net.metamike.paymentreminder.EntryActivity.LOAD";
	public static final String DATA = "DATA";
	
	private ListView listView;
	private SimpleCursorAdapter adapter;
	private PaymentDBAdapter dbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        dbAdapter = new PaymentDBAdapter(this);
        dbAdapter.open();
        Cursor c = dbAdapter.getAllPayments();
        startManagingCursor(c);
        
        adapter = new SimpleCursorAdapter(this, R.id.reminderItemView, c, 
        	new String[]{
        		PaymentDBAdapter.KEY_ACCOUNT,
        		PaymentDBAdapter.KEY_DATE_DUE
        	}, new int[] {
        		R.id.firstLine,
        		R.id.secondLine
        	});
        
        listView = (ListView)findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				sendForEdit(position);
			}
		});
        listView.setAdapter(adapter);

        
	}
	
	private void sendForView(int position) {
		
	}
	
	private void sendForEdit(int position) {
		Cursor c = (Cursor)this.adapter.getItem(position);
		Intent i = new Intent(ListActivity.LOAD_INTENT);
		i.putExtras(populateBundle(c));
		startActivity(i);
	}

	//TODO:
	private Bundle populateBundle(Cursor c) {
		Bundle b = new Bundle();
		b.putString(PaymentDBAdapter.KEY_ACCOUNT, Payments.getAccount(c));
		b.putLong(PaymentDBAdapter.KEY_DATE_DUE, Payments.getDueDateAsLong(c));
		b.putLong(PaymentDBAdapter.KEY_AMOUNT_PAID, Payments.getAmountPaid(c));
		//TODO: doesn't like nulls. probably need to create a DAO
		b.putLong(PaymentDBAdapter.KEY_DATE_TRANSFER, Payments.getTransferDateAsLong(c));
		b.putString(PaymentDBAdapter.KEY_CONFIRMATION, Payments.getConfirmation(c));
		//TODO: the rest
		return b;
	}

}
