import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GerenciarFuncionariosFrame extends JFrame {
    private JTable tabelaFuncionarios;
    private DefaultTableModel modeloTabela;
    private JButton btnAdicionar, btnEditar, btnRemover;

    public GerenciarFuncionariosFrame() {
        setTitle("Gerenciar Funcionários");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        modeloTabela = new DefaultTableModel();
        modeloTabela.addColumn("ID");
        modeloTabela.addColumn("Nome");
        modeloTabela.addColumn("CPF");
        modeloTabela.addColumn("Cargo");

        tabelaFuncionarios = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaFuncionarios);

        btnAdicionar = new JButton("Adicionar Funcionário");
        btnEditar = new JButton("Editar Funcionário");
        btnRemover = new JButton("Remover Funcionário");

        JPanel panelBotoes = new JPanel();
        panelBotoes.setLayout(new GridLayout(1, 3));
        panelBotoes.add(btnAdicionar);
        panelBotoes.add(btnEditar);
        panelBotoes.add(btnRemover);

        add(scrollPane, BorderLayout.CENTER);
        add(panelBotoes, BorderLayout.SOUTH);

        carregarFuncionarios();

        // Ação para adicionar um novo funcionário
        btnAdicionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                novoFuncionario();
            }
        });

        // Ação para editar funcionário selecionado
        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tabelaFuncionarios.getSelectedRow();
                if (selectedRow != -1) {
                    editarFuncionario(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione um funcionário para editar.");
                }
            }
        });

        // Ação para remover funcionário selecionado
        btnRemover.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tabelaFuncionarios.getSelectedRow();
                if (selectedRow != -1) {
                    removerFuncionario(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione um funcionário para remover.");
                }
            }
        });

        setVisible(true);
    }

    private void carregarFuncionarios() {
        modeloTabela.setRowCount(0); // Limpa a tabela
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id, nome, cpf, cargo FROM funcionarios";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                modeloTabela.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("cargo")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void novoFuncionario() {
        FuncionarioDialog dialog = new FuncionarioDialog(this, "Adicionar Funcionário");
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
            funcionarioDAO.adicionarFuncionario(dialog.getNome(), dialog.getCpf(), dialog.getCargo());
            carregarFuncionarios();  // Atualiza a tabela
        }
    }

    private void editarFuncionario(int rowIndex) {
        int id = (int) modeloTabela.getValueAt(rowIndex, 0);
        String nomeAtual = (String) modeloTabela.getValueAt(rowIndex, 1);
        String cpfAtual = (String) modeloTabela.getValueAt(rowIndex, 2);
        String cargoAtual = (String) modeloTabela.getValueAt(rowIndex, 3);

        FuncionarioDialog dialog = new FuncionarioDialog(this, "Editar Funcionário", nomeAtual, cpfAtual, cargoAtual);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
            funcionarioDAO.atualizarFuncionario(id, dialog.getNome(), dialog.getCpf(), dialog.getCargo());
            carregarFuncionarios();  // Atualiza a tabela
        }
    }

    private void removerFuncionario(int rowIndex) {
        int id = (int) modeloTabela.getValueAt(rowIndex, 0);
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
        funcionarioDAO.removerFuncionario(id);
        carregarFuncionarios();  // Atualiza a tabela
    }
}
