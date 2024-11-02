import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;

public class IntervaloAutomatico {

    private final LocalTime inicioIntervalo = LocalTime.of(12, 0);
    private final LocalTime fimIntervalo = LocalTime.of(13, 0);

    public void iniciarIntervaloAutomatico() {
        Timer timer = new Timer();

        TimerTask tarefa = new TimerTask() {
            @Override
            public void run() {
                LocalTime agora = LocalTime.now();

                if (agora.equals(inicioIntervalo)) {
                    atualizarEstadoFuncionarios("Intervalo");
                } else if (agora.equals(fimIntervalo)) {
                    atualizarEstadoFuncionarios("Presente");
                }
            }
        };

        timer.scheduleAtFixedRate(tarefa, 0, 60 * 1000); // Executa a cada minuto
    }

    private void atualizarEstadoFuncionarios(String novoEstado) {
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
        funcionarioDAO.atualizarEstadoFuncionariosPresentes(novoEstado);
    }
}
