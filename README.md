This project implements the core components of a basic spreadsheet, allowing users to set and view cell content via a textual interface.

Repository Includes:

- Use cases and domain model.
- Domain model implementation in Java.
- Key files: Spreadsheet.java, Main.java, Cell.java, and SpreadsheetTest.java (JUnit).

Features:
- Set and Edit Cell Content: Input text, numbers, or formulas (e.g., =A1+10).
- View Cell Content: Display raw or evaluated values.
- Formula Evaluation: Supports operations (+, -, *, /) and functions (MAX, MIN, SUM, AVERAGE).
- Range Support: Evaluate ranges in formulas (e.g., =SUM(A1:A5)).
- Circular Reference Detection: Prevents invalid calculations.
- File Operations: Save and load spreadsheet data.
- Table View: Display spreadsheet in a formatted table.

Menu Options:
1. Set Cell Content: Modify a cell's content.
2. View Cell Content: Show raw or evaluated values.
3. Show Spreadsheet: List all cells with their values.
4. Exit Program: Close the application.
5. Save Spreadsheet: Save data to a file.
6. Load Spreadsheet: Restore data from a file.
7. Evaluate Cell Value: Evaluate the formula in a cell.
8. Show Spreadsheet in Table Form: View the spreadsheet in a tabular format.
   
