import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RelatorioDAO {

    // Método para gerar um relatório diário com as horas trabalhadas de cada funcionário
    public List<RelatorioHoras> gerarRelatorioPorData(LocalDate dataSelecionada) {
        List<RelatorioHoras> relatorio = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT f.nome, p.entrada, p.saida " +
                    "FROM ponto p " +
                    "JOIN funcionarios f ON p.funcionario_id = f.id " +
                    "WHERE p.entrada >= ? AND p.entrada < ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setTimestamp(1, Timestamp.valueOf(dataSelecionada.atStartOfDay()));
            stmt.setTimestamp(2, Timestamp.valueOf(dataSelecionada.plusDays(1).atStartOfDay()));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String nome = rs.getString("nome");
                Timestamp entrada = rs.getTimestamp("entrada");
                Timestamp saida = rs.getTimestamp("saida");

                if (entrada != null && saida != null) {
                    LocalDateTime entradaTime = entrada.toLocalDateTime();
                    LocalDateTime saidaTime = saida.toLocalDateTime();
                    long minutosTrabalhados = java.time.temporal.ChronoUnit.MINUTES.between(entradaTime, saidaTime);

                    relatorio.add(new RelatorioHoras(nome, entradaTime, saidaTime, minutosTrabalhados));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return relatorio;
    }

    /**
     * Gera um relatório de horas trabalhadas para um período específico
     * @param dataInicio Data de início do período
     * @param dataFim Data de fim do período
     * @return Lista de horas trabalhadas para o período
     */
    public List<RelatorioHoras> gerarRelatorioPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        List<RelatorioHoras> relatorio = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT f.nome, p.entrada, p.saida " +
                    "FROM ponto p " +
                    "JOIN funcionarios f ON p.funcionario_id = f.id " +
                    "WHERE p.entrada >= ? AND p.entrada <= ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setTimestamp(1, Timestamp.valueOf(dataInicio.atStartOfDay()));
            stmt.setTimestamp(2, Timestamp.valueOf(dataFim.plusDays(1).atStartOfDay().minusNanos(1)));  // Inclui o final do dia

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String nome = rs.getString("nome");
                Timestamp entrada = rs.getTimestamp("entrada");
                Timestamp saida = rs.getTimestamp("saida");

                if (entrada != null && saida != null) {
                    LocalDateTime entradaTime = entrada.toLocalDateTime();
                    LocalDateTime saidaTime = saida.toLocalDateTime();
                    long minutosTrabalhados = java.time.temporal.ChronoUnit.MINUTES.between(entradaTime, saidaTime);

                    relatorio.add(new RelatorioHoras(nome, entradaTime, saidaTime, minutosTrabalhados));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return relatorio;
    }

    /**
     * Gera um relatório consolidado de horas trabalhadas para um dia específico, somando todas as entradas e saídas
     * do mesmo funcionário, e mostrando apenas a primeira entrada e a última saída.
     * @param dataSelecionada A data do relatório
     * @return Lista de horas trabalhadas consolidada por funcionário
     */
    public List<RelatorioHoras> gerarRelatorioPorDataComSoma(LocalDate dataSelecionada) {
        List<RelatorioHoras> relatorio = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT f.nome, MIN(p.entrada) AS primeira_entrada, MAX(p.saida) AS ultima_saida, " +
                    "SUM(EXTRACT(EPOCH FROM (p.saida - p.entrada)) / 60) AS minutos_trabalhados " +
                    "FROM ponto p " +
                    "JOIN funcionarios f ON p.funcionario_id = f.id " +
                    "WHERE DATE(p.entrada) = ? " +
                    "GROUP BY f.nome";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDate(1, java.sql.Date.valueOf(dataSelecionada));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String nome = rs.getString("nome");
                Timestamp primeiraEntrada = rs.getTimestamp("primeira_entrada");
                Timestamp ultimaSaida = rs.getTimestamp("ultima_saida");
                long minutosTrabalhados = rs.getLong("minutos_trabalhados");

                if (primeiraEntrada != null && ultimaSaida != null) {
                    LocalDateTime entradaTime = primeiraEntrada.toLocalDateTime();
                    LocalDateTime saidaTime = ultimaSaida.toLocalDateTime();

                    relatorio.add(new RelatorioHoras(nome, entradaTime, saidaTime, minutosTrabalhados));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return relatorio;
    }
}
