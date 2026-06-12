package clientes;
import interfaces.Cliente;
import ph.Ticket;

public class ClienteAvulso extends Cliente{
    private final String placa;
    private final double valorHora = 7;
    private final double valorDiaria = 50;

    public ClienteAvulso(String placa){
        this.placa = placa;
    }

    @Override
    public String getId(){ return placa; }

    public String getPlaca(){ return placa; }

    @Override
    public double calculaValor(Ticket ticket){
        if (ticket.diffHoras() > 6){
            return valorDiaria*(ticket.diffDias()+1);
        }
        return valorHora*(ticket.diffHoras()+1);
    }
}