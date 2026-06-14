package customExceptions;

public class ClienteBloqueadoException extends RuntimeException{
    public ClienteBloqueadoException(){
        super("Cliente está bloqueado");
    }
    
}
