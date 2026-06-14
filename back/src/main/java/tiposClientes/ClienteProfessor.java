package tiposClientes;

import interfaces.PreRegistrado;

import sistema.Ticket;


public class ClienteProfessor extends PreRegistrado{
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