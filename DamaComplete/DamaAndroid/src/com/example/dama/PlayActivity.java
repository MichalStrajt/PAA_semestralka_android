package com.example.dama;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dama.figures.Figure;
import com.example.dama.figures.Pawn;
import com.example.dama.figures.Queen;
import com.example.dama.other.BoardPoint;

public class PlayActivity extends Activity {
	
	private RelativeLayout rl_board;
	private ImageView iv_board;
	private TextView tv_active_player;
	private TextView tv_active_player_string;
	
	private Context context;
	
	private static final int FIGURES_IN_LINE = 8;
	private static int SCREEN_SIZE;
	private static int SQUARE_SIZE;
	private static final int OUT_OF_BOUNDS = -1;
		
	private ArrayList<Figure> figures = new ArrayList<Figure>();
	private int active_player;
	private Figure active_figure;
	private BoardPoint [] possible_moves = null;
	private boolean has_player_any_jump = false;
	private String [] player_names;
	private static final int [] PLAYER_COLOR = {Color.BLUE, Color.RED};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  
	
        calculate_constants();
        init_background();
        start_new_game();
        
        setContentView(rl_board);
	}
	
	/*
	 * Funkce na vypocet hlavnich konstant pouzivanych v prubehu programu
	 */
	private void calculate_constants(){
		context = getApplicationContext();
		get_screen_width();
		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		player_names = b.getStringArray(getResources().getString(R.string.intent_players));
	}
	
	/*
	 * Metoda na zjisteni sirky obrazovky
	 * Sirku ulozi do konstanty DISPLAY_WIDTH
	 */
	@SuppressLint("NewApi")
	private void get_screen_width(){
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2){
			display.getSize(size);
			SCREEN_SIZE = size.x;
		} else {			
			SCREEN_SIZE = display.getWidth();
		}		
		
		SQUARE_SIZE = SCREEN_SIZE / FIGURES_IN_LINE;
	}
	
	/*
	 * Metoda na inicializaci pozadi hry
	 * Vytvori hlavni layout a pripoji k nemu obrazek sachovnice
	 */
	private void init_background(){
		rl_board = new RelativeLayout(context);
		LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT,      
				LayoutParams.MATCH_PARENT
		);
		rl_board.setLayoutParams(params);
		
		iv_board = new ImageView(context);
		params = new LayoutParams(
				SCREEN_SIZE,      
				SCREEN_SIZE
		);
		iv_board.setLayoutParams(params);
		iv_board.setBackgroundDrawable(getResources().getDrawable(R.drawable.board));
		rl_board.addView(iv_board);
		
		LinearLayout ll_show_info = new LinearLayout(context);
		params = new LayoutParams(
				LayoutParams.MATCH_PARENT,      
				LayoutParams.MATCH_PARENT
		);
		params.setMargins(0, SCREEN_SIZE, 0, 0);
		ll_show_info.setLayoutParams(params);
		ll_show_info.setOrientation(LinearLayout.HORIZONTAL);
		
		tv_active_player_string = new TextView(context);
		params = new LayoutParams(
				(int) (SCREEN_SIZE * 0.4),      
				LayoutParams.WRAP_CONTENT
		);
		tv_active_player_string.setLayoutParams(params);
		tv_active_player_string.setText(getResources().getString(R.string.active_player));
		ll_show_info.addView(tv_active_player_string);
		
		tv_active_player = new TextView(context);
		params = new LayoutParams(
				(int) (SCREEN_SIZE * 0.6),      
				LayoutParams.WRAP_CONTENT
		);
		tv_active_player.setLayoutParams(params);
		ll_show_info.addView(tv_active_player);
		
		rl_board.addView(ll_show_info);
		
		rl_board.setOnTouchListener(new BoardOnTouchListener());
	}
	
	/*
	 * Metoda na vyplneni inromracniho okenka textem
	 */
	private void update_tv_active_player(){
		tv_active_player.setText(player_names[active_player]);
		tv_active_player.setTextColor(PLAYER_COLOR[active_player]);
		tv_active_player_string.setTextColor(PLAYER_COLOR[active_player]);
	}
	
	/*
	 * Metoda na spusteni nove hry
	 */
	private void start_new_game(){
		active_player = Figure.WHITE;
		active_figure = null;
		for(Figure figure : figures){
			rl_board.removeView(figure);
		}
		figures = new ArrayList<Figure>();
		generate_figures(Figure.WHITE);
		generate_figures(Figure.BLACK);
		update_tv_active_player();
	}
	
	/*
	 * Funkce na nagenerovani figurek do hraciho pole
	 * @param figure: typ figurky, ktera bude generovana
	 * @return doplnene hraci pole
	 */
	private void generate_figures(int color) {
	    int start = 0;
	    int finish = 3;
	    if (color == Figure.WHITE) {
	        start += 5;
	        finish += 5;
	    }

	    for (int y = start; y < finish; y++) {
	        int x = y % 2 - 1;
	        if (x < 0) {
	            x += 2;
	        }
	        while (x < 8) {
	            Pawn new_figure = new Pawn(context);
	            new_figure.set_type(Figure.PAWN);
	            new_figure.set_color(color);
	            new_figure.set_x_relative(x);
	            new_figure.set_y_relative(y);
	        	if(color == Figure.WHITE){
	        		new_figure.set_end_line(0);
	    		} else {
	    			new_figure.set_end_line(FIGURES_IN_LINE - 1);
	    		}
	        	LayoutParams params = new LayoutParams(
	    				SQUARE_SIZE,      
	    				SQUARE_SIZE
	    		);
	    		params.setMargins(SQUARE_SIZE*new_figure.get_x_relative(), 
	    						  SQUARE_SIZE*new_figure.get_y_relative(), 0, 0);
	    		new_figure.setLayoutParams(params);
	    		new_figure.setImageDrawable(new_figure.get_drawable_image());
	    		new_figure.setOnClickListener(new FigureOnClickListener());
	        	
	            figures.add(new_figure);
	            rl_board.addView(new_figure);
	            x += 2;
	        }
	    }
	}
	
	/*
	 * Funkce na vymazani moznych tahu
	 */
	private void clean_possible_moves(){
		if(possible_moves == null){
			return;
		}
		for(int i = 0; i < possible_moves.length; i++){
			rl_board.removeView(possible_moves[i]);
		}
	}
	
	/*
	 * Funkce na osetreni kliknuti
	 * @param event: event, ktery vyvolal funkci
	 * @return souradnice, na ktere bylo klinuto
	 */
	private BoardPoint get_click_position(MotionEvent event){
		BoardPoint result = new BoardPoint(context);
		
		result.set_x_relative((int) event.getX() / SQUARE_SIZE);
		result.set_y_relative((int) event.getY() / SQUARE_SIZE);
		
		return result;
	}
	
	/*
	 * Funkce na zjisteni, zda je tah v moznych tazich
	 * Mozne tahy se zjistuji z globalni promenne
	 * @param move: zkoumany tah
	 * @return true, pokud je tah v moznych tazich
	 */
	private boolean is_possible_move(BoardPoint move){
	    for(int i = 0; i < possible_moves.length; i++){
	        if(move.get_x_relative() == possible_moves[i].get_x_relative() 
	        && move.get_y_relative() == possible_moves[i].get_y_relative()){
	            return true;
	        }
	    }
	    return false;
	}
	
	/*
	 * Funkce na posunuti aktivni figurky
	 * @param move: tah, na ktery se ma figurka presunout
	 * @return true, pokud byl tah platny
	 */
	private boolean move_figure(BoardPoint move){    
	    if(has_player_any_jump){
	        if(!did_figure_jump(active_figure, move)){
	            return false;
	        }
	        int dx = (move.get_x_relative() - active_figure.get_x_relative()) / 2;
	        int dy = (move.get_y_relative() - active_figure.get_y_relative()) / 2;
	        for(int i = 0; i < figures.size(); i++){
	        	Figure figure = figures.get(i);
	        	if(figure.is_a_figure_here(active_figure.get_x_relative() + dx, 
	        							   active_figure.get_y_relative() + dy)){	        		
	        		rl_board.removeView(figure);
	        		figures.remove(i);
	        	}
	        }
	    }
	    return true;
	}
	
	/*
	 * Funkce na otestovani, zda figurka provedla akci skoku
	 * @param figure: zkoumana figurka
	 * @param move: pole, na ktere se ma pohnout
	 * @return true, pokud figurka skocila
	 */
	private boolean did_figure_jump(Figure figure, BoardPoint move){
	    if(Math.abs(figure.get_x_relative() - move.get_x_relative()) > 1 
	    && Math.abs(figure.get_y_relative() - move.get_y_relative()) > 1){
	        return true;
	    }
	    return false;
	}
	
	/*
	 * Funkce na zjisteni, zda figurka ma alespon jeden mozny skok
	 * @param figure: zkoumana figurka
	 * @return true, pokud figurka muze skakat
	 */
	private boolean has_figure_any_jump(Figure figure){
	    BoardPoint[] moves = figure.get_possible_moves();
	    boolean test = false;
	    for(int i = 0; i < moves.length; i++){
	        BoardPoint move = moves[i];
	        if(move.get_x_relative() > OUT_OF_BOUNDS && move.get_x_relative() < FIGURES_IN_LINE &&
	           move.get_y_relative() > OUT_OF_BOUNDS && move.get_y_relative() < FIGURES_IN_LINE){
	            if(can_figure_jump(figure, move)){
	                test = true;
	            }
	        }
	    }
	    return test;
	}
	
	/*
	 * Funkce na otestovani, zda figurka muze skakat jinou figuru
	 * @param figure: testovana figurka
	 * @param move: zkoumany tah
	 * @return true, pokud figurka muze skakat
	 */
	private boolean can_figure_jump(Figure figure, BoardPoint move){
	    BoardPoint tmp_move = new BoardPoint(context);
	    tmp_move.set_x_relative(move.get_x_relative());
	    tmp_move.set_y_relative(move.get_y_relative());
	    
	    boolean test = false;
	    Figure figur = null;
	    Figure fig = null;
	    for(int i = 0; i < figures.size(); i ++){
	    	fig = figures.get(i);
	    	if(fig.is_a_figure_here(tmp_move.get_x_relative(), tmp_move.get_y_relative())){
	    		figur = fig;
	    		test = true;	    		
	    	}
	    }
	    if(!test){
	        return false;
	    }
	    
	    if(figur.get_color() != active_player){
	        int dx = tmp_move.get_x_relative() - figure.get_x_relative();
	        int dy = tmp_move.get_y_relative() - figure.get_y_relative();
	        tmp_move.set_x_relative(tmp_move.get_x_relative() + dx);
	        tmp_move.set_y_relative(tmp_move.get_y_relative() + dy);
	        if(tmp_move.get_x_relative() == OUT_OF_BOUNDS || tmp_move.get_x_relative() == FIGURES_IN_LINE ||
	           tmp_move.get_y_relative() == OUT_OF_BOUNDS || tmp_move.get_y_relative() == FIGURES_IN_LINE){
	            return false;
	        }
	        
	        test = false;
		    for(int i = 0; i < figures.size(); i ++){
		    	fig = figures.get(i);
		    	if(fig.is_a_figure_here(tmp_move.get_x_relative(), tmp_move.get_y_relative())){
		    		test = true;		    		
		    	}
		    }
	        if(!test){
	            return true;
	        } else {
	            return false;
	        }
	    }
	    return false;
	}
	
	/*
	 * Funkce na upravu moznych tahu
	 * Funkce upravi mozne tahy tak, aby bylo zaznameno skakani a zruseny neplatne tahy 
	 * @param figure: aktivni figurka
	 * @param moves: pole moznych tahu
	 * @return upravene pole moznych tahu
	 */
	private BoardPoint [] edit_possible_moves(Figure figure, BoardPoint [] moves){
	    for(int i = 0; i < moves.length; i++){
	        BoardPoint move = moves[i];
	        if(move.get_x_relative() >= FIGURES_IN_LINE || move.get_y_relative() >= FIGURES_IN_LINE) {
	            move.set_x_relative(OUT_OF_BOUNDS);
	            move.set_y_relative(OUT_OF_BOUNDS);
	        }
	        if(move.get_x_relative() < 0 || move.get_y_relative() < 0){
	            move.set_x_relative(OUT_OF_BOUNDS);
	            move.set_y_relative(OUT_OF_BOUNDS);
	        }
	        boolean test = false;
	        Figure fig = null;
		    for(int j = 0; j < figures.size(); j ++){
		    	fig = figures.get(j);
		    	if(fig.is_a_figure_here(move.get_x_relative(), move.get_y_relative())){
		    		test = true;		    		
		    	}
		    }
	        if(test){
	            if(can_figure_jump(figure, move)){
	                move = edit_jump_move(move, figure);
	            } else {
	                move.set_x_relative(OUT_OF_BOUNDS);
	                move.set_y_relative(OUT_OF_BOUNDS);
	            }            
	        }
	        moves[i] = move;
	    }
	    return moves;
	}
	
	/*
	 * Funkce na posunuti mozneho tahu z duvodu skoku
	 * @param move: zkoumany tah
	 * @param figure: zkoumana figurka
	 * @return upraveny tah
	 */
	private BoardPoint edit_jump_move(BoardPoint move, Figure figure){
	    int dx = move.get_x_relative() - figure.get_x_relative();
	    int dy = move.get_y_relative() - figure.get_y_relative();
	    move.set_x_relative(move.get_x_relative() + dx);
	    move.set_y_relative(move.get_y_relative() + dy);
	    return move;
	}
	
	/*
	 * Funkce na vykresleni moznych tahu
	 */
	private void draw_possible_moves(){
	    for(int i = 0; i < possible_moves.length; i ++){
	        BoardPoint move = possible_moves[i];
	        if(move.get_x_relative() > OUT_OF_BOUNDS && move.get_y_relative() > OUT_OF_BOUNDS){
	        	LayoutParams params = new LayoutParams(
						SQUARE_SIZE,      
						SQUARE_SIZE
						);
				params.setMargins(SQUARE_SIZE*move.get_x_relative(), SQUARE_SIZE*move.get_y_relative(), 0, 0);
				move.setLayoutParams(params);
				move.setBackgroundColor(Color.YELLOW);
				move.getBackground().setAlpha(33);					
	        }
	        rl_board.addView(move);
	    }
	}
	
	/*
	 * Funkce na zmenu aktivniho hrace a vymazani vsech globalnich promennych
	 */
	private void change_active_player(){
	    if(active_figure.get_type() == Figure.PAWN && 
	       active_figure.get_y_relative() == active_figure.get_end_line()){
	        change_figure_to_queen();
	    }

	    possible_moves = null;
	    has_player_any_jump = false;

	    active_figure = null;
	    
	    active_player = (active_player + 1) % 2;
	    update_tv_active_player();

	    if(test_game_over()){
	    	game_over_dialog();
	        return;
	    }

	    for(Figure figure : figures){
	    	if(figure.get_color() == active_player){
	            boolean can_jump = has_figure_any_jump(figure);
	            if(can_jump){
	                has_player_any_jump = true;
	            }
	        }
	    }
	}
	
	/*
	 * Metoda na ukonceni hry
	 * Metoda vyvola dialog, kde hrac vybere, zda chce hrat novou hru nebo hru ukoncit
	 */
	private void game_over_dialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(PlayActivity.this);
		builder.setNegativeButton("Nov· hra", new DialogInterface.OnClickListener() {				
			public void onClick(DialogInterface dialog, int which) {
				start_new_game();
			}
		});
		builder.setPositiveButton("Konec hry", new DialogInterface.OnClickListener() {				
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		builder.setMessage(String.format("Vyhr·l hr·Ë: " + player_names[(active_player + 1) % 2]));
	    builder.setTitle("Konec hry");
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	/*
	 * Funkce na osetreni konce hry
	 */
	private boolean test_game_over(){
	    boolean the_end = true;
	    for(Figure figure : figures){
	        if(figure.get_color() == active_player){
	            BoardPoint [] moves = figure.get_possible_moves();
	            moves = edit_possible_moves(figure, moves);
	            for(int k = 0; k < moves.length; k++){
	                BoardPoint move = moves[k];
	                if(move.get_x_relative() > OUT_OF_BOUNDS && move.get_y_relative() > OUT_OF_BOUNDS){
	                    the_end = false; 
	                }
	            }
	        }
	    }
	    return the_end;
	}
	
	/*
	 * Funkce na premenu figurky na damu
	 */
	private void change_figure_to_queen(){
		Queen queen = new Queen(context);
		queen.set_type(Figure.QUEEN);
		queen.set_x_relative(active_figure.get_x_relative());
		queen.set_y_relative(active_figure.get_y_relative());
		queen.set_color(active_figure.get_color());
		
		LayoutParams params = new LayoutParams(
				SQUARE_SIZE,      
				SQUARE_SIZE
		);
		params.setMargins(SQUARE_SIZE*queen.get_x_relative(), 
						  SQUARE_SIZE*queen.get_y_relative(), 0, 0);
		queen.setLayoutParams(params);
		queen.setImageDrawable(queen.get_drawable_image());
		queen.setOnClickListener(new FigureOnClickListener());
    	
        figures.add(queen);
        rl_board.addView(queen);
        
        rl_board.removeView(active_figure);
        figures.remove(active_figure);
	}
	
	/*
	 * Funkce na oznaceni figurky
	 * @param coordinates: souradnice figurky, ktera by mela byt oznacena
	 */
	private void select_figure(BoardPoint coordinates){
        Figure figure = null;
		for(Figure fig : figures){
        	if(fig.is_a_figure_here(coordinates.get_x_relative(), coordinates.get_y_relative())){
        		figure = fig;
        	}
        }
		if(figure.get_color() == active_player){
			active_figure = figure;
	    }
	}
	
	private class FigureOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Figure figure = (Figure) v;
			if(figure.get_color() == active_player){
				BoardPoint coordinates = new BoardPoint(context);
				coordinates.set_x_relative(figure.get_x_relative());
				coordinates.set_y_relative(figure.get_y_relative());
				
				if(active_figure != null){
					clean_possible_moves();
				}
				select_figure(coordinates);
				possible_moves = active_figure.get_possible_moves();
				possible_moves = edit_possible_moves(active_figure, possible_moves);
				draw_possible_moves();
			}
		}		
	}
	
	/*
	 * Trida na osetreni kliknuti na hraci plochu
	 */
	private class BoardOnTouchListener implements OnTouchListener {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			if(action == MotionEvent.ACTION_DOWN){
				BoardPoint coordinates = get_click_position(event);
				if(active_figure != null){
					if(is_possible_move(coordinates)){
						if(move_figure(coordinates)){
							clean_possible_moves();
							boolean jump = did_figure_jump(active_figure, coordinates);
							
							int index = -1;
							for(int i = 0; i < figures.size(); i++){
								Figure fig = figures.get(i);
								if(fig.is_a_figure_here(active_figure.get_x_relative(), active_figure.get_y_relative())){
									index = i;
								}
							}
							
						    active_figure.set_x_relative(coordinates.get_x_relative());
						    active_figure.set_y_relative(coordinates.get_y_relative());
						    
						    Figure move_figure = figures.get(index);
						    move_figure.set_x_relative(coordinates.get_x_relative());
						    move_figure.set_y_relative(coordinates.get_y_relative());
						    LayoutParams params = new LayoutParams(
									SQUARE_SIZE,      
									SQUARE_SIZE
							);
							params.setMargins(SQUARE_SIZE*move_figure.get_x_relative(), 
											  SQUARE_SIZE*move_figure.get_y_relative(), 0, 0);
							move_figure.setLayoutParams(params);
							figures.set(index, move_figure);
							rl_board.invalidate();
							if(jump && has_figure_any_jump(active_figure)){
								possible_moves = active_figure.get_possible_moves();
								possible_moves = edit_possible_moves(active_figure, possible_moves);
								draw_possible_moves();
							} else {
								change_active_player();
							}
						} else {
							Toast.makeText(context, getResources().getString(R.string.have_to_jump), Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
			return true;
		}		
	}
}
