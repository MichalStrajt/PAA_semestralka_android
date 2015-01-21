package com.example.dama.figures;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.example.dama.R;
import com.example.dama.other.BoardPoint;

public class Queen extends Figure {
	
	public Queen(Context context) {
		super(context);
	}

	@Override
	public BoardPoint[] get_possible_moves() {
		BoardPoint[] result = new BoardPoint[4];
				
		result[0] = new BoardPoint(getContext());
		result[0].set_x_relative(x_relative - 1);
		result[0].set_y_relative(y_relative - 1);
		result[1] = new BoardPoint(getContext());
		result[1].set_x_relative(x_relative + 1);
		result[1].set_y_relative(y_relative - 1);
		result[2] = new BoardPoint(getContext());
		result[2].set_x_relative(x_relative - 1);
		result[2].set_y_relative(y_relative + 1);
		result[3] = new BoardPoint(getContext());
		result[3].set_x_relative(x_relative + 1);
		result[3].set_y_relative(y_relative + 1);
		
		return result;
	}

	@Override
	public Drawable get_drawable_image() {
		if(color == WHITE){
			return getResources().getDrawable(R.drawable.double_circle_white);
		} else {
			return getResources().getDrawable(R.drawable.double_circle_black);
		}
	}

}
