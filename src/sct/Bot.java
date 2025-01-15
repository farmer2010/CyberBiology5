package sct;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;
import java.util.ListIterator;

public class Bot{
	ArrayList<Bot> objects;
	Random rand = new Random();
	private int x;
	private int y;
	public int xpos;
	public int ypos;
	public Color color;
	public int energy;
	public int minerals;
	public int killed = 0;
	public Bot[][] map;
	public int[][] commands = new int[16][4];
	private int index = 0;
	public int state = 0;//тип клетки
	public int state2 = 1;//что ставить в массив с миром
	private int rotate = rand.nextInt(4);
	private int[][] movelist = {
		{0, -1},
		{1, 0},
		{0, 1},
		{-1, 0}
	};
	private int[] minerals_list = {
		1,
		2,
		3
	};
	private int[] photo_list = {
		10,
		8,
		6,
		5,
		4,
		3
	};
	private int[] world_scale = {162, 108};
	private int sector_len = world_scale[1] / 8;
	public int parent = -1;
	public int[] energy_to = {0, 0, 0, 0};
	public int[] energy_from = {0, 0, 0, 0};
	public Bot(int new_xpos, int new_ypos, Color new_color, int new_energy, Bot[][] new_map, ArrayList<Bot> new_objects) {
		xpos = new_xpos;
		ypos = new_ypos;
		x = new_xpos * 10;
		y = new_ypos * 10;
		color = new_color;
		energy = new_energy;
		minerals = 0;
		objects = new_objects;
		map = new_map;
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 4; j++) {
				commands[i][j] = rand.nextInt(128);
			}
		}
		//world_scale[0] = map.length;
		//world_scale[1] = map[0].length;
	}
	public void Draw(Graphics canvas, int draw_type) {
		if (state == 0) {//рисуем отросток
			canvas.setColor(new Color(0, 0, 0));
			canvas.fillRect(x, y, 10, 10);
			canvas.setColor(new Color(195, 123, 146));
			canvas.fillRect(x + 1, y + 1, 8, 8);
		}else if (state == 1){//рисуем транспортную
			//canvas.setColor(new Color(0, 0, 0));
			//canvas.fillRect(x, y, 10, 10);
			//canvas.setColor(new Color(128, 128, 128));
			//canvas.fillRect(x + 1, y + 1, 8, 8);
			//canvas.setColor(new Color(0, 0, 0));
			//if (draw_type == 0) {
			//	if (parent != -1) {
			//		canvas.drawLine(x + 5, y + 5, x + 5 + movelist[parent][0] * 4, y + 5 + movelist[parent][1] * 4);
			//	}
			//}else {
			//	for (int i = 0; i < 4; i++) {
			//		if (energy_to[i] != 0) {
			//			canvas.drawLine(x + 5, y + 5, x + 5 + movelist[i][0] * 4, y + 5 + movelist[i][1] * 4);
			//		}
			//	}
			//}
			canvas.setColor(new Color(128, 128, 128));
			if (energy_to[0] == 1 || energy_from[0] == 1) {//палка вверх
				canvas.fillRect(x + 3, y, 4, 7);
			}
			if (energy_to[1] == 1 || energy_from[1] == 1) {//палка вправо
				canvas.fillRect(x + 3, y + 3, 7, 4);
			}
			if (energy_to[2] == 1 || energy_from[2] == 1) {//палка вниз
				canvas.fillRect(x + 3, y + 3, 4, 7);
			}
			if (energy_to[3] == 1 || energy_from[3] == 1) {//палка влево
				canvas.fillRect(x, y + 3, 7, 4);
			}
		}else if (state == 2) {
			canvas.setColor(new Color(0, 0, 0));
			canvas.fillRect(x, y, 10, 10);
			canvas.setColor(new Color(0, 255, 0));
			canvas.fillRect(x + 1, y + 1, 8, 8);
		}
	}
	public int Update(ListIterator<Bot> iterator) {
		if (killed == 0) {
			if (state == 0) {//отросток
				//energy -= 1;
				update_commands(iterator);
				//if (energy <= 0) {
				//	killed = 1;
				//	map[xpos][ypos] = 0;
				//	return(0);
				//}else if (energy > 1000) {
				//	energy = 1000;
				//}
			}else if (state == 1) {//транспортная
				for (int i = 0; i < 4; i++) {
					int[] pos = get_rotate_position(i);
					if (pos[1] >= 0 & pos[1] < world_scale[1]) {
						if (map[pos[0]][pos[1]] == null) {
							energy_to[i] = 0;
						}
					}
				}
				if (energy_to[0] == 0 && energy_to[1] == 0 && energy_to[2] == 0 && energy_to[3] == 0) {
					if (parent != -1) {
						energy_to[parent] = 1;
						energy_from[parent] = 0;
						int[] pos = get_rotate_position(parent);
						Bot p = map[pos[0]][pos[1]];
						if (p != null) {
							p.energy_to[(parent + 2) % 4] = 0;
							p.energy_from[(parent + 2) % 4] = 1;
						}else {
							die();
						}
					}else {
						die();
					}
				}
			}else if (state == 2){//лист
				if (energy_to[0] == 0 && energy_to[1] == 0 && energy_to[2] == 0 && energy_to[3] == 0 || parent == -1) {
					die();
				}
			}
		}
		return(0);
	}
	public void die() {
		killed = 1;
		map[xpos][ypos] = null;
		for (int i = 0; i < 4; i++) {
			int[] pos = get_rotate_position(i);
			if (pos[1] >= 0 & pos[1] < world_scale[1]) {
				Bot b = map[pos[0]][pos[1]];
				if (b != null) {
					b.energy_to[(i + 2) % 2] = 0;
					if (b.parent == (i + 2) % 4) {
						b.parent = -1;
					}
				}
			}
		}
	}
	public void update_commands(ListIterator<Bot> iterator) {//мозг
		int[] command = commands[index];
		if (command[0] <= 37) {
			int rot = (rotate + 3) % 4;
			int type = 0;
			if (command[0] > 31) {
				type = 1;
			}
			multiply(rot, command[0] % 16, type, iterator);
		}
		if (command[1] <= 37) {
			int rot = rotate % 4;
			int type = 0;
			if (command[1] > 31) {
				type = 1;
			}
			multiply(rot, command[1] % 16, type, iterator);
		}
		if (command[2] <= 37) {
			int rot = (rotate + 1) % 4;
			int type = 0;
			if (command[2] > 31) {
				type = 1;
			}
			multiply(rot, command[2] % 16, type, iterator);
		}
		state = 1;
	}
	public int[] get_rotate_position(int rot){
		int[] pos = new int[2];
		pos[0] = (xpos + movelist[rot][0]) % world_scale[0];
		pos[1] = ypos + movelist[rot][1];
		if (pos[0] < 0) {
			pos[0] = 161;
		}else if(pos[0] >= world_scale[0]) {
			pos[0] = 0;
		}
		return(pos);
	}
	public void multiply(int rot, int gen, int type, ListIterator<Bot> iterator) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] == null) {
				//energy -= 150;
				if (energy <= 0) {
					killed = 1;
					map[xpos][ypos] = null;
				}else {
					if (type == 0) {
						energy_to[rot] = 1;
					}else if (type == 1){
						energy_from[rot] = 1;
					}
					Color new_color = color;
					int[][] new_brain = new int[16][4];
					for (int i = 0; i < 16; i++) {
						for (int j = 0; j < 4; j++) {
							new_brain[i][j] = commands[i][j];
						}
					}
					if (rand.nextInt(4) == 0) {//мутация
						//new_color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
						new_brain[rand.nextInt(16)][rand.nextInt(4)] = rand.nextInt(128);
					}
					Bot new_bot = new Bot(pos[0], pos[1], new_color, energy, map, objects);
					new_bot.minerals = minerals / 2;
					//energy /= 2;
					minerals /= 2;
					new_bot.commands = new_brain;
					new_bot.index = gen;
					if (type == 0) {
						new_bot.energy_from[(rot + 2) % 4] = 1;
					}else {
						new_bot.energy_to[(rot + 2) % 4] = 1;
					}
					new_bot.rotate = rot;
					new_bot.parent = (rot + 2) % 4;
					new_bot.state = type * 2;
					map[pos[0]][pos[1]] = new_bot;
					iterator.add(new_bot);
				}
			}
		}
	}
	public int bot_in_sector() {
		int sec = ypos / sector_len;
		if (sec > 7) {
			sec = 10;
		}
		return(sec);
	}
	public int border(int number, int border1, int border2) {
		if (number > border1) {
			number = border1;
		}else if (number < border2) {
			number = border2;
		}
		return(number);
	}
	public int max(int number1, int number2) {//максимальное из двух чисел
		if (number1 > number2) {
			return(number1);
		}else if (number2 > number1) {
			return(number2);
		}else {
			return(number1);
		}
	}
}
