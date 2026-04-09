package com.SpringBootMVC.ExpensesTracker.controller;

import com.SpringBootMVC.ExpensesTracker.DTO.FilterDTO;
import com.SpringBootMVC.ExpensesTracker.entity.Category;
import com.SpringBootMVC.ExpensesTracker.entity.Client;
import com.SpringBootMVC.ExpensesTracker.entity.Expense;
import com.SpringBootMVC.ExpensesTracker.service.ExpenseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ReportController {
    private static final Logger log = LoggerFactory.getLogger(ReportController.class);
    private static final DateTimeFormatter INPUT_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final ExpenseService expenseService;
    private final ObjectMapper objectMapper;

    public ReportController(ExpenseService expenseService, ObjectMapper objectMapper) {
        this.expenseService = expenseService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/reports/expenses.csv")
    public ResponseEntity<byte[]> exportExpensesCsv(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "from", required = false) Integer from,
            @RequestParam(value = "to", required = false) Integer to,
            @RequestParam(value = "month", required = false) String month,
            @RequestParam(value = "year", required = false) String year,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "datePreset", required = false) String datePreset,
            HttpSession session
    ) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }

        FilterDTO filter = toFilter(category, from, to, month, year, keyword, sortBy, datePreset);
        List<Expense> expenses = expenseService.findFilterResult(client.getId(), filter);
        String csv = buildCsv(expenses);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("expense-report.csv").build());

        log.info("CSV report generated for client {} with {} rows", client.getId(), expenses.size());
        return ResponseEntity.ok().headers(headers).body(csv.getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping("/reports/expenses.json")
    public ResponseEntity<byte[]> exportExpensesJson(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "from", required = false) Integer from,
            @RequestParam(value = "to", required = false) Integer to,
            @RequestParam(value = "month", required = false) String month,
            @RequestParam(value = "year", required = false) String year,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "datePreset", required = false) String datePreset,
            HttpSession session
    ) throws JsonProcessingException {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }

        FilterDTO filter = toFilter(category, from, to, month, year, keyword, sortBy, datePreset);
        List<Expense> expenses = expenseService.findFilterResult(client.getId(), filter);
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(buildJsonRows(expenses));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentDisposition(ContentDisposition.attachment().filename("expense-report.json").build());

        log.info("JSON report generated for client {} with {} rows", client.getId(), expenses.size());
        return ResponseEntity.ok().headers(headers).body(json.getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping("/reports/expenses.pdf")
    public ResponseEntity<byte[]> exportExpensesPdf(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "from", required = false) Integer from,
            @RequestParam(value = "to", required = false) Integer to,
            @RequestParam(value = "month", required = false) String month,
            @RequestParam(value = "year", required = false) String year,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "datePreset", required = false) String datePreset,
            HttpSession session
    ) throws IOException {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }

        FilterDTO filter = toFilter(category, from, to, month, year, keyword, sortBy, datePreset);
        List<Expense> expenses = expenseService.findFilterResult(client.getId(), filter);
        byte[] pdf = buildPdf(expenses);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("expense-report.pdf").build());

        log.info("PDF report generated for client {} with {} rows", client.getId(), expenses.size());
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @GetMapping("/reports/monthly-summary")
    @ResponseBody
    public ResponseEntity<Map<String, Integer>> monthlySummary(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "from", required = false) Integer from,
            @RequestParam(value = "to", required = false) Integer to,
            @RequestParam(value = "month", required = false) String month,
            @RequestParam(value = "year", required = false) String year,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "datePreset", required = false) String datePreset,
            HttpSession session
    ) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }

        FilterDTO filter = toFilter(category, from, to, month, year, keyword, "dateAsc", datePreset);
        List<Expense> expenses = expenseService.findFilterResult(client.getId(), filter);
        Map<String, Integer> summary = new LinkedHashMap<>();

        for (Expense expense : expenses) {
            if (expense.getDateTime() == null || expense.getDateTime().length() < 7) {
                continue;
            }
            String key = expense.getDateTime().substring(0, 7);
            summary.put(key, summary.getOrDefault(key, 0) + expense.getAmount());
        }

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/reports/category-summary")
    @ResponseBody
    public ResponseEntity<Map<String, Integer>> categorySummary(
            @RequestParam(value = "from", required = false) Integer from,
            @RequestParam(value = "to", required = false) Integer to,
            @RequestParam(value = "month", required = false) String month,
            @RequestParam(value = "year", required = false) String year,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "datePreset", required = false) String datePreset,
            HttpSession session
    ) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return ResponseEntity.status(401).build();
        }

        FilterDTO filter = toFilter("all", from, to, month, year, keyword, "amountDesc", datePreset);
        List<Expense> expenses = expenseService.findFilterResult(client.getId(), filter);
        Map<String, Integer> summary = new LinkedHashMap<>();

        for (Expense expense : expenses) {
            String categoryName = expense.getCategory() != null ? expense.getCategory().getName() : "Uncategorized";
            summary.put(categoryName, summary.getOrDefault(categoryName, 0) + expense.getAmount());
        }

        return ResponseEntity.ok(summary);
    }

    private FilterDTO toFilter(String category, Integer from, Integer to, String month, String year,
                               String keyword, String sortBy, String datePreset) {
        FilterDTO filter = new FilterDTO();
        filter.setCategory(defaultIfBlank(category, "all"));
        filter.setMonth(defaultIfBlank(month, "all"));
        filter.setYear(defaultIfBlank(year, "all"));
        filter.setKeyword(defaultIfBlank(keyword, ""));
        filter.setSortBy(defaultIfBlank(sortBy, "dateDesc"));
        filter.setDatePreset(defaultIfBlank(datePreset, "all"));
        filter.setFrom(from != null ? from : 0);
        filter.setTo(to != null ? to : 0);
        return filter;
    }

    private String buildCsv(List<Expense> expenses) {
        StringBuilder sb = new StringBuilder();
        sb.append("id,category,amount,date,time,description\n");
        for (Expense expense : expenses) {
            Map<String, Object> row = expenseToExportRow(expense);

            sb.append(row.get("id")).append(',')
                    .append(csvEscape((String) row.get("category"))).append(',')
                    .append(row.get("amount")).append(',')
                    .append(csvEscape((String) row.get("date"))).append(',')
                    .append(csvEscape((String) row.get("time"))).append(',')
                    .append(csvEscape((String) row.get("description")))
                    .append('\n');
        }
        return sb.toString();
    }

    private byte[] buildPdf(List<Expense> expenses) throws IOException {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            float margin = 40f;
            float lineHeight = 14f;
            float y = page.getMediaBox().getHeight() - margin;
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            y = writePdfLine(contentStream, margin, y, PDType1Font.HELVETICA_BOLD, 14,
                    "Expense Report");
            y = writePdfLine(contentStream, margin, y, PDType1Font.HELVETICA, 10,
                    "Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            y = writePdfLine(contentStream, margin, y, PDType1Font.HELVETICA_BOLD, 10,
                    "ID | Category | Amount | Date | Time | Description");

            for (Expense expense : expenses) {
                if (y <= margin + lineHeight) {
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    y = page.getMediaBox().getHeight() - margin;
                    y = writePdfLine(contentStream, margin, y, PDType1Font.HELVETICA_BOLD, 12,
                            "Expense Report (continued)");
                    y = writePdfLine(contentStream, margin, y, PDType1Font.HELVETICA_BOLD, 10,
                            "ID | Category | Amount | Date | Time | Description");
                }

                Map<String, Object> row = expenseToExportRow(expense);
                String line = row.get("id") + " | "
                        + row.get("category") + " | "
                        + row.get("amount") + " | "
                        + row.get("date") + " | "
                        + row.get("time") + " | "
                        + row.get("description");

                y = writePdfLine(contentStream, margin, y, PDType1Font.HELVETICA, 9,
                        truncateForPdf(line, 120));
            }

            contentStream.close();
            document.save(output);
            return output.toByteArray();
        }
    }

    private float writePdfLine(PDPageContentStream contentStream, float x, float y,
                               PDType1Font font, int fontSize, String text) throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text != null ? text : "");
        contentStream.endText();
        return y - 14f;
    }

    private String truncateForPdf(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        String compact = sanitizePdfText(value);
        if (compact.length() <= maxLength) {
            return compact;
        }
        return compact.substring(0, maxLength - 3) + "...";
    }

    private String sanitizePdfText(String value) {
        StringBuilder sb = new StringBuilder();
        for (char ch : value.toCharArray()) {
            char normalized = (ch == '\n' || ch == '\r') ? ' ' : ch;
            if (normalized < 32 || normalized > 255) {
                sb.append('?');
            } else {
                sb.append(normalized);
            }
        }
        return sb.toString();
    }

    private List<Map<String, Object>> buildJsonRows(List<Expense> expenses) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Expense expense : expenses) {
            rows.add(expenseToExportRow(expense));
        }
        return rows;
    }

    private Map<String, Object> expenseToExportRow(Expense expense) {
        Map<String, Object> row = new LinkedHashMap<>();
        Category category = expense.getCategory();
        String categoryName = category != null ? category.getName() : "Uncategorized";
        String date = "";
        String time = "";
        try {
            LocalDateTime parsed = LocalDateTime.parse(expense.getDateTime(), INPUT_DATE_TIME);
            date = parsed.toLocalDate().toString();
            time = parsed.toLocalTime().toString();
        } catch (Exception ex) {
            log.warn("Unable to parse dateTime '{}' for expense {}", expense.getDateTime(), expense.getId());
        }

        row.put("id", expense.getId());
        row.put("category", categoryName);
        row.put("amount", expense.getAmount());
        row.put("date", date);
        row.put("time", time);
        row.put("description", expense.getDescription() != null ? expense.getDescription() : "");
        return row;
    }

    private String csvEscape(String value) {
        if (value == null) {
            return "\"\"";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private String defaultIfBlank(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value.trim();
    }
}
