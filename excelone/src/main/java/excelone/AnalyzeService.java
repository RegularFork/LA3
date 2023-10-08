package excelone;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class AnalyzeService {
	
	double[][] getColumnsArrayFromBre(int ... columns) throws IOException{
		double[][] result = new double[24][columns.length];
		
		FileInputStream fis = null;
		Workbook wb = null;
		try {
			fis = new FileInputStream(AppData.dailyBrePath + AppData.dailyBreFileName);
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
				if (result[i][j] == -7) result[i][j] = 0;
			}
		}
		fis.close();
		wb.close();
		return result;
	}
	
	void fillAnalyze(double[][] dataArray, int ... columns) throws IOException {
		
		try (FileInputStream fis  = new FileInputStream(AppData.dailyAnalyzePath + AppData.dailyAnalyzeFileName); Workbook wb = new XSSFWorkbook(fis)) {
			Sheet sheet = wb.getSheetAt(0);
			sheet.setForceFormulaRecalculation(true);
			for (int i = 0; i < dataArray.length; i++) {
				for (int j = 0; j < columns.length; j++) {
					int targetRow = (3 + (AppData.day - 1) * 25) + i + AppData.firstHour - 1;
					if (targetRow >= sheet.getLastRowNum() - 1) {
						wb.close();
						fis.close();
						throw new IllegalArgumentException();
					}
				sheet.getRow(targetRow).getCell(columns[j]).setCellValue(dataArray[i][j]);
				}
			}
			fis.close();
			FileOutputStream fos = new FileOutputStream(AppData.dailyAnalyzePath + AppData.dailyAnalyzeFileName);
			wb.write(fos);
			wb.close();
			fos.close();
			System.out.println("Analyze file write success");
		}
		
	}

}
