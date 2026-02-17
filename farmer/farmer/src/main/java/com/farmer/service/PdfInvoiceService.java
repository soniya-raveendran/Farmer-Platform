package com.farmer.service;

import com.farmer.entity.Order;
import com.farmer.entity.OrderItem;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfInvoiceService {

    public File generateInvoice(Order order) {
        try {
            File pdfFile = File.createTempFile("invoice_" + order.getId(), ".pdf");
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

            document.open();

            // Font styles
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.GREEN);
            Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            // Brand Header
            Paragraph brand = new Paragraph("ROOTS - AGRI TRADE", headerFont);
            brand.setAlignment(Element.ALIGN_CENTER);
            document.add(brand);
            document.add(new Paragraph("Connecting Farmers & Retailers",
                    FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY)));
            document.add(new Paragraph("\n"));

            // Invoice Info
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);

            infoTable.addCell(getNoBorderCell(
                    "Bill To:\n" + order.getRetailer().getName() + "\n" + order.getRetailer().getEmail(), normalFont));
            infoTable.addCell(getNoBorderCell(
                    "Invoice ID: #" + order.getId() + "\nDate: "
                            + order.getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "\nStatus: PAID",
                    normalFont));

            document.add(infoTable);
            document.add(new Paragraph("\n"));

            // Items Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 4, 1, 2, 2 });

            table.addCell(new PdfPCell(new Phrase("Product", subHeaderFont)));
            table.addCell(new PdfPCell(new Phrase("Qty", subHeaderFont)));
            table.addCell(new PdfPCell(new Phrase("Price (per kg)", subHeaderFont)));
            table.addCell(new PdfPCell(new Phrase("Total", subHeaderFont)));

            for (OrderItem item : order.getItems()) {
                table.addCell(new Phrase(item.getProduct().getName(), normalFont));
                table.addCell(new Phrase(String.valueOf(item.getQuantity()), normalFont));
                table.addCell(new Phrase("₹" + item.getPrice(), normalFont));
                table.addCell(new Phrase("₹" + (item.getPrice() * item.getQuantity()), normalFont));
            }

            document.add(table);
            document.add(new Paragraph("\n"));

            // Grand Total
            Paragraph total = new Paragraph("Grand Total: ₹" + order.getTotalAmount(), subHeaderFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.add(new Paragraph("\n\nThank you for choosing ROOTS!", normalFont));

            document.close();
            return pdfFile;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private PdfPCell getNoBorderCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }
}
