package tiposClientes;

import interfaces.Cliente;
import sistema.Ticket;

public class ClienteAvulso implements Cliente{
    private final String placa;
    private final Double valorHora = 7.0;
    private final Double valorDiaria = 50.0;

    public ClienteAvulso(String placa){
        this.placa = placa;
    }
    @Override
    public String getId(){ return placa; }

    public String getPlaca(){ return placa; }

    @Override
    public Double calculaValor(Ticket ticket){
        if (ticket.diffHoras() > 6){
            return valorDiaria*(ticket.diffDias()+1);
        }
        return valorHora*(ticket.diffHoras()+1);
    }
    @Override
    public boolean equals(Cliente cliente){
        return this.getId().equals(cliente.getId());
    }
    @Override
    public boolean equals (String outroId){
        return this.getId().equals(outroId);
    }
}