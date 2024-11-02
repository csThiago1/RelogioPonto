import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FuncionarioDialog extends JDialog {
    private JTextField nomeField, cpfField, cargoField;
    private JButton btnSalvar, btnCancelar;
    private boolean confirmed;

    public FuncionarioDialog(Frame owner, String title) {
        this(owner, title, "", "", "");
    }

    public FuncionarioDialog(Frame owner, String title, String nome, String cpf, String cargo) {
        super(owner, title, true);
        setSize(300, 200);
        setLocationRelativeTo(owner);

        nomeField = new JTextField(nome, 20);
        cpfField = new JTextField(cpf, 11);
        cargoField = new JTextField(cargo, 20);

        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));
        panel.add(new JLabel("Nome:"));
        panel.add(nomeField);
        panel.add(new JLabel("CPF:"));
        panel.add(cpfField);
        panel.add(new JLabel("Cargo:"));
        panel.add(cargoField);

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnSalvar);
        btnPanel.add(btnCancelar);

        add(panel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmed = true;
                setVisible(false);
            }
        });

        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmed = false;
                setVisible(false);
            }
        });
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getNome() {
        return nomeField.getText();
    }

    public String getCpf() {
        return cpfField.getText();
    }

    public String getCargo() {
        return cargoField.getText();
    }
}
