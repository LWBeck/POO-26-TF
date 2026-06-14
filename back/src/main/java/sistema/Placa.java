package sistema;
//Classe wrapper para nao deixar com o tipo generico String
public class Placa{
    private final String placa;

    public Placa(String placa){
        this.placa = placa;
    }

    public String getPlaca(){ return placa; } // nao sei se eh necessario

    public boolean equals(String outraPlaca){
        return this.placa.equals(outraPlaca);
    }
    public boolean equals(Placa outraPlaca){
        return this.placa.equals(outraPlaca.toString());
    }

    @Override
    public String toString(){ return placa; }
}
