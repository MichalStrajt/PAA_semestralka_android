package com.example.dama.other;

import android.content.Context;
import android.widget.ImageView;

public class BoardPoint extends ImageView {

	private int x_realtive;
	private int y_realtive;
		
	public BoardPoint(Context context) {
		super(context);
	}

	public int get_x_relative() {
		return x_realtive;
	}

	public void set_x_relative(int x) {
		this.x_realtive = x;
	}

	public int get_y_relative() {
		return y_realtive;
	}

	public void set_y_relative(int y) {
		this.y_realtive = y;
	}
}
