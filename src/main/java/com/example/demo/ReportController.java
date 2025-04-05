package com.example.demo;

import com.example.demo.JasperReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final JasperReportService reportService;

    public ReportController(JasperReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/generate")
    public ResponseEntity<byte[]> generateReport() {
        try {
            // Define parameters (if any)
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ReportTitle", "Sample Report");

            // Sample data (Replace with your actual data)
            List<Map<String, Object>> data = List.of(
                    Map.of("name", "John Doe", "age", 30),
                    Map.of("name", "Jane Smith", "age", 25)
            );

            // Generate PDF report
            byte[] pdfBytes = reportService.generatePdfReport("template.jrxml", parameters, data);

            // Return PDF as response
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}


import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
        import net.sf.jasperreports.engine.*;
        import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final JasperReportService reportService;
    private static final String REPORTS_DIR = "reports/";

    public ReportController(JasperReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/generate")
    public ResponseEntity<Resource> generateReport(@RequestParam String filePath, @RequestParam String jrxmlFilePath, @RequestParam String userId, @RequestBody List<Map<String, Object>> jsonArray) {
        try {
            // Ensure directory exists
            File directory = new File(filePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Define parameters (if any)
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ReportTitle", "Sample Report");

            // Generate PDF report
            byte[] pdfBytes = reportService.generatePdfReport(jrxmlFilePath, parameters, jsonArray);

            // Define file path
            String fullFilePath = Paths.get(filePath, userId + ".pdf").toString();

            // Save PDF to file
            try (FileOutputStream fos = new FileOutputStream(fullFilePath)) {
                fos.write(pdfBytes);
            }

            // Return file as response
            Resource fileResource = new FileSystemResource(fullFilePath);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + userId + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(fileResource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}

@Service
class JasperReportService {

    public byte[] generatePdfReport(String jrxmlFilePath, Map<String, Object> parameters, List<?> data) throws Exception {
        // Load JRXML file
        InputStream reportStream = new ClassPathResource(jrxmlFilePath).getInputStream();

        // Compile the JRXML file to a JasperReport
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        // Set data source
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

        // Fill report with data
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Export report to PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}

