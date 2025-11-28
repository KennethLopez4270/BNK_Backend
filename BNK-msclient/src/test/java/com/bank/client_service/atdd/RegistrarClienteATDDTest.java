package com.bank.client_service.atdd;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/****************************************/
// Historia de Usuario: Como funcionario del banco, quiero registrar un nuevo cliente con datos válidos
// para que quede almacenado en el sistema.
//
// Prueba de Aceptación: Verificar que un cliente se puede registrar exitosamente y aparece en el listado.
//
// 1. Navegar a la página principal (http://localhost:5173)
// 2. Hacer clic en la tarjeta "Registrar Cliente"
// 3. Completar todos los campos del formulario con datos válidos
// 4. Hacer clic en "Guardar Cliente"
// 5. Verificar mensaje de éxito
// 6. Navegar a "Listado de Clientes"
// 7. Verificar que el cliente registrado aparece en la tabla
//
// Resultado Esperado: Cliente registrado exitosamente y visible en el listado
/****************************************/

// Para ejecutar en la línea de comando: mvn clean compile test
// -Dtest=RegistrarClienteATDDTest

public class RegistrarClienteATDDTest {

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
    public void testRegistrarClienteExitoso() {

        /********** PREPARACIÓN (Arrange) **********/
        // Datos del cliente a registrar
        String fullName = "Carlos Alberto Rodríguez";
        String email = "carlos.rodriguez.test@mail.com";
        String phoneNumber = "77123456";
        String address = "Av. Ballivián #123, Zona Sur";
        String documentType = "CI";
        String documentNumber = "12345678";

        // Paso 1: Navegar a la página principal
        String homeUrl = "http://localhost:5174";
        driver.get(homeUrl);

        /********** LÓGICA (Act) **********/

        // Paso 2: Hacer clic en la tarjeta "Registrar Cliente"
        WebElement tarjetaRegistrar = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//h3[text()='Registrar Cliente']")));
        tarjetaRegistrar.click();

        // Esperar a que cargue el formulario
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Paso 3: Completar el formulario con datos válidos
        WebElement campoNombre = driver.findElement(By.id("fullName"));
        campoNombre.sendKeys(fullName);

        WebElement campoEmail = driver.findElement(By.id("email"));
        campoEmail.sendKeys(email);

        WebElement campoTelefono = driver.findElement(By.id("phoneNumber"));
        campoTelefono.sendKeys(phoneNumber);

        WebElement campoDireccion = driver.findElement(By.id("address"));
        campoDireccion.sendKeys(address);

        Select selectTipoDoc = new Select(driver.findElement(By.id("documentType")));
        selectTipoDoc.selectByValue(documentType);

        WebElement campoDocumento = driver.findElement(By.id("documentNumber"));
        campoDocumento.sendKeys(documentNumber);

        // Paso 4: Hacer clic en "Guardar Cliente"
        WebElement botonGuardar = driver.findElement(
                By.xpath("//button[@type='submit' and contains(text(), 'Guardar Cliente')]"));
        botonGuardar.click();

        // Esperar un breve momento para que el mensaje aparezca
        // NOTA: El frontend redirige después de 2 segundos, así que debemos buscar el
        // mensaje antes
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /********** VERIFICACIÓN (Assert) **********/

        // Paso 5: Verificar mensaje de éxito
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
                textoMensaje.contains("Cliente creado exitosamente"),
                "El mensaje de éxito debe contener 'Cliente creado exitosamente'");

        System.out.println("✓ Mensaje de éxito mostrado: " + textoMensaje);

        // Esperar a que complete la redirección automática a home (el frontend redirige
        // después de 2 segundos)
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Paso 6: Navegar a "Listado de Clientes"
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

        // Paso 7: Verificar que el cliente aparece en la tabla
        WebElement tabla = driver.findElement(By.cssSelector(".tabla-clientes tbody"));
        String contenidoTabla = tabla.getText();

        Assert.assertTrue(
                contenidoTabla.contains(fullName),
                "La tabla debe contener el nombre del cliente registrado: " + fullName);
        Assert.assertTrue(
                contenidoTabla.contains(email),
                "La tabla debe contener el email del cliente: " + email);
        Assert.assertTrue(
                contenidoTabla.contains(documentNumber),
                "La tabla debe contener el número de documento: " + documentNumber);

        System.out.println("✓ Cliente registrado encontrado en la tabla");
        System.out.println("  - Nombre: " + fullName);
        System.out.println("  - Email: " + email);
        System.out.println("  - Documento: " + documentNumber);
    }

    @AfterTest
    public void closeDriver() throws Exception {
        if (driver != null) {
            driver.quit();
        }
    }
}
