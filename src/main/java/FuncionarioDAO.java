import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {


    // Método para retornar todos os funcionários
    public List<Funcionario> obterFuncionarios() {
        List<Funcionario> funcionarios = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id, nome FROM funcionarios";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                funcionarios.add(new Funcionario(id, nome));  // Adiciona cada funcionário à lista
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return funcionarios;
    }

    // Método para adicionar um funcionário ao banco de dados
    public void adicionarFuncionario(String nome, String cpf, String cargo) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO funcionarios (nome, cpf, cargo) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, nome);
            stmt.setString(2, cpf);
            stmt.setString(3, cargo);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para atualizar as informações de um funcionário no banco de dados
    public void atualizarFuncionario(int id, String nome, String cpf, String cargo) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE funcionarios SET nome = ?, cpf = ?, cargo = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, nome);
            stmt.setString(2, cpf);
            stmt.setString(3, cargo);
            stmt.setInt(4, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para remover um funcionário do banco de dados
    public void removerFuncionario(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM funcionarios WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
