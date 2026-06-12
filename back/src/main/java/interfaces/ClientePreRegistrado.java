package interfaces;

public interface ClientePreRegistrado {
    public String getNome();
    public boolean registrarVeiculo(String placa);
    public boolean removeVeiculo(String placa);
}
