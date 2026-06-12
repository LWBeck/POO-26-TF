package utilities;

public class ValidaDados {
    public static boolean validaCPF(String cpf){
        if (cpf.length() != 11){ 
            //System.out.println("falhou no lenght");
            return false; 
        }
        if(!(cpf.chars()
                 .mapToObj(c -> (char) c)
                 .allMatch(c -> Character.isDigit(c))))
        {
            //System.out.println("falhou no parseInt");
            return false;
        }
        int soma1 = 0;
        for (int i = 0; i < 9; i++){
            soma1 += Character.getNumericValue(cpf.charAt(i))*(10-i);
            //System.out.println("cpf.charAt(i): "+Character.getNumericValue(cpf.charAt(i)));
            //System.out.println("10-i: "+(10-i));
            //System.out.println("soma1: "+soma1);
        }
        int n1 = soma1%11;
        //System.out.println("n1 = "+n1);
        //System.out.println("11 - n1 = "+ (11 - n1));
        if (n1 < 2){
            if (Character.getNumericValue(cpf.charAt(9)) != 0){ 
                //System.out.println("falhou no primeiro digito n1 < 2");
                return false;}
        }
        else if(!(Character.getNumericValue(cpf.charAt(9)) == (11 - n1))){ 
            //System.out.println("falhou no primeiro digito n1 > 2");
            return false;
        }
        int soma2 = 0;
        for (int i = 0; i < 11; i++){
            soma2 += Character.getNumericValue(cpf.charAt(i))*(11-i);
            //System.out.println("soma2: "+soma2);
        }
        int n2 = soma2%11;
        if (n2 < 2){
            //System.out.println("chegou no ultimo check n2 < 2");
            return (Character.getNumericValue(cpf.charAt(10)) != 0);
        }
        //System.out.println("chegou no ultimo check n2 > 1");
        return (Character.getNumericValue(cpf.charAt(10)) == 11 - n2);
    }

    public static boolean validaPlaca(String placa){
        if (placa.length() != 7){ return false; }
        if (!(placa.substring(0,3).chars()
                      .mapToObj(c -> (char) c)
                      .allMatch(c -> Character.isAlphabetic(c)))){ return false; }
        return (Character.isDigit(placa.charAt(3)) && 
                Character.isDigit(placa.charAt(5)) && 
                Character.isDigit(placa.charAt(6)) &&
                Character.isLetterOrDigit(placa.charAt(4)));
    }
    
}
