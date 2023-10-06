package dodonov;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.formdev.flatlaf.intellijthemes.FlatCyanLightIJTheme;

import com.raven.datechooser.DateChooser;
import com.raven.datechooser.listener.DateChooserAction;
import com.raven.datechooser.listener.DateChooserAdapter;


import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;

public class MakeDodonov {

	private JFrame frame;
	private JTextField fileTextField;
	private static DateChooser chDate = new DateChooser();
	private JTextField dataTextField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MakeDodonov window = new MakeDodonov();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MakeDodonov() {
		FlatCyanLightIJTheme.setup();

		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		XLSXService service = new XLSXService();
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 204, 564, 0);
		frame.getContentPane().add(separator);
		
		JTabbedPane mainTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		mainTabbedPane.setBounds(10, 11, 564, 182);
		frame.getContentPane().add(mainTabbedPane);
		
		
		// === DIRECTOR ===
		DirectorAction dirAct = new DirectorAction();
		JPanel createDirectorPanel = new JPanel();
		mainTabbedPane.addTab("Отчёт директору", null, createDirectorPanel, null);
		createDirectorPanel.setLayout(null);
		
		fileTextField = new JTextField();
		fileTextField.setEditable(false);
		fileTextField.setBounds(10, 56, 498, 33);
		createDirectorPanel.add(fileTextField);
		fileTextField.setColumns(10);
		fileTextField.setText(dirAct.dailyStatsPath + dirAct.dailyFileName);
		
		dataTextField = new JTextField();
		dataTextField.setHorizontalAlignment(SwingConstants.CENTER);
		dataTextField.setBounds(10, 11, 86, 34);
		createDirectorPanel.add(dataTextField);
		dataTextField.setColumns(10);
		chDate.setTextField(dataTextField);
		chDate.setLabelCurrentDayVisible(true);
		AppData.setCurrentDate(chDate);
		chDate.addActionDateChooserListener(new DateChooserAdapter() {

			@Override
			public void dateChanged(Date date, DateChooserAction action) {
				super.dateChanged(date, action);
				AppData.setCurrentDate(chDate);
			}
			
		});
		
		JButton selectPathButton = new JButton("...");
		selectPathButton.setBounds(518, 55, 31, 34);
		createDirectorPanel.add(selectPathButton);
		selectPathButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(dirAct.dailyStatsPath));
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				fileChooser.showDialog(frame, "Выбрать папку");
				File f;
				try {
					f = fileChooser.getSelectedFile();
					fileTextField.setText(f.getAbsolutePath() + "\\" + dirAct.dailyFileName);
					dirAct.dailyStatsPath = f.getAbsolutePath() + "\\";
					System.out.println("dailyStatPath set to " + dirAct.dailyStatsPath);
				} catch (NullPointerException e1) {
						System.out.println("Kegoc Folder not choosed");

				}
			}
		});
		
		
		JButton createDirectorButton = new JButton("Записать в файл");
		createDirectorButton.setBounds(424, 110, 125, 33);
		createDirectorPanel.add(createDirectorButton);
		createDirectorButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int input = JOptionPane.showConfirmDialog(frame,
				("Создать файл \"" + dirAct.dailyFileName + "\"?"),
				"Подтверждение операции", JOptionPane.YES_NO_OPTION);
				System.out.println(input);
				if (input == 0) {
//					try {
//					service.copyRowsToArray(new File(AppData.pathBreMonthly), new File(dirAct.dailyStatsPath + dirAct.dailyFileName), 
//								dirAct.ROWS_OFFSET, dirAct.CELLS_OFFSET, 24, new int[] {8, 7});
//					} catch (FileNotFoundException ex) {
//						JOptionPane.showMessageDialog(createDirectorPanel, "Файл \"" + dirAct.dailyFileName + "\" недоступен", "ОШИБКА", JOptionPane.ERROR_MESSAGE);
//					} catch (IOException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
					service.openXLSX();

				}
			}
		});
		
		
		// === KEGOC ===
		JPanel createKegocPanel = new JPanel();
		mainTabbedPane.addTab("Брэ для KEGOC", null, createKegocPanel, null);
		createKegocPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Under Construction...");
		lblNewLabel.setBounds(10, 11, 177, 14);
		createKegocPanel.add(lblNewLabel);
		
	}
}
