package excelone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelService {
	boolean isSuccess = false;
	private final int SKIP_ROWS_SBRE = 4;
	private final int SKIP_ROWS_ASKUE = 10;
	private final int HOURS_IN_DAY = 24;
	public int progressBarValue = 0;
	public int currentDay = 1;
	public int currentMonth = 1;
	public int currentYear = 2024;
	public int firstHour = 1;
	public int currentHour = 1;
	public static int progressCount = 0;
	public static int progressValue = 0;
	
	private int currentRow = 32;
	private int rowTarget = 507;
	
	String sourceFile;
	String targetFile;
	public String fileToReadPath = "C:\\Users\\commercial\\Documents\\РасходПоОбъектам1.xlsx";
	// Test file
//	private String fileToWritePath = "C:\\Users\\commercial\\Documents\\MyFiles\\excelpoint\\СЕНТЯБРЬ БРЭ result.xlsx";
	// Original file
	public String fileToWritePath = "\\\\172.16.16.16\\коммерческий отдел\\ОКТЯБРЬ БРЭ ежедневный.xlsx";
	public String fileToWriteDailyTemplate = "C:\\Users\\commercial\\Documents\\MyFiles\\excelpoint\\KEGOC_template.xlsx";
	public String fileToWriteDailyPath = "C:\\Users\\commercial\\Desktop\\Суточная ведомость\\";
	public String analyzePath = "\\\\172.16.16.16\\коммерческий отдел\\Анализ_октябрь.xlsx";
	public String analyzeFileName = "Анализ_октябрь.xlsx";
	int[] cellNumbersToReadSBRE = { 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34,
			35, 37, 39, 41, 47, 49, 42, 44 };

	public void copyAllRowsPerDay() throws FileNotFoundException, IOException {
		
		System.out.println("=======");
		System.out.println(currentYear + " / " + currentMonth + " / " + currentDay);
		System.out.println(fileToReadPath);
		System.out.println(fileToWritePath);
		System.out.println("=======");

		for (int i = firstHour - 1; i < currentHour; i++) {
			currentRow = SKIP_ROWS_ASKUE + i;
			rowTarget = ((currentDay - 1) * HOURS_IN_DAY + SKIP_ROWS_SBRE) + (i);
			copyOneRow();
			System.out.println("День " + currentDay + "; Час " + i + " - " + (i + 1));
		}

	}

	private void copyOneRow() throws IllegalArgumentException, IOException {
		try {
			double[] rowValues = readRowFromPrimaryFile();
			progressValue += progressCount;
			writeRowToFile(rowValues);
		} catch (FileNotFoundException e) {
			System.out.println("\n*** ОШИБКА:\n*** Файл \"" + fileToWritePath.substring(34) + "\" открыт\n"
					+ "*** НЕВОЗМОЖНО СОХРАНИТЬ!");
			throw new FileNotFoundException();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Чтение массива из АСКУЭ-файла
	private double[] readRowFromPrimaryFile() throws IllegalArgumentException, IOException {

		double[] readedValue = new double[cellNumbersToReadSBRE.length];
		FileInputStream fileToRead = new FileInputStream(fileToReadPath);
		Workbook wbToRead = new XSSFWorkbook(fileToRead);
		System.out.println("WB is open");
		try {
			if(compareDateAskue()) {
				Row rowToWrite = wbToRead.getSheetAt(0).getRow(currentRow);
				for (int i = 0; i < 30; i++) {
					readedValue[i] = rowToWrite.getCell(cellNumbersToReadSBRE[i]).getNumericCellValue();
				}
			}
		} catch (IllegalArgumentException e) {
			wbToRead.close();
			throw new IllegalArgumentException();
		} catch (IOException e) {
			e.printStackTrace();
		}
		fileToRead.close();
		wbToRead.close();
		return readedValue;
	}

	// Запись массива в Ежедневный БРЭ
	private void writeRowToFile(double[] rowValues) throws FileNotFoundException, IOException {
		System.out.println("Запись в строку " + (rowTarget + 1));
		FileInputStream fis = new FileInputStream(fileToWritePath);
		Workbook wbToWrite = new XSSFWorkbook(fis);
		Sheet sheetToWrite = wbToWrite.getSheetAt(0);
		sheetToWrite.setForceFormulaRecalculation(true);
		for (int i = 0; i < 28; i++) {
			System.out.println("=================== " + progressValue);
			sheetToWrite.getRow(rowTarget).getCell(i + 11).setCellValue(rowValues[i]);
		}
		sheetToWrite.getRow(rowTarget).getCell(5).setCellValue(rowValues[28]);
		sheetToWrite.getRow(rowTarget).getCell(6).setCellValue(rowValues[29]);
		fis.close();
		FileOutputStream fos = new FileOutputStream(new File(fileToWritePath));
		wbToWrite.write(fos);
		fos.close();
		wbToWrite.close();
		isSuccess = true;
	}

	// ======== Создание БРЭ для КЕГОК
	// =====================================================================

	public void copyDailyRows() throws FileNotFoundException, IOException {
//		setCurrentDay(); // ВКЛЮЧИТЬ МЕТОД ДЛЯ РАБОТЫ В КОНСОЛИ
		sourceFile = fileToWritePath;
		targetFile = fileToWriteDailyPath + "БРЭ для KEGOC Bassel " + currentDay + getMonthStringNameWithSuffix()+ ".xlsx";
		System.out.println(targetFile);
		try {
			double[][] dailyRows = readDailyRows();
			writeDailyValues(dailyRows);
		} catch (IllegalArgumentException e) {
			System.out.println("\n*** ОШИБКА:\n" + "*** В этом месяце меньше дней, чем ты думаешь!");
		} catch (FileNotFoundException e) {
			System.out.println("\n*** ОШИБКА:\n*** Файл \"БРЭ для KEGOC Bassel " + currentDay
					+ " сентября.xlsx\" открыт\n" + "*** НЕВОЗМОЖНО СОХРАНИТЬ!");
			throw new FileNotFoundException();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Чтение данных за прошедшие сутки
	private double[][] readDailyRows() throws IllegalArgumentException, FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(sourceFile);
		Workbook wb = new XSSFWorkbook(fis);
		Sheet sheet = wb.getSheetAt(0);
		double[][] dailyRows = new double[24][30];
		for (int rows = 0; rows < 24; rows++) {
			int targetRow = ((currentDay - 1) * HOURS_IN_DAY + SKIP_ROWS_SBRE) + (rows);
			if (targetRow >= sheet.getLastRowNum() - 1) {
				wb.close();
				fis.close();
				throw new IllegalArgumentException();
			}
			for (int columns = 0; columns < 28; columns++) {
				dailyRows[rows][columns] = sheet.getRow(targetRow).getCell(columns + 11).getNumericCellValue();
			}
			dailyRows[rows][28] = sheet.getRow(targetRow).getCell(5).getNumericCellValue();
			dailyRows[rows][29] = sheet.getRow(targetRow).getCell(6).getNumericCellValue();
		}
		fis.close();
		wb.close();
		System.out.println("Данные считаны");
		return dailyRows;
	}

	// Запись данных в новый файл БРЭ для КЕГОК
	private void writeDailyValues(double[][] dailyRows)
			throws IllegalArgumentException, FileNotFoundException, IOException {
		FileInputStream fis = null;
		Workbook wb = null;
		try {
			fis = new FileInputStream(fileToWriteDailyTemplate);
			wb = new XSSFWorkbook(fis);
			Sheet sheet = wb.getSheetAt(0);
			sheet.setForceFormulaRecalculation(true);
			for (int rows = 0; rows < 24; rows++) {
				for (int columns = 0; columns < 28; columns++) {
					sheet.getRow(rows + 4).getCell(columns + 5).setCellValue(dailyRows[rows][columns]);
				}
				sheet.getRow(rows + 4).getCell(2).setCellValue(dailyRows[rows][28]);
				sheet.getRow(rows + 4).getCell(3).setCellValue(dailyRows[rows][29]);
				System.out.println("Записаны данные часа " + rows + " - " + (rows + 1));
			}
			sheet.getRow(0).getCell(0).setCellValue(new GregorianCalendar(2023, currentMonth - 1, currentDay));
			fis.close();
		} catch (FileNotFoundException e) {
			fis.close();
			wb.close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		FileOutputStream fos = new FileOutputStream(targetFile);
		wb.write(fos);
		fos.close();
		wb.close();
		isSuccess = true;
	}

	// =========== Сравнение дат =================
	// ===========================================
	public boolean compareDateAskue() throws IllegalArgumentException, IOException {
		System.out.println("Entering compare date");
		FileInputStream fis = new FileInputStream(fileToReadPath);
		Workbook wbToRead = new XSSFWorkbook(fis);
		DateFormat df = new SimpleDateFormat("DD/MMMM/YY");
		@SuppressWarnings("deprecation")
		Date dateToday = new Date((currentYear - 1900), currentMonth - 1, currentDay);
		System.out.println(currentDay + " / " + currentMonth + " / " + (currentYear + 1900));
		Date date = wbToRead.getSheetAt(0).getRow(0).getCell(5).getDateCellValue();
		System.out.println(dateToday);
		System.out.println(date);

//		String cell1 = df.format(dateToday);
//		String cell2 = df.format(date);
//		System.out.println(cell1 + "   |||   " + cell2);
//		if (cell1.equals(cell2)) {
		if (dateToday.compareTo(date) == 0) {
			System.out.println("Дата соответствует");
			fis.close();
			return true;
		} else {
			System.out.println(
					"\n*** ОШИБКА:\n*** Введённая дата не совпадает с датой в файле \"РасходПоОбъектам1.xlsx\"\n");
			fis.close();
			return false;
//			throw new IllegalArgumentException();
		}
	}
	
	public boolean checkTrial() {
		return new Date().getMonth() == 9;
	}
	

	// =========== Выбор режима порграммы ==========
	// ==============================================

	public void chooseMode() throws IllegalArgumentException, IOException {
		isSuccess = false;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Выбрать действие:\n1 - Копирование данных\n2 - Создание файла для КЕГОК\n");
		int mode = Integer.parseInt(reader.readLine());
		System.out.println("====================\n");
		if (mode == 1) {
			setTimeToCopy();
		} else if (mode == 2) {
			copyDailyRows();
		} else {
			System.out.println("\n*** ОШИБКА:\n" + "*** Неверно вывбрано действие\n");
		}
	}

	// Сетап для копирования показаний
	private void setTimeToCopy() throws IllegalArgumentException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("**  ВНИМАНИЕ:\n"
				+ "**  В папку \"C:\\Users\\commercial\\Documents\\\"\n"
				+ "**  должен быть загружен и сохранён актуальный файл \"РасходПоОбъектам1.xlsx\"\n\n"
				+ "За какое число нужно снять показания? (1 - 31):\n");
		currentDay = Integer.parseInt(reader.readLine());
		System.out.println("====================\n");
		if (currentDay < 1 || currentDay > 31) {
			System.out.println("*** ОШИБКА:\n*** Неверно выбрана дата: " + currentDay);
			return;
		}
		System.out.println("\nЗа какой час снять показания? (0 - с начала суток)");
		currentHour = Integer.parseInt(reader.readLine());
		System.out.println("====================\n");
		if (currentHour < 0 || currentHour > 24) {
			System.out.println("*** ОШИБКА:\n*** Неверно выбрано время: " + currentHour);
			return;
		}
		if (currentHour == 0) {
			System.out.println("\nДо какого часа скопировать показания?");
			int input = Integer.parseInt(reader.readLine());
			System.out.println("====================\n");
			if (input > 0 || input < 25) {
				currentHour = input;
				copyAllRowsPerDay();
			} else {
				System.out.println("*** ОШИБКА:\n*** Неверно выбрано время: " + currentHour);
				return;
			}
		} else {
			currentRow = SKIP_ROWS_ASKUE + currentHour - 1;
			rowTarget = ((currentDay - 1) * HOURS_IN_DAY + SKIP_ROWS_SBRE) + (currentHour - 1);
			copyOneRow();
		}
	}

	// Сетап для создания БРЭ для КЕГОК
	private void setCurrentDay() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("\nВведите число месяца для создания файла \"БРЭ для КЕГОК\"\n");
		int input = Integer.parseInt(reader.readLine());
		System.out.println("====================\n");
		if (input < 1 || input > 31) {
			System.out.println("*** ОШИБКА:\n*** Неверно введена дата\n");
			throw new IOException();
		} else
			currentDay = input;

	}

	public void printEndMessageSuccess() {
		System.out.println("\n===========================\n" + "= Готово. Вы великолепны! =\n"
				+ "=  Слава ленивым жопам!   =\n" + "===========================");
	}

	public void printEndMessageFail() {
		System.out.println("\n================================\n" + "= Операция завершена с ошибкой =\n"
				+ "=      Жопа ты криворукая!     =\n" + "================================");
	}
	
	public void setCurrentDate() {
		Date date = new Date();
		currentYear = date.getYear();
		currentMonth = date.getMonth();
		currentDay = date.getDate() + 1;
		System.out.println("Setting current date to " + currentDay + " / " + (currentMonth + 1) + " / " + (currentYear + 1900));
	}
	public String getCorrechHourString() {
		int hoursPeriod = currentHour - (firstHour - 1);
		if ((hoursPeriod >= 2 && hoursPeriod <= 4) || ( hoursPeriod >=22 && hoursPeriod <= 24)) return " часа";
		if (hoursPeriod >= 5 && hoursPeriod <= 20 ) return " часов";
		return " час";
	}
	public String getMonthStringNameWithSuffix() {
		if (currentMonth == 1) return " января";
		if (currentMonth == 2) return " февраля";
		if (currentMonth == 3) return " марта";
		if (currentMonth == 4) return " апреля";
		if (currentMonth == 5) return " мая";
		if (currentMonth == 6) return " июня";
		if (currentMonth == 7) return " июля";
		if (currentMonth == 8) return " августа";
		if (currentMonth == 9) return " сентября";
		if (currentMonth == 10) return " октября";
		if (currentMonth == 11) return " ноября";
		if (currentMonth == 12) return " декабря";
		return " месяц";
	}
	
	public double[] getStatistic() throws FileNotFoundException, IOException {
		double[] result = new double[4];
		double[] dailyPowerData = new double[24];
		try (FileInputStream fis = new FileInputStream(fileToWritePath)){
			System.out.println(new File(fileToWritePath).getAbsolutePath());
			Workbook wb = new XSSFWorkbook(fis);
			Sheet sheet = wb.getSheetAt(0);
			for (int row = 0; row < 24; row++) {
				int targetRow = ((currentDay - 1) * HOURS_IN_DAY + SKIP_ROWS_SBRE) + (row);
				
				if (targetRow >= sheet.getLastRowNum() - 1) {
					wb.close();
					throw new IllegalArgumentException();
				}
				dailyPowerData[row] = sheet.getRow(targetRow).getCell(7).getNumericCellValue();
				System.out.println(dailyPowerData[row]);
			}
			wb.close();
			fis.close();
			System.out.println("Данные статистики считаны");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result[0] = getMaxStatistic(dailyPowerData);
		result[1] = getMinStatistic(dailyPowerData);
		result[2] = getAvgStatistic(dailyPowerData);
		result[3] = getTotalStatistic(dailyPowerData);
		return result;
	}
	
	private double getMaxStatistic(double[] array) {
		double maxVal = array[0];
		for (double val : array) {
			if (val > maxVal) maxVal = val;
		}
		return maxVal;
	}
	private double getMinStatistic(double[] array) {
		double minVal = array[0];
		for (double val : array) {
			if (val < minVal && val !=0) minVal = val;
		}
		return minVal;
	}
	private double getAvgStatistic(double[] array) {
		int count = 24;
		double sum = 0;
		for (double val : array) {
			sum += val;
			if (val == 0) count--;
		}
		return sum / count;
	}
	private double getTotalStatistic(double[] array) {
		double sum = 0;
		for (double val : array) {
			sum += val;
		}
		return sum;
	}
	
	public String getMonthStringName() {
		if (currentMonth == 1) return " январь";
		if (currentMonth == 2) return " февраль";
		if (currentMonth == 3) return " март";
		if (currentMonth == 4) return " апрель";
		if (currentMonth == 5) return " май";
		if (currentMonth == 6) return " июнь";
		if (currentMonth == 7) return " июль";
		if (currentMonth == 8) return " август";
		if (currentMonth == 9) return " сентябрь";
		if (currentMonth == 10) return " октябрь";
		if (currentMonth == 11) return " ноябрь";
		if (currentMonth == 12) return " декабрь";
		return " месяц";
	}
	
	public double[][] getColumnsArrayFromBre(int ... columns) throws IOException{
		double[][] result = new double[currentHour - firstHour + 1][columns.length];
		
		FileInputStream fis = null;
		Workbook wb = null;
		try {
			fis = new FileInputStream(fileToWritePath);
			wb = new XSSFWorkbook(fis);
		} catch (FileNotFoundException e) {
			fis.close();
			wb.close();
			e.printStackTrace();
		}
		for (int i = 0; i < result.length; i++) {
			int targetRow = ((currentDay - 1) * HOURS_IN_DAY + SKIP_ROWS_SBRE) + firstHour - 1;
			for (int j = 0; j < columns.length; j++) {
				System.out.println(targetRow);
				System.out.println("row: " + i + " / column: " + j);
				result[i][j] = wb.getSheetAt(0).getRow(targetRow + i).getCell(columns[j]).getNumericCellValue();
				System.out.println("result: " + result[i][j]);
				if (result[i][j] == -7) result[i][j] = 0;
			}
		}
		fis.close();
		wb.close();
		return result;
	}
	public void fillAnalyze(double[][] dataArray, int ... columns) throws IOException {
		
		try (FileInputStream fis  = new FileInputStream(analyzePath); Workbook wb = new XSSFWorkbook(fis)) {
			Sheet sheet = wb.getSheetAt(0);
			sheet.setForceFormulaRecalculation(true);
			for (int i = 0; i < dataArray.length; i++) {
				for (int j = 0; j < columns.length; j++) {
					int targetRow = (3 + (currentDay - 1) * 25) + i + firstHour - 1;
					if (targetRow >= sheet.getLastRowNum() - 1) {
						wb.close();
						fis.close();
						throw new IllegalArgumentException();
					}
				sheet.getRow(targetRow).getCell(columns[j]).setCellValue(dataArray[i][j]);
				}
			}
			fis.close();
			FileOutputStream fos = new FileOutputStream(analyzePath);
			wb.write(fos);
			wb.close();
			fos.close();
			System.out.println("Analyze file write success");
		}
		
	}
	
	public void saveFile(File f) throws IOException {
		Files.copy(f.toPath(), new File("C:\\Users\\commercial\\Documents\\"+f.getName()+" бэкап").toPath());
	}
	
}
