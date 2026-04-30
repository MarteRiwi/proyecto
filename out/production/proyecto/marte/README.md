# Creador por :
### -Sebastian Torres Lopez
### -Stiven Mozquera
### -Camilo Villada.

# Descripcion de Marte
### Marte o El SIGP es una solución tecnológica diseñada como un Producto Mínimo Viable (MVP) para centralizar y optimizar la atención médica en instituciones de salud colombianas. Su objetivo principal es migrar la gestión tradicional hacia un entorno digital eficiente, seguro y alineado con la normativa legal vigente (Ley 1581 de 2012)

# Estructura del proyecto 
### PROYECTO-SIGP (Raíz)
```text
marte/
 ├── src/
 │   └── com/sigp/
 │        ├── controller/
 │        ├── service/
 │        ├── repository/
 │        ├── model/
 │        ├── exception/
 │        └── Main.java
 │
 ├── docs/
 ├── diagrams/
 └── README.md
 ```

# Inicializar proyecto 
```text
1. Descarga JavaFX SDK desde https://openjfx.io
*     (elige la versión compatible con tu JDK, ej: JavaFX 21 para JDK 21)
*
*  2. En IntelliJ: File → Project Structure → Libraries
*     → ➕ Java → selecciona la carpeta lib/ del SDK de JavaFX
*
*  3. En Run/Debug Configurations → VM options, agrega:
*     --module-path "/ruta/al/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml
*
*     Ejemplo en Windows:
*     --module-path "C:/javafx-sdk-21/lib" --add-modules javafx.controls,javafx.fxml
*
*  4. Cambia la clase principal de com.sigp.Main a com.sigp.ui.MarteApp
*
*
*  OPCIÓN B: Con Maven (pom.xml)
*  ──────────────────────────────
*  Agrega en <dependencies>:
*
*    <dependency>
*      <groupId>org.openjfx</groupId>
*      <artifactId>javafx-controls</artifactId>
*      <version>21</version>
*    </dependency>
*
*  Y en <plugins>:
*
*    <plugin>
*      <groupId>org.openjfx</groupId>
*      <artifactId>javafx-maven-plugin</artifactId>
*      <version>0.0.8</version>
*      <configuration>
*        <mainClass>com.sigp.ui.MarteApp</mainClass>
*      </configuration>
*    </plugin>
```
# Credenciales
### se encuentra en nuestra data 
### te ponemos una credencial como pacinete stiven12@gmail.com|1234|PATIENT
### credencial : usuario/contraseña/rol


## controller
### Esta carpeta contiene las clases que controlan el flujo de la aplicación, reciben las solicitudes del usuario o de la interfaz, y delegan la lógica a otras capas.

## service
### Aquí está la lógica de negocio de la aplicación.

## repository
### Simula la base de datos (Aqui se guardara los arrayList)

## model
### Contiene las clases que representan los datos o entidades de la aplicación.

## excpetion
### Manejo de errores

