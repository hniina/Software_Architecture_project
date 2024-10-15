import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        Spreadsheet spreadsheet = new Spreadsheet(); //create new spreadsheet
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        String column;
        int row= 0;
        boolean validRow;

        while (!exit) {
            System.out.println("Select action");
            System.out.println("1.Set Cell Content:");
            System.out.println("2.View Cell Content:");
            System.out.println("3.Show Spreadsheet");
            System.out.println("4.Exit");
            System.out.println("5.Save Spreadsheet");
            System.out.println("6.Load Spreadsheet");

            int choice = 0;
            boolean validInput = false;

            // Error handling for invalid menu selection
            while (!validInput) {
                try {
                    choice = scanner.nextInt();
                    validInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.next(); // clear invalid input
                }
            }

            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Column, e.g. A): ");
                    column = scanner.nextLine().toUpperCase();
                    System.out.println("Row, e.g. 1): ");

                    validRow = false;
                    while (!validRow) {
                        try {
                            row = scanner.nextInt();
                            validRow = true;
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a valid row number.");
                            scanner.next(); // clear invalid input
                        }
                    }
                    scanner.nextLine(); // clear newline
                    System.out.println("Set cell content: ");
                    String content = scanner.nextLine();
                    spreadsheet.setCellContent(column, row, content);
                    break;

                case 2:
                    System.out.println("Column, e.g. A): ");
                    column = scanner.nextLine().toUpperCase();
                    System.out.println("Row, e.g. 1): ");

                    validRow = false;
                    while (!validRow) {
                        try {
                            row = scanner.nextInt();
                            validRow = true;
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a valid row number.");
                            scanner.next(); // clear invalid input
                        }
                    }
                    scanner.nextLine(); // clear newline
                    System.out.println("The cell content, e.g = A1+B2: " + spreadsheet.getCellContent(column, row));
                    break;

                case 3:
                    spreadsheet.showSpreadsheet();
                    break;

                case 4:
                    exit = true;
                    break;

                case 5:
                    System.out.println("Enter filename to save: ");
                    String saveFile = scanner.nextLine();
                    try {
                        spreadsheet.saveSpreadsheet(saveFile);
                    } catch (IOException e) {
                        System.out.println("Error saving file: " + e.getMessage());
                    }
                    break;

                case 6:
                    System.out.println("Enter filename to load: ");
                    String loadFile = scanner.nextLine();
                    try {
                        spreadsheet.loadSpreadsheet(loadFile);
                    } catch (IOException e) {
                        System.out.println("Error loading file: " + e.getMessage());
                    }
                    break;

                default:
                    System.out.println("Invalid choice");
                    break;
            }
        }
        scanner.close();
    }
}
