package sct;

import java.awt.Color;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

public class Tree {
	Random rand = new Random();
	int energy;
	int age = 90;
	int[][] genom;
	int killed = 0;
	int cells_count = 0;
	int seed_count = 0;
	ArrayList<Cell> cells = new ArrayList<Cell>();
	public Tree(int new_energy, int[][] new_genom) {
		energy = new_energy;
		genom = new_genom;
	}
	public void kill(int[][] light, int[][] map) {
		ListIterator<Cell> cell_iterator = cells.listIterator();
		while (cell_iterator.hasNext()) {
			Cell next_cell = cell_iterator.next();
			if (next_cell.state == 0) {
				next_cell.state = 2;
				next_cell.my_tree = null;
				next_cell.energy = (int)(energy / seed_count);
				if (rand.nextInt(4) == 0) {
					next_cell.color = new Color(rand.nextInt(256),rand.nextInt(256), rand.nextInt(256));
					next_cell.commands[rand.nextInt(16)][rand.nextInt(9)] = rand.nextInt(64);
					next_cell.max_age = border(next_cell.max_age + rand.nextInt(-3, 3), 100, 80);
				}
				next_cell.map[next_cell.xpos][next_cell.ypos] = 3;
			}else {
				next_cell.killed = 1;
				next_cell.map[next_cell.xpos][next_cell.ypos] = 0;
			}
		}
	}
	public void kill_without_seeds(int[][] light, int[][] map) {
		ListIterator<Cell> cell_iterator = cells.listIterator();
		while (cell_iterator.hasNext()) {
			Cell next_cell = cell_iterator.next();
			next_cell.killed = 1;
			next_cell.map[next_cell.xpos][next_cell.ypos] = 0;
		}
	}
	public int border(int number, int border1, int border2) {
		if (number > border1) {
			number = border1;
		}else if (number < border2) {
			number = border2;
		}
		return(number);
	}
}
