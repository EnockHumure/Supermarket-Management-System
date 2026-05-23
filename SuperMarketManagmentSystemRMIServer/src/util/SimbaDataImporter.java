package util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Simple utility to import Simba products data into our system
 * This reads the JSON file and creates SQL INSERT statements
 */
public class SimbaDataImporter {
    
    public static void main(String[] args) {
        String jsonFilePath = "C:\\Users\\humur\\Documents\\NetBeansProjects\\SuperMarketManagementSystem\\simba_products.json";
        String outputSqlFile = "C:\\Users\\humur\\Documents\\NetBeansProjects\\SuperMarketManagementSystem\\import_simba_products.sql";


        try {
            System.out.println("Starting import process...");
            
            // Step 1: Read JSON file
            JSONParser parser = new JSONParser();
            JSONObject jsonData = (JSONObject) parser.parse(new FileReader(jsonFilePath));
            JSONArray products = (JSONArray) jsonData.get("products");
            
            System.out.println("Found " + products.size() + " products in JSON file");
            
            // Step 2: Extract unique categories
            Set<String> uniqueCategories = new HashSet<>();
            for (Object obj : products) {
                JSONObject product = (JSONObject) obj;
                String category = (String) product.get("category");
                uniqueCategories.add(category);
            }
            
            System.out.println("Found " + uniqueCategories.size() + " unique categories");
            
            // Step 3: Generate SQL statements
            StringBuilder sql = new StringBuilder();
            sql.append("-- Simba Products Import SQL Script\n");
            sql.append("-- Generated on: ").append(new Date()).append("\n\n");
            
            // Insert categories first (let database auto-generate category_id)
            sql.append("-- Step 1: Insert Categories\n");
            Map<String, Integer> categoryMap = new HashMap<>();
            int categoryCounter = 1;
            
            for (String categoryName : uniqueCategories) {
                sql.append("INSERT INTO category (name, description) VALUES (");
                sql.append("'").append(categoryName.replace("'", "''")).append("', '");
                sql.append("Imported from Simba Supermarket');\n");
                
                // Map category name to expected ID (will be auto-generated sequentially)
                categoryMap.put(categoryName, categoryCounter);
                categoryCounter++;
            }
            
            sql.append("\n-- Step 2: Insert Products\n");
            
            // Insert products with unique temporary barcodes
            int tempCounter = 1;
            for (Object obj : products) {
                JSONObject product = (JSONObject) obj;
                
                String name = (String) product.get("name");
                Double priceDouble = (Double) product.get("price");
                String category = (String) product.get("category");
                
                // Convert price to BigDecimal
                BigDecimal price = BigDecimal.valueOf(priceDouble);
                
                // Set default stock quantity (random between 10-100)
                int stockQuantity = 10 + (int)(Math.random() * 90);
                
                // Set reorder level (20% of stock quantity)
                int reorderLevel = (int)(stockQuantity * 0.2);
                
                // Get category ID
                int catId = categoryMap.get(category);
                
                // Escape single quotes in product name
                String safeName = name.replace("'", "''");
                
                // Insert with unique temporary barcode (TEMP1, TEMP2, TEMP3...)
                sql.append("INSERT INTO product (name, barcode, price, stock_quantity, reorder_level, category_id) VALUES (");
                sql.append("'").append(safeName).append("', ");
                sql.append("'TEMP").append(tempCounter).append("', "); // Unique temp barcode
                sql.append(price).append(", ");
                sql.append(stockQuantity).append(", ");
                sql.append(reorderLevel).append(", ");
                sql.append(catId).append(");\n");
                
                tempCounter++;
            }
            
            // Step 3: Update all barcodes to SIM + (product_id + 99)
            // This makes first product SIM100, second SIM101, etc.
            sql.append("\n-- Step 3: Update barcodes to SIM format (SIM100, SIM101, etc.)\n");
            sql.append("UPDATE product SET barcode = 'SIM' || CAST((product_id + 99) AS TEXT) WHERE barcode LIKE 'TEMP%';\n");
            
            // Step 4: Write to SQL file
            FileWriter writer = new FileWriter(outputSqlFile);
            writer.write(sql.toString());
            writer.close();
            
            System.out.println("\n✅ SUCCESS!");
            System.out.println("SQL file created at: " + outputSqlFile);
            System.out.println("\nNext steps:");
            System.out.println("1. Open pgAdmin or your PostgreSQL client");
            System.out.println("2. Connect to your 'supermarket_db' database");
            System.out.println("3. Run the SQL file: " + outputSqlFile);
            System.out.println("4. Refresh your application to see the new products!");
            
        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
