package com.example.dama;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private Button btn_new_game;
	private Button btn_exit;
	private EditText et_player_one;
	private EditText et_player_two;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init_components();		
	}
	
	private void init_components(){
		btn_new_game = (Button) findViewById(R.id.btn_new_game);
		btn_new_game.setOnClickListener(this);
		btn_exit = (Button) findViewById(R.id.btn_exit);
		btn_exit.setOnClickListener(this);
		
		et_player_one = (EditText) findViewById(R.id.et_player_one);
		et_player_two = (EditText) findViewById(R.id.et_player_two);
	}

	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_new_game:
			String player_one_name = et_player_one.getText().toString();
			String player_two_name = et_player_two.getText().toString();
			
			if(player_one_name.equals("") || player_two_name.equals("")){
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_empty_et), Toast.LENGTH_SHORT).show();
			} else {			
				Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.btn_exit:
			finish();
			break;
		}
	}
}
