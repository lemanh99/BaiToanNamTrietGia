package Manh;

import javax.swing.JLabel;

public class Fork {
	private int identity; //Đánh số thức tự của các nĩa
	private boolean taken = false; // trạng thái của nĩa
	private DiningPhilosophers display; //Cập nhật trạng thái

	public Fork(DiningPhilosophers disp, int id) {
		// TODO Auto-generated constructor stub
		display = disp;
		identity = id;
	}

	// Phương thức put - Ngừng sử dụng đũa, trả lại chiếc đĩa không sử dụng taken=false
	synchronized void put() {
		taken = false;
		display.setFork(identity, taken);
		notify();
	}

	// Phương thức get- Đánh dấu là sử dụng đĩa
	synchronized void get() throws InterruptedException {
		while (taken)
			wait();
		taken = true;
		display.setFork(identity, taken);
	}
}
