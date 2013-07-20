package pl.rafalmanka.fiszki.shaker;

import java.util.ArrayList;
import java.util.HashMap;
 
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
 
public class WordListViewAdapter extends BaseAdapter {
 
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
 
    public WordListViewAdapter(Activity activity, ArrayList<HashMap<String, String>> data) {
        this.activity = activity;
        this.data=data;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
 
    public int getCount() {
        return data.size();
    }
 
    public Object getItem(int position) {
        return position;
    }
 
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_item, null);
 
        TextView name = (TextView)vi.findViewById(R.id.name);
 
        HashMap<String, String> item = new HashMap<String, String>();
        item = data.get(position);
        //Setting all values in listview
        name.setText(item.get(WordsListActivity.KEY_WORD)+" ("+item.get(WordsListActivity.KEY_TRANSLATION)+")");
        return vi;
    }
}
