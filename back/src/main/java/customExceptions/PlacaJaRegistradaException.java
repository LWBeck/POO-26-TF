package customExceptions;

public class PlacaJaRegistradaException extends RuntimeException {
    public PlacaJaRegistradaException(String placa){
        super(String.format("Placa %s já foi registrada", placa));
    }
}
