import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Login - Controle de Ponto");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");

        panel.add(new JLabel("Usuário:"));
        panel.add(usernameField);
        panel.add(new JLabel("Senha:"));
        panel.add(passwordField);
        panel.add(loginButton);

        add(panel);

        loginButton.addActionListener(new LoginAction());

        setVisible(true);
    }

    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            String role = usuarioDAO.authenticate(username, password);

            if (role != null) {
                JOptionPane.showMessageDialog(null, "Login realizado com sucesso!");

                // Redireciona baseado no papel do usuário
                if (role.equals("admin")) {
                    new AdminFrame();  // Abre o painel de administrador
                } else if (role.equals("porteiro")) {
                    new PorteiroFrame();  // Abre o painel de porteiro
                }
                dispose(); // Fecha a tela de login
            } else {
                JOptionPane.showMessageDialog(null, "Usuário ou senha inválidos.");
            }
        }
    }

    public static void main(String[] args) {
        new LoginFrame();
    }
}
