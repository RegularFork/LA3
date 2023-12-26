package excelone;
import java.io.IOException;


public class Main2 {
	public static void main(String[] args) throws IOException{
		
		System.out.println("====================================================\n"
				+ "= Добро пожаловать в программу \"Ленивая Жопа V1.3\" =\n"
				+ "====================================================\n");
		
            ExcelService service = new ExcelService();

		while(true) {
			try {
				service.chooseMode();
			} catch (IllegalArgumentException e) {
				System.out.println("Всё сломалось!");
			} catch (IOException e) {
				System.out.println("Всё пошло не так!");;
			}

			if (service.isSuccess) {
				service.printEndMessageSuccess();
			} else {
				service.printEndMessageFail();
			}
			
			System.out.println("\n\nЧто-нибудь ещё?\n\n\n");
			System.out.println();
		}
		
	}

	public Main2() {
		super();
		// TODO Auto-generated constructor stub
	}

}
