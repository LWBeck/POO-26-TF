package tiposClientes;


import interfaces.PreRegistrado;

import sistema.Ticket;

import customExceptions.SaldoInsuficienteException;

public class ClienteAluno extends PreRegistrado{
    private final String cpf;
    private final Double valorFixo = 15.0;

    private double saldo;

    public ClienteAluno(String cpf, String nome){
        super(nome, 1);
        this.cpf = cpf;
        this.saldo = 0;
    }

    @Override
    public String getId(){ return cpf; }

    public Double getSaldo(){ return saldo; }

    // duvida: tem problema em deixar essa funcao como publica? como eu faria para ela nao precisar ser publica?
    public void modSaldo(double mod){
        saldo += mod;
        if ((saldo) < 0){
            throw new SaldoInsuficienteException();
        }
    }

    @Override
    public Double calculaValor(Ticket ticket){
        return (ticket.diffDias()+1)*valorFixo;
    }
    
}
