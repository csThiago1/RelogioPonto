import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class PontoDAO {

    // Verifica se o funcionário já tem um registro de entrada hoje
    public boolean verificarEntrada(int funcionarioId, LocalDate data) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM ponto WHERE funcionario_id = ? AND DATE(entrada) = ? AND saida IS NULL";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, funcionarioId);
            stmt.setDate(2, Date.valueOf(data));

            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Registra a entrada do funcionário no banco de dados
    public void registrarEntrada(int funcionarioId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO ponto (funcionario_id, entrada) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, funcionarioId);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));  // Hora atual
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Registra a saída do funcionário no banco de dados e calcula horas trabalhadas
    public void registrarSaida(int funcionarioId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE ponto SET saida = ? WHERE funcionario_id = ? AND saida IS NULL";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));  // Hora atual
            stmt.setInt(2, funcionarioId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Verifica o status de presença do funcionário (se ele já está registrado com entrada hoje)
    public boolean verificarStatusPresenca(int funcionarioId, LocalDate data) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM ponto WHERE funcionario_id = ? AND DATE(entrada) = ? AND saida IS NULL";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, funcionarioId);
            stmt.setDate(2, Date.valueOf(data));

            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Calcula as horas trabalhadas no dia para exibir no `PorteiroFrame`
    public String calcularHorasEMinutosTrabalhadosHoje(int funcionarioId, LocalDate data) {
        long totalMinutos = 0;
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT entrada, saida FROM ponto WHERE funcionario_id = ? AND DATE(entrada) = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, funcionarioId);
            stmt.setDate(2, Date.valueOf(data));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Timestamp entrada = rs.getTimestamp("entrada");
                Timestamp saida = rs.getTimestamp("saida");

                if (entrada != null && saida != null) {
                    LocalDateTime entradaTime = entrada.toLocalDateTime();
                    LocalDateTime saidaTime = saida.toLocalDateTime();
                    totalMinutos += ChronoUnit.MINUTES.between(entradaTime, saidaTime);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        long horas = totalMinutos / 60;
        long minutos = totalMinutos % 60;
        return horas + "h " + minutos + "min";
    }

}
