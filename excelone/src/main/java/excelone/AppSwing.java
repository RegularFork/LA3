package excelone;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme;

public class AppSwing {
	
	

	public static void main(String[] args) {
		JFrame frame = getFrame();
		JPanel panel = new JPanel();
		frame.add(panel);
		
		JButton button = new JButton("BUTTON");
		panel.add(button);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar = new JProgressBar();
		progressBar.setValue(5);
		progressBar.setStringPainted(false);
		progressBar.setBounds(7, 572, 567, 14);
		frame.getContentPane().add(progressBar);
		double d = 0;
		for (int i = 1; i < 1000000000; i++) {
			d += Math.sqrt(i);
			
			if (i % 10000000 == 0) {
				int val = (int)Math.round(i/10000000);
				System.out.println(val);
				progressBar.setValue(val);
			}
		}
		System.out.println(d);
		
	}
	
	
	static JFrame getFrame() {
		FlatGitHubDarkIJTheme.setup();
		JFrame frame = new JFrame("Excelone 2");
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dimension = toolkit.getScreenSize();
		frame.setBounds(dimension.width / 2 - 250, dimension.height / 2 - 150, 500, 300);
		
		return frame;
	}
	
	
	static void viewFonts() {
		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for (String string : fonts) {
			System.out.println(string);
		}
	}
	

}
