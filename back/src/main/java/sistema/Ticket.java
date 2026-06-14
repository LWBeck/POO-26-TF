package sistema;

import interfaces.Cliente;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

public class Ticket{
    private final Cliente cliente;
    private final LocalDateTime hEntrada;
    private LocalDateTime hSaida;
    private Double valor;

    public Ticket(Cliente cliente, LocalDateTime hEntrada){
        this.cliente = cliente;
        this.hEntrada = hEntrada;
        this.hSaida = null;
        this.valor = null;
    }

    public String getIdCliente(){ return cliente.getId(); }

    protected void setHSaida(LocalDateTime hSaida){
        this.hSaida = hSaida;
        this.valor = cliente.calculaValor(this);
    }

    public Double getValor(){ return valor; }
    
    public long diffHoras(){
        return ChronoUnit.HOURS.between(hSaida, hEntrada);
    }

    public int diffDias(){
        return hSaida.get(ChronoField.EPOCH_DAY) - hEntrada.get(ChronoField.EPOCH_DAY);
    }
}