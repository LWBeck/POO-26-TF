package sistema;


public class Placa {
    private final String placa;

    public Placa(String placa){
        this.placa = placa;
    }

    public String getPlaca(){ return placa; } // nao sei se eh necessario

    
    public boolean equals(String outraPlaca){
        return this.placa.equals(outraPlaca);
    }
    
    public boolean equals(Placa outraPlaca){
        if (outraPlaca == null) return false;
        return this.placa.equals(outraPlaca.toString());
    }

    // --- ADIÇÕES O PARA O HASHMAP FUNCIONAR ---

    // 1. O equals oficial do Java (compara objetos genéricos)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // É exatamente o mesmo objeto na memória
        if (obj == null || getClass() != obj.getClass()) return false; // Tipos diferentes
        Placa outraPlaca = (Placa) obj;
        return this.placa.equals(outraPlaca.placa); // Compara o texto da placa
    }

    // 2. O hashCode
    @Override
    public int hashCode() {
        return placa != null ? placa.hashCode() : 0;
    }

    // -----------------------------------------------------

    @Override
    public String toString(){ return placa; }
}