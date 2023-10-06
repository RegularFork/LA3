package excelone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

class XLSXService {
	
	void openXLSX() {
		try {
			FileInputStream fis = new FileInputStream("\\\\172.16.16.16\\коммерческий отдел\\ОКТЯБРЬ БРЭ ежедневный backup.xlsx");
			System.out.println("File Opened");
			try {
				Workbook wb = new XSSFWorkbook(fis);
				double d = wb.getSheetAt(0).getRow(25).getCell(6).getNumericCellValue();
				System.out.println(d);
				wb.close();
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public double[][] getColumnsArrayFromBre(int ... columns) throws IOException{
		double[][] result = new double[AppData.lastHour - AppData.firstHour + 1][columns.length];
		
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
				if (result[i][j] == -7) result[i][j] = 0;
			}
		}
		fis.close();
		wb.close();
		return result;
	}

}
