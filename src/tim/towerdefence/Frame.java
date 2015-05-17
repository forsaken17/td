package tim.towerdefence;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Frame extends JFrame {
	Screen screen;

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new Frame();

			}
		});
	}

	public Frame() {
		// JFrame jframe = new JFrame();

		// this.setSize(800, 600);
		this.setTitle("Tower Defence");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		this.setExtendedState(MAXIMIZED_BOTH);
		this.setUndecorated(true);
		this.setResizable(false);
		this.setVisible(true);
		this.setLocationRelativeTo(null);

		this.screen = new Screen(this);
		this.add(this.screen);

	}

}
