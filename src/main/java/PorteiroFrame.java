import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class PorteiroFrame extends JFrame {
    private JTable tabelaFuncionarios;
    private NonEditableTableModel modeloTabela;
    private JButton btnMostrarTodos;
    private JButton btnMostrarPresentes;
    private JButton btnMostrarAusentes;
    private JButton btnEntrada;
    private JButton btnSaida;
    private int funcionarioSelecionadoId = -1;

    public PorteiroFrame() {
        setTitle("Registro de Ponto - Porteiro");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Configuração dos botões de filtro e ação
        JPanel botoesPanel = new JPanel(new BorderLayout());

        JPanel filtroPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Filtros à esquerda
        btnMostrarTodos = new JButton("Todos");
        btnMostrarPresentes = new JButton("Presentes");
        btnMostrarAusentes = new JButton("Ausentes");
        btnMostrarTodos.setBackground(new Color(70, 130, 180)); // Azul
        btnMostrarPresentes.setBackground(new Color(70, 130, 180)); // Azul
        btnMostrarAusentes.setBackground(new Color(70, 130, 180)); // Azul
        btnMostrarTodos.setForeground(Color.WHITE);
        btnMostrarPresentes.setForeground(Color.WHITE);
        btnMostrarAusentes.setForeground(Color.WHITE);

        filtroPanel.add(btnMostrarTodos);
        filtroPanel.add(btnMostrarPresentes);
        filtroPanel.add(btnMostrarAusentes);

        JPanel acaoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Botões de entrada/saída à direita
        btnEntrada = new JButton("Entrada");
        btnEntrada.setBackground(Color.GREEN);
        btnEntrada.setForeground(Color.WHITE);

        btnSaida = new JButton("Saída");
        btnSaida.setBackground(Color.RED);
        btnSaida.setForeground(Color.WHITE);

        acaoPanel.add(btnEntrada);
        acaoPanel.add(btnSaida);

        botoesPanel.add(filtroPanel, BorderLayout.WEST);
        botoesPanel.add(acaoPanel, BorderLayout.EAST);

        // Configuração da tabela de funcionários
        Object[] columnNames = {"ID", "Nome", "Status", "Horas Trabalhadas Hoje"};
        modeloTabela = new NonEditableTableModel(columnNames, 0);
        tabelaFuncionarios = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaFuncionarios);

        add(botoesPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Ações dos botões de filtro
        btnMostrarTodos.addActionListener(e -> carregarFuncionariosComFiltro(""));
        btnMostrarPresentes.addActionListener(e -> carregarFuncionariosComFiltro("Presente"));
        btnMostrarAusentes.addActionListener(e -> carregarFuncionariosComFiltro("Ausente"));

        // Ações dos botões de entrada e saída
        btnEntrada.addActionListener(e -> registrarEntrada());
        btnSaida.addActionListener(e -> registrarSaida());

        // Ação ao selecionar uma linha da tabela
        tabelaFuncionarios.getSelectionModel().addListSelectionListener(event -> {
            int selectedRow = tabelaFuncionarios.getSelectedRow();
            if (selectedRow != -1) {
                funcionarioSelecionadoId = (int) modeloTabela.getValueAt(selectedRow, 0);
            }
        });

        carregarFuncionariosComFiltro("");
        setVisible(true);
    }

    /**
     * Carrega os funcionários na tabela com base no filtro de presença especificado
     * @param filtroStatus "Presente", "Ausente" ou vazio para exibir todos
     */
    private void carregarFuncionariosComFiltro(String filtroStatus) {
        modeloTabela.setRowCount(0);
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
        List<Funcionario> funcionarios = funcionarioDAO.obterFuncionarios();
        PontoDAO pontoDAO = new PontoDAO();
        LocalDate hoje = LocalDate.now();

        // Filtra funcionários pelo status, se especificado
        List<Funcionario> funcionariosFiltrados = funcionarios.stream()
                .filter(funcionario -> {
                    String status = pontoDAO.verificarStatusPresenca(funcionario.getId(), hoje) ? "Presente" : "Ausente";
                    return filtroStatus.isEmpty() || status.equals(filtroStatus);
                })
                .collect(Collectors.toList());

        // Adiciona os funcionários filtrados à tabela
        for (Funcionario funcionario : funcionariosFiltrados) {
            String status = pontoDAO.verificarStatusPresenca(funcionario.getId(), hoje) ? "Presente" : "Ausente";
            String horasTrabalhadas = pontoDAO.calcularHorasEMinutosTrabalhadosHoje(funcionario.getId(), hoje);
            modeloTabela.addRow(new Object[]{
                    funcionario.getId(),
                    funcionario.getNome(),
                    status,
                    horasTrabalhadas
            });
        }
    }

    /**
     * Registra a entrada do funcionário selecionado na tabela
     */
    private void registrarEntrada() {
        if (funcionarioSelecionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um funcionário primeiro.");
            return;
        }

        PontoDAO pontoDAO = new PontoDAO();
        boolean jaRegistrado = pontoDAO.verificarEntrada(funcionarioSelecionadoId, LocalDate.now());

        if (!jaRegistrado) {
            pontoDAO.registrarEntrada(funcionarioSelecionadoId);
            JOptionPane.showMessageDialog(this, "Entrada registrada com sucesso.");
            carregarFuncionariosComFiltro(""); // Atualiza a tabela após a ação
        } else {
            JOptionPane.showMessageDialog(this, "O funcionário já registrou a entrada hoje.");
        }
    }

    /**
     * Registra a saída do funcionário selecionado na tabela
     */
    private void registrarSaida() {
        if (funcionarioSelecionadoId == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um funcionário primeiro.");
            return;
        }

        PontoDAO pontoDAO = new PontoDAO();
        boolean jaRegistrado = pontoDAO.verificarEntrada(funcionarioSelecionadoId, LocalDate.now());

        if (jaRegistrado) {
            pontoDAO.registrarSaida(funcionarioSelecionadoId);
            JOptionPane.showMessageDialog(this, "Saída registrada com sucesso.");
            carregarFuncionariosComFiltro(""); // Atualiza a tabela após a ação
        } else {
            JOptionPane.showMessageDialog(this, "O funcionário ainda não registrou a entrada hoje.");
        }
    }
}
