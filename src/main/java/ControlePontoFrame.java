import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

public class ControlePontoFrame extends JFrame {
    private JTable tabelaFuncionarios;
    private DefaultTableModel modeloTabela;

    public ControlePontoFrame() {
        setTitle("Controle de Ponto");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        modeloTabela = new DefaultTableModel();
        modeloTabela.addColumn("ID");
        modeloTabela.addColumn("Nome");
        modeloTabela.addColumn("CPF");

        tabelaFuncionarios = new JTable(modeloTabela);
        tabelaFuncionarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = tabelaFuncionarios.getSelectedRow();
                    int funcionarioId = (int) tabelaFuncionarios.getValueAt(selectedRow, 0);
                    registrarEntradaSaida(funcionarioId);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabelaFuncionarios);
        add(scrollPane, BorderLayout.CENTER);

        carregarFuncionarios();
        setVisible(true);
    }

    private void carregarFuncionarios() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id, nome, cpf FROM funcionarios";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                modeloTabela.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cpf")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registrarEntradaSaida(int funcionarioId) {
        PontoDAO pontoDAO = new PontoDAO();
        boolean jaRegistrado = pontoDAO.verificarEntrada(funcionarioId, LocalDate.now());


        if (jaRegistrado) {
            pontoDAO.registrarSaida(funcionarioId);
            JOptionPane.showMessageDialog(this, "Saída registrada com sucesso.");
        } else {
            pontoDAO.registrarEntrada(funcionarioId);
            JOptionPane.showMessageDialog(this, "Entrada registrada com sucesso.");
        }
    }
}
