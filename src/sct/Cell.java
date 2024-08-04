package sct;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;
import java.util.ListIterator;

public class Cell{
	ArrayList<Cell> objects;
	ArrayList<Tree> trees;
	Random rand = new Random();
	private int x;
	private int y;
	public int xpos;
	public int ypos;
	public Color color;
	public int energy;
	public int killed = 0;
	public int[][] map;
	public int[][] commands = new int[16][4];
	private int index = 0;
	public int state;//бот или органика
	public int state2;//что ставить в массив с миром
	private int[][] movelist = {
		{0, -1},
		{1, 0},
		{0, 1},
		{-1, 0}
	};
	private int[] world_scale = {324, 100};
	public Tree my_tree;
	public int max_age = 90;
	public Cell(int new_xpos, int new_ypos, Color new_color, int new_energy, int[][] new_map, ArrayList<Cell> new_objects, ArrayList<Tree> new_trees, int new_state) {
		xpos = new_xpos;
		ypos = new_ypos;
		x = new_xpos * 5;
		y = new_ypos * 5;
		color = new_color;
		energy = new_energy;
		objects = new_objects;
		trees = new_trees;
		map = new_map;
		state = new_state;
		state2 = state + 1;
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 4; j++) {
				commands[i][j] = rand.nextInt(32);
			}
		}
		//world_scale[0] = map.length;
		//world_scale[1] = map[0].length;
	}
	public void Draw(Graphics canvas, int draw_type, int[][] light) {
		if (draw_type == 0) {
			if (state == 0 || state == 2) {//рисуем отросток/семечко
				canvas.setColor(new Color(0, 0, 0));
				canvas.fillRect(x, y, 5, 5);
				canvas.setColor(new Color(255, 255, 255));
				canvas.fillRect(x + 1, y + 1, 3, 3);
			}else if (state == 1){//рисуем лист
				canvas.setColor(new Color(0, 0, 0));
				canvas.fillRect(x, y, 5, 5);
				canvas.setColor(color);
				canvas.fillRect(x + 1, y + 1, 3, 3);
			}
		}else if (draw_type == 1) {
			canvas.setColor(new Color(0, 0, 0));
			canvas.fillRect(x, y, 5, 5);
			canvas.setColor(new Color((int)(light[xpos][ypos] / 42.0 * 255), (int)(light[xpos][ypos] / 42.0 * 255), 255 - (int)(light[xpos][ypos] / 42.0 * 255)));
			canvas.fillRect(x + 1, y + 1, 3, 3);
		}else if (draw_type == 2) {
			canvas.setColor(new Color(0, 0, 0));
			canvas.fillRect(x, y, 5, 5);
			int a = (int)my_tree.energy / 10000;
			canvas.setColor(new Color(0, 0, 0));
			canvas.fillRect(x + 1, y + 1, 3, 3);
		}
	}
	public int Update(ListIterator<Cell> iterator, Cell self, int[][] light) {
		if (killed == 0) {
			if (state == 0) {//отросток
				my_tree.energy -= 10;
				energy += light[xpos][ypos];
				if (energy >= 18){
					multiply(iterator);
				}
			}else if (state == 1) {//лист
				my_tree.energy -= 10;
				my_tree.energy += light[xpos][ypos];
				//System.out.println(light[xpos][ypos]);
			}else {//семечко
				move(2);
				int[] pos = get_rotate_position(2);
				if (pos[1] > 0 & pos[1] < world_scale[1]) {
					if (map[pos[0]][pos[1]] != 0) {
						map[xpos][ypos] = 0;
						killed = 1;
						return(1);
					}
				}else if (pos[1] >= world_scale[1]) {
					Tree new_tree = new Tree(energy, commands);
					new_tree.seed_count++;
					energy = 0;
					new_tree.cells.add(self);
					new_tree.age = max_age;
					my_tree = new_tree;
					trees.add(new_tree);
					state = 0;
					state2 = 1;
					map[xpos][ypos] = 1;
				}
			}
		}
		return(0);
	}
	public Cell find(int[] pos) {//только если есть сосед
		for (Cell b: objects) {
			if (b.killed == 0 & b.xpos == pos[0] & b.ypos == pos[1]) {
				return(b);
			}
		}
		return(null);
	}
	public int[] get_rotate_position(int rot){
		int[] pos = new int[2];
		pos[0] = (xpos + movelist[rot][0]) % world_scale[0];
		pos[1] = ypos + movelist[rot][1];
		if (pos[0] < 0) {
			pos[0] = 323;
		}else if(pos[0] >= world_scale[0]) {
			pos[0] = 0;
		}
		return(pos);
	}
	public int move(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] == 0) {
				map[xpos][ypos] = 0;
				xpos = pos[0];
				ypos = pos[1];
				x = xpos * 5;
				y = ypos * 5;
				map[xpos][ypos] = state2;
				return(1);
			}
		}
		return(0);
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
	public void multiply(ListIterator<Cell> iterator) {
		my_tree.seed_count--;
		my_tree.cells_count++;
		for (int i = 0; i < 4; i++) {
			if (commands[index][i] < 16) {
				int[] pos = get_rotate_position(i);
				if (pos[1] > 0 & pos[1] < world_scale[1]) {
					if (map[pos[0]][pos[1]] == 0) {
						int[][] new_commands = new int [16][4];
						for (int g = 0; g < 16; g++) {
							for (int j = 0; j < 4; j++) {
								new_commands[g][j] = commands[g][j];
							}
						}
						Cell new_cell = new Cell(pos[0], pos[1], color, 0, map, objects, trees, 0);
						map[pos[0]][pos[1]] = 1;
						new_cell.commands = new_commands;
						new_cell.my_tree = my_tree;
						new_cell.index = commands[index][i];
						my_tree.cells.add(new_cell);
						my_tree.seed_count++;
						iterator.add(new_cell);
					}
				}
			}
		}
		state = 1;
		state2 = 2;
		map[xpos][ypos] = 2;
	}
}
