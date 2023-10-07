package excelone;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import exceptions.checkAskueFileException;

public class DailyBreService {

	int[] cellNumbersToReadSBRE = { 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34,
			35, 37, 39, 41, 47, 49, 42, 44 };
	final int ASKUE_ROWS_OFFSET = 10;
	final int BRE_ROWS_OFFSET = 4;
	double[][] readedArray;

	public void copyAskueToBreData() throws FileNotFoundException, IOException, checkAskueFileException {
		readedArray = copyAskueData();
		writeToBre();
		System.out.println("Askue to bre copy completed");
	}

	private double[][] copyAskueData() throws checkAskueFileException{
		int rowsToRead = AppData.currentHour - AppData.firstHour + 1;
		double[][] resultArray = new double[rowsToRead][30];
		try (FileInputStream fis = new FileInputStream(AppData.askuePath + AppData.askueFileName);
			Workbook wb = new XSSFWorkbook(fis)) {
			System.out.println("Start askue check"); // Выбросит исключение, если количество рядов в файле больше стандартных 38
			int lastRow = wb.getSheetAt(0).getLastRowNum();
			System.out.println(lastRow);
			if (lastRow > 38) {
				wb.close();
				System.out.println("Askue does not match!");
				throw new checkAskueFileException("Askue file does not match");
			}
			System.out.println("Start askue reading");
			for (int i = 0; i < rowsToRead; i++) {
				for (int j = 0; j < 30; j++) {
					resultArray[i][j] = wb.getSheetAt(0).getRow(i + ASKUE_ROWS_OFFSET + AppData.firstHour - 1)
							.getCell(cellNumbersToReadSBRE[j]).getNumericCellValue();
					System.out.println("Readed value " + resultArray[i][j]);
				}
				System.out.println("\n");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultArray;
	}

	private void writeToBre() throws FileNotFoundException, IOException {
		System.out.println("Started write data to DailyBRE");
		int rowsToWrite = AppData.currentHour - AppData.firstHour + 1;
		int firstRow = BRE_ROWS_OFFSET + ((AppData.day - 1) * 24) + AppData.firstHour - 1;
		Workbook wb = null;
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(AppData.dailyBrePath + AppData.dailyBreFileName);
			wb = new XSSFWorkbook(fis);
			for (int i = 0; i < rowsToWrite; i++) {
				for (int j = 0; j < 28; j++) {
					wb.getSheetAt(0).getRow(firstRow + i).getCell(j + 11).setCellValue(readedArray[i][j]);
				}
				wb.getSheetAt(0).getRow(firstRow + i).getCell(5).setCellValue(readedArray[i][28]);
				wb.getSheetAt(0).getRow(firstRow + i).getCell(6).setCellValue(readedArray[i][29]);
				wb.getSheetAt(0).setForceFormulaRecalculation(true);
			}
			fis.close();
		} catch (IOException e) {
			fis.close();
			wb.close();
			e.printStackTrace();
		}
		FileOutputStream fos = new FileOutputStream(AppData.dailyBrePath + AppData.dailyBreFileName);
		wb.write(fos);
		wb.close();
		fos.close();

	}

	public boolean compareDateAskue() throws IllegalArgumentException, IOException {
		System.out.println("Entering compare date");
		FileInputStream fis = new FileInputStream(AppData.askuePath + AppData.askueFileName);
		Workbook wbToRead = new XSSFWorkbook(fis);
		DateFormat df = new SimpleDateFormat("DD/MMMM/YY");
		@SuppressWarnings("deprecation")
		Date dateToday = new Date((AppData.year - 1900), AppData.month - 1, AppData.day);

		Date date = wbToRead.getSheetAt(0).getRow(0).getCell(5).getDateCellValue();
		System.out.println(dateToday);
		System.out.println(date);

		if (dateToday.compareTo(date) == 0) {

			System.out.println("Дата соответствует");
			fis.close();
			return true;
		} else {
			System.out.println(
					"\n*** ОШИБКА:\n*** Введённая дата не совпадает с датой в файле \"РасходПоОбъектам1.xlsx\"\n");
			fis.close();
			return false;
		}
	}

}
