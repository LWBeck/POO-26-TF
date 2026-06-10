import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.ChronoField;

public class Ticket{
    private final String placa;
    private final Cliente cliente;
    private final LocalDateTime hEntrada;
    private LocalDateTime hSaida;
    private double valor;

    public Ticket(String placa, Cliente cliente, LocalDateTime hEntrada){
        this.placa = placa;
        this.cliente = cliente;
        this.hEntrada = hEntrada;
    }

    protected void setHSaida(LocalDateTime hSaida){
        this.hSaida = hSaida;
        this.valor = cliente.calculaValor(this);
    }

    public double getValor(){ return valor; }
    
    public long diffHoras(){
        return ChronoUnit.HOURS.between(hSaida, hEntrada);
    }

    public int diffDias(){
        return hSaida.get(ChronoField.EPOCH_DAY) - hEntrada.get(ChronoField.EPOCH_DAY);
    }
}