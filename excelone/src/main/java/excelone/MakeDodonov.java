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
import java.awt.Font;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JProgressBar;

import excelone.threads.*;
import java.awt.Color;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.border.TitledBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JCheckBox;

public class MakeDodonov {

	private JFrame frame;
	private JTextField fileTextField;
	private static DateChooser chDate = new DateChooser();
	private JTextField textField;
	private JTextField dateField;
	private JTextField askuePathTextField;
	private JTextField dailyBrePathTextField;
	private JComboBox currentHourCombo;
	private JComboBox analyzeCurrentHourCombo;
	private JComboBox analyzeFirstHourCombo;
	private JTextField analyzeField;
	private JLabel powerMinStats;
	private JLabel powerMaxStats;
	private JLabel powerAvgStats;
	private JLabel powerTotalStats;
	private JLabel snMinStats;
	private JLabel snMaxStats;
	private JLabel snAvgStats;
	private JLabel snTotalStats;
	private AnalyzeService analyzeService;
	private JPanel fillAnalyzePanel;
	private XLSXService service;
	private JPanel createKegocPanel;
	private JPanel createDirectorPanel;
	private JPanel koremPanel;
	private DailyBreService dailyBreService;
	private JPanel dailyBrePanel;
	ProgressThread progressThread;
	JProgressBar progressBar;
	private JRadioButton priceRadioBuy;
	private JRadioButton priceRadioSale;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JTextField koremSourceField;
	private JTextField koremTargetField;

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
		service = new XLSXService();
		frame = new JFrame("ЛЕНИВАЯ ЖОПА  Premium");
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dimension = toolkit.getScreenSize();
		frame.setBounds(dimension.width / 2 - 350, dimension.height / 2 - 235, 700, 530);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		ButtonListener buttonListener = new ButtonListener();
		frame.getContentPane().setLayout(null);

		JSeparator separator = new JSeparator();
		separator.setBounds(10, 243, 564, 0);
		frame.getContentPane().add(separator);

		JTabbedPane mainTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		mainTabbedPane.setBounds(10, 81, 664, 225);
		frame.getContentPane().add(mainTabbedPane);

		JLabel dateLabel = new JLabel("Дата (UTC+1)");
		dateLabel.setBounds(105, 36, 86, 34);
		dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(dateLabel);

		// === Date set ===
		dateField = new JTextField();
		dateField.setBounds(20, 36, 86, 34);
		dateField.setHorizontalAlignment(SwingConstants.CENTER);
		dateField.setColumns(10);
		frame.getContentPane().add(dateField);
		chDate.setTextField(dateField);
		chDate.setLabelCurrentDayVisible(false);
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

		// === Daily BRE
		// ================================================================================================

		dailyBreService = new DailyBreService();

		dailyBrePanel = new JPanel();
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
		selectAskuePathButton.addActionListener(buttonListener::selectAskueFileButtonListener);
		selectAskuePathButton.setBounds(618, 11, 31, 34);
		dailyBrePanel.add(selectAskuePathButton);

		JButton copyDailyBreButton = new JButton("Записать в файл");
		copyDailyBreButton.addActionListener(buttonListener::copyBreButtonListener);
		copyDailyBreButton.setBounds(524, 151, 125, 33);
		dailyBrePanel.add(copyDailyBreButton);

		dailyBrePathTextField = new JTextField();
		dailyBrePathTextField.setText(AppData.dailyBrePath + AppData.dailyBreFileName);
		dailyBrePathTextField.setEditable(false);
		dailyBrePathTextField.setColumns(10);
		dailyBrePathTextField.setBounds(10, 57, 598, 33);
		dailyBrePanel.add(dailyBrePathTextField);

		JButton selectDailyBrePathButton = new JButton("...");
		selectDailyBrePathButton.addActionListener(buttonListener::selectDailyBrePathButtonListener);
		selectDailyBrePathButton.setBounds(618, 56, 31, 34);
		dailyBrePanel.add(selectDailyBrePathButton);

		JComboBox firstHourCombo = new JComboBox();
		firstHourCombo.setModel(new DefaultComboBoxModel(AppData.hoursArray));
		firstHourCombo.setBounds(10, 124, 55, 22);
		dailyBrePanel.add(firstHourCombo);
		firstHourCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int comboValue = Integer.parseInt((String) firstHourCombo.getSelectedItem());
				AppData.firstHour = comboValue;
				if (comboValue > AppData.currentHour) {
					AppData.currentHour = comboValue;
					currentHourCombo.setSelectedItem(String.valueOf(comboValue));
					;
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
				int comboValue = Integer.parseInt((String) currentHourCombo.getSelectedItem());
				AppData.currentHour = Integer.parseInt((String) currentHourCombo.getSelectedItem());
				if (comboValue < AppData.firstHour) {
					AppData.firstHour = comboValue;
					firstHourCombo.setSelectedItem(String.valueOf(comboValue));
					;
				}
				System.out.println("Current hour set to " + AppData.currentHour);
				AppData.printTimeLimits();
			}
		});

		JLabel timeLabel = new JLabel("Время снятия показаний");
		timeLabel.setBounds(10, 101, 137, 14);
		dailyBrePanel.add(timeLabel);

		// === KEGOC
		// ==================================================================================================
		createKegocPanel = new JPanel();
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
		selectPathButton_1.addActionListener(buttonListener::selectKegocPathButtonListener);

		JButton createDirectorButton_1 = new JButton("Создать файл");
		createDirectorButton_1.addActionListener(buttonListener::createKegocButtonListener);
		createDirectorButton_1.setBounds(524, 151, 125, 33);
		createKegocPanel.add(createDirectorButton_1);

		// === Анализ ПОЧИНИТЬ ВРЕМЯ
		// =============================================================================================

		analyzeService = new AnalyzeService();

		fillAnalyzePanel = new JPanel();
		fillAnalyzePanel.setLayout(null);
		mainTabbedPane.addTab("Анализ", null, fillAnalyzePanel, null);

		analyzeField = new JTextField();
		analyzeField.setText(AppData.dailyAnalyzePath + AppData.dailyAnalyzeFileName);
		analyzeField.setEditable(false);
		analyzeField.setColumns(10);
		analyzeField.setBounds(10, 12, 598, 33);
		fillAnalyzePanel.add(analyzeField);

		JButton selectAnalyzePathButton = new JButton("...");
		selectAnalyzePathButton.addActionListener(buttonListener::selectAnalyzePathButtonListener);
		selectAnalyzePathButton.setBounds(618, 11, 31, 34);
		fillAnalyzePanel.add(selectAnalyzePathButton);

		JButton fillAnalyzeButton = new JButton("Записать в файл");
		fillAnalyzeButton.addActionListener(buttonListener::fillAnalyzeButtonListener);
		fillAnalyzeButton.setBounds(524, 151, 125, 33);
		fillAnalyzePanel.add(fillAnalyzeButton);

		ButtonGroup group = new ButtonGroup();

		// === DIRECTOR
		// ================================================================================================
		createDirectorPanel = new JPanel();
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
		selectPathButton.addActionListener(buttonListener::selectDirectorPathButtonListener);

		JButton createDirectorButton = new JButton("Записать в файл");
		createDirectorButton.setEnabled(false);
		createDirectorButton.setBounds(524, 151, 125, 33);
		createDirectorPanel.add(createDirectorButton);
		createDirectorButton.addActionListener(buttonListener::createDirectorButtonListener);

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

		JLabel infoLabel = new JLabel("Функционал данной вкладки отключен по причине отсутствия необходимости.");
		infoLabel.setFont(new Font("Arial", Font.BOLD, 12));
		infoLabel.setForeground(new Color(255, 0, 0));
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		infoLabel.setBounds(10, 151, 504, 35);
		createDirectorPanel.add(infoLabel);
		
		
		
		// KOREM =============================================================
		koremPanel = new JPanel();
		mainTabbedPane.addTab("КОРЕМ", null, koremPanel, null);
		mainTabbedPane.setEnabledAt(4, false);
		koremPanel.setLayout(null);
		
				JPanel radioPanel = new JPanel();
				radioPanel.setBorder(new TitledBorder(null, "\u0412\u0438\u0434 \u0442\u043E\u0440\u0433\u043E\u0432\u043B\u0438", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				radioPanel.setBounds(10, 109, 97, 77);
				koremPanel.add(radioPanel);
				
						priceRadioBuy = new JRadioButton("Покупка");
						radioPanel.add(priceRadioBuy);
						priceRadioBuy.setSelected(true);
						priceRadioBuy.addActionListener(buttonListener::setRadioButtonPriceListener);
						
						priceRadioSale = new JRadioButton("Продажа");
						radioPanel.add(priceRadioSale);
						priceRadioSale.addActionListener(buttonListener::setRadioButtonPriceListener);
						group.add(priceRadioBuy);
						group.add(priceRadioSale);
						
						koremSourceField = new JTextField();
						koremSourceField.setEditable(false);
						koremSourceField.setBounds(10, 22, 600, 29);
						koremPanel.add(koremSourceField);
						koremSourceField.setColumns(10);
						
						koremTargetField = new JTextField();
						koremTargetField.setText(AppData.koremTargetPath);
						koremTargetField.setEditable(false);
						koremTargetField.setBounds(10, 61, 600, 29);
						koremPanel.add(koremTargetField);
						koremTargetField.setColumns(10);
						
						JButton koremSourceButton = new JButton("...");
						koremSourceButton.addActionListener(buttonListener::selectKoremPathButtonListener);
						koremSourceButton.setBounds(617, 22, 27, 29);
						koremPanel.add(koremSourceButton);
						
						
						JButton koremTargetButton = new JButton("New button");
						koremTargetButton.addActionListener(buttonListener::setKoremPathButtonListener);
						koremTargetButton.setBounds(617, 60, 27, 29);
						koremPanel.add(koremTargetButton);
						
						JButton koremFillButton = new JButton("Записать в файл");
						koremFillButton.addActionListener(buttonListener::createKoremFileButtonListener);
						koremFillButton.setBounds(519, 153, 125, 33);
						koremPanel.add(koremFillButton);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 317, 664, 2);
		frame.getContentPane().add(separator_1);

		JLabel lblNewLabel = new JLabel("Минимум");
		lblNewLabel.setBounds(22, 417, 102, 23);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Максимум");
		lblNewLabel_1.setBounds(134, 417, 102, 23);
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("Среднее");
		lblNewLabel_2.setBounds(246, 417, 102, 23);
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblNewLabel_2);

		JLabel lblNewLabel_3 = new JLabel("Всего");
		lblNewLabel_3.setBounds(358, 417, 102, 23);
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblNewLabel_3);

		snMinStats = new JLabel("-");
		snMinStats.setBounds(22, 375, 102, 34);
		snMinStats.setFont(new Font("Tahoma", Font.BOLD, 15));
		snMinStats.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(snMinStats);

		snMaxStats = new JLabel("-");
		snMaxStats.setBounds(134, 375, 102, 34);
		snMaxStats.setFont(new Font("Tahoma", Font.BOLD, 15));
		snMaxStats.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(snMaxStats);

		snAvgStats = new JLabel("-");
		snAvgStats.setBounds(246, 375, 102, 34);
		snAvgStats.setFont(new Font("Tahoma", Font.BOLD, 15));
		snAvgStats.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(snAvgStats);

		snTotalStats = new JLabel("-");
		snTotalStats.setBounds(358, 375, 102, 34);
		snTotalStats.setFont(new Font("Tahoma", Font.BOLD, 15));
		snTotalStats.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(snTotalStats);

		powerMinStats = new JLabel("-");
		powerMinStats.setBounds(22, 330, 102, 34);
		powerMinStats.setFont(new Font("Tahoma", Font.BOLD, 15));
		powerMinStats.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(powerMinStats);

		powerMaxStats = new JLabel("-");
		powerMaxStats.setBounds(134, 330, 102, 34);
		powerMaxStats.setFont(new Font("Tahoma", Font.BOLD, 15));
		powerMaxStats.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(powerMaxStats);

		powerAvgStats = new JLabel("-");
		powerAvgStats.setBounds(246, 330, 102, 34);
		powerAvgStats.setFont(new Font("Tahoma", Font.BOLD, 15));
		powerAvgStats.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(powerAvgStats);

		powerTotalStats = new JLabel("-");
		powerTotalStats.setBounds(358, 330, 102, 34);
		powerTotalStats.setFont(new Font("Tahoma", Font.BOLD, 15));
		powerTotalStats.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(powerTotalStats);

		JLabel powerTitleLabel = new JLabel("Выработка");
		powerTitleLabel.setBounds(537, 342, 65, 14);
		frame.getContentPane().add(powerTitleLabel);

		JLabel snTitleLabel = new JLabel("Собственные Нужды");
		snTitleLabel.setBounds(537, 387, 125, 14);
		frame.getContentPane().add(snTitleLabel);

		JButton getStatisticButton = new JButton("Статистика");
		getStatisticButton.setBounds(537, 417, 125, 33);
		frame.getContentPane().add(getStatisticButton);
		getStatisticButton.addActionListener(buttonListener::statsButtonListener);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 674, 22);
		frame.getContentPane().add(menuBar);

		JMenu mnNewMenu = new JMenu("Файл");
		menuBar.add(mnNewMenu);

		JMenuItem quitMenuItem = new JMenuItem("Выход");
		mnNewMenu.add(quitMenuItem);
		quitMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		JMenu mnNewMenu_1 = new JMenu("Инструменты");
		menuBar.add(mnNewMenu_1);

		JMenuItem fileCloserMenuItem = new JMenuItem("Закрывашка");
		mnNewMenu_1.add(fileCloserMenuItem);

		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(10, 461, 664, 2);
		frame.getContentPane().add(separator_2);

		JLabel lblNewLabel_4 = new JLabel("Для лучших Космических Диспетчеров и Игоря © BasselGroup LLS 2023");
		lblNewLabel_4.setBounds(276, 466, 398, 14);
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.TRAILING);
		frame.getContentPane().add(lblNewLabel_4);

		progressBar = new JProgressBar();
		progressBar.setBounds(10, 466, 256, 14);
		progressBar.setValue(0);
		progressBar.setVisible(false);
		frame.getContentPane().add(progressBar);
		progressThread = new ProgressThread(progressBar);

		fileCloserMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				FileCloserWindow fileCloserWindow = new FileCloserWindow();
				fileCloserWindow.secondFrame.setVisible(true);
			}
		});

	}

	class ButtonListener {
		private void statsButtonListener(ActionEvent e) {
			{
				try {
					double[][] statsArray = AppData.getStatistic();
					powerMinStats.setText(String.valueOf(Math.round(statsArray[0][0])));
					powerMaxStats.setText(String.valueOf(Math.round(statsArray[0][1])));
					powerAvgStats.setText(String.valueOf(Math.round(statsArray[0][2])));
					powerTotalStats.setText(String.valueOf(Math.round(statsArray[0][3])));
					snMinStats.setText(String.valueOf(Math.round(statsArray[1][0])));
					snMaxStats.setText(String.valueOf(Math.round(statsArray[1][1])));
					snAvgStats.setText(String.valueOf(Math.round(statsArray[1][2])));
					snTotalStats.setText(String.valueOf(Math.round(statsArray[1][3])));
				} catch (IllegalArgumentException ei) {
					JOptionPane.showMessageDialog(frame, "Выбери дату текущего месяца");
				} catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(frame,
							"Файл \"" + AppData.dailyFileName + "\" недоступен либо открыт", "ОШИБКА",
							JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		private void fillAnalyzeButtonListener(ActionEvent e) {
			int input = JOptionPane.showConfirmDialog(frame,
					("Записать данные в \"" + AppData.dailyAnalyzeFileName + "\"\n от " + AppData.day + " "
							+ AppData.getMonthStringNameWithSuffix() + "?"),
					"Подтверждение операции", JOptionPane.YES_NO_OPTION);
			System.out.println(input);
			if (input == 0) {
				try {
					double[][] data = analyzeService.getColumnsArrayFromBre(new int[] { 7, 9 });
					analyzeService.fillAnalyze(data, new int[] { 5, 6 });
					JOptionPane.showMessageDialog(fillAnalyzePanel, "Данные записаны", "ИНФОРМАЦИЯ",
							JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(fillAnalyzePanel,
							"Файл \"" + AppData.dailyAnalyzeFileName + "\" недоступен либо открыт", "ОШИБКА",
							JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}

			}
		}

		private void selectAnalyzePathButtonListener(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(AppData.dailyAnalyzePath));
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			fileChooser.showDialog(frame, "Выбрать папку");
			File f;
			try {
				f = fileChooser.getSelectedFile();
				fileTextField.setText(f.getAbsolutePath() + "\\" + AppData.dailyAnalyzeFileName);
				AppData.dailyAnalyzePath = f.getAbsolutePath() + "\\";
				System.out.println("dailyAnalyzePath set to " + AppData.dailyAnalyzePath);
			} catch (NullPointerException e1) {
				System.out.println("Analyze Folder not choosed");
			}
		}

		private void createKegocButtonListener(ActionEvent e) {
			int input = JOptionPane.showConfirmDialog(
					frame, ("Создать файл \"" + "БРЭ для KEGOC Bassel " + AppData.day
							+ AppData.getMonthStringNameWithSuffix() + ".xlsx" + "\"?"),
					"Подтверждение операции", JOptionPane.YES_NO_OPTION);
			if (input == 0) {
				try {
					service.copyDailyRowsForKegoc();
					JOptionPane.showMessageDialog(
							createKegocPanel, "Файл \"" + "БРЭ для KEGOC Bassel " + AppData.day
									+ AppData.getMonthStringNameWithSuffix() + ".xlsx" + "\" создан",
							"ИНФОРМАЦИЯ", JOptionPane.INFORMATION_MESSAGE);
				} catch (FileNotFoundException ex) {
					JOptionPane.showMessageDialog(
							createKegocPanel, "Файл \"" + "БРЭ для KEGOC Bassel " + AppData.day
									+ AppData.getMonthStringNameWithSuffix() + ".xlsx" + "\" недоступен либо открыт",
							"ОШИБКА", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

		private void selectKegocPathButtonListener(ActionEvent e) {
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

		private void createDirectorButtonListener(ActionEvent e) {
			int input = JOptionPane.showConfirmDialog(frame,
					("Записать данные в \"" + AppData.dailyFileName + "\"\n от " + AppData.day + " "
							+ AppData.getMonthStringNameWithSuffix() + "?\nПроверь дату и фамилии НСС!"),
					"Подтверждение операции", JOptionPane.YES_NO_OPTION);
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

		private void selectDirectorPathButtonListener(ActionEvent e) {
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

		private void selectDailyBrePathButtonListener(ActionEvent e) {
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
					if ((f.getName().endsWith(AppData.FILE_EXTENSION)
							&& f.getName().toUpperCase().contains(("Ежедневный").toUpperCase())) || f.isDirectory())
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

		private void copyBreButtonListener(ActionEvent e) {
			try {
				if (!dailyBreService.compareDateAskue()) {
					JOptionPane.showMessageDialog(dailyBrePanel,
							("Дата в \"" + AppData.askueFileName + "\"\n" + "не соответствует выбранной!"), "ОШИБКА",
							JOptionPane.ERROR_MESSAGE);
				} else {
					int input = JOptionPane.showConfirmDialog(frame,
							("Записать данные в \"" + AppData.dailyBreFileName + "\"\n от " + AppData.day + " "
									+ AppData.getMonthStringNameWithSuffix() + "?"),
							"Подтверждение операции", JOptionPane.YES_NO_OPTION);
					System.out.println(input);
					if (input == 0) {
						try {
//							new Thread(progressThread).start(); // НЕ РАБОТАЕТ, ХРЕН ЗНАЕТ ПОЧЕМУ			
							dailyBreService.copyAskueToBreData();
						} catch (checkAskueFileException e1) {
							JOptionPane.showMessageDialog(dailyBrePanel,
									("Файл \"" + AppData.askueFileName + "\"\n" + "не соответствует стандартому!"),
									"ОШИБКА", JOptionPane.ERROR_MESSAGE);
							e1.printStackTrace();
							return;
						}
						JOptionPane.showMessageDialog(dailyBrePanel, ("Данные скопированы"), "ВЫПОЛНЕНО",
								JOptionPane.PLAIN_MESSAGE);

					}
				}
			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(dailyBrePanel,
						("Файл \"" + AppData.dailyBreFileName + "\"\n" + "недоступен либо открыт!"), "ОШИБКА",
						JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

		private void selectAskueFileButtonListener(ActionEvent e) {
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
		
		private void setRadioButtonPriceListener(ActionEvent e) {
			if (e.getSource() == priceRadioBuy) {
				AppData.modeString = AppData.modeBuyString;
				System.out.println("Mode set to " + AppData.modeString);
			}
			if (e.getSource() == priceRadioSale) {
				AppData.modeString = AppData.modeSellString;
				System.out.println("Mode set to " + AppData.modeString);
			}
		}
		private void selectKoremPathButtonListener(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(AppData.koremSourcePath));
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
			fileChooser.showDialog(koremPanel, "Выбрать файл с ценами");
			File f;
			try {
				f = fileChooser.getSelectedFile();
				AppData.koremSourceFile = f.getAbsolutePath();
				System.out.println("Korem Source File set to " + AppData.koremSourceFile);
				koremSourceField.setText(f.getAbsolutePath());

			} catch (NullPointerException e1) {
				System.out.println("Korem File not choosed");
			}
		}
		
		private void setKoremPathButtonListener(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(AppData.koremTargetPath));
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.showDialog(koremPanel, "Выбрать папку для сохранения файла");
			File f;
			try {
				f = fileChooser.getSelectedFile();
				koremTargetField.setText(f.getAbsolutePath());
				AppData.koremTargetPath = f.getAbsolutePath();
				System.out.println("Korem Target Path set to " + AppData.koremTargetPath);
			} catch (NullPointerException e1) {
				System.out.println("Korem Target Path not choosed");
			}
		}
		
		private void createKoremFileButtonListener(ActionEvent e) {
			if (AppData.koremSourceFile == null) {
				JOptionPane.showMessageDialog(koremPanel, "Файл-источник не выбран!", "ОШИБКА", JOptionPane.WARNING_MESSAGE);
				System.out.println("Korem source file not choosed");
				return;
			}
			int input;
			switch (AppData.modeString) {
			case "ПОКУПКА":
				input = JOptionPane.showConfirmDialog(koremPanel, "Записать данные из файла\n" 
						+ new File(AppData.koremSourceFile).getName() + "\nв режиме \"ПОКУПКА\"?",
						"Подтверждение операции", 
						JOptionPane.YES_NO_OPTION);
				if (input == 0) {
					System.out.println("BUYING...");				
				}
				break;
			case "ПРОДАЖА":
				input = JOptionPane.showConfirmDialog(koremPanel, "Записать данные из файла\n" 
						+ new File(AppData.koremSourceFile).getName() + "\nв режиме \"ПРОДАЖА\"?",
						"Подтверждение операции", 
						JOptionPane.YES_NO_OPTION);
				if (input == 0) {
					System.out.println("SELLING...");				
				}
				break;
			}
		}
	}
}
