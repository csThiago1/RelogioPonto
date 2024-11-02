import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.mindrot.jbcrypt.BCrypt;

public class UsuarioDAO {

    public String authenticate(String username, String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT password_hash, role FROM usuarios WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String passwordHash = rs.getString("password_hash");
                if (BCrypt.checkpw(password, passwordHash)) {
                    return rs.getString("role");  // Retorna o papel (admin ou porteiro)
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;  // Se a autenticação falhar, retorna null
    }
}
