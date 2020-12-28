package Manh;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class DiningPhilosophers {

	public static final int NUMPHILS = 5;
	public static final int THINKING = 0;
	public static final int HUNGRY = 1;
	public static final int GOTRIGHT = 2;
	public static final int EATING = 3;
	public static final int GOTLEFT = 4;

	public int[] state = new int[NUMPHILS];

	boolean[] untable = new boolean[NUMPHILS];
	boolean frozen = false;

	String Message = "";

	private JFrame frame;
	Thread[] phil = new Thread[NUMPHILS];
	Fork[] fork = new Fork[NUMPHILS];
	JButton restart;
	JButton freeze;
	JButton start;
	JButton continous;
	TextArea txtMes;

	JLabel[] PhilsLables = new JLabel[5];
	JLabel[] forksLables = new JLabel[5];
	JLabel[] comments = new JLabel[5];
	JLabel[] meals = new JLabel[5];

	JLabel philLable_0;
	JLabel philLable_1;
	JLabel philLable_2;
	JLabel philLable_3;
	JLabel philLable_4;
	JLabel forkLable_0;
	JLabel forkLable_1;
	JLabel forkLable_2;
	JLabel forkLable_3;
	JLabel forkLable_4;
	private JLabel lblNewLabel_0;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JLabel lblNewLabel_3;
	private JLabel lblNewLabel_4;
	private JLabel meal_0;
	private JLabel meal_1;
	private JLabel meal_2;
	private JLabel meal_3;
	private JLabel meal_4;

	//Tạo mảng lưu hình ảnh của cái nĩa và triết gia
	ImageIcon plateIcon = new ImageIcon(getClass().getResource("../img/plate1.png"));
	static ImageIcon[] philThinkingIcons = new ImageIcon[5];
	static ImageIcon[] philEatingIcons = new ImageIcon[5];
	static ImageIcon[] forkAvailableIcons = new ImageIcon[5];
	static ImageIcon[] forkUsedIcons = new ImageIcon[5];

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DiningPhilosophers window = new DiningPhilosophers();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public DiningPhilosophers() {
		initialize();
	}
	
	//Tạo GUI
	private void initialize() {
		// GUI Components
		frame = new JFrame("DiningPhilosophers");
		frame.setBounds(0, 0, 1200, 800);
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		start = new JButton("Start");
		start.setBounds(220, 639, 77, 30);
//		start.setBackground(Color.GREEN);
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				start();
			}

		});
		restart = new JButton("Restart");
		restart.setBounds(300, 639, 77, 30);
		restart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (deadlocked()) {
					stop();
					start();
				}
				stop();
				thaw();
				for (int i = 0; i < NUMPHILS; ++i) {
					meals[i].setText("Meal # :");
					meals[i].setForeground(Color.black);
				}
				start();
				freeze.setEnabled(true);
			}
		});
		freeze = new JButton("Freeze");
		freeze.setBounds(380, 639, 77, 30);
		freeze.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				freeze();
				continous.setEnabled(true);
				freeze.setEnabled(false);
			}
		});

		continous = new JButton("Continous");
		continous.setEnabled(false);
		continous.setBounds(460, 639, 100, 30);
		continous.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (deadlocked()) {
					stop();
					start();
					
				}
				thaw();
				freeze.setEnabled(true);
				continous.setEnabled(false);
			}
		});

		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(start);
		frame.getContentPane().add(restart);
		frame.getContentPane().add(freeze);
		frame.getContentPane().add(continous);
		
		//Sử dụng hình
		for (int i = 0; i < philThinkingIcons.length; i++) {
			philThinkingIcons[i] = new ImageIcon(getClass().getResource("../img/phil_" + i + "_thinking.png"));
			philEatingIcons[i] = new ImageIcon(getClass().getResource("../img/phil_" + i + "_eating.png"));
			forkAvailableIcons[i] = new ImageIcon(getClass().getResource("../img/fork_" + i + "_available.png"));
			forkUsedIcons[i] = new ImageIcon(getClass().getResource("../img/fork_" + i + "_used.png"));
		}

		JLabel plateLable = new JLabel("Midle Plate");
		plateLable.setBounds(245, 170, 336, 333);
		plateLable.setIcon(plateIcon);
		frame.getContentPane().add(plateLable);

		philLable_0 = new JLabel("Philosopher 0");
		philLable_0.setBounds(369, 50, 89, 110);
		philLable_0.setIcon(philThinkingIcons[0]);
		frame.getContentPane().add(philLable_0);

		philLable_1 = new JLabel("Philosopher 1");
		philLable_1.setBounds(573, 165, 100, 110);
		philLable_1.setIcon(philThinkingIcons[1]);
		frame.getContentPane().add(philLable_1);

		philLable_2 = new JLabel("Philosopher 2");
		philLable_2.setBounds(548, 420, 100, 110);
		philLable_2.setIcon(philThinkingIcons[2]);
		frame.getContentPane().add(philLable_2);

		philLable_3 = new JLabel("Philosopher 3");
		philLable_3.setBounds(187, 434, 100, 110);
		philLable_3.setIcon(philThinkingIcons[3]);
		frame.getContentPane().add(philLable_3);

		philLable_4 = new JLabel("Philosopher 4");
		philLable_4.setBounds(159, 165, 100, 110);
		philLable_4.setIcon(philThinkingIcons[4]);
		frame.getContentPane().add(philLable_4);

		forkLable_0 = new JLabel("Fork 0");
		forkLable_0.setBounds(269, 88, 55, 110);
		forkLable_0.setIcon(forkAvailableIcons[0]);
		frame.getContentPane().add(forkLable_0);

		forkLable_1 = new JLabel("Fork 1");
		forkLable_1.setBounds(504, 88, 63, 110);
		forkLable_1.setIcon(forkAvailableIcons[1]);
		frame.getContentPane().add(forkLable_1);

		forkLable_2 = new JLabel("Fork 2");
		forkLable_2.setBounds(583, 286, 120, 100);
		forkLable_2.setIcon(forkAvailableIcons[2]);
		frame.getContentPane().add(forkLable_2);

		forkLable_3 = new JLabel("Fork 3");
		forkLable_3.setBounds(407, 514, 27, 96);
		forkLable_3.setIcon(forkAvailableIcons[3]);
		frame.getContentPane().add(forkLable_3);

		forkLable_4 = new JLabel("Fork 4");
		forkLable_4.setBounds(124, 298, 111, 96);
		forkLable_4.setIcon(forkAvailableIcons[4]);
		frame.getContentPane().add(forkLable_4);

		lblNewLabel_0 = new JLabel("Philosopher# 0");
		lblNewLabel_0.setBounds(369, 8, 169, 14);
		frame.getContentPane().add(lblNewLabel_0);

		lblNewLabel_1 = new JLabel("Philosopher# 1");
		lblNewLabel_1.setBounds(683, 213, 169, 14);
		frame.getContentPane().add(lblNewLabel_1);

		lblNewLabel_2 = new JLabel("Philosopher# 2");
		lblNewLabel_2.setBounds(657, 489, 169, 14);
		frame.getContentPane().add(lblNewLabel_2);

		lblNewLabel_3 = new JLabel("Philosopher# 3");
		lblNewLabel_3.setBounds(25, 482, 169, 14);
		frame.getContentPane().add(lblNewLabel_3);

		lblNewLabel_4 = new JLabel("Philosopher# 4");
		lblNewLabel_4.setBounds(25, 213, 169, 14);
		frame.getContentPane().add(lblNewLabel_4);

		meal_0 = new JLabel("Meal #");
		meal_0.setBounds(369, 25, 80, 14);
		frame.getContentPane().add(meal_0);

		meal_1 = new JLabel("Meal #");
		meal_1.setBounds(683, 238, 80, 14);
		frame.getContentPane().add(meal_1);

		meal_2 = new JLabel("Meal #");
		meal_2.setBounds(658, 514, 80, 14);
		frame.getContentPane().add(meal_2);

		meal_3 = new JLabel("Meal #");
		meal_3.setBounds(25, 501, 80, 14);
		frame.getContentPane().add(meal_3);

		meal_4 = new JLabel("Meal #");
		meal_4.setBounds(25, 238, 80, 14);
		frame.getContentPane().add(meal_4);
		txtMes = new TextArea();
		txtMes.setBounds(900, 200, 250, 350);
		frame.getContentPane().add(txtMes);

		// adding all the labels to Arrays of labels for easy access
		PhilsLables[0] = philLable_0;
		PhilsLables[1] = philLable_1;
		PhilsLables[2] = philLable_2;
		PhilsLables[3] = philLable_3;
		PhilsLables[4] = philLable_4;

		forksLables[0] = forkLable_0;
		forksLables[1] = forkLable_1;
		forksLables[2] = forkLable_2;
		forksLables[3] = forkLable_3;
		forksLables[4] = forkLable_4;

		comments[0] = lblNewLabel_0;
		comments[1] = lblNewLabel_1;
		comments[2] = lblNewLabel_2;
		comments[3] = lblNewLabel_3;
		comments[4] = lblNewLabel_4;

		meals[0] = meal_0;
		meals[1] = meal_1;
		meals[2] = meal_2;
		meals[3] = meal_3;
		meals[4] = meal_4;
	}
	
	//Khởi tạo luồng cho 5 triết gia
	Thread makePhilosopher(DiningPhilosophers d, int id, Fork left, Fork right, JLabel philsLable, JLabel comments,
			JLabel meals, JLabel forkLeft, JLabel forkRight) {
		return new Philosopher(d, id, left, right, philsLable, comments, meals, forkLeft, forkRight);
	}

	public void start() {
		// TODO Auto-generated method stub
//		super.start();

		for (int i = 0; i < this.NUMPHILS; ++i)
			fork[i] = new Fork(this, i);

		for (int i = 0; i < this.NUMPHILS; ++i) {
			phil[i] = makePhilosopher(this, i, fork[(i - 1 + this.NUMPHILS) % this.NUMPHILS], fork[i], PhilsLables[i],
					comments[i], meals[i], forksLables[i], forksLables[(i + 1 + this.NUMPHILS) % this.NUMPHILS]);
			phil[i].start();
		}
		start.setEnabled(false);
		continous.setEnabled(false);
	}

	public void stop() {
		// TODO Auto-generated method stub
//		super.stop();
		for (int i = 0; i < this.NUMPHILS; ++i) {
			phil[i].interrupt();
		}
	}

	synchronized void setPhil(int id, int s) throws InterruptedException {
		while (frozen)
			wait();
		state[id] = s;
	}

	synchronized void freeze() {
		frozen = true;
	}

	synchronized void thaw() {
		frozen = false;
		notifyAll();
		Message = "";
	}

	synchronized void setFork(int id, boolean taken) {
		untable[id] = !taken;
	}

	public boolean deadlocked() {
		int i = 0;
		while (i < NUMPHILS && state[i] == GOTRIGHT)
			++i;
		return i == NUMPHILS;
	}
}
