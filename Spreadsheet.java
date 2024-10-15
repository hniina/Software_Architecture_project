import java.io.*;
import java.util.HashMap;
import java.util.Map;

// here cells are stored
public class Spreadsheet {
    private final Map<String, Cell> cells; // Cells are stored by their coordinates

    public Spreadsheet() {
        this.cells = new HashMap<>(); //empty
    }

    // Creates a key for cell coordinates ("A1", "B2")
    private String createKey(String column, int row) {
        return column + row;
    }

    // Set the cell content at a specific coordinate
    public void setCellContent(String column, int row, String content) {
        String key = createKey(column, row); // create key for the cell
        Cell cell = cells.getOrDefault(key, new Cell()); // Get the cell or create a new one (if cell don't create already)
        cell.setContent(content);
        cells.put(key, cell);
    }

    // Show the cell content at a specific coordinate
    public String getCellContent(String column, int row) {
        String key = createKey(column, row); // create key for the cell
        Cell cell = cells.get(key); // get the cell

        if (cell == null) {
            return "the cell doesn't exist,"; }
        return cell.getContent();
    }

    // Shows all cells that have been set in the spreadsheet
    public void showSpreadsheet() {
        for (String key : cells.keySet()) {
            System.out.println("Coordinates: " + key + " | Content: " + cells.get(key));
        }
    }

    public void saveSpreadsheet(String spreadsheet_file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(spreadsheet_file));
        for (String key : cells.keySet()) {
            Cell cell = cells.get(key);
            writer.write(key + ";" + cell.getContent());
            writer.newLine();
        }
        writer.close();
    }
    public void loadSpreadsheet(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(";");
            String key = parts[0]; //coordinate like "A1"
            String content = parts[1];
            String column = key.substring(0, 1); // first character
            int row = Integer.parseInt(key.substring(1));
            setCellContent(column, row, content);
        }
        reader.close();
    }

}
