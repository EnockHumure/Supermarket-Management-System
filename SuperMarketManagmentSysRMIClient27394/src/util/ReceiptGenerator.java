package util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import model.Sale;
import model.SaleItem;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

public class ReceiptGenerator {
    
    private static final String RECEIPT_FOLDER = "C:/Users/humur/Documents/NetBeansProjects/SuperMarketManagementSystem/reports/recipt/";
    
    public static String generateReceipt(Sale sale, List<SaleItem> saleItems, String customerName) {
        try {
            java.io.File folder = new java.io.File(RECEIPT_FOLDER);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String filename = RECEIPT_FOLDER + "receipt_" + sale.getSaleId() + "_" + sdf.format(sale.getSaleDate()) + ".pdf";
            
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();
            
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);
            
            Paragraph title = new Paragraph("SUPERMARKET RECEIPT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            
            Paragraph storeInfo = new Paragraph("Supermarket Management System\nThank you for shopping with us!", normalFont);
            storeInfo.setAlignment(Element.ALIGN_CENTER);
            document.add(storeInfo);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("________________________________________________________________________________", normalFont));
            document.add(new Paragraph(" "));
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            document.add(new Paragraph("Receipt #: " + sale.getSaleId(), normalFont));
            document.add(new Paragraph("Date: " + dateFormat.format(sale.getSaleDate()), normalFont));
            document.add(new Paragraph("Cashier: " + sale.getCashier().getFullName(), normalFont));
            if (customerName != null && !customerName.trim().isEmpty()) {
                document.add(new Paragraph("Customer: " + customerName, normalFont));
            }
            document.add(new Paragraph("Payment Method: " + sale.getPaymentMethod(), normalFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("________________________________________________________________________________", normalFont));
            document.add(new Paragraph(" "));
            
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 1, 1, 1.5f});
            
            PdfPCell cell;
            cell = new PdfPCell(new Phrase("Item", headerFont));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase("Price", headerFont));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase("Qty", headerFont));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase("Subtotal", headerFont));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
            
            for (SaleItem item : saleItems) {
                table.addCell(new Phrase(item.getProduct().getName(), normalFont));
                
                cell = new PdfPCell(new Phrase(String.format("%.0f FRW", item.getUnitPrice()), normalFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);
                
                cell = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), normalFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                
                cell = new PdfPCell(new Phrase(String.format("%.0f FRW", item.getSubTotal()), normalFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);
            }
            
            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("________________________________________________________________________________", normalFont));
            
            Paragraph total = new Paragraph("TOTAL: " + String.format("%.0f FRW", sale.getTotalAmount()), titleFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);
            
            document.add(new Paragraph("________________________________________________________________________________", normalFont));
            document.add(new Paragraph(" "));
            
            Paragraph footer = new Paragraph("Thank you for your purchase!\nPlease come again!", normalFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            
            document.close();
            
            System.out.println("✓ Receipt generated: " + filename);
            return filename;
            
        } catch (Exception ex) {
            System.err.println("Error generating receipt: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }
}
