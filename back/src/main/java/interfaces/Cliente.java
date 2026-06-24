package interfaces;

import java.util.HashSet;
import java.util.List;

import customExceptions.LimiteVeiculosAtingidoException;
import customExceptions.PlacaNaoEncontradaException;
import sistema.Ticket;

public abstract class Cliente{
    private final String nome;
    private final int nMaxVeiculos;
    private final HashSet<String> veiculos;

    public Cliente(String nome, int nMaxVeiculos){
        this.nome = nome;
        this.nMaxVeiculos = nMaxVeiculos;
        this.veiculos = new HashSet<>();
    }

    public abstract String getId();
    public String getNome(){ return nome; }

    public abstract Double calculaValor(Ticket ticket);

    public List<String> getPlacas(){ return veiculos.stream().toList(); }

    public boolean registrarVeiculo(String placa){
        if (veiculos.size() >= nMaxVeiculos) throw new LimiteVeiculosAtingidoException(this.getId());
        return this.veiculos.add(placa);
    }

    public boolean removeVeiculo(String placa){
        String veiculo = veiculos.stream()
                                  .filter(v -> v.equals(placa))
                                  .findAny()
                                  .orElse(null);
        if (veiculo == null){
            throw new PlacaNaoEncontradaException(placa);
        }
        this.veiculos.remove(veiculo);
        return true;
    }

    public boolean equals(Cliente cliente){
        return this.getId().equals(cliente.getId());
    }

    public boolean equals(String outroId){
        return this.getId().equals(outroId);
    }
}
