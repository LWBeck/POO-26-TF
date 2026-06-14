package customExceptions;

public class VeiculoJaEstacionadoException extends RuntimeException{
    public VeiculoJaEstacionadoException(String placa){
        super(String.format("Veículo %s já estacionado", placa));
    }
}
