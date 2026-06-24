package tiposClientes;

import interfaces.Cliente;

import sistema.Ticket;


public class ClienteProfessor extends Cliente{
    private final String cpf;

    public ClienteProfessor(String cpf, String nome){
        super(nome, 2);
        this.cpf = cpf;
    }

    @Override
    public String getId(){ return cpf; }

    @Override
    public Double calculaValor(Ticket ticket){
        return 0.0;
    }
}