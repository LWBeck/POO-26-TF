package sistema;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

import interfaces.Cliente;

public class Ticket {
    private final Cliente cliente;
    private final String placa;
    private final LocalDateTime hEntrada;
    private LocalDateTime hSaida;
    
    // Novos atributos exigidos para os registos e relatórios
    private Double valorTotalDevido;
    private Double valorPago;
    private String nomeDesconto;
    private Double percentualDesconto;
    private Double valorDesconto;

    private final Double valorHora = 7.0;
    private final Double valorDiaria = 50.0;

    public Ticket(Cliente cliente, String placa, LocalDateTime hEntrada){
        this.cliente = cliente;
        this.placa = placa;
        this.hEntrada = hEntrada;
        this.hSaida = null;
        
        // Inicialização padrão sem descontos
        this.valorTotalDevido = null;
        this.valorPago = null;
        this.nomeDesconto = "nenhum";
        this.percentualDesconto = 0.0;
        this.valorDesconto = 0.0;
    }

    public Ticket(Cliente cliente, String placa, LocalDateTime hEntrada, LocalDateTime hSaida, Double valorTotalDevido, String nomeDesconto, Double valorDesconto, Double valorPago){
        this.cliente = cliente;
        this.placa = placa;
        this.hEntrada = hEntrada;
        this.hSaida = hSaida;

        // Inicialização padrão sem descontos
        this.valorTotalDevido = valorTotalDevido;
        this.valorPago = valorPago;
        this.nomeDesconto = nomeDesconto;
        this.percentualDesconto = 0.0;
        this.valorDesconto = valorDesconto;
    }

    public String getIdCliente(){ 
        if (cliente != null) return cliente.getId();
        return null;
    }

    public String getPlaca(){ return placa; }
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
        this.valorTotalDevido = calculaValor();
        
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

    public Double calculaValor(){
        // esse método está no ticket pra que não precise de uma classe ClienteAvulso
        if (cliente != null){
            return cliente.calculaValor(this);
        }
        if (this.diffHoras() > 6){
            return valorDiaria*(this.diffDias()+1);
        }
        return valorHora*(this.diffHoras()+1);
    }
}