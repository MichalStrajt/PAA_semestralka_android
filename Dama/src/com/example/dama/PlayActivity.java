package com.example.dama;

import java.util.ArrayList;

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
	
	private static final int FIGURES_IN_LINE = 8;
	private static int DISPLAY_WIDTH;
	private static int SQUAE_SIZE;
	private static String ACTIVE_PLAYER_STRING;
	private static String NOF_PLAYER_FIGURES_STRING;
	private static String HAVE_TO_JUMP_STRING;
	private static String [] PLAYER_STRINGS;
	private static final int [] PLAYER_COLORS = {Color.BLUE, Color.RED}; 
	
	private Context context;
	
	private RelativeLayout rl_board;
	private ImageView iv_board;
	
	private LinearLayout ll_info;
	private TextView tv_active_player;
	private TextView tv_active_player_figures;
		
	ArrayList<Figure> figures;
	BoardPoint [] possible_moves = null;
	
	Figure active_figure = null;
	private int current_player_color;
	private boolean is_possible_jump = false;
	private boolean can_change_figure = true;
	
	private int [] figures_danger_index = new int [2];
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  
		
		context = getApplicationContext();
		Intent i = getIntent();
		
		init_strings(i);
		get_screen_width();
		
		init_background();
		
		setContentView(rl_board);
		current_player_color = Figure.WHITE;		
		
		init_other_components();
		init_figures();		
		tv_number_set_text();
	}
	
	/*
	 * Metoda na restartovani hry vynuluje vsechny informace o hre
	 * a nastavi je do pocatecnich hodnot
	 */
	private void restart_game(){
		rl_board.removeAllViews();
		figures = null;
		possible_moves = null;
		active_figure = null;
		current_player_color = Figure.WHITE;
		is_possible_jump = false;
		figures_danger_index = new int [2];
		
		rl_board.addView(iv_board);
		rl_board.addView(ll_info);
		init_figures();
		tv_active_player_set_text();
		tv_number_set_text();
	}
	
	/*
	 * Metoda na zjisteni sirky obrazovky
	 * Sirku ulozi do konstanty DISPLAY_WIDTH
	 */
	private void get_screen_width(){
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		DISPLAY_WIDTH = size.x;
		SQUAE_SIZE = DISPLAY_WIDTH / FIGURES_IN_LINE;
	}
	
	/*
	 * Metoda na inicializaci retezcu ze souboru strings.xml
	 */
	private void init_strings(Intent i){
		ACTIVE_PLAYER_STRING = getResources().getString(R.string.active_player);
		NOF_PLAYER_FIGURES_STRING = getResources().getString(R.string.nof_figures);
		HAVE_TO_JUMP_STRING = getResources().getString(R.string.have_to_jump);
		Bundle b = i.getExtras();
		PLAYER_STRINGS = b.getStringArray(getResources().getString(R.string.intent_players));
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
				DISPLAY_WIDTH,      
				DISPLAY_WIDTH
		);
		iv_board.setLayoutParams(params);
		iv_board.setBackgroundDrawable(getResources().getDrawable(R.drawable.board));
		rl_board.addView(iv_board);
		
		rl_board.setOnTouchListener(new BoardOnTouchListener());
	}
	
	/*
	 * Metoda na inicializaci ostatnich grafickych komponent
	 * metoda inicializuje pomocne TextView a pripoji je k hlavnimu layoutu
	 */
	private void init_other_components(){
		ll_info = new LinearLayout(context);
		LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT,      
				LayoutParams.WRAP_CONTENT
		);
		params.setMargins(0, DISPLAY_WIDTH, 0, 0);
		ll_info.setLayoutParams(params);
		ll_info.setOrientation(LinearLayout.VERTICAL);
		
		tv_active_player = new TextView(context);
		params = new LayoutParams(
				LayoutParams.MATCH_PARENT,      
				LayoutParams.WRAP_CONTENT
		);
		tv_active_player.setLayoutParams(params);
		tv_active_player_set_text();
		ll_info.addView(tv_active_player);
		
		tv_active_player_figures = new TextView(context);
		params = new LayoutParams(
				LayoutParams.MATCH_PARENT,      
				LayoutParams.WRAP_CONTENT
		);
		tv_active_player_figures.setLayoutParams(params);
		ll_info.addView(tv_active_player_figures);
		
		rl_board.addView(ll_info);
	}
	
	/*
	 * Metoda na nastaveni textu v TextView, obsahujicim informaci o aktivnim hraci
	 */
	private void tv_active_player_set_text(){
		String text = ACTIVE_PLAYER_STRING;
		text += "\t";
		text += PLAYER_STRINGS[current_player_color];
		tv_active_player.setTextColor(PLAYER_COLORS[current_player_color]);
		tv_active_player.setText(text);	
	}
	
	/*
	 * Metoda na nastaveni textu v TextView, obsahujicim informaci
	 * o poctu figurek aktivniho hrace
	 */
	private void tv_number_set_text(){
		String text = NOF_PLAYER_FIGURES_STRING;
		text += "\t";
		text += count_figures();
		tv_active_player_figures.setTextColor(PLAYER_COLORS[current_player_color]);
		tv_active_player_figures.setText(text);
	}
	
	/*
	 * Metoda na zjisteni poctu figurek aktivniho hrace
	 */
	private String count_figures(){
		int count = 0;
		for(Figure fig : figures){
			if(fig.get_color() == current_player_color){
				count += 1;
			}
		}
		return Integer.toString(count);
	}
	
	/*
	 * Metoda na inicializaci figurek pro novou hru
	 */
	private void init_figures(){
		figures = new ArrayList<Figure>();
		
		// generovani cernych kamenu
		for(int y = 0; y < 3; y++){
			int x = y % 2 - 1;
			if(x < 0){
				x += 2;
			}
			while(x < 8){
				generate_figure(x, y, Figure.BLACK, Figure.PAWN);
				x += 2; 
			}
		}
		
		// generovani bilych kamenu
		for(int y = 5; y < 8; y++){
			int x = y % 2 - 1;
			if(x < 0){
				x += 2;
			}
			while(x < 8){
				generate_figure(x, y, Figure.WHITE,  Figure.PAWN);
				x += 2; 
			}
		}
	}
	
	/*
	 * Metoda na vytvoteni jedne figurky
	 * @param x: x souradnice figurky
	 * @param y: y souradnice figurky
	 * @param color: barva figurky
	 * @param type: typ figurky
	 */
	private void generate_figure(int x, int y, int color, int type){
		// nastaveni rozsirujicich parametru
		Figure fig_add;
		if(type == Figure.PAWN){
			fig_add = new Pawn(context);
		} else {
			fig_add = new Queen(context);
		}
		fig_add.set_x_relative(x);
		fig_add.set_y_relative(y);
		fig_add.set_color(color);
		fig_add.set_type(Figure.PAWN);
		if(color == Figure.WHITE){
			fig_add.set_end_line(0);
		} else {
			fig_add.set_end_line(FIGURES_IN_LINE - 1);
		}
		
		// nastaveni parametru ImageView
		LayoutParams params = new LayoutParams(
				SQUAE_SIZE,      
				SQUAE_SIZE
		);
		params.setMargins(SQUAE_SIZE*fig_add.get_x_relative(), 
						  SQUAE_SIZE*fig_add.get_y_relative(), 0, 0);
		fig_add.setLayoutParams(params);
		fig_add.setImageDrawable(fig_add.get_drawable_image());
		
		fig_add.setOnClickListener(new FigureOnClickListener());
		
		// pripojeni
		figures.add(fig_add);
		rl_board.addView(fig_add);
	}
	
	/*
	 * Metoda na odstraneni predeslych moznych tahu
	 */
	private void remove_possible_moves(){
		if(possible_moves == null){
			return;
		}
		for(int i = 0; i < possible_moves.length; i++){
			rl_board.removeView(possible_moves[i]);
		}
	}
	
	/*
	 * Metoda na vytvoreni vsech moznych tahu.
	 * Tahy, ktere povoluje figurka, ale jsou nemozne (blokovane jinou figurkou)
	 * jsou neviditelne
	 */
	public void set_possible_moves(){
		figures_danger_index[0] = -1;
		figures_danger_index[1] = -1;
		
		for(int i = 0; i < possible_moves.length; i++){
			boolean jump_enabled = true;
			for(int j = 0; j < figures.size(); j++){
				Figure fig = figures.get(j);
				
				// na pozici stoji jina figurka
				if(fig.get_x_relative() == possible_moves[i].get_x_relative() &&
						fig.get_y_relative() == possible_moves[i].get_y_relative()){
					if(!jump_enabled){
						// figurka jiz nemuze dale skakat
						possible_moves[i].setVisibility(ImageView.INVISIBLE);
					} else if(fig.get_color() == current_player_color) {
						// figurka je hrace, ktery je na tahu
						possible_moves[i].setVisibility(ImageView.INVISIBLE);
					} else {
						// figurka je protihracova a je mozne ji preskocit
						// posunuti mozneho tahu na novou pozici a znovuotestovani
						int x_relative = fig.get_x_relative() - active_figure.get_x_relative();
						int y_relative = fig.get_y_relative() - active_figure.get_y_relative();
						
						possible_moves[i].set_x_relative(possible_moves[i].get_x_relative() + x_relative);
						possible_moves[i].set_y_relative(possible_moves[i].get_y_relative() + y_relative);
						
						if(figures_danger_index[0] == -1){
							figures_danger_index[0] = j;
						} else {
							figures_danger_index[1] = j;
						}
						
						jump_enabled = false;
						j = -1;
					}
				}					
			}
			
			// vypocet souradnic figurky v hlavnim layoutu
			LayoutParams params = new LayoutParams(
					SQUAE_SIZE,      
					SQUAE_SIZE
					);
			params.setMargins(SQUAE_SIZE*possible_moves[i].get_x_relative(), 
					SQUAE_SIZE*possible_moves[i].get_y_relative(), 0, 0);
			possible_moves[i].setLayoutParams(params);
			possible_moves[i].setBackgroundColor(Color.YELLOW);
			possible_moves[i].getBackground().setAlpha(33);
			rl_board.addView(possible_moves[i]);
		}
	}
	
	/*
	 * Trida na ostetreni kliknuti na figurku
	 */
	private class FigureOnClickListener implements OnClickListener {
		public void onClick(View v) {
			if(((Figure) v).get_color() == current_player_color && can_change_figure) {
				remove_possible_moves();
				active_figure = (Figure) v;
				possible_moves = active_figure.get_possible_moves();
				for(int i = 0; i < possible_moves.length; i++){
					if(possible_moves[i].get_x_relative() < 0 ||
							possible_moves[i].get_y_relative() < 0 ||
							possible_moves[i].get_x_relative() > FIGURES_IN_LINE ||
							possible_moves[i].get_y_relative() > FIGURES_IN_LINE){
						// osetreni tahu mimo hraci pole
						possible_moves[i].set_x_relative(-1);
						possible_moves[i].set_y_relative(-1);
					}
				}
				set_possible_moves();
			}
		}
	}
	
	/*
	 * Trida na osetreni kliknuti na hraci plochu
	 */
	private class BoardOnTouchListener implements OnTouchListener{

		private int x_relative;
		private int y_relative;

		public boolean onTouch(View v, MotionEvent event) {
			x_relative = (int) event.getX() / SQUAE_SIZE;
			y_relative = (int) event.getY() / SQUAE_SIZE;
			
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				if(active_figure != null){
					process_move();
				}
			}
			
			return true;
		}
		
		/*
		 * Metoda na provedeni tahu aktivni figurkou
		 */
		private void process_move(){
			for(int i = 0; i < possible_moves.length; i++){
				if(possible_moves[i].getVisibility() == ImageView.VISIBLE){
					// zjisteni, zda bylo kliknuto na pozici nektereho mozneho tahu
					if(possible_moves[i].get_x_relative() == x_relative && 
							possible_moves[i].get_y_relative() == y_relative){	
						
						// zjisteni, zda figurka musi skakat
						if(!jump_figure()){
							return;
						}
						
						// vypocet novych souradnic
						active_figure.set_x_relative(x_relative);
						active_figure.set_y_relative(y_relative);
						LayoutParams params = new LayoutParams(
								SQUAE_SIZE,      
								SQUAE_SIZE
						);
						params.setMargins(SQUAE_SIZE*active_figure.get_x_relative(), 
										  SQUAE_SIZE*active_figure.get_y_relative(), 0, 0);
						active_figure.setLayoutParams(params);
						
						// odstaneni moznych tahu
						remove_possible_moves();
						
						if(is_possible_jump && test_jumps(active_figure)){
							// hrac preskocil figurku a muze touto figurou preskocit jeste dalsi
							possible_moves = active_figure.get_possible_moves();
							set_possible_moves();
							can_change_figure = false;
						} else {
							// hrac uz dal hrat nemuze
							can_change_figure = true;
							process_queen_transform();
							if(current_player_color == Figure.WHITE){
								current_player_color = Figure.BLACK;
							} else {
								current_player_color = Figure.WHITE;
							}
							tv_active_player_set_text();
							tv_number_set_text();
							is_possible_jump = test_all_jumps();
							if(test_game_over()){
								game_over();
							}
						}
					}
				}
			}
		}
		
		/*
		 * Metoda na otestvani konce hry
		 * @return true, pokud nastal konec hry
		 */
		private boolean test_game_over(){
			for(Figure fig : figures){
				if(fig.get_color() == current_player_color){
					return false;
				}
			}
			return true;
		}
		
		/*
		 * Metoda na ukonceni hry
		 * Metoda vyvola dialog, kde hrac vybere, zda chce hrat novou hru nebo hru ukoncit
		 */
		private void game_over(){
			AlertDialog.Builder builder = new AlertDialog.Builder(PlayActivity.this);
			builder.setNegativeButton("Nová hra", new DialogInterface.OnClickListener() {				
				public void onClick(DialogInterface dialog, int which) {
					restart_game();
				}
			});
			builder.setPositiveButton("Konec hry", new DialogInterface.OnClickListener() {				
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			builder.setMessage(String.format("Vyhrál hráè: " + PLAYER_STRINGS[(current_player_color + 1) % 2]));
		    builder.setTitle("Konec hry");
			AlertDialog dialog = builder.create();
			dialog.show();
		}
		
		/*
		 * Metoda na premenu pesaka na kralovnu
		 */
		private void process_queen_transform(){
			if(active_figure.get_type() == Figure.PAWN){
				if(active_figure.get_y_relative() == active_figure.get_end_line()){
					generate_figure(active_figure.get_x_relative(), 
									active_figure.get_y_relative(), 
									active_figure.get_color(), 
									Figure.QUEEN);
					rl_board.removeView(active_figure);
					figures.remove(active_figure);
					active_figure = null;
				}
			}
		}
		
		/*
		 * Metoda na otestovani, jestli nejaka hracova figurka muze skakat
		 */
		private boolean test_all_jumps(){
			for(Figure fig : figures){
				if(test_jumps(fig)){
					return true;
				}
			}
			return false;
		}
		
		/*
		 * Metoda na zjisteni, zda figurka muze skakat
		 * @param _active_figure: vybrana figurka
		 * @return true, pokud vstupni figurka muze skakat
		 */
		private boolean test_jumps(Figure _active_figure){
			if(_active_figure.get_color() == current_player_color){
				// zjisteni moznych tahu figurky
				BoardPoint [] _possible_moves = _active_figure.get_possible_moves();
				for(int i = 0; i < _possible_moves.length; i++){
					for(int j = 0; j < figures.size(); j++){
						// zjisteni, zda v nekterem moznem tahu stoji jina figurka
						Figure fig = figures.get(j);
						if(fig.get_x_relative() == _possible_moves[i].get_x_relative() &&
								fig.get_y_relative() == _possible_moves[i].get_y_relative()){
							if(fig.get_color() != current_player_color) {
								// posunuti tahu a znovuotestovani v pripade, ze figurka je protihacova
								int x_relative = fig.get_x_relative() - _active_figure.get_x_relative();
								int y_relative = fig.get_y_relative() - _active_figure.get_y_relative();
								
								_possible_moves[i].set_x_relative(_possible_moves[i].get_x_relative() + x_relative);
								_possible_moves[i].set_y_relative(_possible_moves[i].get_y_relative() + y_relative);									
								
								if(_possible_moves[i].get_x_relative() > -1 &&
										_possible_moves[i].get_y_relative() > -1 &&
										_possible_moves[i].get_x_relative() < FIGURES_IN_LINE &&
										_possible_moves[i].get_y_relative() < FIGURES_IN_LINE){
									boolean to_return = true;
									
									for(Figure figu : figures){									
										// znovuotestovani
										if(figu.get_x_relative() == _possible_moves[i].get_x_relative() &&
												figu.get_y_relative() == _possible_moves[i].get_y_relative()){
											to_return = false;
										}
									}
									
									if(to_return){
										return true;
									}
								}
								
							}
						}					
					}
				}
			}
			return false;
		}
		
		/*
		 * Metoda na provedeni preskoceni figurky
		 * @return false, pokud ma byt preskocena figurka a nebyla
		 */
		private boolean jump_figure(){
			if(Math.abs(active_figure.get_x_relative() - x_relative) > 1){
				// preskoceni figurky a odstraneni protihracovy
				Figure fig = figures.get(figures_danger_index[0]);
				int remove_index;
				if(Math.abs(x_relative - fig.get_x_relative()) == 1 && 
						Math.abs(y_relative - fig.get_y_relative()) == 1){
					remove_index = figures_danger_index[0];
				} else {
					fig = figures.get(figures_danger_index[1]);
					remove_index = figures_danger_index[1];
				}
				
				figures.remove(remove_index);
				rl_board.removeView(fig);
				return true;
			} else if (is_possible_jump){
				// je potreba provest skok
				Toast.makeText(context, HAVE_TO_JUMP_STRING, Toast.LENGTH_SHORT).show();
				return false;
			} else {
				// figurka nemohla skakat
				return true;
			}
		}
	}
}
