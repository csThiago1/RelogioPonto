import java.time.LocalDateTime;

public class RelatorioHoras {
    private String nomeFuncionario;
    private LocalDateTime entrada;
    private LocalDateTime saida;
    private long minutosTrabalhados;

    public RelatorioHoras(String nomeFuncionario, LocalDateTime entrada, LocalDateTime saida, long minutosTrabalhados) {
        this.nomeFuncionario = nomeFuncionario;
        this.entrada = entrada;
        this.saida = saida;
        this.minutosTrabalhados = minutosTrabalhados;
    }

    public String getNomeFuncionario() {
        return nomeFuncionario;
    }

    public String getEntradaFormatada() {
        return entrada != null ? entrada.format(DateTimeUtils.DATA_HORA_BR) : "";
    }

    public String getSaidaFormatada() {
        return saida != null ? saida.format(DateTimeUtils.DATA_HORA_BR) : "";
    }

    public String getHorasTrabalhadasFormatado() {
        long horas = minutosTrabalhados / 60;
        long minutos = minutosTrabalhados % 60;
        return horas + "h " + minutos + "min";
    }
}
