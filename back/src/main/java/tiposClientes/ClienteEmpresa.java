package tiposClientes;

import interfaces.PreRegistrado;
import sistema.Ticket;

public class ClienteEmpresa extends PreRegistrado{
    private final String cnpj;
    private final Double valorDiaria = 30.0;
    private final Double valorMulta = 50.0;

    private boolean emDebito;

    public ClienteEmpresa(String cnpj, String nome){
        super(nome, Integer.MAX_VALUE);
        this.cnpj = cnpj;
        this.emDebito = false;
    }

    public boolean estaEmDebito(){ return emDebito; }
    public void setDebito(boolean debito){emDebito = debito;}
    
    @Override
    public String getId(){ return cnpj; }

    @Override
    public Double calculaValor(Ticket ticket){
        return valorDiaria + ticket.diffDias()*valorMulta;
    }
}
