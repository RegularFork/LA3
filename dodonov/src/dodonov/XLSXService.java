package dodonov;

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
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void copyRowsToArray(File source, File destination, int rowsOffset, int cellsOffset, int rowsCount, int... columns)
			throws FileNotFoundException, IOException {
		double[][] values = new double[rowsCount][columns.length];
		values = getCellsValues(source, values, rowsOffset, columns);
	}

	double[][] getCellsValues(File source, double[][] array, int rowsOffset, int... columns)
			throws FileNotFoundException, IOException {
		System.out.println("Entered getCellsValues");
		System.out.println("SourcePath:\n" + source.getAbsolutePath());
		FileInputStream fis = null;
		Workbook wb = null;
		try {
			fis = new FileInputStream(source);
			System.out.println("Opened stream:\n" + source.getAbsolutePath());
			wb = new XSSFWorkbook();
			for (int row = 0; row < array.length; row++) {
				int targetRow = (AppData.BRE_OFFSET + (AppData.day - 1) * 24) + row + AppData.firstHour - 1;
				for (int column = 0; column < array[0].length; column++) {
					array[row][column] = wb.getSheetAt(0).getRow(targetRow).getCell(columns[column])
							.getNumericCellValue();
					System.out.println(array[row][column]);
				}
				System.out.println("===");

			}
		} catch (FileNotFoundException e) {
			fis.close();
			wb.close();
			throw new FileNotFoundException();

		}
		fis.close();
		wb.close();
		return array;
	}
}
