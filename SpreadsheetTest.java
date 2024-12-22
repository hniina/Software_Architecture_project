import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SpreadsheetTest {

    //Basic setup

        @Test
        void testSetAndGetCellContent() {
            Spreadsheet spreadsheet = new Spreadsheet();

            // Set text content
            spreadsheet.setCellContent("A", 1, "Hello");
            assertEquals("Hello", spreadsheet.getCellContent("A", 1));

            // Set numeric content
            spreadsheet.setCellContent("B", 1, "42");
            assertEquals("42", spreadsheet.getCellContent("B", 1));

            // Set formula content
            spreadsheet.setCellContent("C", 1, "=A1+B1");
            assertEquals("=A1+B1", spreadsheet.getCellContent("C", 1));
        }


    //Testing Formulas
    @Test
    void testEvaluateFormula() {
        Spreadsheet spreadsheet = new Spreadsheet();

        // Set cell values
        spreadsheet.setCellContent("A", 1, "10");
        spreadsheet.setCellContent("B", 1, "20");

        // Evaluate simple formulas
        assertEquals(30.0, spreadsheet.evaluateFormula("=A1+B1", "C1"));

        // Test formula with multiplication
        spreadsheet.setCellContent("C", 1, "=A1*B1");
        assertEquals(200.0, spreadsheet.evaluateFormula("=A1*B1", "C1"));
    }

//Testing Functions
@Test
void testFunctions() {
    Spreadsheet spreadsheet = new Spreadsheet();

    // Set cell values
    spreadsheet.setCellContent("A", 1, "10");
    spreadsheet.setCellContent("B", 1, "20");
    spreadsheet.setCellContent("A", 2, "30");
    spreadsheet.setCellContent("B", 2, "40");

    // Test SUM function
    assertEquals(100.0, spreadsheet.evaluateFormula("=SUM(A1:B2)", "C1"));

    // Test MAX function
    assertEquals(40.0, spreadsheet.evaluateFormula("=MAX(A1:B2)", "C1"));

    // Test AVERAGE function
    assertEquals(25.0, spreadsheet.evaluateFormula("=AVERAGE(A1:B2)", "C1"));
}

//Testing Ranges
    @Test
    void testRanges() {
        Spreadsheet spreadsheet = new Spreadsheet();

        // Set values in range
        spreadsheet.setCellContent("A", 1, "5");
        spreadsheet.setCellContent("A", 2, "15");
        spreadsheet.setCellContent("B", 1, "10");
        spreadsheet.setCellContent("B", 2, "20");

        // Test a range formula
        assertEquals(50.0, spreadsheet.evaluateFormula("=SUM(A1:B2)", "C1"));
    }

//Testing Circular References
@Test
void testCircularReference() {
    Spreadsheet spreadsheet = new Spreadsheet();

    spreadsheet.setCellContent("A", 1, "=B1");
    spreadsheet.setCellContent("B", 1, "=A1");


    Exception exceptionA1 = assertThrows(IllegalArgumentException.class, () -> {
        spreadsheet.evaluateFormula("=A1", "A1");
    });

    assertTrue(exceptionA1.getMessage().contains("Circular reference detected"));


    Exception exceptionB1 = assertThrows(IllegalArgumentException.class, () -> {
        spreadsheet.evaluateFormula("=B1", "B1");
    });

    assertTrue(exceptionB1.getMessage().contains("Circular reference detected"));
}

}
