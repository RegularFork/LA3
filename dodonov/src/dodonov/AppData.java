package dodonov;

import com.raven.datechooser.DateChooser;

public class AppData {
	static int day;
	static int month;
	static int year;
	static String pathBreMonthly = "\\\\172.16.16.16\\коммерческий отдел\\ОКТЯБРЬ БРЭ ежедневный backup.xlsx";
	static int firstHour = 1;
	static int lastHour = 24;
	static final int BRE_OFFSET = 4;
	
	static void setCurrentDate(DateChooser chDate) {
		day = chDate.getSelectedDate().getDate();
		month = chDate.getSelectedDate().getMonth() + 1;
		year = chDate.getSelectedDate().getYear() + 1900;
		System.out.println("Date set to " + day + " / " + month + " / " + year);
	}
}
