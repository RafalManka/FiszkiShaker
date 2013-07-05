package pl.rafalmanka.harrypotter.spellshaker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.util.Log;

public class FileHandler {
	private final String TAG = "FileManager";
	private final String FILENAME = "pl.txt";
	private File mFiszkiDir = new File(
			Environment.getExternalStorageDirectory(), "FiszkiShaker");
	private File mFile;

	public FileHandler() {
		mFile = new File(mFiszkiDir, FILENAME);
	}

	public List<String[]> getAllRecords() {
		Log.d(TAG, "getAllRecords() method");
		if (!trySDCardReady())
			return null;
		prepareDir();

		Log.d(TAG, "creating empty list of strings");
		List<String[]> list = new ArrayList<String[]>();
		try {
			Log.d(TAG, "creating instance of BufferReader");
			BufferedReader buffreader = new BufferedReader(
			new InputStreamReader(new FileInputStream(mFile)));			
			String[] fileName = mFile.getName().split("\\.");
			Log.d(TAG, "extracting language from filename: " + fileName[0]);
			String line = "";
			while ((line = buffreader.readLine()) != null) {
				String[] row = new String[3];
				Log.d(TAG, "new line fetched from file: " + line);
				String[] phrase = line.split(";");
				Log.d(TAG, "phrase: " + phrase[0]);
				row[0] = phrase[0];
				Log.d(TAG, "description: " + phrase[1]);
				row[1] = phrase[1];
				Log.d(TAG, "language: " + fileName[0]);
				row[2] = fileName[0];
				Log.d(TAG, "adding row");
				list.add(row);
			}
			buffreader.close();
			Log.d(TAG, "returning results");
			return list;
		} catch (FileNotFoundException e) {
			Log.e(TAG, "unable to open file " + mFile.getAbsolutePath());
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			Log.e(TAG, "unable to read file");
			e.printStackTrace();
			return null;
		}
	}

	private boolean trySDCardReady() {
		Log.d(TAG, "checking external storage state");
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			Log.d(TAG, "SD card ready");
			return true;
		} else {
			Log.e(TAG, "SD card storage unavaileable");
			return false;
		}
	}

	private void prepareDir() {
		Log.d(TAG, "preparing dir to write to");
		if (!mFiszkiDir.exists()) {
			mFiszkiDir.mkdirs();
			Log.d(TAG, "new directory created");
		} else {
			Log.d(TAG, "directory already exists");
		}
	}

	public String[] readFromFile(int lineNumber) {
		if (!trySDCardReady())
			return null;
		prepareDir();
		try {
			BufferedReader buffreader = new BufferedReader(
					new InputStreamReader(new FileInputStream(mFile)));
			Log.d(TAG, "bufferedReader created");
			for (int i = 0; i < lineNumber; i++) {
				buffreader.readLine();
			}
			Log.d(TAG, "skipped lines that are before line " + lineNumber);
			String line = buffreader.readLine();
			Log.d(TAG, "returning line: " + line);
			String[] fiszkaInfo = lineToArray(line);
			buffreader.close();
			return fiszkaInfo;
		} catch (FileNotFoundException e) {
			Log.e(TAG, "unable to open file");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			Log.e(TAG, "unable to read file");
			e.printStackTrace();
			return null;
		}
	}

	private String[] lineToArray(String line) {
		return line.split(";");
	}

	public int getNumberOfElements() {
		Log.d(TAG, "counting number of lines in a file");
		LineNumberReader lineNumberReader;
		try {
			lineNumberReader = new LineNumberReader(new FileReader(mFile));
			Log.d(TAG, "LineNumberReader iniciated");
			lineNumberReader.skip(Long.MAX_VALUE);
			int numberOfLines = lineNumberReader.getLineNumber();
			Log.d(TAG, "lines counted = " + numberOfLines);
			lineNumberReader.close();
			return numberOfLines;
		} catch (FileNotFoundException e) {
			Log.d(TAG, "FileNotFoundException");
			e.printStackTrace();
		} catch (IOException e) {
			Log.d(TAG, "IOException");
			e.printStackTrace();
		}
		return 0;

	}

}