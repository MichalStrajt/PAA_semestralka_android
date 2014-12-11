package com.example.dama.figures;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.example.dama.other.BoardPoint;

public abstract class Figure extends ImageView {	

	public static final int WHITE = 0;
	public static final int BLACK = 1;
	public static final int PAWN = 0;
	public static final int QUEEN = 1;
		
	protected int x_relative;
	protected int y_relative;
	protected int type;
	protected int color;
	protected int end_line;
	
	public Figure(Context context) {
		super(context);
	}
	
	public abstract BoardPoint[] get_possible_moves();
	public abstract Drawable get_drawable_image();
	
	public void do_a_move(int x, int y){
		this.x_relative += x;
		this.y_relative += y;
	}

	public int get_x_relative() {
		return x_relative;
	}

	public void set_x_relative(int x) {
		this.x_relative = x;
	}

	public int get_y_relative() {
		return y_relative;
	}

	public void set_y_relative(int y) {
		this.y_relative = y;
	}

	public int get_type() {
		return type;
	}

	public void set_type(int type) {
		this.type = type;
	}

	public int get_color() {
		return color;
	}

	public void set_color(int color) {
		this.color = color;
	}
	
	public void set_end_line(int end_line){
		this.end_line = end_line;
	}
	
	public int get_end_line(){
		return end_line;
	}
}
