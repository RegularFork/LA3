package excelone.threads;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class ProgressThread implements Runnable {
	int min;
	int max;
	int block = 10;
	JFrame frame;
	JProgressBar progressbar;
	
	public ProgressThread(JProgressBar progressbar) {
		this.progressbar = progressbar;
	}

	@Override
	public void run() {
		progressbar.setVisible(true);
		for(int i = 0; i < progressbar.getMaximum(); i += block) {
			progressbar.setValue(i);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		progressbar.setVisible(false);
		progressbar.setValue(0);
	}

}
