public class Hola {
    public static void main(String[] args) {
        System.out.println("Compilación con JDK y ejecución con JVM"); 
        Hola instancia = new Hola();
        instancia.saludar();
    }

    public void saludar() {
        System.out.println("Hola somos marte ");
    }
}


/*public class Hola {
    public static void main(String[] args) {
        System.out.println("Compilación con JDK y ejecución con JVM"); 
        saludar();
    }

    public static void saludar() {
        System.out.println("Hola somos marte ");
    }
}*/