import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.List;

public class RelatorioGenerator {

    public void gerarRelatorioPdfPorData(LocalDate dataSelecionada, List<RelatorioHoras> horasTrabalhadas) {
        String nomeArquivo = "relatorio_horas_trabalhadas_" + dataSelecionada.format(DateTimeUtils.DATA_BR) + ".pdf";

        try {
            PdfWriter writer = new PdfWriter(nomeArquivo);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Relatório de Horas Trabalhadas")
                    .setBold()
                    .setFontSize(16));
            document.add(new Paragraph("Data: " + dataSelecionada.format(DateTimeUtils.DATA_BR)));

            Table table = new Table(4);
            table.addCell(new Cell().add(new Paragraph("Funcionário")));
            table.addCell(new Cell().add(new Paragraph("Data de Entrada")));
            table.addCell(new Cell().add(new Paragraph("Data de Saída")));
            table.addCell(new Cell().add(new Paragraph("Horas Trabalhadas")));

            for (RelatorioHoras horas : horasTrabalhadas) {
                table.addCell(new Cell().add(new Paragraph(horas.getNomeFuncionario())));
                table.addCell(new Cell().add(new Paragraph(horas.getEntradaFormatada())));
                table.addCell(new Cell().add(new Paragraph(horas.getSaidaFormatada())));
                table.addCell(new Cell().add(new Paragraph(horas.getHorasTrabalhadasFormatado())));
            }

            document.add(table);
            document.close();

            System.out.println("Relatório gerado com sucesso: " + nomeArquivo);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
