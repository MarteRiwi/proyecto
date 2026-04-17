<<<<<<< HEAD
import com.sigp.Main;

public class Hola {
    public static void main(String[] args) {
        Main.main(args);
=======
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

public class Hola {
    public static void main(String[] args) {
        try {
            Path outputDir = Path.of("marte", "out").toAbsolutePath();
            URL[] classpath = {outputDir.toUri().toURL()};

            try (URLClassLoader loader = new URLClassLoader(classpath)) {
                Class<?> mainClass = Class.forName("com.sigp.Main", true, loader);
                Method mainMethod = mainClass.getMethod("main", String[].class);
                mainMethod.invoke(null, (Object) args);
            }
        } catch (Exception e) {
            System.err.println("No se pudo iniciar com.sigp.Main desde marte/out.");
            System.err.println("Compila primero el proyecto Marte o ejecuta la configuración 'SIGP Marte' en VS Code.");
            e.printStackTrace();
        }
>>>>>>> 477a5e3 (Feat: Cambios en el menu rol paciente salida y gestion)
    }
}
