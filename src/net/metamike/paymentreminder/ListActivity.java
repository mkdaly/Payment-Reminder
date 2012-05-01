package net.metamike.paymentreminder;

import net.metamike.paymentreminder.data.PaymentDBAdapter;
import net.metamike.paymentreminder.data.Payment;
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
	public static final String VIEW_INTENT = "net.metamike.paymentreminder.EntryActivity.VIEW";
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
                
        listView = (ListView)findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				sendIntent(position, new Intent(ListActivity.LOAD_INTENT));
			}
		});

        adapter = new SimpleCursorAdapter(this, R.layout.reminder_item, c, 
            	new String[]{
            		PaymentDBAdapter.KEY_ACCOUNT,
            		PaymentDBAdapter.KEY_DATE_DUE
            	}, new int[] {
            		R.id.firstLine,
            		R.id.secondLine
            	});

        listView.setAdapter(adapter);

        
	}
		
	private void sendIntent(int position, Intent i) {
		Cursor c = (Cursor)this.adapter.getItem(position);
		i.setClass(this, EntryActivity.class);
		i.putExtras(populateBundle(c));
		startActivity(i);
	}

	private Bundle populateBundle(Cursor c) {
		Payment p = new Payment(c);
		return p.generateBundle();
	}

}
