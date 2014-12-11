package com.example.dama.figures;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.example.dama.R;
import com.example.dama.other.BoardPoint;

public class Pawn extends Figure {
		
	public Pawn(Context context) {
		super(context);
	}
	
	@Override
	public BoardPoint[] get_possible_moves() {
		BoardPoint[] result = new BoardPoint[2];
		
		int y_new; 
		if(color == WHITE){
			y_new = y_relative - 1;
		} else {
			y_new = y_relative + 1;
		}
		
		result[0] = new BoardPoint(getContext());
		result[0].set_x_relative(x_relative - 1);
		result[0].set_y_relative(y_new);
		result[1] = new BoardPoint(getContext());
		result[1].set_x_relative(x_relative + 1);
		result[1].set_y_relative(y_new);
		
		return result;
	}

	@Override
	public Drawable get_drawable_image() {
		if(color == WHITE){
			return getResources().getDrawable(R.drawable.circle_white);
		} else {
			return getResources().getDrawable(R.drawable.circle_black);
		}
	}
}
