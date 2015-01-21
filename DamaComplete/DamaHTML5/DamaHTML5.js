/* jshint devel: true */

'use strict';

var FIGURES_IN_LINE = 8;
var MIN_SIZE = 240;
var CANVAS_DRAWER;
var SQUARE_SIZE;
var SCREEN_SIZE;
var TA_SIZE = 30;
var OUT_OF_BOUNDS = -1;

var NO_FIGURE = -1;
var WHITE_PAWN = 0;
var WHITE_QUEEN = 1;
var BLACK_PAWN = 2;
var BLACK_QUEEN = 3;

var PLAYER_WHITE = 0;
var LAST_LINE = [0, 7];
var PLAYER_COLOR = ['#0000ff', '#ff0000'];
var PLAYER_NAMES = new Array(2);

var board;
var active_player;
var active_figure;
var possible_moves;
var has_player_any_jump = false;

/*
 * Funkce na vypocet hlavnich konstant pouzivanych v prubehu programu
 */
var calculate_constants = function calculate_constants() {
    CANVAS_DRAWER = document.getElementById('cnv_main_drawer');
    CANVAS_DRAWER.width = (window.innerWidth > 0) ? window.innerWidth : screen.width;
    CANVAS_DRAWER.height = (window.innerHeight > 0) ? window.innerHeight : screen.height;

    SCREEN_SIZE = Math.min(CANVAS_DRAWER.width, CANVAS_DRAWER.height);
    SCREEN_SIZE = Math.max(SCREEN_SIZE, MIN_SIZE);
    SCREEN_SIZE -= TA_SIZE;
    CANVAS_DRAWER.width = Math.max(CANVAS_DRAWER.width, SCREEN_SIZE);
    CANVAS_DRAWER.height = Math.max(CANVAS_DRAWER.height, SCREEN_SIZE);

    SQUARE_SIZE = SCREEN_SIZE / FIGURES_IN_LINE;
};

/*
 * Funkce na vykresleni sachovnice na pozadi
 */
var draw_background = function draw_background() {
    var image = new Image();
    image.src = 'board.png';
    image.onload = function() {
    	var background = document.getElementById('cnv_background_drawer');
    	background.width = CANVAS_DRAWER.width;
    	background.height = CANVAS_DRAWER.height;
        var context = background.getContext('2d');
        image.width = background.width;
        image.height = background.height;
        context.drawImage(image, 0, 0, SCREEN_SIZE, SCREEN_SIZE);
    };
};

/*
 * Funkce na inicializaci hraciho pole
 * Funkce se stara pouze o naplneni pole figurami
 */
var init_board = function init_board() {
	active_player = PLAYER_WHITE;
    active_figure = {type: NO_FIGURE, x: OUT_OF_BOUNDS, y: OUT_OF_BOUNDS};

    board = new Array(FIGURES_IN_LINE);
    for (var i = 0; i < FIGURES_IN_LINE; i++) {
        board[i] = new Array(FIGURES_IN_LINE);
        for (var j = 0; j < FIGURES_IN_LINE; j++) {
            board[i][j] = NO_FIGURE;
        }
    }

    var ta_info = document.getElementById('ta_info');
    ta_info.style.top = SCREEN_SIZE + 10 + 'px';
    ta_info.style.width = SCREEN_SIZE + 'px';
};

/*
 * Funkce na nagenerovani figurek do hraciho pole
 * @param figure: typ figurky, ktera bude generovana
 * @return doplnene hraci pole
 */
var generate_figures = function generate_figures(figure) {
    var start = 0;
    var finish = 3;
    if (figure === WHITE_PAWN) {
        start += 5;
        finish += 5;
    }

    for (var y = start; y < finish; y++) {
        var x = y % 2 - 1;
        if (x < 0) {
            x += 2;
        }
        while (x < 8) {
            board[y][x] = figure;
            x += 2;
        }
    }
};

/*
 * Funkce na vykresneni hraciho pole
 */
var redraw_figures = function redraw_figures() {
	var context = CANVAS_DRAWER.getContext('2d');
    for (var i = 0; i < FIGURES_IN_LINE; i++) {
        for (var j = 0; j < FIGURES_IN_LINE; j++) {
	        if(board[i][j] > NO_FIGURE){
	        	var gradient;
	            switch (board[i][j]) {
	                case WHITE_PAWN:
	                	gradient = context.createRadialGradient(j * SQUARE_SIZE + Math.sqrt(2) * SQUARE_SIZE / 4,
	                											i * SQUARE_SIZE + Math.sqrt(2) * SQUARE_SIZE / 4,
	                											Math.sqrt(2) * SQUARE_SIZE / 8,
	                											j * SQUARE_SIZE + Math.sqrt(2) * SQUARE_SIZE / 4,
	                											i * SQUARE_SIZE + Math.sqrt(2) * SQUARE_SIZE / 4,
	                											Math.sqrt(2) * SQUARE_SIZE / 2);
	                	gradient.addColorStop(0, '#0000ff');
						gradient.addColorStop(1, '#000040');
	                    break;
	                case WHITE_QUEEN:
                        gradient = context.createRadialGradient(j * SQUARE_SIZE + Math.sqrt(2) * SQUARE_SIZE / 4,
                                                                i * SQUARE_SIZE + Math.sqrt(2) * SQUARE_SIZE / 4,
                                                                Math.sqrt(2) * SQUARE_SIZE / 8,
                                                                j * SQUARE_SIZE + Math.sqrt(2) * SQUARE_SIZE / 4,
                                                                i * SQUARE_SIZE + Math.sqrt(2) * SQUARE_SIZE / 4,
                                                                Math.sqrt(2) * SQUARE_SIZE / 2);
                        gradient.addColorStop(0, '#0000ff');
                        gradient.addColorStop(0.5, '#00ff00');
                        gradient.addColorStop(1, '#000040');
	                    break;
	                case BLACK_PAWN:
	                	gradient = context.createRadialGradient(j * SQUARE_SIZE + Math.sqrt(2) * SQUARE_SIZE / 4,
	                											i * SQUARE_SIZE + Math.sqrt(2) * SQUARE_SIZE / 4,
	                											Math.sqrt(2) * SQUARE_SIZE / 8,
	                											j * SQUARE_SIZE + Math.sqrt(2) * SQUARE_SIZE / 4,
	                											i * SQUARE_SIZE + Math.sqrt(2) * SQUARE_SIZE / 4,
	                											Math.sqrt(2) * SQUARE_SIZE / 2);
	                	gradient.addColorStop(0, '#ff0000');
						gradient.addColorStop(1, '#800000');
	                    break;
	                case BLACK_QUEEN:
                        gradient = context.createRadialGradient(j * SQUARE_SIZE + Math.sqrt(2) * SQUARE_SIZE / 4,
                                                                i * SQUARE_SIZE + Math.sqrt(2) * SQUARE_SIZE / 4,
                                                                Math.sqrt(2) * SQUARE_SIZE / 8,
                                                                j * SQUARE_SIZE + Math.sqrt(2) * SQUARE_SIZE / 4,
                                                                i * SQUARE_SIZE + Math.sqrt(2) * SQUARE_SIZE / 4,
                                                                Math.sqrt(2) * SQUARE_SIZE / 2);
                        gradient.addColorStop(0, '#ff0000');
                        gradient.addColorStop(0.5, '#00ff00');
                        gradient.addColorStop(1, '#800000');
	                    break;
	            }
	            context.beginPath();     
	            context.arc(j * SQUARE_SIZE + SQUARE_SIZE / 2,
	                		i * SQUARE_SIZE + SQUARE_SIZE / 2,
	                		SQUARE_SIZE / 2, 0, 2 * Math.PI);
	            context.fillStyle = gradient;
	            context.fill();
	        } else {
                context.beginPath();
                context.clearRect(j * SQUARE_SIZE, i * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }
};

/*
 * Funkce na nastaveni listeneru, ktery obsluhuje hru
 */
var set_listener = function set_listener(){
    CANVAS_DRAWER.addEventListener('touchstart', function(event) {
        process_the_click(event);
    }, false);
};

/*
 * Funkce na zjisteni souradnic, kam bylo kliknuto
 * @param e: event, ktery vyvolal funkci
 * @return souradnice kliknuti vzhledem k sachovnici
 */
var get_click_postition = function get_click_postition(e) {
    var xPosition = Math.floor(e.touches[0].pageX / SQUARE_SIZE);
    var yPosition = Math.floor(e.touches[0].pageY/ SQUARE_SIZE);

    var coordinates = {x: xPosition, y: yPosition};
    return coordinates;
};

/*
 * Funkce na osetreni kliknuti
 * @param event: event, ktery vyvolal funkci
 */
var process_the_click = function process_the_click(event){
    var coordinates = get_click_postition(event);
    // neni zatim oznacena figurka
    if(active_figure.type == NO_FIGURE){
        select_figure(coordinates);
        // klikem byla oznacena figurka
        if(active_figure.type > NO_FIGURE){
            possible_moves = get_possible_moves(active_figure);
            possible_moves = edit_possible_moves(active_figure, possible_moves);
            draw_possible_moves();
        }
    } else {
        // byla uz oznacena figurka
        var square = board[coordinates.y][coordinates.x];
        // klikem byla oznacena jina figurka
        if(is_figure_players(square, active_player)){
            clean_possible_moves();
            select_figure(coordinates);
            possible_moves = get_possible_moves(active_figure);
            possible_moves = edit_possible_moves(active_figure, possible_moves);
            draw_possible_moves();
        } else if (square == NO_FIGURE){
            // bylo kliknuto na volne pole
            if(is_possible_move(coordinates)){
                if(move_figure(coordinates)){
                    clean_possible_moves();
                    redraw_figures();
                    var jump = did_figure_jump(active_figure, coordinates);
                    active_figure.x = coordinates.x;
                    active_figure.y = coordinates.y;
                    if(jump && has_figure_any_jump(active_figure)){
                        // dvojskok
                        possible_moves = get_possible_moves(active_figure);
                        possible_moves = edit_possible_moves(active_figure, possible_moves);
                        draw_possible_moves();
                    } else {
                        // neni mozny dvojskok
                        change_active_player();
                    }
                } else {
                    alert('Musíš skočit soupeřovu figurku.');
                }
            }
        }
    }
};

/*
 * Funkce na oznaceni figurky
 * @param coordinates: souradnice figurky, ktera by mela byt oznacena
 */
var select_figure = function select_figure(coordinates){
    var selected_square = board[coordinates.y][coordinates.x];
    if(selected_square > NO_FIGURE){
        if(is_figure_players(selected_square, active_player)){
            active_figure.type = selected_square;
            active_figure.x = coordinates.x;
            active_figure.y = coordinates.y;
        }
    }
};

/*
 * Funkce na vypocteni moznych tahu figurkou
 * @param figure: zkoumana figurka
 * @return pole moznych tahu
 */
var get_possible_moves = function get_possible_moves(figure){
    var moves;
    if(figure.type % 2 == WHITE_PAWN){
        moves = new Array(2);
        var dy = 1;
        if(is_figure_players(figure.type, PLAYER_WHITE)){
            dy = -1;
        }
        moves[0] = {x: figure.x - 1, y: figure.y + dy};
        moves[1] = {x: figure.x + 1, y: figure.y + dy};
    } else {
        moves = new Array(4);
        moves[0] = {x: figure.x - 1, y: figure.y + 1};
        moves[1] = {x: figure.x + 1, y: figure.y + 1};
        moves[2] = {x: figure.x - 1, y: figure.y - 1};
        moves[3] = {x: figure.x + 1, y: figure.y - 1};
    }
    return moves;
};

/*
 * Funkce na upravu moznych tahu
 * Funkce upravi mozne tahy tak, aby bylo zaznameno skakani a zruseny neplatne tahy 
 * @param figure: aktivni figurka
 * @param moves: pole moznych tahu
 * @return upravene pole moznych tahu
 */
var edit_possible_moves = function edit_possible_moves(figure, moves){
    for(var i = 0; i < moves.length; i++){
        var move = moves[i];
        if(move.x >= FIGURES_IN_LINE || move.y >= FIGURES_IN_LINE) {
            move.x = OUT_OF_BOUNDS;
            move.y = OUT_OF_BOUNDS;
        }
        if(move.x < 0 || move.y < 0){
            move.x = OUT_OF_BOUNDS;
            move.y = OUT_OF_BOUNDS;
        }
        if(move.x > OUT_OF_BOUNDS && board[move.y][move.x] > NO_FIGURE){
            if(can_figure_jump(figure, move)){
                move = edit_jump_move(move, figure);
            } else {
                move.x = OUT_OF_BOUNDS;
                move.y = OUT_OF_BOUNDS;
            }            
        }
        moves[i] = move;
    }
    return moves;
};

/*
 * Funkce na posunuti mozneho tahu z duvodu skoku
 * @param move: zkoumany tah
 * @param figure: zkoumana figurka
 * @return upraveny tah
 */
var edit_jump_move = function edit_jump_move(move, figure){
    var dx = move.x - figure.x;
    var dy = move.y - figure.y;
    move.x += dx;
    move.y += dy;
    return move;
};

/*
 * Funkce na vykresleni moznych tahu
 */
var draw_possible_moves = function draw_possible_moves(){
    var context = CANVAS_DRAWER.getContext('2d');
    for(var i = 0; i < possible_moves.length; i ++){
        var move = possible_moves[i];
        if(move.x > OUT_OF_BOUNDS && move.y > OUT_OF_BOUNDS){
            context.beginPath();
            context.fillStyle = 'rgba(255, 0, 0, 0.5)';
            context.fillRect(move.x * SQUARE_SIZE, move.y * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
        }
    }
};

/*
 * Funkce na vymazani moznych tahu
 */
var clean_possible_moves = function clean_possible_moves(){
    var context = CANVAS_DRAWER.getContext('2d');
    for(var i = 0; i < possible_moves.length; i ++){
        var move = possible_moves[i];
        if(move.x > OUT_OF_BOUNDS && move.y > OUT_OF_BOUNDS){
            context.beginPath();
            context.clearRect(move.x * SQUARE_SIZE, move.y * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
        }
    }
};

/*
 * Funkce na zjisteni, zda vybrana figurka patri vybranemu hraci hraci
 * @param figure: zkoumana figurka
 * @param player: zkoumany hrac
 * @return true, pokud je figurka vybraneho hrace
 */
var is_figure_players = function is_figure_players(figure, player){
    return Math.floor(figure / 2) == player;
};

/*
 * Funkce na zjisteni, zda figurka ma alespon jeden mozny skok
 * @param figure: zkoumana figurka
 * @return true, pokud figurka muze skakat
 */
var has_figure_any_jump = function has_figure_any_jump(figure){
    var moves = get_possible_moves(figure);
    var test = false;
    for(var i = 0; i < moves.length; i++){
        var move = moves[i];
        if(move.x > OUT_OF_BOUNDS && move.x < FIGURES_IN_LINE &&
           move.y > OUT_OF_BOUNDS && move.y < FIGURES_IN_LINE){
            if(can_figure_jump(figure, move)){
                test = true;
            }
        }
    }
    return test;
};

/*
 * Funkce na otestovani, zda figurka muze skakat jinou figuru
 * @param figure: testovana figurka
 * @param move: zkoumany tah
 * @return true, pokud figurka muze skakat
 */
var can_figure_jump = function can_figure_jump(figure, move){
    var tmp_move = {x: move.x, y: move.y};
    if(board[tmp_move.y][tmp_move.x] == NO_FIGURE){
        return false;
    }
    if(is_figure_players(board[tmp_move.y][tmp_move.x], active_player) === false){
        var dx = tmp_move.x - figure.x;
        var dy = tmp_move.y - figure.y;
        tmp_move.x += dx;
        tmp_move.y += dy;
        if(tmp_move.x == OUT_OF_BOUNDS || tmp_move.x == FIGURES_IN_LINE ||
           tmp_move.y == OUT_OF_BOUNDS || tmp_move.y == FIGURES_IN_LINE){
            return false;
        }
        if(board[tmp_move.y][tmp_move.x] == NO_FIGURE){
            return true;
        } else {
            return false;
        }
    }
    return false;
};

/*
 * Funkce na zjisteni, zda je tah v moznych tazich
 * Mozne tahy se zjistuji z globalni promenne
 * @param move: zkoumany tah
 * @return true, pokud je tah v moznych tazich
 */
var is_possible_move = function is_possible_move(move){
    for(var i = 0; i < possible_moves.length; i++){
        if(move.x == possible_moves[i].x && move.y == possible_moves[i].y){
            return true;
        }
    }
    return false;
};

/*
 * Funkce na posunuti aktivni figurky
 * @param move: tah, na ktery se ma figurka presunout
 * @return true, pokud byl tah platny
 */
var move_figure = function move_figure(move){    
    if(has_player_any_jump){
        if(did_figure_jump(active_figure, move) === false){
            return false;
        }
        var dx = (move.x - active_figure.x) / 2;
        var dy = (move.y - active_figure.y) / 2;
        board[active_figure.y + dy][active_figure.x + dx] = NO_FIGURE;
    }
    board[move.y][move.x] = active_figure.type;
    board[active_figure.y][active_figure.x] = NO_FIGURE;

    return true;
};

/*
 * Funkce na otestovani, zda figurka provedla akci skoku
 * @param figure: zkoumana figurka
 * @param move: pole, na ktere se ma pohnout
 * @return true, pokud figurka skocila
 */
var did_figure_jump = function did_figure_jump(figure, move){
    if(Math.abs(figure.x - move.x) > 1 && Math.abs(figure.y - move.y) > 1){
        return true;
    }
    return false;
};

/*
 * Funkce na zmenu aktivniho hrace a vymazani vsech globalnich promennych
 */
var change_active_player = function change_active_player(){
    if((active_figure.type % 2) == WHITE_PAWN && is_figure_at_end()){
        change_figure_to_queen();
        redraw_figures();
    }

    possible_moves = null;
    has_player_any_jump = false;

    active_figure.type = NO_FIGURE;
    active_figure.x = OUT_OF_BOUNDS;
    active_figure.y = OUT_OF_BOUNDS;
    
    active_player = (active_player + 1) % 2;
    ta_info_set_color();

    if(test_game_over()){
        alert('Konec hry! Vyhrál hráč ' + PLAYER_NAMES[(active_player + 1) % 2]);
        make_game_invisible();
        start_new_game();
        return;
    }

    for(var i = 0; i < FIGURES_IN_LINE; i++){
        for(var j = 0; j < FIGURES_IN_LINE; j++){
            if(is_figure_players(board[i][j], active_player)){
                var figure = {type: board[i][j], x: j, y: i};
                var can_jump = has_figure_any_jump(figure);
                if(can_jump){
                    has_player_any_jump = true;
                }
            }
        }
    }
};

/*
 * Funkce na zjisteni, jestli je figurka na poslednim radku
 * @return true, pokud je figurka na poslednim radku
 */
var is_figure_at_end = function is_figure_at_end(){
    if(active_figure.y == LAST_LINE[active_player]){
        return true;
    }
    return false;
};

/*
 * Funkce na premenu figurky na damu
 */
var change_figure_to_queen = function change_figure_to_queen(){
    board[active_figure.y][active_figure.x] += 1;
};

/*
 * Funkce na osetreni konce hry
 */
var test_game_over = function test_game_over(){
    var the_end = true;
    for(var i = 0; i < FIGURES_IN_LINE; i++){
        for(var j = 0; j < FIGURES_IN_LINE; j++){
            if(is_figure_players(board[j][i], active_player)){
                var figure = {type: board[j][i], x: i, y: j};
                var moves = get_possible_moves(figure);
                moves = edit_possible_moves(figure, moves);
                for(var k = 0; k < moves.length; k++){
                    var move = moves[k];
                    if(move.x > OUT_OF_BOUNDS && move.y > OUT_OF_BOUNDS){
                        the_end = false; 
                    }
                }
            }
        }
    }
    return the_end;
};

/*
 * Funkce na nastaveni barvy informacni textarea podle barvy hrace
 */
var ta_info_set_color = function ta_info_set_color(){
    var ta_info = document.getElementById('ta_info');
    ta_info.style.color = PLAYER_COLOR[active_player];
    ta_info.value = 'Hraje hráč:\t' + PLAYER_NAMES[active_player];
};

/*
 * Funkce na zviditelneni hry
 */
var make_game_visible = function make_game_visible(){
    document.getElementById('cnv_background_drawer').style.visibility='visible';
    document.getElementById('cnv_main_drawer').style.visibility='visible';
    document.getElementById('ta_info').style.visibility='visible';
    document.getElementById('ul').style.visibility='hidden';
};

/*
 * Funkce na zneviditelneni hry
 */
var make_game_invisible = function make_game_invisible(){
    document.getElementById('cnv_background_drawer').style.visibility='hidden';
    document.getElementById('cnv_main_drawer').style.visibility='hidden';
    document.getElementById('ta_info').style.visibility='hidden';
    document.getElementById('ul').style.visibility='visible';
};

/*
 * Funkce na inicializaci hlavniho menu
 */
var init_main_menu = function init_main_menu(){
    document.getElementById('ul').style.width= SCREEN_SIZE + 'px';
    document.getElementById('li_player_one').style.width= SCREEN_SIZE + 'px';
    document.getElementById('li_player_two').style.width= SCREEN_SIZE + 'px';

    button_on_click();
};

/*
 * Funkce na osetreni kliku na tlacitko formulare
 */
var button_on_click = function button_on_click(){
    document.getElementById('btn_start').addEventListener('click', function() {
        process_start();
    }, false);
};

/*
 * Funkce na start hry
 * @param event: event, ktery vyvolal funkci
 */
var process_start = function process_start(){
    if(set_player_names()){
        ta_info_set_color();
        make_game_visible();
    } else {
        alert('Vyplňte jména hráčů.');
    }
};

/*
 * Funkce na nastaveni jmen hracu
 * @return true, pokud se jmena podarilo uspesne nastavit
 */
var set_player_names = function set_player_names(){
    for(var i = 0; i < 2; i++){
        PLAYER_NAMES[i] = document.getElementsByTagName('input')[i].value;
        if(PLAYER_NAMES[i].length === 0){
            return false;
        }
    }
    return true;
};

/*
 * Funkce na spusteni nove hry
 */
var start_new_game = function start_new_game(){
    active_player = PLAYER_WHITE;
    init_board();
    generate_figures(WHITE_PAWN);
    generate_figures(BLACK_PAWN);
    redraw_figures();
};

/*
 * Proste main, co vic dodat
 */ 
 var main = function main() {    
    calculate_constants();
    init_main_menu();
    draw_background();
    start_new_game();
    set_listener();
};

main();
