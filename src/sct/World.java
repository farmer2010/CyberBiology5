package sct;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;
import javax.swing.*;

import java.awt.Font;
//
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.awt.Graphics2D;

public class World extends JPanel{
	ArrayList<Cell> objects;
	int size = 25;
	Timer timer;
	int delay = 10;
	Random rand = new Random();
	int[][] Map = new int[324][100];//0 - none, 1 - bot, 2 - organics
	int[][] light = new int[324][100];
	ArrayList<Tree> trees = new ArrayList<Tree>();
	Color gray = new Color(100, 100, 100);
	Color green = new Color(0, 255, 0);
	Color red = new Color(255, 0, 0);
	Color black = new Color(0, 0, 0);
	int steps = 0;
	int draw_type = 0;
	int b_count = 0;
	int obj_count = 0;
	int org_count = 0;
	String txt;
	String txt2;
	int mouse = 0;
	int W = 1920;
	int H = 1080;
	JButton stop_button = new JButton("Stop");
	boolean pause = false;
	boolean render = true;
	JButton save_button = new JButton("Save");
	JButton render_button = new JButton("Render: on");
	boolean rec = false;
	int[] botpos;
	public World() {
		setLayout(null);
		timer = new Timer(delay, new BotListener());
		objects = new ArrayList<Cell>();
		setBackground(new Color(255, 255, 255));
		addMouseListener(new BotListener());
		addMouseMotionListener(new BotListener());
		stop_button.addActionListener(new start_stop());
		stop_button.setBounds(W - 300, 125, 250, 35);
        add(stop_button);
        //
        JButton new_population_button = new JButton("New population");
        new_population_button.addActionListener(new nwp());
        new_population_button.setBounds(W - 300, 165, 250, 35);
        add(new_population_button);
        //
        render_button.addActionListener(new rndr());
        render_button.setBounds(W - 300, 205, 250, 35);
        add(render_button);
        //
        JButton types_button = new JButton("Types");
        types_button.addActionListener(new dr1());
        types_button.setBounds(W - 300, 265, 250, 35);
        add(types_button);
        //
        JButton light_button = new JButton("Light");
        light_button.addActionListener(new dr2());
        light_button.setBounds(W - 300, 305, 250, 35);
        add(light_button);
		timer.start();
	}
	public void paintComponent(Graphics canvas) {
		super.paintComponent(canvas);
		if (render) {
			for(Cell b: objects) {
				b.Draw(canvas, draw_type, light);
			}
		}
		//for (int x = 0; x < 324; x++) {
		//	for (int y = 0; y < 216; y++) {
		//		if (Map[x][y] == 1) {
		//			canvas.setColor(green);
		//			canvas.fillRect(x * 5, y * 5, 2, 2);
		//		}else if (Map[x][y] == 2){
		//			canvas.setColor(red);
		//			canvas.fillRect(x * 5, y * 5, 2, 2);
		//		}else if (Map[x][y] == 2){
		//			canvas.setColor(new Color(0, 255, 0));
		//			canvas.fillRect(x * 5, y * 5, 2, 2);
		//		}
		//	}
		//}
		canvas.setColor(gray);
		canvas.fillRect(W - 300, 0, 300, 1080);
		canvas.fillRect(0, 500, 1920, 580);
		canvas.setColor(black);
		canvas.setFont(new Font("arial", Font.BOLD, 18));
		canvas.drawString("Main: ", W - 300, 20);
		canvas.drawString("version 1", W - 300, 40);
		canvas.drawString("steps: " + String.valueOf(steps), W - 300, 60);
		canvas.drawString("objects: " + String.valueOf(obj_count) + ", bots: " + String.valueOf(b_count), W - 300, 80);
		if (draw_type == 0) {
			txt = "color view";
		}else if (draw_type == 1) {
			txt = "light view";
		}else if (draw_type == 2) {
			txt = "energy+ view";
		}else if (draw_type == 3) {
			txt = "energy for cell view";
		}
		canvas.drawString("render type: " + txt, W - 300, 100);
		if (mouse == 0) {
			txt2 = "select";
		}else if (mouse == 1) {
			txt2 = "set";
		}else {
			txt2 = "remove";
		}
		//canvas.drawString("mouse function: " + txt2, W - 300, 120);
		canvas.drawString("Render types:", W - 300, 260);
		//canvas.drawString("Selection:", W - 300, 275);
		//canvas.drawString("enter name:", W - 300, 405);
		//canvas.drawString("Mouse functions:", W - 300, 445);
		//canvas.drawString("Load:", W - 300, 490);
		//canvas.drawString("enter name:", W - 300, 510);
		//canvas.drawString("Controls:", W - 300, 580);
	}
	public void newPopulation() {
		steps = 0;
		objects = new ArrayList<Cell>();
		trees = new ArrayList<Tree>();
		Map = new int[324][216];//0 - none, 1 - bot, 2 - organics
		light = new int[324][216];
		for (int i = 0; i < 10; i++) {
			while(true){
				int x = rand.nextInt(324);
				int y = 99;
				if (Map[x][y] == 0) {
					objects.add(new Cell(
						x,
						y,
						new Color(rand.nextInt(256),rand.nextInt(256), rand.nextInt(256)),
						1000,
						Map,
						objects,
						trees,
						2
					));
					Map[x][y] = 3;
					break;
				}
			}
		}
		repaint();
	}
	public void sun(int solid, int levels, int xpos) {
		int max = solid;
		for (int ypos = 0; ypos < 100; ypos++) {
			if (Map[xpos][ypos] == 0) {
				light[xpos][ypos] = 0;
				solid++;
				if (solid > max) {
					solid = max;
				}
			}else if (levels > 0){
				if (levels * solid > 0) {
					light[xpos][ypos] = levels * solid;
				}
				levels--;
				solid--;
			}else {
				light[xpos][ypos] = 0;
			}
		}
	}
	private class BotListener extends MouseAdapter implements ActionListener{
		public void mousePressed(MouseEvent e) {
			//if (e.getX() < W - 300) {
			//	botpos[0] = e.getX() / 5;
			//	botpos[1] = e.getY() / 5;
			//	if (mouse == 0) {//select
			//	}else if (mouse == 1) {//set
			//	}else {//remove
			//	}
			//}
		}
		public void mouseDragged(MouseEvent e) {
			//if (e.getX() < W - 300) {
			//	botpos[0] = e.getX() / 5;
			//	botpos[1] = e.getY() / 5;
			//	if (mouse == 1) {//set
			//	}else if (mouse == 2) {//remove
			//	}
			//}
		}
		public void actionPerformed(ActionEvent e) {
			if (!pause) {
				steps++;
				b_count = 0;
				obj_count = 0;
				org_count = 0;
				for (int h = 0; h < 324; h++) {
					sun(3, 10, h);
				}
				ListIterator<Cell> cell_iterator = objects.listIterator();
				while (cell_iterator.hasNext()) {
					Cell next_cell = cell_iterator.next();
					next_cell.Update(cell_iterator, next_cell, light);
					obj_count++;
				}
				ListIterator<Tree> tree_iterator = trees.listIterator();
				while (tree_iterator.hasNext()) {
					Tree next_tree = tree_iterator.next();
					next_tree.age--;
					//System.out.println(next_tree.age);
					if (next_tree.age <= 0) {
						next_tree.kill(light, Map);
						tree_iterator.remove();
					}else if (next_tree.energy <= 0) {
						next_tree.kill_without_seeds(light, Map);
						tree_iterator.remove();
					}
				}
			}
			ListIterator<Cell> iterator = objects.listIterator();
			while (iterator.hasNext()) {
				Cell next_cell = iterator.next();
				if (next_cell.killed == 1) {
					iterator.remove();
					//System.out.println(1);
				}
			}
			repaint();
			
		}
		
	}
	private class dr1 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			draw_type = 0;
		}
	}
	private class dr2 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			draw_type = 1;
		}
	}
	private class dr3 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			draw_type = 2;
		}
	}
	private class dr4 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			draw_type = 3;
		}
	}
	private class dr5 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			draw_type = 4;
		}
	}
	private class start_stop implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			pause = !pause;
			if (pause) {
				stop_button.setText("Start");
			}else {
				stop_button.setText("Stop");
			}
		}
	}
	private class nwp implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			newPopulation();
		}
	}
	private class rndr implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			render = !render;
			if (render) {
				render_button.setText("Render: on");
			}else {
				render_button.setText("Render: off");
			}
		}
	}
}
