package customExceptions;

public class PlacaInvalidaException extends RuntimeException{
    public PlacaInvalidaException(String placa){
        super(String.format("Placa %s é inválida", placa));
    }
}
