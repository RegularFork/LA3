package excelone;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;
import com.raven.datechooser.DateChooser;
import com.raven.datechooser.listener.DateChooserAction;
import com.raven.datechooser.listener.DateChooserAdapter;
import java.awt.Color;
import javax.swing.JProgressBar;

public class Window {

	private JFrame frame;
	JProgressBar progressBar;
	private JTextField textSourceField;
	private JTextField textTargetField;
	private JButton getTargetFileButton;
	private JTextField textBreDataField;
	private JTextField textDataKegocField;
	private JTextField textKegocField;
	private static DateChooser chDate = new DateChooser();
	private static DateChooser chDate2 = new DateChooser();
	private final static String FILE_EXTENSION = ".xlsx";
	private JTextField analyzeField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window window = new Window();
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
	public Window() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		FlatDraculaIJTheme.setup();
		final ExcelService service = new ExcelService();
		service.setCurrentDate();

		class ProgressThread extends Thread {
			@Override
			public void run() {
				ExcelService.progressValue = 0;
				ExcelService.progressCount = 100 / (service.currentHour - service.firstHour + 1);
				while (true) {
					progressBar.setValue(ExcelService.progressValue);
					progressBar.setStringPainted(true);
					if (isInterrupted()) {
						progressBar.setValue(0);
						progressBar.setStringPainted(false);
						ExcelService.progressValue = 0;
						ExcelService.progressCount = 0;
						return;
					}
				}
			}

		}

		frame = new JFrame("ЛЕНИВАЯ ЖОПА v2.0");
		frame.getContentPane().setFont(new Font("Arial Narrow", Font.PLAIN, 11));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dimension = toolkit.getScreenSize();
		frame.setBounds(dimension.width / 2 - 300, dimension.height / 2 - 325, 600, 650);
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);

		textSourceField = new JTextField();
//		textSourceField.setBackground(SystemColor.inactiveCaptionBorder);
		textSourceField.setText(service.fileToReadPath);
		textSourceField.setEditable(false);
		textSourceField.setBounds(7, 113, 567, 20);
		frame.getContentPane().add(textSourceField);
		textSourceField.setColumns(10);
		frame.setVisible(true);
		frame.revalidate();

		if (!service.checkTrial()) {
			JOptionPane.showMessageDialog(frame, "Тестовый период окончен", "ВНИМАНИЕ!", JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}

		progressBar = new JProgressBar();
		progressBar.setValue(0);
		progressBar.setStringPainted(false);
		progressBar.setBounds(7, 572, 567, 14);
		frame.getContentPane().add(progressBar);

		JLabel copyMessageLabel = new JLabel("Идёт копирование данных");
		copyMessageLabel.setForeground(new Color(255, 255, 128));
		copyMessageLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
		copyMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		copyMessageLabel.setBounds(309, 38, 251, 20);
		copyMessageLabel.setVisible(false);
		frame.getContentPane().add(copyMessageLabel);

		JButton getSourceFileButton = new JButton("Выбрать источник данных");
		getSourceFileButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		getSourceFileButton.setToolTipText("Выбрать файл \"РасходПоОбъектам\" для извлечения данных");
//		getSourceFileButton.setBackground(SystemColor.text);
		getSourceFileButton.setBounds(7, 134, 205, 23);
		getSourceFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(service.fileToReadPath));
				// fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

				fileChooser.setFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						return "Excel (.xlsx)";
					}

					@Override
					public boolean accept(File f) {
						if (f.getName().endsWith(FILE_EXTENSION) || f.isDirectory())
							return true;
						return false;
					}
				});

				fileChooser.showDialog(frame, "Выбрать файл");
				File f;
				try {
					f = fileChooser.getSelectedFile();
					textSourceField.setText(f.getAbsolutePath());
					service.fileToReadPath = f.getAbsolutePath();
					System.out.println(service.fileToReadPath);
				} catch (NullPointerException e1) {

					if (!textSourceField.getText().endsWith(FILE_EXTENSION)) {
						textSourceField.setText("Файл не выбран");
						System.out.println("Source File not choosed");
					}

				}
			}
		});
		frame.getContentPane().add(getSourceFileButton);

		textTargetField = new JTextField();
//		textTargetField.setBackground(SystemColor.inactiveCaptionBorder);
		textTargetField.setText(service.fileToWritePath);
		textTargetField.setEditable(false);
		textTargetField.setColumns(10);
		textTargetField.setBounds(7, 168, 567, 20);
		frame.getContentPane().add(textTargetField);

		getTargetFileButton = new JButton("Выбрать файл для записи");
		getTargetFileButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		getTargetFileButton.setToolTipText("Выбрать файл \"Ежедневный БРЭ\" для записи данных");
//		getTargetFileButton.setBackground(SystemColor.text);
		getTargetFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(service.fileToWritePath));
				// fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

				fileChooser.setFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						return "Excel (.xlsx)";
					}

					@Override
					public boolean accept(File f) {
						if (f.getName().endsWith(FILE_EXTENSION) || f.isDirectory())
							return true;
						return false;
					}
				});

				fileChooser.showDialog(frame, "Выбрать файл");
				File f;
				try {
					f = fileChooser.getSelectedFile();
					textTargetField.setText(f.getAbsolutePath());
					service.fileToWritePath = f.getAbsolutePath();
					System.out.println(service.fileToWritePath);
				} catch (NullPointerException e1) {

					if (!textTargetField.getText().endsWith(FILE_EXTENSION)) {
						textTargetField.setText("Файл не выбран");
						System.out.println("Target File not choosed");
					}

				}

			}

		});
		getTargetFileButton.setBounds(7, 189, 205, 23);
		frame.getContentPane().add(getTargetFileButton);

//		final JProgressBar progressBar = new JProgressBar();
//		progressBar.setEnabled(false);
//		progressBar.setStringPainted(true);
//		progressBar.setIndeterminate(false);
//		progressBar.setBounds(7, 436, 567, 14);
//		progressBar.setValue(0);
//		frame.getContentPane().add(progressBar);

		JSeparator separator = new JSeparator();
		separator.setBounds(7, 343, 567, 2);
		frame.getContentPane().add(separator);

		final JComboBox hourFromCombo = new JComboBox();
		final JComboBox hourUntilCombo = new JComboBox();
//		hourFromCombo.setBackground(SystemColor.inactiveCaptionBorder);
		hourFromCombo.setToolTipText("Включительно");
		hourFromCombo.setModel(new DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9",
				"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24" }));
		hourFromCombo.setBounds(10, 39, 51, 22);
		System.out.println("firstHour " + service.firstHour);
		hourFromCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int compareFirst = Integer.parseInt((String) hourFromCombo.getSelectedItem());
				if (compareFirst < service.currentHour) {
					service.firstHour = compareFirst;
					System.out.println("firstHour set to " + service.firstHour);
				} else {
					service.firstHour = compareFirst;
					service.currentHour = compareFirst;
					System.out.println("firstHour set to " + service.firstHour);
					System.out.println("currenttHour up to " + service.currentHour);
					hourFromCombo.setSelectedItem(String.valueOf(service.firstHour));
					hourUntilCombo.setSelectedItem(String.valueOf(service.currentHour));

				}
			}
		});
		frame.getContentPane().add(hourFromCombo);

//		final JComboBox hourUntilCombo = new JComboBox();
//		hourUntilCombo.setBackground(SystemColor.inactiveCaptionBorder);
		hourUntilCombo.setToolTipText("Включительно");
		hourUntilCombo.setModel(new DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9",
				"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24" }));
		hourUntilCombo.setBounds(81, 39, 51, 22);
		System.out.println("CurrentHour " + service.currentHour);
		hourUntilCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int compareLast = Integer.parseInt((String) hourUntilCombo.getSelectedItem());
				if (compareLast > service.firstHour) {
					service.currentHour = compareLast;
					System.out.println("currentHour set to " + service.currentHour);
				} else {
					service.currentHour = compareLast;
					service.firstHour = compareLast;
					System.out.println("currentHour set to " + service.currentHour);
					System.out.println("firstHour down to " + service.firstHour);
					hourUntilCombo.setSelectedItem(String.valueOf(service.currentHour));
					hourFromCombo.setSelectedItem(String.valueOf(service.firstHour));
				}
			}
		});
		frame.getContentPane().add(hourUntilCombo);

		JLabel hourFromLabel = new JLabel("От");
		hourFromLabel.setHorizontalAlignment(SwingConstants.CENTER);
		hourFromLabel.setBounds(10, 22, 46, 14);
		frame.getContentPane().add(hourFromLabel);

		JLabel hourUntilLabel = new JLabel("До");
		hourUntilLabel.setHorizontalAlignment(SwingConstants.CENTER);
		hourUntilLabel.setBounds(81, 22, 46, 14);
		frame.getContentPane().add(hourUntilLabel);

		textBreDataField = new JTextField();
//		textBreDataField.setBackground(SystemColor.inactiveCaptionBorder);
		textBreDataField.setEditable(false);
		textBreDataField.setToolTipText("Дата снятия показаний");
		textBreDataField.setBounds(142, 39, 86, 22);
		chDate.setTextField(textBreDataField);
		service.currentDay = chDate.getSelectedDate().getDate();
		service.currentMonth = chDate.getSelectedDate().getMonth() + 1;
		service.currentYear = chDate.getSelectedDate().getYear() + 1900;
		System.out.println(service.currentDay + " / " + service.currentMonth + " / " + service.currentYear);
		chDate.setLabelCurrentDayVisible(false);
		chDate.addActionDateChooserListener(new DateChooserAdapter() {

			@Override
			public void dateChanged(Date date, DateChooserAction action) {
				super.dateChanged(date, action);
				service.currentDay = date.getDate();
				service.currentMonth = date.getMonth() + 1;
				service.currentYear = date.getYear() + 1900;
				textKegocField.setText(service.fileToWriteDailyPath + "БРЭ для KEGOC Bassel " + service.currentDay
						+ service.getMonthStringNameWithSuffix() + ".xlsx");
				System.out.println(service.currentDay + " / " + service.currentMonth + " / " + service.currentYear);
			}

		});
		frame.getContentPane().add(textBreDataField);
		textBreDataField.setColumns(10);

		JLabel dataKEGOCLabel = new JLabel("Дата");
//		dataKEGOCLabel.setBackground(UIManager.getColor("Button.light"));
		dataKEGOCLabel.setHorizontalAlignment(SwingConstants.CENTER);
		dataKEGOCLabel.setBounds(159, 22, 46, 14);
		frame.getContentPane().add(dataKEGOCLabel);

		JButton startCopyBreButton = new JButton("Копировать данные");
		startCopyBreButton.setFont(new Font("Tahoma", Font.BOLD, 11));
//		startCopyBreButton.setBackground(SystemColor.inactiveCaption);
		startCopyBreButton.setToolTipText("Запустить копирование данных");
		startCopyBreButton.setBounds(369, 189, 205, 23);
		startCopyBreButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
//					progressLabel.setText("Копирование данных...");
					if (!service.compareDateAskue())
						JOptionPane.showMessageDialog(frame,
								("Различные даты в файлах\n\"" + new File(service.fileToReadPath).getName()
										+ "\"\nи\n\"" + new File(service.fileToWritePath).getName() + "\""),
								"Ошибка", JOptionPane.ERROR_MESSAGE);
					else {
						copyMessageLabel.setVisible(true);
						int input = JOptionPane.showConfirmDialog(frame,
								("Скопировать данные в\n\"" + new File(service.fileToWritePath).getName()) + "\"?",
								"Подтвердить действие", JOptionPane.YES_NO_OPTION);
						if (input == 0) {
							service.saveFile(new File(service.fileToWritePath));
							System.out.println("Copy warn");
//							ProgressThread progressThread = new ProgressThread();
//							progressThread.start();
							try {
								service.copyAllRowsPerDay();
//								progressLabel.setText("Выполнено!");
								JOptionPane.showMessageDialog(frame,
										("Скопированы данные за " + (service.currentHour - (service.firstHour - 1))
												+ service.getCorrechHourString()),
										"Копирование данных завершено", JOptionPane.INFORMATION_MESSAGE);
								System.out.println("Copy Complete!");
								System.out.println(input);
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								JOptionPane
										.showMessageDialog(frame,
												"Файл \"" + new File(service.fileToWritePath).getName()
														+ "\"\n недоступен либо открыт",
												"Ошибка", JOptionPane.ERROR_MESSAGE);
								System.out.println("Файл недоступен или открыт");
							}
//							progressThread.interrupt();
						}
						copyMessageLabel.setVisible(false);
					}
//					progressLabel.setText("");
				} catch (IllegalArgumentException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		frame.getContentPane().add(startCopyBreButton);

		JLabel titleBRE = new JLabel("Копирование данных в \"БРЭ-ежедневный\"");
		titleBRE.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 16));
		titleBRE.setBounds(7, 88, 567, 14);
		frame.getContentPane().add(titleBRE);

		JLabel titleBRE_1 = new JLabel("Создание файла \"БРЭ для КЕГОК\"");
		titleBRE_1.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 16));
		titleBRE_1.setBounds(7, 356, 567, 14);
		frame.getContentPane().add(titleBRE_1);

		textKegocField = new JTextField();
//		textKegocField.setBackground(SystemColor.inactiveCaptionBorder);
		textKegocField.setText(service.fileToWriteDailyPath + "БРЭ для KEGOC Bassel " + service.currentDay
				+ service.getMonthStringNameWithSuffix() + ".xlsx");
		textKegocField.setEditable(false);
		textKegocField.setColumns(10);
		textKegocField.setBounds(7, 379, 566, 20);
		frame.getContentPane().add(textKegocField);

		JButton setKegocFolderButton = new JButton("Выбрать папку");
		setKegocFolderButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		setKegocFolderButton.setToolTipText("Выбрать папку для сохранения файла");
//		setKegocFolderButton.setBackground(SystemColor.text);
		setKegocFolderButton.setBounds(7, 405, 204, 23);
		setKegocFolderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(service.fileToWriteDailyPath));
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				fileChooser.showDialog(frame, "Выбрать папку");
				File f;
				try {
					f = fileChooser.getSelectedFile();
					textKegocField.setText(f.getAbsolutePath() + "\\БРЭ для KEGOC Bassel " + service.currentDay
							+ service.getMonthStringNameWithSuffix() + ".xlsx");
					service.fileToWriteDailyPath = f.getAbsolutePath() + "\\";
					System.out.println(service.fileToWriteDailyPath);
				} catch (NullPointerException e1) {

					if (!textKegocField.getText().endsWith(FILE_EXTENSION)) {
						textKegocField.setText("Файл не выбран");
						System.out.println("Kegoc Folder not choosed");
					}

				}
			}
		});
		frame.getContentPane().add(setKegocFolderButton);

		JButton createFileBre = new JButton("Создать файл");
		createFileBre.setFont(new Font("Tahoma", Font.BOLD, 11));
		createFileBre.setToolTipText("Создание ежедневного файла \"БРЭ для КЕГОК\"");
//		createFileBre.setBackground(SystemColor.inactiveCaption);
		createFileBre.setBounds(370, 405, 204, 23);
		createFileBre.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int input = JOptionPane.showConfirmDialog(frame,
						("Создать файл \n" + "\"БРЭ для KEGOC Bassel " + service.currentDay
								+ service.getMonthStringNameWithSuffix() + ".xlsx\"?"),
						"Подтверждение операции", JOptionPane.YES_NO_OPTION);
				if (input == 0) {
					try {
						service.copyDailyRows();
						JOptionPane.showMessageDialog(frame,
								("Файл " + "\"БРЭ для KEGOC Bassel " + service.currentDay
										+ service.getMonthStringNameWithSuffix() + ".xlsx\"\nуспешно создан!"),
								"Файл создан", JOptionPane.INFORMATION_MESSAGE);
					} catch (FileNotFoundException e1) {
						JOptionPane.showMessageDialog(frame,
								("Файл " + "\"БРЭ для KEGOC Bassel " + service.currentDay
										+ service.getMonthStringNameWithSuffix() + ".xlsx\"\nнедоступен либо открыт"),
								"Ошибка", JOptionPane.ERROR_MESSAGE);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});

		frame.getContentPane().add(createFileBre);

		JLabel maxLabel = new JLabel("Максимум");
		maxLabel.setHorizontalAlignment(SwingConstants.CENTER);
		maxLabel.setBounds(25, 488, 86, 14);
		frame.getContentPane().add(maxLabel);

		JLabel minLabel = new JLabel("Минимум");
		minLabel.setHorizontalAlignment(SwingConstants.CENTER);
		minLabel.setBounds(181, 488, 83, 14);
		frame.getContentPane().add(minLabel);

		JLabel avgLabel = new JLabel("Среднее");
		avgLabel.setHorizontalAlignment(SwingConstants.CENTER);
		avgLabel.setBounds(337, 488, 86, 14);
		frame.getContentPane().add(avgLabel);

		JLabel totalLabel = new JLabel("Всего");
		totalLabel.setHorizontalAlignment(SwingConstants.CENTER);
		totalLabel.setBounds(489, 488, 71, 14);
		frame.getContentPane().add(totalLabel);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(7, 450, 567, 2);
		frame.getContentPane().add(separator_1);

		JLabel statsDailyLabel = new JLabel("Статистика с начала суток до текущего часа");
		statsDailyLabel.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 16));
		statsDailyLabel.setBounds(10, 463, 564, 14);
		frame.getContentPane().add(statsDailyLabel);

		final JLabel maxValueLabel = new JLabel("0");
		maxValueLabel.setHorizontalAlignment(SwingConstants.CENTER);
		maxValueLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		maxValueLabel.setBounds(25, 513, 86, 14);
		frame.getContentPane().add(maxValueLabel);

		final JLabel minValueLabel = new JLabel("0");
		minValueLabel.setHorizontalAlignment(SwingConstants.CENTER);
		minValueLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		minValueLabel.setBounds(181, 513, 83, 14);
		frame.getContentPane().add(minValueLabel);

		final JLabel avgValueLabel = new JLabel("0");
		avgValueLabel.setHorizontalAlignment(SwingConstants.CENTER);
		avgValueLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		avgValueLabel.setBounds(337, 513, 86, 14);
		frame.getContentPane().add(avgValueLabel);

		final JLabel totalValueLabel = new JLabel("0");
		totalValueLabel.setHorizontalAlignment(SwingConstants.CENTER);
		totalValueLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		totalValueLabel.setBounds(489, 513, 71, 14);
		frame.getContentPane().add(totalValueLabel);

		JButton statsButton = new JButton("Получить статистику");
		statsButton.setToolTipText("Получить статистику из \"БРЭ-ежедневный\"");
		statsButton.setFont(new Font("Tahoma", Font.BOLD, 11));
//		statsButton.setBackground(SystemColor.text);
		statsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, "Для корректных данных \"БРЭ-ежедневный\" должен быть сохранён",
						"ВНИМАНИЕ!", JOptionPane.INFORMATION_MESSAGE);
				try {
					double[] stats = service.getStatistic();
					maxValueLabel.setText(String.valueOf(Math.round(stats[0])));
					minValueLabel.setText(String.valueOf(Math.round(stats[1])));
					avgValueLabel.setText(String.valueOf(Math.round(stats[2])));
					totalValueLabel.setText(String.valueOf(Math.round(stats[3])));
				} catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(frame, "Файл недоступен", "ОШИБКА", JOptionPane.ERROR_MESSAGE);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		statsButton.setBounds(7, 538, 205, 23);
		frame.getContentPane().add(statsButton);

		JSeparator separator_1_1 = new JSeparator();
		separator_1_1.setBounds(7, 597, 567, 2);
		frame.getContentPane().add(separator_1_1);

		JLabel lblNewLabel = new JLabel(
				"От Романа Сидорова специально для Космических Диспетчеров BasselGroup LLS  ©  2023 ");
		lblNewLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		lblNewLabel.setFont(new Font("Tahoma", Font.ITALIC, 11));
		lblNewLabel.setBounds(7, 597, 567, 14);
		frame.getContentPane().add(lblNewLabel);

		analyzeField = new JTextField();
		analyzeField.setBounds(7, 274, 567, 20);
		analyzeField.setEditable(false);
		analyzeField.setText(service.analyzePath);
		frame.getContentPane().add(analyzeField);
		analyzeField.setColumns(10);

		JButton chooseAnalyzeButton = new JButton("Выбрать файл анализа");
		chooseAnalyzeButton.setToolTipText("Выбрать файл \"Анализ\" для копирования данных");
		chooseAnalyzeButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		chooseAnalyzeButton.setBounds(8, 296, 205, 23);
		frame.getContentPane().add(chooseAnalyzeButton);
		chooseAnalyzeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(service.analyzePath));
				// fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

				fileChooser.setFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						return "Excel (.xlsx)";
					}

					@Override
					public boolean accept(File f) {
						if (f.getName().endsWith(FILE_EXTENSION) || f.isDirectory())
							return true;
						return false;
					}
				});

				fileChooser.showDialog(frame, "Выбрать файл");
				File f;
				try {
					f = fileChooser.getSelectedFile();
					analyzeField.setText(f.getAbsolutePath());
					service.analyzePath = f.getAbsolutePath();
					System.out.println(service.analyzePath);
				} catch (NullPointerException e1) {

					if (!analyzeField.getText().endsWith(FILE_EXTENSION)) {
						analyzeField.setText("Файл не выбран");
						System.out.println("Target File not choosed");
					}

				}
			}
		});

		JButton fillAnalyzeButton = new JButton("Заполнить анализ");
		fillAnalyzeButton.setToolTipText("\"БРЭ-ежедневный\"");
		fillAnalyzeButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		fillAnalyzeButton.setBounds(369, 296, 205, 23);
		frame.getContentPane().add(fillAnalyzeButton);

		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(7, 230, 567, 2);
		frame.getContentPane().add(separator_2);

		JLabel titleAnalyze = new JLabel("Копирование данных в \"Анализ\"");
		titleAnalyze.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 16));
		titleAnalyze.setBounds(7, 249, 567, 14);
		frame.getContentPane().add(titleAnalyze);

		JSeparator separator_2_1 = new JSeparator();
		separator_2_1.setBounds(7, 75, 567, 2);
		frame.getContentPane().add(separator_2_1);

		JLabel titleDate = new JLabel("Настройка даты (UTC+1)");
		titleDate.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 16));
		titleDate.setBounds(6, 6, 264, 14);
		frame.getContentPane().add(titleDate);

		JSeparator separator_2_1_1 = new JSeparator();
		separator_2_1_1.setBounds(7, 0, 567, 2);
		frame.getContentPane().add(separator_2_1_1);

		fillAnalyzeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				copyMessageLabel.setVisible(true);
				int input = JOptionPane.showConfirmDialog(frame,
						("Скопировать данные\nиз \"" + new File(service.fileToWritePath).getName() + "\"\nв \""
								+ new File(service.analyzePath).getName() + "\"?\n(БРЭ должен быть сохранён!)"),
						"Заполнение данных за " + service.currentDay + " " + service.getMonthStringNameWithSuffix(),
						JOptionPane.YES_NO_OPTION);
				System.out.println(input);
				if (input == 0) {
					
					try {
						service.saveFile(new File(service.analyzePath));
						double[][] data = service.getColumnsArrayFromBre(new int[] { 7, 9 });
						Arrays.stream(data).map(Arrays::toString).forEach(System.out::println);
						service.fillAnalyze(data, new int[] { 5, 6 });
						JOptionPane.showMessageDialog(frame, "Копирование данных завершено",
								("Записаны данные в \"" + new File(service.analyzePath).getName()) + "\"",
								JOptionPane.PLAIN_MESSAGE);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(frame,
								"Файл \"" + new File(service.analyzePath).getName() + "\" недоступен либо открыт",
								"Ошибка", JOptionPane.ERROR_MESSAGE);
					}
				}
				copyMessageLabel.setVisible(false);
			}
		});

	}

}
