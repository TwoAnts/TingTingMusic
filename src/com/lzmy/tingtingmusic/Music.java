package com.lzmy.tingtingmusic;

import java.io.Serializable;

public class Music implements Serializable{
	/**
	 * Music
	 */
	private static final long serialVersionUID = 4633738436984100369L;
	public int id;
	public String name = null;
	public String album = null;
	public String artist = null;
	public String path = null;
	public int duration;
	public long size;
}
