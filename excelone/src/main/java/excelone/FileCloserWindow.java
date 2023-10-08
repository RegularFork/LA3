package excelone;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme;

public class FileCloserWindow {

	JFrame secondFrame;
	private JTextField fileChooseField;
	private String path = null;
	private JButton chooseButton;
	private JLabel lblNewLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FileCloserWindow window = new FileCloserWindow();
					window.secondFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FileCloserWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
//		FlatArcOrangeIJTheme.setup();
		secondFrame = new JFrame("ЗАКРЫВАШКА");
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dimension = toolkit.getScreenSize();
		secondFrame.setBounds(dimension.width / 2 - 250, dimension.height / 2 - 75, 500, 150);
		secondFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		secondFrame.setResizable(false);
		secondFrame.getContentPane().setLayout(null);
		
		fileChooseField = new JTextField();
		fileChooseField.setBounds(10, 42, 370, 29);
		secondFrame.getContentPane().add(fileChooseField);
		fileChooseField.setColumns(10);
		fileChooseField.setText(path);
		fileChooseField.setEditable(false);
		
		JButton closeButton = new JButton("Закрыть файл принудительно");
		closeButton.setBounds(135, 77, 219, 23);
		secondFrame.getContentPane().add(closeButton);
		closeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (path != null && path != "") {
					try {
						FileInputStream fis = new FileInputStream(path);
						fis.close();
						JOptionPane.showMessageDialog(secondFrame, "Поток файла закрыт", "Файл закрыт", JOptionPane.PLAIN_MESSAGE);
						secondFrame.dispose();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else System.out.println("File field is empty!");
			}
		});
		
		chooseButton = new JButton("Выбрать");
		chooseButton.setBounds(385, 41, 89, 30);
		secondFrame.getContentPane().add(chooseButton);
		
		lblNewLabel = new JLabel("Закрыватель заглохших файлов");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(10, 11, 464, 20);
		secondFrame.getContentPane().add(lblNewLabel);
		chooseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.showDialog(secondFrame, "Выбрать файл для закрытия");
				File f;
				f = fileChooser.getSelectedFile();
				if (f == null) System.out.println("File not choosed");
				else {
					fileChooseField.setText(f.getAbsolutePath());
					path = f.getAbsolutePath();
				}
			}
		});
	}

}
