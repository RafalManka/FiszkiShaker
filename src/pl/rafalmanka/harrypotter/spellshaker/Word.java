package pl.rafalmanka.harrypotter.spellshaker;

import android.util.Log;

public class Word {
	public static final String TAG = Word.class.getSimpleName();
    //private variables
    int _id;
    String _word;
    String _description;
    String _language;
     
    // Empty constructor
    public Word(){         
    }
    
    // constructor
    public Word(int id, String word, String description, String language){
    	Log.d(TAG, "4 argument constructor (id:"+id+",word:"+word+",description:"+description+",languuage:"+language+")");
    	this._id = id;
        this._word = word;
        this._description = description;
        this._language = language;
    }
     
    // constructor
    public Word(String word, String description, String language){
    	Log.d(TAG, "3 argument constructor (word:"+word+",description:"+description+",languuage:"+language+")");
        this._word = word;
        this._description = description;
        this._language = language;
    }
    // getting ID
    public int getID(){
        return this._id;
    }
     
    // setting id
    public void setID(int id){
        this._id = id;
    }
     
    // getting word
    public String getWord(){
        return this._word;
    }
     
    // setting word
    public void setWord(String word){
        this._word = word;
    }
     
    // getting description
    public String getDescription(){
        return this._description;
    }
     
    // setting description
    public void setDescription(String phone_number){
        this._description = phone_number;
    }
    
 // getting description
    public String getLanguage(){
        return this._language;
    }
     
    // setting description
    public void setLanguage(String language){
        this._language = language;
    }
}
