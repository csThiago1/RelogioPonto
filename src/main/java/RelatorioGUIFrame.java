import javax.swing.*;
import java.time.LocalDate;
import java.util.List;

public class RelatorioGUIFrame extends JFrame {
    private JTable tabelaRelatorio;
    private NonEditableTableModel modeloTabela;

    public RelatorioGUIFrame(LocalDate inicio, LocalDate fim, List<RelatorioHoras> horasTrabalhadas) {
        setTitle("Relatório de Horas Trabalhadas - " + inicio.format(DateTimeUtils.DATA_BR) + " a " + fim.format(DateTimeUtils.DATA_BR));
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        Object[] columnNames = {"Funcionário", "Data de Entrada", "Data de Saída", "Horas Trabalhadas"};
        modeloTabela = new NonEditableTableModel(columnNames, 0);

        tabelaRelatorio = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaRelatorio);
        add(scrollPane);

        carregarDados(horasTrabalhadas);
        setVisible(true);
    }

    private void carregarDados(List<RelatorioHoras> horasTrabalhadas) {
        modeloTabela.setRowCount(0);
        for (RelatorioHoras horas : horasTrabalhadas) {
            modeloTabela.addRow(new Object[]{
                    horas.getNomeFuncionario(),
                    horas.getEntradaFormatada(),
                    horas.getSaidaFormatada(),
                    horas.getHorasTrabalhadasFormatado()
            });
        }
    }
}
