package sistema;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

import interfaces.Cliente;

public class Ticket{
    private final Cliente cliente;
    private final String placa;
    private final LocalDateTime hEntrada;
    private LocalDateTime hSaida;
    private Double valor;

    private final Double valorHora = 7.0;
    private final Double valorDiaria = 50.0;

    public Ticket(Cliente cliente, String placa, LocalDateTime hEntrada){
        this.cliente = cliente;
        this.placa = placa;
        this.hEntrada = hEntrada;
        this.hSaida = null;
        this.valor = null;
    }

    public String getIdCliente(){ 
        if (cliente != null) return cliente.getId();
        return null;
    }

    public String getPlaca(){ return placa; }

    protected void setHSaida(LocalDateTime hSaida){
        this.hSaida = hSaida;
        this.valor = calculaValor();
    }

    public Double getValor(){ return valor; }
    
    public long diffHoras(){
        return ChronoUnit.HOURS.between(hSaida, hEntrada);
    }

    public int diffDias(){
        return hSaida.get(ChronoField.EPOCH_DAY) - hEntrada.get(ChronoField.EPOCH_DAY);
    }

    public Double calculaValor(){
        if (cliente != null){
            return cliente.calculaValor(this);
        }
        if (this.diffHoras() > 6){
            return valorDiaria*(this.diffDias()+1);
        }
        return valorHora*(this.diffHoras()+1);
    }
}