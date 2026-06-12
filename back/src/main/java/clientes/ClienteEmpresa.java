package clientes;

import interfaces.Cliente;
import interfaces.ClientePreRegistrado;
import java.util.HashSet;
import ph.Ticket;
import ph.Veiculo;

public class ClienteEmpresa extends Cliente implements ClientePreRegistrado{
    private final String cnpj;
    private final String nome;
    private final double valorDiaria = 30;
    private final double valorMulta = 50;

    private final HashSet<Veiculo> veiculos;

    public ClienteEmpresa(String cnpj, String nome){
        this.cnpj = cnpj;
        this.nome = nome;
        this.veiculos = new HashSet<>();
    }
    
    @Override
    public String getId(){ return cnpj; }
    @Override
    public String getNome(){ return nome; }

    @Override
    public double calculaValor(Ticket ticket){
        return valorDiaria + ticket.diffDias()*valorMulta;
    }

    @Override
    public boolean registrarVeiculo(String placa){
        if (veiculos.stream().anyMatch(v -> v.equals(placa))){
            return false;
        }
        this.veiculos.add(new Veiculo(this, placa));
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
