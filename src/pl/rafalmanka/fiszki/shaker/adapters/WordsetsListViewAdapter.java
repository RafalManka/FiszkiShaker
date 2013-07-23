package pl.rafalmanka.fiszki.shaker.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import pl.rafalmanka.fiszki.shaker.R;
import pl.rafalmanka.fiszki.shaker.model.DatabaseHandler;
import pl.rafalmanka.fiszki.shaker.view.ChooseLocalSetActivity;
import pl.rafalmanka.fiszki.shaker.view.MainActivity;
import pl.rafalmanka.fiszki.shaker.view.SettingsActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class WordsetsListViewAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;

	public WordsetsListViewAdapter(Activity activity,
			ArrayList<HashMap<String, String>> data) {
		this.activity = activity;
		this.data = data;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

	public View getView(final int position, final View convertView,
			final ViewGroup parent) {
		View vi = inflater.inflate(R.layout.list_item_manage_set, null);

		final HashMap<String, String> item = data.get(position);

		TextView name = (TextView) vi.findViewById(R.id.name);
		Button chooseButton = (Button) vi
				.findViewById(R.id.button_choose_wordset);
		chooseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences preferenceManager = PreferenceManager
						.getDefaultSharedPreferences(v.getContext());
				Editor editor = preferenceManager.edit();
				editor.putString(SettingsActivity.CURRENT_WORDSET,
						item.get(ChooseLocalSetActivity.KEY_WORDSET));
				editor.commit();
				Intent intent = new Intent(v.getContext(), MainActivity.class);
				v.getContext().startActivity(intent);

			}
		});

		Button deleteButton = (Button) vi
				.findViewById(R.id.button_delete_wordset);
		deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {

				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							DatabaseHandler databaseHandler = new DatabaseHandler(
									v.getContext());
							databaseHandler.deleteWordset(item
									.get(ChooseLocalSetActivity.KEY_WORDSET));
							Intent intent = new Intent(v.getContext(),MainActivity.class);
							v.getContext().startActivity(intent);
							break;

						case DialogInterface.BUTTON_NEGATIVE:
							// No button clicked
							break;
						}
					}
				};

				new AlertDialog.Builder(v.getContext())
						.setMessage(
								R.string.are_you_sure_you_want_to_delete_wordset)
						.setPositiveButton(R.string.yes, dialogClickListener)
						.setNegativeButton(R.string.no, dialogClickListener)
						.show();

			}
		});

		// Setting all values in listview
		name.setText(item.get(ChooseLocalSetActivity.KEY_WORDSET));
		return vi;
	}
}
