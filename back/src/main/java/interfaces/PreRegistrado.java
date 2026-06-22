package interfaces;

import java.util.HashSet;
import java.util.List;

import customExceptions.LimiteVeiculosAtingidoException;
import customExceptions.PlacaNaoEncontradaException;
import sistema.Placa;

public abstract class PreRegistrado implements Cliente{
    private final String nome;
    private final int nMaxVeiculos;
    private final HashSet<Placa> veiculos;

    public PreRegistrado(String nome, int nMaxVeiculos){
        this.nome = nome;
        this.nMaxVeiculos = nMaxVeiculos;
        this.veiculos = new HashSet<>();
    }
    @Override
    public abstract String getId();
    public String getNome(){ return nome; }

    public List<Placa> getPlacas(){ return veiculos.stream().toList(); }

    public boolean registrarVeiculo(String placa){
        if (veiculos.size() >= nMaxVeiculos) throw new LimiteVeiculosAtingidoException(this.getId());
        return this.veiculos.add(new Placa(placa));
    }

    public boolean removeVeiculo(String placa){
        Placa veiculo = veiculos.stream()
                                  .filter(v -> v.equals(placa))
                                  .findAny()
                                  .orElse(null);
        if (veiculo == null){
            throw new PlacaNaoEncontradaException(placa);
        }
        this.veiculos.remove(veiculo);
        return true;
    }
    @Override
    public boolean equals(Cliente cliente){
        return this.getId().equals(cliente.getId());
    }
    @Override
    public boolean equals(String outroId){
        return this.getId().equals(outroId);
    }
}
