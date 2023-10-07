package excelone;

import com.raven.datechooser.DateChooser;

public class AppData {
	static int day;
	static int month;
	static int year;
	static String[] nssList = new String[] { "Корниенко В.А.", "Малетин А.И.", "Симон Ф.И.", "Состравчук А.С.", "Павлов А.С." };
	static String[] hoursArray = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"};
	static String pathBreMonthly = "\\\\172.16.16.16\\коммерческий отдел\\ОКТЯБРЬ БРЭ ежедневный.xlsx";
	static String kegocTemplate = "C:\\Users\\commercial\\Documents\\MyFiles\\excelpoint\\KEGOC_template.xlsx";
	static String kegocPath = "C:\\Users\\commercial\\Desktop\\Суточная ведомость\\";
	static String dailyStatsPath = "\\\\172.16.16.16\\коммерческий отдел\\";
	static String dailyFileName = "Суточная выработка ОКТЯБРЬ.xlsx";
	static String askuePath = "C:\\Users\\commercial\\Documents\\";
	static String askueFileName = "РасходПоОбъектам1.xlsx";
	static String dailyBrePath =  "\\\\172.16.16.16\\коммерческий отдел\\";
	static String dailyBreFileName = "ОКТЯБРЬ БРЭ ежедневный.xlsx";
	static final String FILE_EXTENSION = ".xlsx";
	static final int ROWS_OFFSET = 2;
	static final int CELLS_OFFSET = 3;
	static final int BRE_OFFSET = 4;
	static int firstHour = 1;
	static int currentHour = 1;
	static int lastHour = 24;
	static String startNssString = "Корниенко В.А.";
	static String middleNssString = "Корниенко В.А.";
	static String endNssString = "Корниенко В.А.";
	static String fileFilterText = "Excel (.xlsx)";
	
	
	static void printTimeLimits() {
		System.out.println("Time limits is " + firstHour + " - " + currentHour);
	}
	
	static void setCurrentDate(DateChooser chDate) {
		day = chDate.getSelectedDate().getDate();
		month = chDate.getSelectedDate().getMonth() + 1;
		year = chDate.getSelectedDate().getYear() + 1900;
		System.out.println("Date set to " + day + " / " + month + " / " + year);
	}
	static String getActualDate() {
		return day + " " + getMonthStringNameWithSuffix();
	}
	
	static String getMonthStringName() {
		if (month == 1) return " январь";
		if (month == 2) return " февраль";
		if (month == 3) return " март";
		if (month == 4) return " апрель";
		if (month == 5) return " май";
		if (month == 6) return " июнь";
		if (month == 7) return " июль";
		if (month == 8) return " август";
		if (month == 9) return " сентябрь";
		if (month == 10) return " октябрь";
		if (month == 11) return " ноябрь";
		if (month == 12) return " декабрь";
		return " месяц";
	}
	
	static String getMonthStringNameWithSuffix() {
		if (month == 1) return " января";
		if (month == 2) return " февраля";
		if (month == 3) return " марта";
		if (month == 4) return " апреля";
		if (month == 5) return " мая";
		if (month == 6) return " июня";
		if (month == 7) return " июля";
		if (month == 8) return " августа";
		if (month == 9) return " сентября";
		if (month == 10) return " октября";
		if (month == 11) return " ноября";
		if (month == 12) return " декабря";
		return " месяц";
	}
}
