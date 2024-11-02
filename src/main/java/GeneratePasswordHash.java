import org.mindrot.jbcrypt.BCrypt;

public class GeneratePasswordHash {
    public static void main(String[] args) {
        String senha = "porteiro123";
        String hash = BCrypt.hashpw(senha, BCrypt.gensalt());
        System.out.println("Hash da senha 'porteiro123': " + hash);
    }
}