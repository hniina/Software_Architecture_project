// a single cell in the spreadsheet
public class Cell {
    private String content; // Cell: can be text or numeric

    public Cell() {
        this.content = ""; // empty cell
    }

    //set the cell content
    public void setContent(String content) {
        this.content = content;
    }

    // Retrieves the cell content
    public String getContent() {
        return this.content;
    }

    // Returns the cell a string
    @Override
    public String toString() {
        return content;
    }
}
