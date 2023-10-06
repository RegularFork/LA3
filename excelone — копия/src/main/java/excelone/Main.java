package excelone;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class Main {
	public static void main(String[] args){

		ExcelService service = new ExcelService();

		try {
			service.chooseMode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("\n\n===========================\n" + 
						       "= Готово. Вы великолепны! =\n" +
						       "===========================");
	}

}
