package Manh;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.util.Random;

import javax.swing.JLabel;

public class Philosopher extends Thread {
	private int identity;//Đánh số thức tự của các triết gia
	private DiningPhilosophers controller;//Dùng để cập nhật giao diện
	private Fork left;//Nĩa trái
	private Fork right;//Nĩa phải
	private JLabel philLable;
	private JLabel comments;
	private JLabel meals;
	private JLabel forkLeft, forkRight;
	private Random randomPeriod = new Random();
	int numberOfMeals = 0;

	public Philosopher(DiningPhilosophers ctr, int id, Fork left, Fork right, JLabel philLable, JLabel comments,
			JLabel meals, JLabel forkLeft, JLabel forkRight) {
		// TODO Auto-generated constructor stub
		this.controller = ctr;
		this.identity = id;
		this.left = left;
		this.right = right;
		this.philLable = philLable;
		this.comments = comments;
		this.meals = meals;
		this.forkLeft = forkLeft;
		this.forkRight = forkRight;
	}

	public void Wait() throws InterruptedException {
		if (identity % 2 == 0) {
			controller.setPhil(identity, controller.HUNGRY);
			hungry();
			right.get();
			// gotright fork
			controller.setPhil(identity, controller.GOTRIGHT);	
			sleep(500);
			left.get();
		} else {
			controller.setPhil(identity, controller.HUNGRY);
			hungry();
			left.get();
			// gotleft fork
			controller.setPhil(identity, controller.GOTLEFT);
			sleep(500);
			right.get();
		}
	}

	public void Signal() {
		right.put();
		left.put();
	}
	
	@Override
	public void run() {
//		super.run();
		try {
			while (true) {
				// thinking
				controller.setPhil(identity, controller.THINKING);
				think();
				sleep(50 * randomPeriod.nextInt(100));
				// hungry
				Wait();
				// eating
				controller.setPhil(identity, controller.EATING);
				System.out.println("Philosopher# " + identity + " Eating");
				eat();
				sleep(50 * randomPeriod.nextInt(100));
				Signal();
//				controller.Message += "   Philosopher " + identity + " EATED" + "\n";
//				controller.txtMes.setText(controller.Message);
			}
		} catch (InterruptedException e) {
			// TODO: handle exception
		}
	}

	// Philosopher is thinking in this method
	private void think() {
		if(controller.state[(identity -1+controller.NUMPHILS) % controller.NUMPHILS]==controller.EATING) {
			forkRight.setIcon(controller.forkAvailableIcons[(identity + 1 + controller.NUMPHILS) % controller.NUMPHILS]);
		}
		if(controller.state[(identity+1+controller.NUMPHILS) % controller.NUMPHILS]==controller.EATING) {
			forkLeft.setIcon(controller.forkAvailableIcons[identity]);
		}
		if(controller.state[(identity -1+controller.NUMPHILS) % controller.NUMPHILS]!=controller.EATING && controller.state[(identity+1+controller.NUMPHILS) % controller.NUMPHILS]!=controller.EATING) {
			forkRight.setIcon(controller.forkAvailableIcons[(identity + 1 + controller.NUMPHILS) % controller.NUMPHILS]);
			forkLeft.setIcon(controller.forkAvailableIcons[identity]);
		}
		
		philLable.setIcon(controller.philThinkingIcons[identity]);
		comments.setText("Philosopher# " + identity + " Thinking");
		comments.setForeground(Color.RED);
	}

	// Philosopher is hungry and asking for permission to pick up the forks to eat
	public void hungry() {
		comments.setText("Philosopher# " + identity + " Hungry");
		comments.setForeground(Color.ORANGE);
	}

	private void eat() throws InterruptedException {
//		System.out.println("Day la dia: trai"+identity+"----Phai:"+(identity + 1 + controller.NUMPHILS) % controller.NUMPHILS);
		forkRight.setIcon(controller.forkUsedIcons[(identity + 1 + controller.NUMPHILS) % controller.NUMPHILS]);
		forkLeft.setIcon(controller.forkUsedIcons[identity]);
		controller.Message += "   Philosopher " + identity + " EATING" + "\n";
		controller.txtMes.setText(controller.Message);
		numberOfMeals++;
		comments.setText("Philosopher# " + identity + " Eating");
		comments.setForeground(Color.GREEN);
		meals.setText("Meal # :" + numberOfMeals);
		meals.setForeground(Color.BLUE);
		philLable.setIcon(controller.philEatingIcons[identity]);
	}

}
