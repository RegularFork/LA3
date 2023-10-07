package excelone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.GregorianCalendar;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

class XLSXService {
	String sourceFile;
	String targetFile;

	double[][] getColumnsArrayFromBre(int... columns) throws IOException {

		double[][] result = new double[AppData.currentHour - AppData.firstHour + 1][columns.length];

		FileInputStream fis = null;
		Workbook wb = null;
		try {
			fis = new FileInputStream(AppData.pathBreMonthly);
			wb = new XSSFWorkbook(fis);
		} catch (FileNotFoundException e) {
			fis.close();
			wb.close();
			e.printStackTrace();
		}
		for (int i = 0; i < result.length; i++) {
			int targetRow = ((AppData.day - 1) * 24 + AppData.BRE_OFFSET) + AppData.firstHour - 1;
			for (int j = 0; j < columns.length; j++) {
				System.out.println(targetRow);
				System.out.println("row: " + i + " / column: " + j);
				result[i][j] = wb.getSheetAt(0).getRow(targetRow + i).getCell(columns[j]).getNumericCellValue();
				System.out.println("result: " + result[i][j]);
				if (result[i][j] == -7)
					result[i][j] = 0;
			}
		}
		fis.close();
		wb.close();
		return result;
	}

	void writeValuesToDirector(double[][] dataArray, int... columns) throws IOException {

		try (FileInputStream fis = new FileInputStream(
				AppData.dailyStatsPath + AppData.dailyFileName);
				Workbook wb = new XSSFWorkbook(fis)) {
			Sheet sheet = wb.getSheetAt(AppData.day - 1);
			System.out.println("Sheet - " + (AppData.day - 1));
			sheet.setForceFormulaRecalculation(true);
			for (int i = 0; i < dataArray.length; i++) {
				for (int j = 0; j < columns.length; j++) {
					int targetRow = (AppData.ROWS_OFFSET + i);
					if (targetRow > sheet.getLastRowNum()) {
						System.out.println(targetRow);
						System.out.println(sheet.getLastRowNum());
						wb.close();
						fis.close();
						throw new IllegalArgumentException();
					}
					sheet.getRow(targetRow).getCell(columns[j]).setCellValue(dataArray[i][j]);
					System.out.println("Write row " + targetRow);
				}
			}
			System.out.println("Director's file value write success");
			
			sheet.getRow(2).getCell(1).setCellValue(getInitials(AppData.startNssString));
			sheet.getRow(5).getCell(1).setCellValue(AppData.middleNssString);
			sheet.getRow(17).getCell(1).setCellValue(AppData.endNssString);
			
			fis.close();
			FileOutputStream fos = new FileOutputStream(
					AppData.dailyStatsPath + AppData.dailyFileName);
			wb.write(fos);
			wb.close();
			fos.close();
			
			System.out.println("Director's file write success");
		}

	}

	// ======== Создание БРЭ для КЕГОК
	// =====================================================================

	void copyDailyRowsForKegoc() throws FileNotFoundException, IOException {
//			setCurrentDay(); // ВКЛЮЧИТЬ МЕТОД ДЛЯ РАБОТЫ В КОНСОЛИ
		sourceFile = AppData.pathBreMonthly;
		targetFile = AppData.kegocPath + "БРЭ для KEGOC Bassel " + AppData.day + AppData.getMonthStringNameWithSuffix()
				+ ".xlsx";
		System.out.println(targetFile);
		try {
			double[][] dailyRows = readDailyRows();
			writeDailyValues(dailyRows);
		} catch (IllegalArgumentException e) {
			System.out.println("\n*** ОШИБКА:\n" + "*** В этом месяце меньше дней, чем ты думаешь!");
		} catch (FileNotFoundException e) {
			System.out.println("\n*** ОШИБКА:\n*** Файл \"БРЭ для KEGOC Bassel " + AppData.day
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
			int targetRow = ((AppData.day - 1) * 24 + AppData.BRE_OFFSET) + (rows);
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
			fis = new FileInputStream(AppData.kegocTemplate);
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
			sheet.getRow(0).getCell(0).setCellValue(new GregorianCalendar(2023, AppData.month - 1, AppData.day));
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
	}
	
	// === Получить инициалы ===
	String getInitials(String name) {
		if (name.equals("Корниенко В.А.")) return "К.В.";
		if (name.equals("Малетин А.И.")) return "М.А.";
		if (name.equals("Симон Ф.И.")) return "С.И.";
		if (name.equals("Состравчук А.С.")) return "С.А.";
		if (name.equals("Павлов А.С.")) return "П.А.";
		
		return name;
	}
}
