package excelone;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.formdev.flatlaf.intellijthemes.FlatCyanLightIJTheme;

import com.raven.datechooser.DateChooser;
import com.raven.datechooser.listener.DateChooserAction;
import com.raven.datechooser.listener.DateChooserAdapter;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class MakeDodonov {

	private JFrame frame;
	private JTextField fileTextField;
	private static DateChooser chDate = new DateChooser();
	private static DateChooser chDate2 = new DateChooser();
	private JTextField dateTextField;
	private JTextField dateTextFieldKegoc;
	private JTextField textField;
	private String[] nssList = new String[] { "Корниенко В.А.", "Малетин А.И.", "Симон Ф.И.", "Состравчук А.С." };

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
		frame = new JFrame("ЛЕНИВАЯ ЖОПА");
//		frame.setBounds(100, 100, 700, 300);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dimension = toolkit.getScreenSize();
		frame.setBounds(dimension.width / 2 - 350, dimension.height / 2 - 150, 700, 350);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);

		JSeparator separator = new JSeparator();
		separator.setBounds(10, 204, 564, 0);
		frame.getContentPane().add(separator);

		JTabbedPane mainTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		mainTabbedPane.setBounds(10, 11, 664, 223);
		frame.getContentPane().add(mainTabbedPane);
		chDate.setLabelCurrentDayVisible(true);
		AppData.setCurrentDate(chDate);
		chDate.addActionDateChooserListener(new DateChooserAdapter() {

			@Override
			public void dateChanged(Date date, DateChooserAction action) {
				super.dateChanged(date, action);
				AppData.setCurrentDate(chDate);
				fileTextField.setText(null);
			}

		});

		// === DIRECTOR ===
		JPanel createDirectorPanel = new JPanel();
		mainTabbedPane.addTab("Отчёт директору", null, createDirectorPanel, null);
		createDirectorPanel.setLayout(null);

		fileTextField = new JTextField();
		fileTextField.setEditable(false);
		fileTextField.setBounds(10, 56, 598, 33);
		createDirectorPanel.add(fileTextField);
		fileTextField.setColumns(10);
		fileTextField.setText(AppData.dailyStatsPath + AppData.dailyFileName);

		dateTextField = new JTextField();
		dateTextField.setHorizontalAlignment(SwingConstants.CENTER);
		dateTextField.setBounds(10, 11, 86, 34);
		createDirectorPanel.add(dateTextField);
		dateTextField.setColumns(10);
		chDate.setTextField(dateTextField);

		JButton selectPathButton = new JButton("...");
		selectPathButton.setBounds(618, 55, 31, 34);
		createDirectorPanel.add(selectPathButton);
		selectPathButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(AppData.dailyStatsPath));
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				fileChooser.showDialog(frame, "Выбрать папку");
				File f;
				try {
					f = fileChooser.getSelectedFile();
					fileTextField.setText(f.getAbsolutePath() + "\\" + AppData.dailyFileName);
					AppData.dailyStatsPath = f.getAbsolutePath() + "\\";
					System.out.println("dailyStatPath set to " + AppData.dailyStatsPath);
				} catch (NullPointerException e1) {
					System.out.println("Kegoc Folder not choosed");

				}
			}
		});

		JButton createDirectorButton = new JButton("Записать в файл");
		createDirectorButton.setBounds(524, 151, 125, 33);
		createDirectorPanel.add(createDirectorButton);

		JLabel lblNewLabel = new JLabel("Дата (UTC+1)");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(95, 11, 86, 34);
		createDirectorPanel.add(lblNewLabel);

		JComboBox startCombo = new JComboBox();
		startCombo.setModel(new DefaultComboBoxModel(nssList));
		startCombo.setBounds(10, 100, 126, 22);
		createDirectorPanel.add(startCombo);
		startCombo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AppData.startNssString = (String) startCombo.getSelectedItem();
				System.out.println("Start NSS is " + AppData.startNssString);
			}
		});

		JComboBox middleCombo = new JComboBox();
		middleCombo.setModel(new DefaultComboBoxModel(nssList));
		middleCombo.setBounds(10, 133, 126, 22);
		createDirectorPanel.add(middleCombo);
		middleCombo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AppData.middleNssString = (String) middleCombo.getSelectedItem();
				System.out.println("Middle NSS is " + AppData.middleNssString);
			}
		});

		JComboBox endCombo = new JComboBox();
		endCombo.setModel(new DefaultComboBoxModel(nssList));
		endCombo.setBounds(10, 166, 126, 22);
		createDirectorPanel.add(endCombo);
		endCombo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AppData.endNssString = (String) endCombo.getSelectedItem();
				System.out.println("End NSS is " + AppData.endNssString);
			}
		});

		JLabel startDisLabel = new JLabel("05:00 - 08:00 НСС");
		startDisLabel.setBounds(146, 104, 132, 14);
		createDirectorPanel.add(startDisLabel);

		JLabel middleDisLabel = new JLabel("08:00 - 20:00 НСС");
		middleDisLabel.setBounds(146, 137, 132, 14);
		createDirectorPanel.add(middleDisLabel);

		JLabel endDisLabel = new JLabel("20:00 - 05:00 НСС");
		endDisLabel.setBounds(146, 170, 132, 14);
		createDirectorPanel.add(endDisLabel);

		// === KEGOC ===
		JPanel createKegocPanel = new JPanel();
		mainTabbedPane.addTab("БРЭ для KEGOC", null, createKegocPanel, null);
		createKegocPanel.setLayout(null);

		textField = new JTextField();
		textField.setText(AppData.kegocPath + "БРЭ для KEGOC Bassel " + AppData.day
				+ AppData.getMonthStringNameWithSuffix() + ".xlsx");
		textField.setEditable(false);
		textField.setColumns(10);
		textField.setBounds(10, 56, 598, 33);
		createKegocPanel.add(textField);
		dateTextFieldKegoc = new JTextField();
		dateTextFieldKegoc.setHorizontalAlignment(SwingConstants.CENTER);
		dateTextFieldKegoc.setBounds(10, 11, 86, 34);
		createKegocPanel.add(dateTextFieldKegoc);
		dateTextFieldKegoc.setColumns(10);
		chDate2.setTextField(dateTextFieldKegoc);
		chDate2.setLabelCurrentDayVisible(true);
		AppData.setCurrentDate(chDate2);
		chDate2.addActionDateChooserListener(new DateChooserAdapter() {

			@Override
			public void dateChanged(Date date, DateChooserAction action) {
				super.dateChanged(date, action);
				AppData.setCurrentDate(chDate2);
					textField.setText(AppData.kegocPath + "БРЭ для KEGOC Bassel " + AppData.day
				+ AppData.getMonthStringNameWithSuffix() + ".xlsx");
			}

		});


		JButton selectPathButton_1 = new JButton("...");
		selectPathButton_1.setBounds(618, 55, 31, 34);
		createKegocPanel.add(selectPathButton_1);
		selectPathButton_1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(AppData.kegocPath));
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				fileChooser.showDialog(createKegocPanel, "Выбрать папку");
				File f;
				try {
					f = fileChooser.getSelectedFile();
					fileTextField.setText(f.getAbsolutePath() + "\\" + "БРЭ для KEGOC Bassel " + AppData.day
							+ AppData.getMonthStringNameWithSuffix() + ".xlsx");
					AppData.kegocPath = f.getAbsolutePath() + "\\";
					System.out.println("kegocPath set to " + AppData.kegocPath);
				} catch (NullPointerException e1) {
					System.out.println("Kegoc Folder not choosed");

				}
			}
		});

		JButton createDirectorButton_1 = new JButton("Создать файл");
		createDirectorButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int input = JOptionPane.showConfirmDialog(frame,
						("Создать файл \"" + "БРЭ для KEGOC Bassel " + AppData.day
								+ AppData.getMonthStringNameWithSuffix() + ".xlsx" + "\"?"),
						"Подтверждение операции", JOptionPane.YES_NO_OPTION);
				if (input == 0) {
					try {
						service.copyDailyRowsForKegoc();
						JOptionPane.showMessageDialog(createKegocPanel,
								"Файл \"" + "БРЭ для KEGOC Bassel " + AppData.day
										+ AppData.getMonthStringNameWithSuffix() + ".xlsx" + "\" создан",
								"ИНФОРМАЦИЯ", JOptionPane.INFORMATION_MESSAGE);
					} catch (FileNotFoundException ex) {
						JOptionPane.showMessageDialog(createKegocPanel,
								"Файл \"" + "БРЭ для KEGOC Bassel " + AppData.day
										+ AppData.getMonthStringNameWithSuffix() + ".xlsx"
										+ "\" недоступен либо открыт",
								"ОШИБКА", JOptionPane.ERROR_MESSAGE);
						ex.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		createDirectorButton_1.setBounds(524, 151, 125, 33);
		createKegocPanel.add(createDirectorButton_1);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 247, 664, 53);
		frame.getContentPane().add(separator_1);
		createDirectorButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int input = JOptionPane.showConfirmDialog(frame,
						("Записать данные в \"" + AppData.dailyFileName + "\"\n от " + AppData.day + " " + AppData.getMonthStringNameWithSuffix() + "?\nПроверь дату и фамилии НСС!"), "Подтверждение операции",
						JOptionPane.YES_NO_OPTION);
				System.out.println(input);
				if (input == 0) {
					try {
						double[][] result = service.getColumnsArrayFromBre(new int[] { 3, 2, 10, 9 });
						service.writeValuesToDirector(result, new int[] { 3, 4, 5, 6 });
						JOptionPane.showMessageDialog(createDirectorPanel, "Данные записаны", "ИНФОРМАЦИЯ",
								JOptionPane.INFORMATION_MESSAGE);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(createDirectorPanel,
								"Файл \"" + AppData.dailyFileName + "\" недоступен либо открыт", "ОШИБКА",
								JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}

				}
			}
		});
		

	}
}
