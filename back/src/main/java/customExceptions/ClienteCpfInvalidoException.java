package customExceptions;

public class ClienteCpfInvalidoException extends RuntimeException{
    public ClienteCpfInvalidoException(){
        super("CPF é inválido! Cheque os caracteres.");
    }
}