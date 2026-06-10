public class ClienteProfessor extends Cliente{
    private final String cpf;
    private final String nome;
    
    private final String[] placa;

    public ClienteProfessor(String cpf, String nome)

    @Override
    public String getId(){ return cpf; }

    @Override
    public double calculaValor(Ticket ticket){
        return 0;
    }
}