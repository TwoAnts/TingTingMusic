package com.lzmy.tingtingmusic;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;

import javax.security.auth.PrivateCredentialPermission;

import android.R.integer;
import android.R.interpolator;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ReceiverCallNotAllowedException;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.text.StaticLayout;

public class MusicPlayerService extends Service{
	
	public static final int STATE_SINGLE_REPEAT = 0;
	public static final int STATE_ALL_REPEAT = 1;
	public static final int STATE_NORMAL = 2;
	public static final int STATE_RANDOM = 3;
	
	 //服务要发送的一些Action  
    public static final String UPDATE_ACTION = "com.lzmy.tingting.action.UPDATE_ACTION";  //更新动作  
    public static final String CTL_ACTION = "com.lzmy.tingting.action.CTL_ACTION";        //控制动作  
    public static final String MUSIC_CURRENT = "com.lzmy.tingting.action.MUSIC_CURRENT";  //当前音乐播放时间更新动作  
    public static final String MUSIC_DURATION = "com.lzmy.tingting.action.MUSIC_DURATION";//新音乐长度更新动作  
    
    public static final int STARTID_PLAY = 0;
    public static final int STARTID_PAUSE = 1;
    public static final int STARTID_CONTINUE = 2;
    public static final int STARTID_STOP = 3;
    public static final int STARTID_PREVIOUS = 4;
    public static final int STARTID_NEXT = 5;
    public static final int STARTID_PROGRESS_CHANGE = 6;
    public static final int STARTID_PLAYING = 7;
    
    
    private MyReceiver receiver = null;
	
	private MediaPlayer player = null;
	private ArrayList<Music> list = null;

	private Position position = new Position();
	private int currenttime;
	private int state = STATE_NORMAL;
	private Boolean isPause = false;
	private Music currentMusic = null;
	private ArrayList<Integer> randomlist = null;
	
	
	/** 
     * handler用来接收消息，来发送广播更新播放时间 
     */  
    private Handler handler = new Handler() {  
        public void handleMessage(android.os.Message msg) {  
            if (msg.what == 1) {  
                if(player != null) {  
                    currenttime = player.getCurrentPosition(); // 获取当前音乐播放的位置  
                    Intent intent = new Intent();  
                    intent.setAction(MUSIC_CURRENT);  
                    intent.putExtra("currentTime", currenttime);  
                    sendBroadcast(intent); // 给PlayerActivity发送广播  
                    handler.sendEmptyMessageDelayed(1, 1000);  
                }  
                  
            }  
        };  
    };  
	

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		player = new MediaPlayer();
		
		player.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				switch(state){
					
				case STATE_SINGLE_REPEAT:
					break;
				case STATE_ALL_REPEAT:
					currentMusic = list.get(position.next());
                    play(currentMusic, 0);
					break;
				case STATE_RANDOM:
					currentMusic = list.get(getRandomPosition(position.next(), list.size() - 1, false));
					break;
				default:
					currentMusic = list.get(position.next());
					break;
				}
				play(currentMusic, 0);
				Intent intent = new Intent(UPDATE_ACTION);
				intent.putExtra("current",list.indexOf(currentMusic));
				sendBroadcast(intent);  
			}
		});
		
		receiver = new MyReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(CTL_ACTION);
		registerReceiver(receiver, intentFilter);
	}



	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		IBinder myBinder = new Binder(){
			public Boolean setlistBinder(ArrayList<Music> inlist){
				return setList(inlist);
			}
			
			public void playBinder(Music inMusic, int time){
				play(inMusic, time);
			}
			public void pauseBinder(){
				pause();
			}
			public void resumeBinder(){
				resume();
			}
			public void setstateBinder(int setstate){
				setState(setstate);
			}
			
			
		};
		
		return myBinder;
	}
	
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		ArrayList<Music> intentlist = (ArrayList<Music>)intent.getSerializableExtra("musiclist");
		
		
		switch(startId){
		case STARTID_PLAY:
			play(currentMusic, 0);
			break;
		case STARTID_PAUSE:
			pause();     
			break;
		case STARTID_STOP:
			stop();     
			break;
		case STARTID_CONTINUE:
			resume();     
			break;
		case STARTID_PREVIOUS:
			previous();     
			break;
		case STARTID_NEXT:
			next();     
			break;
		case STARTID_PROGRESS_CHANGE:
			currenttime = intent.getIntExtra("progress", -1);  
            play(currentMusic, currenttime);  
			break;
		case STARTID_PLAYING:
			handler.sendEmptyMessage(1);
			break;
		
		}
		return super.onStartCommand(intent, flags, startId);
	}



	@Override  
    public void onDestroy() {  
        if (player != null) {  
        	player.stop();  
        	player.release();  
        	player = null;  
        }  
          
    }  
	
	
	
	
	
	
	
	private void play(Music music, int time){
		if (list == null) {
			return;
		}
		
		try {
			if(time >= 0 && time <= music.duration){
				player.setDataSource(music.path);
				player.prepare();
				player.seekTo(time);
				player.start();
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void pause(){
		if(player != null && player.isPlaying() == true){
			player.pause();
			isPause = true;
		}
	}
	
	private void resume() {  
	        if (isPause) {  
	            player.start();    
	            isPause = false;
	        }  
	    }  
	
	private void previous(){
		position.previous();
		if(state == STATE_RANDOM){
			currentMusic = list.get(randomlist.get(position.position));
		}else{
			currentMusic = list.get(position.position);
		}
		play(currentMusic, 0);
		Intent intent = new Intent(UPDATE_ACTION);
		intent.putExtra("current",list.indexOf(currentMusic));
		sendBroadcast(intent);  
	}
	
	private void next(){
		position.next();
		if(state == STATE_RANDOM){
			currentMusic = list.get(randomlist.get(position.position));
		}else{
			currentMusic = list.get(position.position);
		}
		play(currentMusic, 0);
		Intent intent = new Intent(UPDATE_ACTION);
		intent.putExtra("current",list.indexOf(currentMusic));
		sendBroadcast(intent);  
	}
	
	private void stop(){
		if (player != null) {  
            player.stop();  
        }  
	}
	
	private class MyReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			setState(intent.getIntExtra("state", -1));
			
		}
		
	}
	
	private ArrayList<Music> getList() {
		return list;
	}

	private Boolean setList(ArrayList<Music> list){
		if(list == null || list.size() == 0){
			return false;
		}
		this.list = list;
		this.currentMusic = this.list.get(0);
		this.position.position = 0;
		this.position.maxposition = list.size() - 1;
		this.currenttime = 0;
		return true;
	}


	private int getPosition() {
		return position.position;
	}

	private int getState() {
		return state;
	}

	private void setState(int state) {
		this.state = state;
		switch (state) {
		case STATE_SINGLE_REPEAT:
		case STATE_ALL_REPEAT:
		case STATE_NORMAL:
		case STATE_RANDOM:
			this.state = state;
			break;
		default:
			break;
		}
	}

	private int getRandomPosition(int position, int maxposition, Boolean resetOrder){
		if(resetOrder || randomlist == null){
			int index;
			while(randomlist.size() < maxposition + 1){
				do {
					index = (int) (Math.random() * maxposition);
				} while (randomlist.contains(index));
				randomlist.add(index);
			}
		}
		
		return (int)randomlist.get(position);
	}
	
	private final class PreparedListener implements OnPreparedListener {
		private int currentTime;  
		  
	    public PreparedListener(int currentTime) {  
	    	this.currentTime = currentTime;  
	    }  
	  
		@Override
		public void onPrepared(MediaPlayer mp) {
			// TODO Auto-generated method stub
			
			player.start(); // 开始播放  
            if (currentTime > 0) { // 如果音乐不是从头播放  
                player.seekTo(currentTime);  
            }  
            Intent intent = new Intent();  
            intent.setAction(MUSIC_DURATION);  //通过Intent来传递歌曲的总长度  
            sendBroadcast(intent);  
		}  
		
	}
	
	private class Position {
		int position = 0;
		int maxposition = 0;
		
		public int previous(){
			if(position > 0){
				position--;
			}
			return position;
		}
		
		public int next(){
			position++;
			if(position > maxposition){
				position = 0;
			}
			return position;
		}
	}

}
