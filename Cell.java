// a single cell in the spreadsheet
public class Cell {
    private String content; // Cell: can be text or numeric
    private Double evaluatedValue; // Calculated value of the cell (if it's a formula
    public Cell() {
        this.content = ""; // empty cell
    }

    //set the cell content
    public void setContent(String content) {
        this.content = content;
    }

    //get the cell content
    public String getContent() {
        return this.content;
    }
    // Set the evaluated value
    public void setEvaluatedValue(Double value) {
        this.evaluatedValue = value;
    }
    // Get the evaluated value
    public Double getEvaluatedValue() {
        return this.evaluatedValue;
    }


    // Returns the cell a string
    @Override
    public String toString() {
        return content;
    }
}
