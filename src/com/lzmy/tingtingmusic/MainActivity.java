package com.lzmy.tingtingmusic;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private Button previousBtn; // 上一首
    private Button repeatBtn; // 重复（单曲循环、全部循环）
    private Button playBtn; // 播放（播放、暂停）
    private Button shuffleBtn; // 随机播放
    private Button nextBtn; // 下一首
    private TextView musicTitle;// 歌曲标题
    private TextView musicDuration; // 歌曲时间
    private Button musicPlaying; // 歌曲专辑
    private ImageView musicAlbum; // 专辑封面

    private ListView mMusic_List;
    private SimpleAdapter mMusic_ListAdapter;
    private ArrayList<Music> list;
    private ArrayList<HashMap<String, Object>> viewlist;
    private static String mainpath = "/mnt/sdcard/TingTingMusic";
    private MusicFiles mMusicFiles;

    public static final String UPDATE_ACTION = "com.lzmy.tingting.action.UPDATE_ACTION";  //更新动作
    public static final String CTL_ACTION = "com.lzmy.tingting.action.CTL_ACTION";        //控制动作
    public static final String MUSIC_CURRENT = "com.lzmy.tingting.action.MUSIC_CURRENT";  //音乐当前时间改变动作
    public static final String MUSIC_DURATION = "com.lzmy.tingting.action.MUSIC_DURATION";//音乐播放长度改变动作
    public static final String MUSIC_PLAYING = "com.lzmy.tingting.action.MUSIC_PLAYING";  //音乐正在播放动作
    public static final String REPEAT_ACTION = "com.lzmy.tingting.action.REPEAT_ACTION";  //音乐重复播放动作
    public static final String SHUFFLE_ACTION = "com.lzmy.tingting.action.SHUFFLE_ACTION";//音乐随机播放动作

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity_layout);
		mainpath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/TingTingMusic";
		mMusicFiles = MusicFiles.getInstance(this, mainpath);

//		mMusic_List = (ListView)findViewById(R.id.music_list);
//		mMusic_ListAdapter = new SimpleAdapter(this, viewlist, 
//				R.layout.menu_item_layout, 
//				new String[]{"name","artist"}, 
//				new int[]{R.id.music_title, R.id.music_Artist});
//		mMusic_List.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent(MainActivity.this, MusicPlayerService.class);
//				intent.putExtra("musiclist", list);
//				//intent.putCharSequenceArrayListExtra("pathlist",list_to_pathlist(list));
//				startService(intent);
//			}
//			
//		});
		
		mfindviews();
		playBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				list = mMusicFiles.findall();
				Intent intent = new Intent(MainActivity.this, MusicPlayerService.class);
				intent.putExtra("musiclist", list);
				startService(intent);
			}
		});
		
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
	
	private void mfindviews(){
		previousBtn = (Button) findViewById(R.id.previous_music);
		repeatBtn = (Button) findViewById(R.id.repeat_music);
		playBtn = (Button) findViewById(R.id.play_music);
		shuffleBtn = (Button) findViewById(R.id.shuffle_music);
		nextBtn = (Button) findViewById(R.id.next_music);
		musicTitle = (TextView) findViewById(R.id.music_title);
		musicDuration = (TextView) findViewById(R.id.music_duration);
		musicPlaying = (Button) findViewById(R.id.playing);
		musicAlbum = (ImageView) findViewById(R.id.music_album);
	}
	
	private ArrayList<CharSequence> list_to_pathlist(ArrayList<HashMap<String, Object>> musiclist){
		ArrayList<CharSequence> pathList = new ArrayList<CharSequence>();
		Music music;
		for(HashMap<String, Object> imap: musiclist){
			music = (Music)imap.get("music");
			pathList.add(music.path);
		}
		if(pathList.size() == 0){
			return null;
		}
		return pathList;
	}

}
