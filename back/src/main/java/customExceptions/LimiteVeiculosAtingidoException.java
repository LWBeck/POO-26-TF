package customExceptions;

public class LimiteVeiculosAtingidoException extends RuntimeException{
    public LimiteVeiculosAtingidoException(String idCliente){
        super(String.format("Limite de veículos do cliente %s atingido", idCliente));
    }
}
