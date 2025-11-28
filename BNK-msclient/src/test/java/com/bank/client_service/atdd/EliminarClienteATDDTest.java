package com.bank.client_service.atdd;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.openqa.selenium.Alert;
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
import java.util.List;

/****************************************/
// Historia de Usuario: Como funcionario del banco, quiero eliminar un cliente del sistema
// cuando ya no sea necesario mantener su información.
//
// Prueba de Aceptación: Verificar que se puede eliminar un cliente y desaparece del listado.
//
// 1. Navegar a la página principal (http://localhost:5173)
// 2. Hacer clic en la tarjeta "Modificar Cliente"
// 3. Ingresar número de documento de cliente existente
// 4. Hacer clic en "Buscar Cliente"
// 5. Hacer clic en "Eliminar"
// 6. Verificar mensaje de confirmación en alert
// 7. Hacer clic en "Aceptar" en el alert
// 8. Verificar mensaje de éxito
// 9. Navegar a "Listado de Clientes"
// 10. Verificar que el cliente eliminado NO aparece en la tabla
//
// Resultado Esperado: Cliente eliminado exitosamente y no visible en el listado
/****************************************/

// Para ejecutar en la línea de comando: mvn clean compile test
// -Dtest=EliminarClienteATDDTest

public class EliminarClienteATDDTest {

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
    public void testEliminarClienteExitoso() {

        /********** PREPARACIÓN (Arrange) **********/
        // Documento de cliente existente a eliminar (debe estar pre-cargado en BD)
        String documentoEliminar = "12345678";

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
        campoBusqueda.sendKeys(documentoEliminar);

        System.out.println("→ Buscando cliente con documento: " + documentoEliminar);

        // Paso 4: Hacer clic en "Buscar Cliente"
        WebElement botonBuscar = driver.findElement(
                By.xpath("//button[@type='submit' and contains(text(), 'Buscar Cliente')]"));
        botonBuscar.click();

        System.out.println("→ Buscando cliente con documento: " + documentoEliminar);

        // Paso 5: Esperar a que el botón "Eliminar" esté disponible (indica que el
        // cliente se cargó)
        // El botón solo aparece cuando el formulario de actualización se muestra con
        // los datos del cliente
        WebElement botonEliminar = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(@class, 'btn-eliminar') and contains(text(), 'Eliminar')]")));

        System.out.println("✓ Cliente encontrado y cargado");

        // Hacer scroll al botón "Eliminar" para que esté visible en el viewport
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",
                botonEliminar);

        // Pequeña pausa para que complete el scroll
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Hacer clic en "Eliminar"
        botonEliminar.click();

        // Pequeña pausa para que aparezca el alert
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /********** VERIFICACIÓN (Assert) - Parte 1 **********/

        // Paso 6: Manejar el alert de confirmación
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String textoAlert = alert.getText();

        // Verificar el texto del alert
        Assert.assertTrue(
                textoAlert.contains("¿Estás seguro de que deseas eliminar este cliente?"),
                "El alert debe contener el mensaje de confirmación");

        System.out.println("✓ Alert de confirmación mostrado:");
        System.out.println("  " + textoAlert);

        // Paso 7: Hacer clic en "Aceptar"
        alert.accept();

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
                textoMensaje.contains("Cliente eliminado exitosamente"),
                "El mensaje debe contener 'Cliente eliminado exitosamente'");

        System.out.println("✓ " + textoMensaje);

        // Esperar a que complete la redirección automática a home (el frontend redirige
        // después de 2 segundos)
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Paso 9: Navegar al listado de clientes
        // IMPORTANTE: El componente Vue de la tabla solo carga datos en onMounted(),
        // por lo que si el componente está en caché, no recargará los datos después de
        // eliminar.
        // Solución: Recargar la página completamente para forzar a Vue a fetchear datos
        // nuevos
        driver.get("http://localhost:5174");

        // Esperar a que cargue la página principal
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//h3[text()='Consultar Clientes']")));

        // Ahora sí, hacer clic en "Consultar Clientes"
        WebElement tarjetaListado = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//h3[text()='Consultar Clientes']")));
        tarjetaListado.click();

        // Esperar a que la tabla cargue (que el tbody esté presente)
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".tabla-clientes tbody")));

        // Esperar un momento adicional para que Vue Complete el fetch de datos desde el
        // backend
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /********** VERIFICACIÓN (Assert) - Parte 3 **********/

        // Paso 10: Verificar que el cliente eliminado NO aparece en la tabla
        // Estrategia: Usar el filtro de búsqueda para confirmar que no se encuentran
        // resultados
        WebElement inputFiltro = driver.findElement(By.cssSelector("input[placeholder='Buscar por documento...']"));
        inputFiltro.sendKeys(documentoEliminar);

        // Esperar un momento para que el filtro se aplique
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verificar que aparece el mensaje de "No se encontraron clientes"
        // Si el cliente todavía existe, este elemento NO estará presente
        List<WebElement> mensajesSinResultados = driver.findElements(By.cssSelector(".sin-resultados"));

        Assert.assertTrue(
                !mensajesSinResultados.isEmpty() && mensajesSinResultados.get(0).isDisplayed(),
                "El cliente con documento " + documentoEliminar
                        + " sigue apareciendo en la tabla (no se mostró mensaje de 'sin resultados')");

        System.out.println("✓ Cliente eliminado correctamente - Verificado con filtro de búsqueda");
    }

    @AfterTest
    public void closeDriver() throws Exception {
        if (driver != null) {
            driver.quit();
        }
    }
}
