package customExceptions;

public class ClienteNaoEncontradoException extends RuntimeException{
    public ClienteNaoEncontradoException(String idCliente){
        super(String.format("Cliente %s não encontrado", idCliente));
    }
}
