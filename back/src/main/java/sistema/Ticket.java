package sistema;

import interfaces.Cliente;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

public class Ticket {
    private final Cliente cliente;
    private final LocalDateTime hEntrada;
    private LocalDateTime hSaida;
    
    // Novos atributos exigidos para os registos e relatórios
    private Double valorTotalDevido;
    private Double valorPago;
    private String nomeDesconto;
    private Double percentualDesconto;
    private Double valorDesconto;

    public Ticket(Cliente cliente, LocalDateTime hEntrada){
        this.cliente = cliente;
        this.hEntrada = hEntrada;
        this.hSaida = null;
        
        // Inicialização padrão sem descontos
        this.valorTotalDevido = null;
        this.valorPago = null;
        this.nomeDesconto = "nenhum";
        this.percentualDesconto = 0.0;
        this.valorDesconto = 0.0;
    }

    public String getIdCliente(){ return cliente.getId(); }
    public LocalDateTime getHEntrada() { return hEntrada; }
    public LocalDateTime getHSaida() { return hSaida; }

    // Método chamado pelo Estacionamento antes de finalizar a saída
    public void configurarDesconto(String nome, double percentual) {
        this.nomeDesconto = nome;
        this.percentualDesconto = percentual;
    }

    protected void setHSaida(LocalDateTime hSaida){
        this.hSaida = hSaida;
        
        // 1. O polimorfismo calcula o valor bruto da estadia
        this.valorTotalDevido = cliente.calculaValor(this);
        
        // 2. Aplica o desconto caso exista (ex: 10% do Cliente Frequente)
        if (this.percentualDesconto > 0) {
            this.valorDesconto = this.valorTotalDevido * this.percentualDesconto;
        } else {
            this.valorDesconto = 0.0;
        }
        
        // 3. Define o valor final que o cliente efetivamente tem a pagar
        this.valorPago = this.valorTotalDevido - this.valorDesconto;
    }

    // Retorna o valor efetivamente pago (mantém a compatibilidade com o resto do sistema)
    public Double getValor(){ return valorPago; }
    
    // Getters para exportação futura no ficheiro CSV
    public Double getValorTotalDevido() { return valorTotalDevido; }
    public Double getValorDesconto() { return valorDesconto; }
    public String getNomeDesconto() { return nomeDesconto; }
    
    public long diffHoras(){
        // Correção: a entrada deve vir primeiro para o valor ser positivo
        return ChronoUnit.HOURS.between(hEntrada, hSaida);
    }

    public int diffDias(){
        return hSaida.get(ChronoField.EPOCH_DAY) - hEntrada.get(ChronoField.EPOCH_DAY);
    }
}