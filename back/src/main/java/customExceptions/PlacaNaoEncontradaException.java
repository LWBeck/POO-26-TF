package customExceptions;

public class PlacaNaoEncontradaException extends RuntimeException{
    public PlacaNaoEncontradaException(String placa){
        super(String.format("Placa %s não encontrada", placa));
    }    
}
