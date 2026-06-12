package clientes;
import interfaces.Cliente;
import interfaces.ClientePreRegistrado;
import java.util.HashSet;
import ph.Ticket;
import ph.Veiculo;

public class ClienteProfessor extends Cliente implements ClientePreRegistrado{
    private final String cpf;
    private final String nome;
    private int nVeiculos;
    
    private final HashSet<Veiculo> veiculos;

    public ClienteProfessor(String cpf, String nome){
        this.cpf = cpf;
        this.nome = nome;
        this.veiculos = new HashSet<>();
        this.nVeiculos = 0;
    }

    @Override
    public String getId(){ return cpf; }
    @Override
    public String getNome(){ return nome; }

    @Override
    public double calculaValor(Ticket ticket){
        return 0;
    }

    @Override
    public boolean registrarVeiculo(String placa){
        if(nVeiculos >= 2){
            throw new IndexOutOfBoundsException();
        }
        this.veiculos.add(new Veiculo(this, placa));
        nVeiculos++;
        return true;
    }
    @Override
    public boolean removeVeiculo(String placa){
        Veiculo veiculo = veiculos.stream()
                                  .filter(v -> v.equals(placa))
                                  .findAny()
                                  .orElse(null);
        if (veiculo == null){
            return false;
        }
        this.veiculos.remove(veiculo);
        return true;
    }
}