package com.example.demo;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class JasperReportService {

    public byte[] generatePdfReport(String jrxmlFile, Map<String, Object> parameters, List<?> data) throws Exception {
        // Load JRXML file
        InputStream reportStream = new ClassPathResource("reports/" + jrxmlFile).getInputStream();

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
