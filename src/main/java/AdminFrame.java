import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class AdminFrame extends JFrame {
    private JButton btnCadastrarFuncionario;
    private JButton btnRelatorioSemanal;
    private JButton btnRelatorioMensal;
    private JButton btnRelatorioDiaAtual;
    private JButton btnRelatorioPersonalizado;
    private JButton btnExportarCSV;
    private JButton btnExportarExcel;
    private JButton btnExportarPDF;

    public AdminFrame() {
        setTitle("Painel Administrativo");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(8, 1, 10, 10));

        // Inicializa botões
        btnCadastrarFuncionario = new JButton("Cadastrar Funcionário");
        btnRelatorioDiaAtual = new JButton("Relatório do Dia Atual");
        btnRelatorioSemanal = new JButton("Relatório Semanal");
        btnRelatorioMensal = new JButton("Relatório Mensal");
        btnRelatorioPersonalizado = new JButton("Relatório Personalizado");
        btnExportarCSV = new JButton("Exportar para CSV");
        btnExportarExcel = new JButton("Exportar para Excel");
        btnExportarPDF = new JButton("Exportar para PDF");

        // Adiciona botões ao painel
        panel.add(btnCadastrarFuncionario);
        panel.add(btnRelatorioDiaAtual);
        panel.add(btnRelatorioSemanal);
        panel.add(btnRelatorioMensal);
        panel.add(btnRelatorioPersonalizado);
        panel.add(btnExportarCSV);
        panel.add(btnExportarExcel);
        panel.add(btnExportarPDF);
        add(panel);

        // Ações dos botões
        btnCadastrarFuncionario.addActionListener(e -> abrirFormularioCadastro());
        btnRelatorioDiaAtual.addActionListener(e -> gerarRelatorioDiaAtual());
        btnRelatorioSemanal.addActionListener(e -> gerarRelatorioSemanal());
        btnRelatorioMensal.addActionListener(e -> gerarRelatorioMensal());
        btnRelatorioPersonalizado.addActionListener(e -> gerarRelatorioPersonalizado());
        btnExportarCSV.addActionListener(e -> exportarRelatorioParaCSV());
        btnExportarExcel.addActionListener(e -> exportarRelatorioParaExcel());
        btnExportarPDF.addActionListener(e -> exportarRelatorioParaPDF());

        setVisible(true);
    }

    private void abrirFormularioCadastro() {
        JTextField nomeField = new JTextField();
        JTextField cpfField = new JTextField();
        JTextField cargoField = new JTextField();

        Object[] message = {"Nome:", nomeField, "CPF:", cpfField, "Cargo:", cargoField};

        int option = JOptionPane.showConfirmDialog(this, message, "Cadastrar Funcionário", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
            funcionarioDAO.adicionarFuncionario(nomeField.getText(), cpfField.getText(), cargoField.getText());
            JOptionPane.showMessageDialog(this, "Funcionário cadastrado com sucesso!");
        }
    }

    private void gerarRelatorioDiaAtual() {
        LocalDate hoje = LocalDate.now();
        RelatorioDAO relatorioDAO = new RelatorioDAO();
        List<RelatorioHoras> relatorioDiario = relatorioDAO.gerarRelatorioPorData(hoje);
        new RelatorioGUIFrame(hoje, hoje, relatorioDiario);
    }

    private void gerarRelatorioSemanal() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioSemana = hoje.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        RelatorioDAO relatorioDAO = new RelatorioDAO();
        List<RelatorioHoras> relatorioSemanal = relatorioDAO.gerarRelatorioPorPeriodo(inicioSemana, hoje);
        new RelatorioGUIFrame(inicioSemana, hoje, relatorioSemanal);
    }

    private void gerarRelatorioMensal() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        RelatorioDAO relatorioDAO = new RelatorioDAO();
        List<RelatorioHoras> relatorioMensal = relatorioDAO.gerarRelatorioPorPeriodo(inicioMes, hoje);
        new RelatorioGUIFrame(inicioMes, hoje, relatorioMensal);
    }

    private void gerarRelatorioPersonalizado() {
        JTextField dataInicioField = new JTextField();
        JTextField dataFimField = new JTextField();

        Object[] message = {"Data de Início (yyyy-MM-dd):", dataInicioField, "Data de Fim (yyyy-MM-dd):", dataFimField};

        int option = JOptionPane.showConfirmDialog(this, message, "Relatório Personalizado", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                LocalDate dataInicio = LocalDate.parse(dataInicioField.getText());
                LocalDate dataFim = LocalDate.parse(dataFimField.getText());

                if (!dataInicio.isAfter(dataFim)) {
                    RelatorioDAO relatorioDAO = new RelatorioDAO();
                    List<RelatorioHoras> relatorio = relatorioDAO.gerarRelatorioPorPeriodo(dataInicio, dataFim);
                    new RelatorioGUIFrame(dataInicio, dataFim, relatorio);
                } else {
                    JOptionPane.showMessageDialog(this, "A data de início deve ser anterior à data de fim.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Formato de data inválido. Use o formato yyyy-MM-dd.");
            }
        }
    }

    private void exportarRelatorioParaCSV() {
        LocalDate hoje = LocalDate.now();
        RelatorioDAO relatorioDAO = new RelatorioDAO();
        List<RelatorioHoras> horasTrabalhadas = relatorioDAO.gerarRelatorioPorData(hoje);
        String nomeArquivo = "relatorio_horas_trabalhadas_" + hoje + ".csv";

        try (FileWriter writer = new FileWriter(nomeArquivo)) {
            writer.write("Funcionário,Data de Entrada,Data de Saída,Horas Trabalhadas\n");
            for (RelatorioHoras horas : horasTrabalhadas) {
                writer.write(horas.getNomeFuncionario() + ",");
                writer.write(horas.getEntradaFormatada() + ",");
                writer.write(horas.getSaidaFormatada() + ",");
                writer.write(horas.getHorasTrabalhadasFormatado() + "\n");
            }
            JOptionPane.showMessageDialog(this, "Relatório exportado com sucesso: " + nomeArquivo);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao exportar o relatório para CSV.");
        }
    }

    private void exportarRelatorioParaExcel() {
        LocalDate hoje = LocalDate.now();
        RelatorioDAO relatorioDAO = new RelatorioDAO();
        List<RelatorioHoras> horasTrabalhadas = relatorioDAO.gerarRelatorioPorData(hoje);
        String nomeArquivo = "relatorio_horas_trabalhadas_" + hoje + ".xlsx";

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Relatório de Horas");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Funcionário");
            header.createCell(1).setCellValue("Entrada");
            header.createCell(2).setCellValue("Saída");
            header.createCell(3).setCellValue("Horas Trabalhadas");

            int rowNum = 1;
            for (RelatorioHoras horas : horasTrabalhadas) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(horas.getNomeFuncionario());
                row.createCell(1).setCellValue(horas.getEntradaFormatada());
                row.createCell(2).setCellValue(horas.getSaidaFormatada());
                row.createCell(3).setCellValue(horas.getHorasTrabalhadasFormatado());
            }

            try (FileOutputStream outputStream = new FileOutputStream(nomeArquivo)) {
                workbook.write(outputStream);
                JOptionPane.showMessageDialog(this, "Relatório exportado com sucesso: " + nomeArquivo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao exportar o relatório para Excel.");
        }
    }

    private void exportarRelatorioParaPDF() {
        LocalDate hoje = LocalDate.now();
        RelatorioDAO relatorioDAO = new RelatorioDAO();
        List<RelatorioHoras> horasTrabalhadas = relatorioDAO.gerarRelatorioPorData(hoje);
        String nomeArquivo = "relatorio_horas_trabalhadas_" + hoje + ".pdf";

        try (PdfWriter writer = new PdfWriter(nomeArquivo);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            document.addDocumentListener(new Paragraph("Relatório de Horas Trabalhadas").setBold().setFontSize(16));

            Table table = new Table(4);
            table.addCell(new Cell().add(new Paragraph("Funcionário")));
            table.addCell(new Cell().add(new Paragraph("Entrada")));
            table.addCell(new Cell().add(new Paragraph("Saída")));
            table.addCell(new Cell().add(new Paragraph("Horas Trabalhadas")));

            for (RelatorioHoras horas : horasTrabalhadas) {
                table.addCell(new Cell().add(new Paragraph(horas.getNomeFuncionario())));
                table.addCell(new Cell().add(new Paragraph(horas.getEntradaFormatada())));
                table.addCell(new Cell().add(new Paragraph(horas.getSaidaFormatada())));
                table.addCell(new Cell().add(new Paragraph(horas.getHorasTrabalhadasFormatado())));
            }

            document.addDocumentListener(table);
            JOptionPane.showMessageDialog(this, "Relatório exportado com sucesso: " + nomeArquivo);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao exportar o relatório para PDF.");
        }
    }
}
