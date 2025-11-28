package com.bank.client_service.atdd;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/****************************************/
// Historia de Usuario: Como funcionario del banco, quiero actualizar los datos de un cliente existente
// para mantener la información actualizada.
//
// Prueba de Aceptación: Verificar que se pueden modificar los datos de un cliente y se reflejan en el listado.
//
// 1. Navegar a la página principal (http://localhost:5173)
// 2. Hacer clic en la tarjeta "Modificar Cliente"
// 3. Ingresar número de documento de cliente existente
// 4. Hacer clic en "Buscar Cliente"
// 5. Verificar que los datos se cargaron (campo documentNumber debe estar disabled)
// 6. Modificar los campos editables (email, teléfono, dirección)
// 7. Hacer clic en "Actualizar"
// 8. Verificar mensaje de éxito
// 9. Navegar a "Listado de Clientes"
// 10. Verificar que los datos actualizados se reflejan en la tabla
//
// Resultado Esperado: Cliente actualizado exitosamente con nuevos datos visibles
/****************************************/

// Para ejecutar en la línea de comando: mvn clean compile test
// -Dtest=ActualizarClienteATDDTest

public class ActualizarClienteATDDTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeTest
    public void setDriver() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void testActualizarClienteExitoso() {

        /********** PREPARACIÓN (Arrange) **********/
        // Documento de cliente existente (debe estar pre-cargado en BD)
        String documentoExistente = "98765432";

        // Nuevos datos para actualizar
        String nuevoEmail = "email.actualizado@mail.com";
        String nuevoTelefono = "77999888";
        String nuevaDireccion = "Nueva Dirección Actualizada #456";

        // Paso 1: Navegar a la página principal
        String homeUrl = "http://localhost:5174";
        driver.get(homeUrl);

        /********** LÓGICA (Act) **********/

        // Paso 2: Hacer clic en la tarjeta "Modificar Cliente"
        WebElement tarjetaModificar = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//h3[text()='Modificar Cliente']")));
        tarjetaModificar.click();

        // Esperar a que cargue el formulario
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Paso 3: Ingresar número de documento en el buscador
        WebElement campoBusqueda = driver.findElement(By.id("document_number"));
        campoBusqueda.sendKeys(documentoExistente);

        // Paso 4: Hacer clic en "Buscar Cliente"
        WebElement botonBuscar = driver.findElement(
                By.xpath("//button[@type='submit' and contains(text(), 'Buscar Cliente')]"));
        botonBuscar.click();

        /********** VERIFICACIÓN (Assert) - Parte 1 **********/

        // Paso 5: Esperar y verificar que los datos se cargaron
        // El campo documentNumber debe estar presente y deshabilitado (solo lectura)
        WebElement campoDocumento = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("documentNumber")));

        Assert.assertFalse(
                campoDocumento.isEnabled(),
                "El campo de número de documento debe estar deshabilitado (solo lectura)");

        System.out.println("✓ Datos del cliente cargados correctamente");
        System.out.println("  - Documento: " + campoDocumento.getAttribute("value"));

        /********** LÓGICA (Act) - Continuación **********/

        // Paso 6: Modificar los campos editables
        WebElement campoEmail = driver.findElement(By.id("email"));
        campoEmail.clear();
        campoEmail.sendKeys(nuevoEmail);

        WebElement campoTelefono = driver.findElement(By.id("phoneNumber"));
        campoTelefono.clear();
        campoTelefono.sendKeys(nuevoTelefono);

        WebElement campoDireccion = driver.findElement(By.id("address"));
        campoDireccion.clear();
        campoDireccion.sendKeys(nuevaDireccion);

        // Paso 7: Hacer clic en "Actualizar"
        WebElement botonActualizar = driver.findElement(
                By.xpath("//button[@type='submit' and contains(text(), 'Actualizar')]"));
        botonActualizar.click();

        // Esperar un breve momento para que el mensaje aparezca
        // NOTA: El frontend redirige después de 2 segundos, así que debemos buscar el
        // mensaje antes
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /********** VERIFICACIÓN (Assert) - Parte 2 **********/

        // Paso 8: Verificar mensaje de éxito
        WebElement mensajeExito = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".mensaje")));

        // Verificar que NO sea un mensaje de error
        String clasesElemento = mensajeExito.getAttribute("class");
        Assert.assertFalse(
                clasesElemento.contains("error"),
                "El mensaje no debe tener la clase 'error'");

        String textoMensaje = mensajeExito.getText();
        Assert.assertTrue(
                textoMensaje.contains("Cliente actualizado exitosamente"),
                "El mensaje debe contener 'Cliente actualizado exitosamente'");

        System.out.println("✓ Mensaje de éxito mostrado: " + textoMensaje);

        // Esperar a que complete la redirección automática a home (el frontend redirige
        // después de 2 segundos)
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Paso 9: Navegar a "Listado de Clientes"
        WebElement tarjetaListado = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//h3[text()='Consultar Clientes']")));
        tarjetaListado.click();

        // Esperar a que cargue la tabla
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Paso 10: Verificar que los datos actualizados se reflejan en la tabla
        WebElement tabla = driver.findElement(By.cssSelector(".tabla-clientes tbody"));
        String contenidoTabla = tabla.getText();

        Assert.assertTrue(
                contenidoTabla.contains(nuevoEmail),
                "La tabla debe contener el nuevo email: " + nuevoEmail);
        Assert.assertTrue(
                contenidoTabla.contains(nuevoTelefono),
                "La tabla debe contener el nuevo teléfono: " + nuevoTelefono);

        System.out.println("✓ Datos actualizados verificados en la tabla");
        System.out.println("  - Nuevo Email: " + nuevoEmail);
        System.out.println("  - Nuevo Teléfono: " + nuevoTelefono);
    }

    @AfterTest
    public void closeDriver() throws Exception {
        if (driver != null) {
            driver.quit();
        }
    }
}
