package excelone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class Service {
	private static String excelFilePath = "\\\\172.16.16.16\\коммерческий отдел\\БРЭ для KEGOC Bassel 23 сентября.xlsx";
	private static String pathNew = "C:\\Users\\commercial\\Desktop\\Суточная ведомость\\testNew.xlsx";
	
	
	public static void setCell() {
		try {
            FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
            Workbook workbook = WorkbookFactory.create(inputStream);
 
            Sheet sheet = workbook.getSheetAt(0);
 
            sheet.getRow(7).getCell(7).setCellValue("WOOOF777");
 
            inputStream.close();
 
            FileOutputStream outputStream = new FileOutputStream(excelFilePath);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
             
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}
}
