package com.lzmy.tingtingmusic;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

public class MusicFiles {
	
	private Context mContext = null;
	private String mainpath = null;
	
	
	private ArrayList<Music> data = null;
	
	private static MusicFiles mMusicFiles = null;
	
	private Cursor cursor = null;
	
	private MusicFiles(Context context){
		this.mContext = context;
	}
	
	public static MusicFiles getInstance(Context context, String mainpath){
		if(!mainpath.endsWith(File.separator)){
			mainpath = mainpath + File.separator;
		}
		if(mMusicFiles == null){
			mMusicFiles = new MusicFiles(context);
			mMusicFiles.mainpath = mainpath;
			return mMusicFiles;
		}
		mMusicFiles.mContext = context;
		mMusicFiles.mainpath = mainpath;
		return mMusicFiles;
	}
	
	public ArrayList<Music> findall(){
		if(data == null){
			data = new ArrayList<Music>();
		}
		File dir = new File(mainpath);
		if(dir.exists() == false){
			dir.mkdirs();
		}
		File[] files = dir.listFiles();
		for(File currentFile : files){
			if(currentFile.isFile()){
				data.add(filetomusic(currentFile));
			}
		}
		if(data.size() == 0){
			data = null;
		}
		return data;
	}
	
	public Music getMusic(String path){
		for(Music music: data){
			if(path.equals(music.path)){
				return music;
			}
		}
		return null;
	}
	
	
	public Context getmContext() {
		return mContext;
	}

	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}

	public String getMainpath() {
		return mainpath;
	}

	public void setMainpath(String mainpath) {
		this.mainpath = mainpath;
	}

	public ArrayList<Music> getData() {
		return data;
	}


	private Music filetomusic(File file){
		if(file.isFile()){
			return getMusicbyCursor(getCursorfromPath(file.getAbsolutePath()));
		}
		return null;
	}
	
	
	/** 
	 * 通过MP3路径得到指向当前MP3的Cursor 
	 *  
	 * @param filePath 
	 *            MP3路径 
	 *  
	 * @return Cursor 返回的Cursor指向当前MP3 
	 */  
	private Cursor getCursorfromPath(String filePath) {  
	    String path = null;  
	    if(cursor == null){
		    cursor = mContext.getContentResolver().query(  
		            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,  
		            MediaStore.Audio.Media.DEFAULT_SORT_ORDER);  
	    }
	    //System.out.println(cursor.getString(cursor.getColumnIndex("_data")));  
	    if (cursor.moveToFirst()) {  
	        do {  
	            // 通过Cursor 获取路径，如果路径相同则break；  
	            path = cursor.getString(cursor  
	                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));  
	            // 查找到相同的路径则返回，此时cursorPosition 便是指向路径所指向的Cursor 便可以返回了  
	            if (path.equals(filePath)) {  
	                // System.out.println("audioPath = " + path);  
	                // System.out.println("filePath = " + filePath);  
	                // cursorPosition = c.getPosition();  
	                break;  
	            }  
	        } while (cursor.moveToNext());  
	    }  
	    
	    if (!path.equals(filePath)) {  
	    	return null;
	    }
	    // 这两个没有什么作用，调试的时候用  
	    // String audioPath = c.getString(c  
	    // .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));  
	    //  
	    // System.out.println("audioPath = " + audioPath);  
	    return cursor;  
	}  
	
	private Music getMusicbyCursor(Cursor currentCursor){
		if(currentCursor == null){
			return null;
		}
		Music music = new Music();
		music.id = currentCursor.getInt(currentCursor.getColumnIndex(MediaStore.Audio.Media._ID));
		music.name = currentCursor.getString(currentCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
		music.album = currentCursor.getString(currentCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
		music.artist = currentCursor.getString(currentCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
		music.album = currentCursor.getString(currentCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
		music.path = currentCursor.getString(currentCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
		music.duration = currentCursor.getInt(currentCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
		music.size = currentCursor.getLong(currentCursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
		return music;
	}
	
}
