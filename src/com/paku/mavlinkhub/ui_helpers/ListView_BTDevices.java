package com.paku.mavlinkhub.ui_helpers;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class ListView_BTDevices extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
	    
	    Bundle extras = getIntent().getExtras();
	    if (extras != null) {
	        String[] values = extras.getStringArray("BTDevList");
		    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		            android.R.layout.simple_list_item_1, values);		
		    setListAdapter(adapter);	        
	    }
	 

			
	    //getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	  }

/*	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.main, menu);
	    return true;
	  }

	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    Toast.makeText(this,
	        String.valueOf(getListView().getCheckedItemCount()),
	        Toast.LENGTH_LONG).show();
	    return true;
	  }	
*/	

}


/*		
setContentView(R.layout.listview_bt_select_device);

//final ArrayList<HashMap<String,String>> listItems = new ArrayList<HashMap<String,String>>();

// prepare the list of all records
List<HashMap<String, String>> listItems = new ArrayList<HashMap<String, String>>();

for(int i = 0; i < 10; i++){
	HashMap<String, String> map = new HashMap<String, String>();
	map.put("rowid", "" + i);
	map.put("col_1", "col_1_item_" + i);
	listItems.add(map);
}		

String[] from = new String[] {"rowid", "col_1"};
int[] to = new int[] { R.id.item1, R.id.item2};

SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.listviewrow_bt_select_device, from, to);
*/

