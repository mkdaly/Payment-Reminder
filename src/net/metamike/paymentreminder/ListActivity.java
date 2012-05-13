package net.metamike.paymentreminder;

import net.metamike.paymentreminder.data.PaymentDBAdapter;
import net.metamike.paymentreminder.data.Payment;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ListActivity extends Activity {
	public static final String EDIT_INTENT = "net.metamike.paymentreminder.EntryActivity.EDIT";
	public static final String VIEW_INTENT = "net.metamike.paymentreminder.EntryActivity.VIEW";
	public static final String NEW_ACTION = "net.metamike.paymentreminder.EntryActivity.NEW";
	public static final String DATA = "DATA";
	
	private Intent newPayment;
	
	private ListView listView;
	private SimpleCursorAdapter adapter;
	private PaymentDBAdapter dbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        newPayment = new Intent(NEW_ACTION, Uri.EMPTY, getApplicationContext(), EntryActivity.class);
        dbAdapter = new PaymentDBAdapter(this);
        dbAdapter.open();
        Cursor c = dbAdapter.getAllPayments();
        startManagingCursor(c);
                
        listView = (ListView)findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i = new Intent(ListActivity.VIEW_INTENT);
				i.setClass(getApplicationContext(), ViewActivity.class);
				sendIntent(position, i);
			}
		});
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i = new Intent(ListActivity.EDIT_INTENT);
				i.setClass(getApplicationContext(), EntryActivity.class);
				sendIntent(position, i);
				return false;
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
	
	
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.list_menu, menu);
		menu.findItem(R.id.list_new_payment).setIntent(newPayment);
		return true;
	}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
	}



	private void sendIntent(int position, Intent i) {
		Cursor c = (Cursor)this.adapter.getItem(position);
		i.putExtras(populateBundle(c));
		startActivity(i);
	}

	private Bundle populateBundle(Cursor c) {
		Payment p = new Payment(c);
		return p.generateBundle();
	}

}
