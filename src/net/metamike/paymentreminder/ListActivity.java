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
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ListActivity extends Activity {
	public static final String EDIT_INTENT = "net.metamike.paymentreminder.EntryActivity.EDIT";
	public static final String VIEW_INTENT = "net.metamike.paymentreminder.EntryActivity.VIEW";
	public static final String DELETE_INTENT = "net.metamike.paymentreminder.EntryActivity.DELETE";
	public static final String NEW_ACTION = "net.metamike.paymentreminder.EntryActivity.NEW";
	public static final String DATA = "DATA";
	
	private Intent newPayment;
	private Intent viewPayment;
	private Intent editPayment;
	private Intent deletePayment;
	
	private ListView listView;
	private SimpleCursorAdapter adapter;
	private PaymentDBAdapter dbAdapter;
	private MenuInflater inflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        inflater = getMenuInflater();

        newPayment = new Intent(NEW_ACTION).setClass(getApplicationContext(), EntryActivity.class);
        viewPayment = new Intent(VIEW_INTENT).setClass(getApplicationContext(), ViewActivity.class);
        editPayment = new Intent(EDIT_INTENT).setClass(getApplicationContext(), EntryActivity.class);
        deletePayment = new Intent(DELETE_INTENT).setClass(getApplicationContext(), ListActivity.class);

        dbAdapter = new PaymentDBAdapter(this);
        dbAdapter.open();
        Cursor c = dbAdapter.getAllPayments();
        startManagingCursor(c);
        adapter = new SimpleCursorAdapter(this, R.layout.reminder_item, c, 
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
				sendIntent(position, ListActivity.this.viewPayment);
			}
		});
        registerForContextMenu(listView);
        listView.setAdapter(adapter);
	}
		
	@Override
	protected void onStart() {
		super.onStart();
		Intent i = getIntent();
		if (i == null) {
			return;
		}
		if (DELETE_INTENT.equals(i.getAction())) {
			this.deleteEntry(i.getStringExtra(PaymentDBAdapter.KEY_ID));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		inflater.inflate(R.menu.list_menu, menu);
		menu.findItem(R.id.list_new_payment).setIntent(newPayment);
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		inflater.inflate(R.menu.list_context_menu, menu);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
		Bundle payment = (new Payment((Cursor)ListActivity.this.adapter.getItem(info.position))).generateBundle();
		menu.setHeaderTitle(payment.getCharSequence(PaymentDBAdapter.KEY_ACCOUNT));
		menu.findItem(R.id.list_menu_view).setIntent(viewPayment.putExtras(payment));
		menu.findItem(R.id.list_menu_edit).setIntent(editPayment.putExtras(payment));
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);
		switch (item.getItemId()) {
		case R.id.list_menu_delete:
			deleteEntry(Long.toString(((AdapterContextMenuInfo)item.getMenuInfo()).id));
			updateAdapter();
			return true;
		}
		return false;
	}
	
	public void deleteEntry(String id) {
		//TODO: Show a toast to show if successful. 
		dbAdapter.deletePayment(id);
	}
	
	private void updateAdapter() {
		adapter.changeCursor(dbAdapter.getAllPayments());
		adapter.notifyDataSetChanged();
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
