package clientes;
import interfaces.Cliente;
import interfaces.ClientePreRegistrado;
import ph.Ticket;
import ph.Veiculo;

public class ClienteAluno extends Cliente implements ClientePreRegistrado{
    private final String cpf;
    private final String nome;   
    private Veiculo veiculo;
    private final double valorFixo = 15;

    public ClienteAluno(String cpf, String nome){
        this.cpf = cpf;
        this.nome = nome;
        this.veiculo = null;
    }

    @Override
    public String getId(){ return cpf; }
    @Override
    public String getNome(){ return nome; }

    @Override
    public double calculaValor(Ticket ticket){
        return (ticket.diffDias()+1)*valorFixo;
    }

    @Override
    public boolean registrarVeiculo(String placa){
        if (this.veiculo != null){
            return false;
        }
        this.veiculo = new Veiculo(this, placa);
        return true;
    }
    @Override
    public boolean removeVeiculo(String placa){
        this.veiculo = null;
        return true;
    }
    
}
