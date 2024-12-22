import java.io.*;
import java.util.*;

// Class representing a Spreadsheet
public class Spreadsheet {
    private final Map<String, Cell> cells = new HashMap<>(); // cells stored by coordinates
    private final Set<String> visitedCells = new HashSet<>(); // For circular reference detection
    private final Map<String, Set<String>> dependencies = new HashMap<>(); // Track cell dependencies

    // Show spreadsheet in table form
    public void showSpreadsheetInTableForm(int maxColumns, int maxRows) {
        int cellWidth = 15;
        System.out.print("   |"); // Space for row numbers
        for (char col = 'A'; col < 'A' + maxColumns; col++) {
            System.out.printf("%-" + cellWidth + "s|", col);  // Print column headers
        }
        System.out.println();

        for (int i = 0; i < maxColumns; i++) {
            System.out.print("-".repeat(cellWidth) + "+"); // separator lines
        }
        System.out.println();

        for (int row = 1; row <= maxRows; row++) {
            System.out.printf("%3d| ", row); // Print row number
            for (char col = 'A'; col < 'A' + maxColumns; col++) {
                String cellContent = getCellContent(String.valueOf(col), row);
                if (cellContent == null || cellContent.isEmpty()) {
                    System.out.printf("%-" + cellWidth + "s|", "0"); // Empty cell as 0
                } else {
                    System.out.printf("%-" + cellWidth + "s|", cellContent); // Print cell content
                }
            }
            System.out.println(); // Move to the next row

            for (int i = 0; i < maxColumns; i++) {
                System.out.print("-".repeat(cellWidth) + "+"); //separator line
            }
            System.out.println();
        }
    }

    // Create a new Spreadsheet
    public Spreadsheet() {
    }

    // Generate a key for cell coordinates (e.g., "A1")
    private String createKey(String column, int row) {
        return column + row;
    }

    // Set the cell content at a specific coordinate
    public void setCellContent(String column, int row, String content) {
        String key = createKey(column, row);
        Cell cell = cells.getOrDefault(key, new Cell());
        cell.setContent(content);

        dependencies.remove(key);

        // If content is a formula, find dependencies
        if (content.startsWith("=")) {
            String[] tokens = content.substring(1).split("(?=[+*/()-])|(?<=[+*/()-])");
            for (String token : tokens) {
                if (token.matches("[A-Z]+\\d+")) {
                    addDependency(key, token);
                }
            }
        }

        // Save the cell and update dependencies
        cells.put(key, cell);

        try {
            updateDependentCells(key);
        } catch (IllegalArgumentException e) {
            System.out.println("Circular reference detected while updating: " + key);
            throw e;
        }
    }

    // Add a dependency between cells
    private void addDependency(String dependent, String dependency) {
        dependencies.putIfAbsent(dependency, new HashSet<>());
        dependencies.get(dependency).add(dependent);
    }

    // Update dependent cells when a cell changes
    private void updateDependentCells(String cellKey) {
        if (!dependencies.containsKey(cellKey)) return;

        for (String dependent : dependencies.get(cellKey)) {
            if (visitedCells.contains(dependent)) {
                throw new IllegalArgumentException("Circular reference detected: " + dependent);
            }

            Cell cell = cells.get(dependent);
            if (cell != null && cell.getContent().startsWith("=")) {
                double newValue = evaluateFormula(cell.getContent(), dependent);
                cell.setEvaluatedValue(newValue);
                updateDependentCells(dependent);
            }
        }
    }

    // Get the cell content at a specific coordinate
    public String getCellContent(String column, int row) {
        String key = createKey(column, row);
        Cell cell = cells.get(key);
        if (cell == null || cell.getContent().isEmpty()) {
            return "0"; // Empty cells default to 0
        }
        if (cell.getEvaluatedValue() != null) {
            return String.format("%.1f", cell.getEvaluatedValue());
        }
        return cell.getContent();
    }

    // Parse ranges with multiple parts (e.g., A1:B2,10)
    private List<Double> parseMultipleRanges(String ranges) {
        List<Double> values = new ArrayList<>();
        String[] rangeParts = ranges.split(",");
        for (String range : rangeParts) {
            range = range.trim();
            if (range.matches("\\d+(\\.\\d+)?")) { // Check if it’s a number
                values.add(Double.parseDouble(range));
            } else if (range.matches("[A-Z]+\\d+:[A-Z]+\\d+")) {  // Range A1:B2
                values.addAll(parseRange(range));
            } else {
                throw new IllegalArgumentException("Invalid range: " + range);
            }
        }

        return values;
    }
    // Extract range from formula like MAX(A1:B2)
    private String extractRange(String formula, String functionName) {
        if (!formula.startsWith(functionName + "(") || !formula.endsWith(")")) {
            throw new IllegalArgumentException("Invalid formula: " + formula);
        }
        return formula.substring(functionName.length() + 1, formula.length() - 1).trim();
    }


    // Evaluate a formula
    public double evaluateFormula(String formula, String currentCell) {
        if (visitedCells.contains(currentCell)) {
            //System.out.println("Circular reference detected while visiting: " + currentCell);
            throw new IllegalArgumentException("Circular reference detected: " + currentCell);
        }

        visitedCells.add(currentCell); // Mark cell as visited
        System.out.println("Visiting cell: " + currentCell);
     try {
        if (formula == null || formula.isEmpty()) {
            throw new IllegalArgumentException("The cell does not exist or is empty.");
        }

         // If not a formula, try parsing as a number
        if (!formula.startsWith("=")) {
            try {
                return Double.parseDouble(formula); // If not a formula, return the numeric value
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid value: " + formula);
            }
        }
        formula = formula.substring(1);

        // Function (MAX, MIN, SUM, AVERAGE)
        if (formula.startsWith("MAX(")) {
            String range = extractRange(formula, "MAX");
            List<Double> rangeValues = parseMultipleRanges(range);
            return rangeValues.stream().max(Double::compare).orElseThrow(() -> new IllegalArgumentException("Empty range."));
        } else if (formula.startsWith("MIN(")) {
            String range = extractRange(formula, "MIN");
            List<Double> rangeValues = parseMultipleRanges(range);
            return rangeValues.stream().min(Double::compare).orElseThrow(() -> new IllegalArgumentException("Empty range."));
        } else if (formula.startsWith("SUM(")) {
            String range = extractRange(formula, "SUM");
            List<Double> rangeValues = parseMultipleRanges(range);
            return rangeValues.stream().mapToDouble(Double::doubleValue).sum();
        } else if (formula.startsWith("AVERAGE(")) {
            String range = extractRange(formula, "AVERAGE");
            List<Double> rangeValues = parseMultipleRanges(range);
            return rangeValues.stream().mapToDouble(Double::doubleValue).average().orElseThrow(() -> new IllegalArgumentException("Empty range."));
        }

        //basic operators (+, -, *, /)
        String[] tokens = formula.split("(?<=[-+*/])|(?=[-+*/])");
        Stack<Double> values = new Stack<>();
        Stack<String> operators = new Stack<>();

        for (String token : tokens) {
            if (token.matches("\\d+")) { //number
                values.push(Double.parseDouble(token));
            } else if (token.matches("[+\\-*/]")) { //operator
                while (!operators.isEmpty() && precedence(token) <= precedence(operators.peek())) {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(token);
            } else if (token.matches("[A-Z]+\\d+")) { //reference
                String refColumn = token.replaceAll("\\d", "");
                int refRow = Integer.parseInt(token.replaceAll("\\D", ""));
                String cellContent = getCellContent(refColumn, refRow);
                values.push(evaluateFormula(cellContent, refColumn + refRow));
            } else {
                throw new IllegalArgumentException("Invalid token in formula: " + token);
            }
        }

        while (!operators.isEmpty()) {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
        }
        return values.pop();
    } finally {
         visitedCells.remove(currentCell); //clear visited mark
         System.out.println("Finished visiting cell: " + currentCell);
    }
}

    // Parse a range of cells like a1:b1
    private List<Double> parseRange(String range) {
        List<Double> values = new ArrayList<>();
        String[] parts = range.split(":");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid range: " + range);
        }

        String start = parts[0];
        String end = parts[1];

        String startColumn = start.replaceAll("\\d", ""); //colum letter
        int startRow = Integer.parseInt(start.replaceAll("\\D", "")); //row number
        String endColumn = end.replaceAll("\\d", "");
        int endRow = Integer.parseInt(end.replaceAll("\\D", ""));

        for (char col = startColumn.charAt(0); col <= endColumn.charAt(0); col++) {
            for (int row = startRow; row <= endRow; row++) {
                String cellKey = String.valueOf(col) + row;
                String content = getCellContent(String.valueOf(col), row);

                if (visitedCells.contains(cellKey)) {
                    throw new IllegalArgumentException("Circular reference detected: " + cellKey);
                }

                if (content != null && !content.isEmpty()) {
                    double evaluated = evaluateFormula(content, cellKey);
                    System.out.println("Evaluated content for cell " + cellKey + ": " + evaluated);
                    values.add(evaluated);
                }
            }
        }
        return values;
    }

    // Operator precedence
    private int precedence(String operator) {
        return switch (operator) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            default -> -1;
        };
    }

    // Apply an operator to two values
    private double applyOperator(String operator, double b, double a) {
        return switch (operator) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> a / b;
            default -> 0;
        };
    }
    public void showSpreadsheet() {
        for (String key : cells.keySet()) {
            System.out.println("Coordinates: " + key + " | Content: " + cells.get(key));
        }
    }


    // Save the spreadsheet to a file
    public void saveSpreadsheet(String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (String key : cells.keySet()) {
            Cell cell = cells.get(key);
            writer.write(key + ";" + cell.getContent());
            writer.newLine();
        }
        writer.close();
    }

    // Load the spreadsheet from a file
    public void loadSpreadsheet(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            // Skip empty lines or invalid lines
            if (line.trim().isEmpty() || !line.contains(";")) {

                System.out.println("Invalid line format: " + line);
                continue;
            }

            int separatorIndex = line.indexOf(';');
            if (separatorIndex == -1) {
                System.out.println("Invalid line format: " + line);
                continue;
            }
            String key = line.substring(0, separatorIndex).trim();
            String content = line.substring(separatorIndex + 1).trim();

            if (!key.matches("[A-Z]+\\d+")) { // "D1"
                System.out.println("Invalid cell reference: " + key);
                continue;
            }
            // Extract column and row from the key
            String column = key.replaceAll("\\d", ""); //column
            String rowString = key.replaceAll("\\D", ""); // row
            if (column.isEmpty() || rowString.isEmpty()) {
                System.out.println("Invalid cell reference in line: " + line);
                continue;
            }

            int row = Integer.parseInt(rowString);

            // Set the content in the spreadsheet
            setCellContent(column, row, content);
        }
        reader.close();
    }
}

