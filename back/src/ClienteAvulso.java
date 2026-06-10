public class ClienteAvulso extends Cliente{
    private String placa;
    private double valor6h = 20;
    private double valorDiaria = 50;
    private boolean bloqueado = false;
    private double desconto = 1;

    public ClienteAvulso(String placa){
        this.placa = placa;
    }

    public String getId(){ return placa; }

    public String getPlaca(){ return placa; }

    public boolean getBloqueado(){ return bloqueado; }

    protected void setDesconto(boolean frequente){
        this.desconto = 0.9;
    }

    public double calculaValor(Ticket ticket){
        if(ticket.diffDias() > 0){
            return valorDiaria*ticket.diffDias()*desconto;
        }
        if (ticket.diffHoras() > 6){
            return valorDiaria*desconto;
        }
        return valor6h*desconto;
    }
}