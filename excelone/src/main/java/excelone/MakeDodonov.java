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

import exceptions.checkAskueFileException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class MakeDodonov {

	private JFrame frame;
	private JTextField fileTextField;
	private static DateChooser chDate = new DateChooser();
	private JTextField textField;
	private JTextField dateField;
	private JTextField askuePathTextField;
	private JTextField dailyBrePathTextField;
	private JComboBox currentHourCombo;

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
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dimension = toolkit.getScreenSize();
		frame.setBounds(dimension.width / 2 - 350, dimension.height / 2 - 150, 700, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);

		JSeparator separator = new JSeparator();
		separator.setBounds(10, 243, 564, 0);
		frame.getContentPane().add(separator);

		JTabbedPane mainTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		mainTabbedPane.setBounds(10, 50, 664, 225);
		frame.getContentPane().add(mainTabbedPane);

		JLabel dateLabel = new JLabel("Дата (UTC+1)");
		dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
		dateLabel.setBounds(107, 11, 86, 34);
		frame.getContentPane().add(dateLabel);
		
		// === Date set ===
		dateField = new JTextField();
		dateField.setHorizontalAlignment(SwingConstants.CENTER);
		dateField.setColumns(10);
		dateField.setBounds(22, 11, 86, 34);
		frame.getContentPane().add(dateField);
		chDate.setTextField(dateField);
		chDate.setLabelCurrentDayVisible(true);
		AppData.setCurrentDate(chDate);
		chDate.addActionDateChooserListener(new DateChooserAdapter() {
			
			@Override
			public void dateChanged(Date date, DateChooserAction action) {
				super.dateChanged(date, action);
				AppData.setCurrentDate(chDate);
				fileTextField.setText(AppData.dailyStatsPath + AppData.dailyFileName);
				textField.setText(AppData.kegocPath + "БРЭ для KEGOC Bassel " + AppData.day
						+ AppData.getMonthStringNameWithSuffix() + ".xlsx");
			}
			
		});
		
		
		// === Daily BRE ================================================================================================
		
		DailyBreService dailyBreService = new DailyBreService();
		
		JPanel dailyBrePanel = new JPanel();
		mainTabbedPane.addTab("БРЭ ежедневный", null, dailyBrePanel, null);
		dailyBrePanel.setLayout(null);
		AppData.printTimeLimits();
		
		askuePathTextField = new JTextField();
		askuePathTextField.setText(AppData.askuePath + AppData.askueFileName);
		askuePathTextField.setEditable(false);
		askuePathTextField.setColumns(10);
		askuePathTextField.setBounds(10, 12, 598, 33);
		dailyBrePanel.add(askuePathTextField);
		
		JButton selectAskuePathButton = new JButton("...");
		selectAskuePathButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(AppData.askuePath));
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				fileChooser.setFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						return "Excel (.xlsx)";
					}

					@Override
					public boolean accept(File f) {
						if (f.getName().endsWith(AppData.FILE_EXTENSION) || f.isDirectory())
							return true;
						return false;
					}
				});

				fileChooser.showDialog(frame, "Выбрать файл");
				File f;
				try {
					f = fileChooser.getSelectedFile();
					askuePathTextField.setText(f.getAbsolutePath());
					AppData.askueFileName = f.getName();
					AppData.askuePath = f.getParent() + "\\";
					System.out.println("AskuePath set to " + AppData.askuePath);
					System.out.println("AskueFileName set to " + AppData.askueFileName);
				} catch (NullPointerException e1) {
					System.out.println("Kegoc Folder not choosed");

				}
			}
		});
		selectAskuePathButton.setBounds(618, 11, 31, 34);
		dailyBrePanel.add(selectAskuePathButton);
		
		JButton copyDailyBreButton = new JButton("Записать в файл");
		copyDailyBreButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (!dailyBreService.compareDateAskue()) {
						JOptionPane.showMessageDialog(dailyBrePanel, ("Дата в \"" + AppData.askueFileName + "\"\n"
								+ "не соответствует выбранной!"), "ОШИБКА", JOptionPane.ERROR_MESSAGE);
					} else {
						int input = JOptionPane.showConfirmDialog(frame,
								("Записать данные в \"" + AppData.dailyBreFileName + "\"\n от " + AppData.day + " " + AppData.getMonthStringNameWithSuffix() + "?"), "Подтверждение операции",
								JOptionPane.YES_NO_OPTION);
						System.out.println(input);
						if (input == 0) {
							try {
								dailyBreService.copyAskueToBreData();
							} catch (checkAskueFileException e1) {
								JOptionPane.showMessageDialog(dailyBrePanel, ("Файл \"" + AppData.askueFileName + "\"\n"
										+ "не соответствует стандартому!"), "ОШИБКА", JOptionPane.ERROR_MESSAGE);
								e1.printStackTrace();
								return;
							}
							JOptionPane.showMessageDialog(dailyBrePanel, ("Данные скопированы"), "ВЫПОЛНЕНО", JOptionPane.PLAIN_MESSAGE);
						}
					}
				} catch (IllegalArgumentException e1) {
					e1.printStackTrace();
				} catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(dailyBrePanel, ("Файл \"" + AppData.dailyBreFileName + "\"\n"
							+ "недоступен либо открыт!"), "ОШИБКА", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
		});
		copyDailyBreButton.setBounds(524, 151, 125, 33);
		dailyBrePanel.add(copyDailyBreButton);
		
		
		dailyBrePathTextField = new JTextField();
		dailyBrePathTextField.setText(AppData.dailyBrePath + AppData.dailyBreFileName);
		dailyBrePathTextField.setEditable(false);
		dailyBrePathTextField.setColumns(10);
		dailyBrePathTextField.setBounds(10, 57, 598, 33);
		dailyBrePanel.add(dailyBrePathTextField);
		
		JButton selectDailyBrePathButton = new JButton("...");
		selectDailyBrePathButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(AppData.dailyBrePath));
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				fileChooser.setFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						return "*Ежедневный* Excel (.xlsx)";
					}

					@Override
					public boolean accept(File f) {
						if ((f.getName().endsWith(AppData.FILE_EXTENSION) && f.getName().toUpperCase().contains(("Ежедневный").toUpperCase())) || f.isDirectory())
							return true;
						return false;
					}
				});

				fileChooser.showDialog(frame, "Выбрать файл");
				File f;
				try {
					f = fileChooser.getSelectedFile();
					dailyBrePathTextField.setText(f.getAbsolutePath());
					AppData.dailyBreFileName = f.getName();
					AppData.dailyBrePath = f.getParent() + "\\";
					System.out.println("AskuePath set to " + AppData.dailyBrePath);
					System.out.println("AskueFileName set to " + AppData.dailyBreFileName);
				} catch (NullPointerException e1) {
					System.out.println("Kegoc Folder not choosed");

				}
			}
		});
		selectDailyBrePathButton.setBounds(618, 56, 31, 34);
		dailyBrePanel.add(selectDailyBrePathButton);
		
		JComboBox firstHourCombo = new JComboBox();
		firstHourCombo.setModel(new DefaultComboBoxModel(AppData.hoursArray));
		firstHourCombo.setBounds(10, 124, 55, 22);
		dailyBrePanel.add(firstHourCombo);
		firstHourCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int comboValue = Integer.parseInt((String)firstHourCombo.getSelectedItem());
				AppData.firstHour = comboValue;
				if(comboValue > AppData.currentHour) {
					AppData.currentHour = comboValue;
					currentHourCombo.setSelectedItem(String.valueOf(comboValue));;
				}
				System.out.println("First hour set to " + AppData.firstHour);
				AppData.printTimeLimits();
			}
		});
		
		currentHourCombo = new JComboBox();
		currentHourCombo.setModel(new DefaultComboBoxModel(AppData.hoursArray));
		currentHourCombo.setBounds(79, 124, 55, 22);
		dailyBrePanel.add(currentHourCombo);
		currentHourCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int comboValue = Integer.parseInt((String)currentHourCombo.getSelectedItem());
				AppData.currentHour = Integer.parseInt((String)currentHourCombo.getSelectedItem());
				if(comboValue < AppData.firstHour) {
					AppData.firstHour = comboValue;
					firstHourCombo.setSelectedItem(String.valueOf(comboValue));;
				}
				System.out.println("Current hour set to " + AppData.currentHour);
				AppData.printTimeLimits();
			}
		});
		
		JLabel timeLabel = new JLabel("Время снятия показаний");
		timeLabel.setBounds(10, 101, 137, 14);
		dailyBrePanel.add(timeLabel);
		
		
		
		// === DIRECTOR ================================================================================================
		JPanel createDirectorPanel = new JPanel();
		mainTabbedPane.addTab("Отчёт директору", null, createDirectorPanel, null);
		createDirectorPanel.setLayout(null);

		fileTextField = new JTextField();
		fileTextField.setEditable(false);
		fileTextField.setBounds(10, 12, 598, 33);
		createDirectorPanel.add(fileTextField);
		fileTextField.setColumns(10);
		fileTextField.setText(AppData.dailyStatsPath + AppData.dailyFileName);
		

		JButton selectPathButton = new JButton("...");
		selectPathButton.setBounds(618, 11, 31, 34);
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

		JComboBox startCombo = new JComboBox();
		startCombo.setModel(new DefaultComboBoxModel(AppData.nssList));
		startCombo.setBounds(10, 56, 126, 22);
		createDirectorPanel.add(startCombo);
		startCombo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AppData.startNssString = (String) startCombo.getSelectedItem();
				System.out.println("Start NSS is " + AppData.startNssString);
			}
		});

		JComboBox middleCombo = new JComboBox();
		middleCombo.setModel(new DefaultComboBoxModel(AppData.nssList));
		middleCombo.setBounds(10, 89, 126, 22);
		createDirectorPanel.add(middleCombo);
		middleCombo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AppData.middleNssString = (String) middleCombo.getSelectedItem();
				System.out.println("Middle NSS is " + AppData.middleNssString);
			}
		});

		JComboBox endCombo = new JComboBox();
		endCombo.setModel(new DefaultComboBoxModel(AppData.nssList));
		endCombo.setBounds(10, 122, 126, 22);
		createDirectorPanel.add(endCombo);
		endCombo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AppData.endNssString = (String) endCombo.getSelectedItem();
				System.out.println("End NSS is " + AppData.endNssString);
			}
		});

		JLabel startDisLabel = new JLabel("05:00 - 08:00 НСС");
		startDisLabel.setBounds(146, 60, 132, 14);
		createDirectorPanel.add(startDisLabel);

		JLabel middleDisLabel = new JLabel("08:00 - 20:00 НСС");
		middleDisLabel.setBounds(146, 93, 132, 14);
		createDirectorPanel.add(middleDisLabel);

		JLabel endDisLabel = new JLabel("20:00 - 05:00 НСС");
		endDisLabel.setBounds(146, 126, 132, 14);
		createDirectorPanel.add(endDisLabel);
		
		
		

		// === KEGOC ==================================================================================================
		JPanel createKegocPanel = new JPanel();
		mainTabbedPane.addTab("БРЭ для KEGOC", null, createKegocPanel, null);
		createKegocPanel.setLayout(null);

		textField = new JTextField();
		textField.setText(AppData.kegocPath + "БРЭ для KEGOC Bassel " + AppData.day
				+ AppData.getMonthStringNameWithSuffix() + ".xlsx");
		textField.setEditable(false);
		textField.setColumns(10);
		textField.setBounds(10, 12, 598, 33);
		createKegocPanel.add(textField);


		JButton selectPathButton_1 = new JButton("...");
		selectPathButton_1.setBounds(618, 11, 31, 34);
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
		separator_1.setBounds(10, 286, 664, 2);
		frame.getContentPane().add(separator_1);
		
		

	}
}
