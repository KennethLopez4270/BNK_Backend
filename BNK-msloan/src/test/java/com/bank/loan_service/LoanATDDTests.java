package com.bank.loan_service;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;

/****************************************
 * ATDD Tests for Loan Management System
 * 
 * Estos tests validan los flujos end-to-end del sistema de gestión de préstamos
 * desde la interfaz de usuario hasta la API del backend.
 * 
 * PREREQUISITOS para ejecutar estos tests:
 * 1. Backend corriendo en http://localhost:8085
 * 2. Frontend corriendo en http://localhost:5178
 * 3. Base de datos PostgreSQL accesible
 * 4. Chrome browser instalado
 * 
 * Para ejecutar: ./gradlew test --tests "com.bank.loan_service.LoanATDDTests"
 ****************************************/

public class LoanATDDTests {

    private WebDriver driver;
    private static final String FRONTEND_URL = "http://localhost:5178";
    private static final int WAIT_SECONDS = 5;

    @BeforeTest
    public void setDriver() throws Exception {
        // Configurar WebDriver usando WebDriverManager (gestión automática de
        // ChromeDriver)
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        // Configurar timeouts implícitos
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(WAIT_SECONDS));
    }

    @AfterTest
    public void closeDriver() throws Exception {
        if (driver != null) {
            driver.quit();
        }
    }

    /****************************************
     * Test Case #11: Verificar en "Solicitar Préstamo" funcionalidad de "Solicitar
     * Prestamo"
     * 
     * Historia de Usuario:
     * Como funcionario del banco, quiero solicitar un nuevo préstamo para un
     * cliente
     * para que el cliente pueda recibir financiamiento.
     * 
     * Criterio de Aceptación:
     * El sistema debe aceptar los datos del préstamo, calcular el pago mensual,
     * y mostrar un mensaje de éxito con el ID del préstamo creado.
     * 
     * Pasos:
     * 1. Ingresar a la página de "Solicitar Préstamo"
     * 2. Llenar todos los campos del formulario
     * 3. Calcular el pago mensual
     * 4. Hacer clic en "Solicitar Préstamo"
     * 
     * Resultado Esperado:
     * El sistema muestra el mensaje "Préstamo creado exitosamente. ID: X"
     ****************************************/
    @Test
    public void testTC11_SolicitarPrestamoExitoso() {

        /************** 1. Preparación de la prueba ***************/

        // Paso 1: Navegar a la página de Solicitar Préstamo
        String crearPrestamoUrl = FRONTEND_URL + "/crear-prestamo";
        driver.get(crearPrestamoUrl);

        System.out.println("✓ Navegado a: " + crearPrestamoUrl);

        // Esperar a que la página cargue completamente
        esperarSegundos(2);

        /************** 2. Lógica de la prueba ***************/

        // Paso 2: Llenar los campos del formulario

        // Encontrar y llenar el campo "ID del Cliente"
        WebElement campoClienteId = driver.findElement(By.cssSelector("input[placeholder*='ID del Cliente']"));
        campoClienteId.clear();
        campoClienteId.sendKeys("101");
        System.out.println("✓ Cliente ID ingresado: 101");

        // Llenar "Monto del Préstamo"
        WebElement campoMonto = driver.findElement(By.cssSelector("input[placeholder*='Monto del Préstamo']"));
        campoMonto.clear();
        campoMonto.sendKeys("5000");
        System.out.println("✓ Monto ingresado: 5000");

        // Llenar "Tasa de Interés"
        WebElement campoTasa = driver.findElement(By.cssSelector("input[placeholder*='Tasa de Interés']"));
        campoTasa.clear();
        campoTasa.sendKeys("5.5");
        System.out.println("✓ Tasa de interés ingresada: 5.5%");

        // Llenar "Plazo en meses"
        WebElement campoPlazo = driver.findElement(By.cssSelector("input[placeholder*='Plazo en meses']"));
        campoPlazo.clear();
        campoPlazo.sendKeys("12");
        System.out.println("✓ Plazo ingresado: 12 meses");

        // Llenar "Fecha de inicio" usando JavaScript (clear() y sendKeys() no funcionan
        // bien con type=date)
        WebElement campoFecha = driver.findElement(By.cssSelector("input[type='date']"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value = '2025-01-15';", campoFecha);
        System.out.println("✓ Fecha de inicio ingresada: 2025-01-15");

        esperarSegundos(1);

        // Paso 3: Hacer clic en "Calcular Pago Mensual"
        WebElement botonCalcular = driver.findElement(By.xpath("//button[contains(text(), 'Calcular Pago Mensual')]"));
        botonCalcular.click();
        System.out.println("✓ Clic en 'Calcular Pago Mensual'");

        esperarSegundos(2);

        // Verificar que aparece el resultado del cálculo
        WebElement resultadoCalculo = driver.findElement(By.cssSelector(".resultado-calculo"));
        Assert.assertTrue(resultadoCalculo.isDisplayed(), "El resultado del cálculo debe mostrarse");
        System.out.println("✓ Resultado de cálculo mostrado");

        // Paso 4: Hacer clic en "Solicitar Préstamo"
        WebElement botonSolicitar = driver.findElement(By.xpath("//button[contains(text(), 'Solicitar Préstamo')]"));
        botonSolicitar.click();
        System.out.println("✓ Clic en 'Solicitar Préstamo'");

        esperarSegundos(3);

        /************ 3. Verificación del resultado esperado - Assert ***************/

        // Verificar que aparece el mensaje de éxito
        WebElement mensaje = driver.findElement(By.cssSelector(".mensaje"));
        String textoMensaje = mensaje.getText();

        System.out.println("✓ Mensaje recibido: " + textoMensaje);

        // Verificar que el mensaje contiene "Préstamo creado exitosamente" y un ID
        Assert.assertTrue(textoMensaje.contains("Préstamo creado exitosamente"),
                "El mensaje debe indicar que el préstamo fue creado exitosamente");
        Assert.assertTrue(textoMensaje.contains("ID"),
                "El mensaje debe contener el ID del préstamo creado");

        System.out.println("✅ TEST TC#11 PASSED: Préstamo creado exitosamente");
    }

    /****************************************
     * Test Case #13: Verificar en "Consultar Préstamo" funcionalidad de acciones en
     * tabla
     * 
     * Historia de Usuario:
     * Como funcionario del banco, quiero consultar los detalles de un préstamo
     * para revisar la información del cliente y condiciones del préstamo.
     * 
     * Criterio de Aceptación:
     * Al hacer clic en el botón "Ver" de un préstamo, se debe mostrar un modal
     * con toda la información detallada del préstamo.
     * 
     * Pasos:
     * 1. Ingresar a la página de "Consultar Préstamos"
     * 2. Localizar un préstamo en la tabla
     * 3. Hacer clic en el botón "Ver"
     * 4. Verificar que el modal se muestra con los detalles
     * 5. Cerrar el modal
     * 
     * Resultado Esperado:
     * El modal muestra correctamente la información del préstamo y se puede cerrar.
     ****************************************/
    @Test(dependsOnMethods = { "testTC11_SolicitarPrestamoExitoso" })
    public void testTC13_ConsultarPrestamoConModal() {

        /************** 1. Preparación de la prueba ***************/

        // Paso 1: Navegar a la página de Consultar Préstamos
        String obtenerPrestamosUrl = FRONTEND_URL + "/obtener-prestamos";
        driver.get(obtenerPrestamosUrl);

        System.out.println("✓ Navegado a: " + obtenerPrestamosUrl);

        // Esperar a que la tabla cargue
        esperarSegundos(3);

        /************** 2. Lógica de la prueba ***************/

        // Paso 2: Verificar que hay préstamos en la tabla
        WebElement tabla = driver.findElement(By.cssSelector(".tabla-prestamos"));
        Assert.assertTrue(tabla.isDisplayed(), "La tabla de préstamos debe estar visible");
        System.out.println("✓ Tabla de préstamos visible");

        // Paso 3: Localizar el primer botón "Ver" en la tabla
        WebElement botonVer = driver.findElement(By.xpath("//button[contains(text(), 'Ver')]"));

        // Obtener el ID del préstamo antes de hacer clic (de la misma fila)
        WebElement filaPrestamo = botonVer.findElement(By.xpath("./ancestor::tr"));
        WebElement celdaId = filaPrestamo.findElement(By.xpath("./td[1]"));
        String prestamoId = celdaId.getText();

        System.out.println("✓ Préstamo seleccionado ID: " + prestamoId);

        // Paso 4: Hacer clic en el botón "Ver"
        botonVer.click();
        System.out.println("✓ Clic en botón 'Ver'");

        esperarSegundos(2);

        /************ 3. Verificación del resultado esperado - Assert ***************/

        // Verificar que el modal se muestra
        WebElement modal = driver.findElement(By.cssSelector(".modal-overlay"));
        Assert.assertTrue(modal.isDisplayed(), "El modal debe mostrarse");
        System.out.println("✓ Modal visible");

        // Verificar que el modal contiene el contenido correcto
        WebElement modalContenido = driver.findElement(By.cssSelector(".modal-contenido"));
        String tituloModal = modalContenido.findElement(By.tagName("h3")).getText();

        Assert.assertTrue(tituloModal.contains("Detalles del Préstamo"),
                "El modal debe mostrar 'Detalles del Préstamo'");
        Assert.assertTrue(tituloModal.contains(prestamoId),
                "El modal debe mostrar el ID del préstamo: " + prestamoId);

        System.out.println("✓ Título del modal correcto: " + tituloModal);

        // Verificar que existen detalles del préstamo en el modal
        WebElement detallesPrestamo = modalContenido.findElement(By.cssSelector(".detalles-prestamo"));
        Assert.assertTrue(detallesPrestamo.isDisplayed(), "Los detalles del préstamo deben mostrarse");

        // Paso 5: Cerrar el modal
        WebElement botonCerrar = modalContenido.findElement(By.xpath("//button[contains(text(), 'Cerrar')]"));
        botonCerrar.click();
        System.out.println("✓ Clic en botón 'Cerrar'");

        esperarSegundos(1);

        // Verificar que el modal se cerró (ya no es visible)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        boolean modalCerrado = wait.until(ExpectedConditions.invisibilityOf(modal));

        Assert.assertTrue(modalCerrado, "El modal debe cerrarse correctamente");
        System.out.println("✓ Modal cerrado correctamente");

        System.out.println("✅ TEST TC#13 PASSED: Modal de detalles funciona correctamente");
    }

    /****************************************
     * Test Case #14: Verificar en "Cancelar Préstamo" funcionalidad de actualizar
     * préstamo
     * 
     * Historia de Usuario:
     * Como funcionario del banco, quiero cancelar un préstamo
     * para eliminar préstamos incorrectos o cancelados por el cliente.
     * 
     * Criterio de Aceptación:
     * El sistema debe permitir seleccionar un préstamo, confirmar la eliminación,
     * y mostrar un mensaje de éxito cuando el préstamo es eliminado.
     * 
     * Pasos:
     * 1. Ingresar a la página de "Cancelar Préstamo"
     * 2. Seleccionar un préstamo de la lista
     * 3. Verificar que aparece la sección de confirmación
     * 4. Hacer clic en "Confirmar Eliminación"
     * 
     * Resultado Esperado:
     * El sistema muestra "Préstamo eliminado correctamente" y el préstamo
     * ya no aparece en la lista.
     ****************************************/
    @Test(dependsOnMethods = { "testTC13_ConsultarPrestamoConModal" })
    public void testTC14_CancelarPrestamoExitoso() {

        /************** 1. Preparación de la prueba ***************/

        // Paso 1: Navegar a la página de Cancelar Préstamo
        String eliminarPrestamoUrl = FRONTEND_URL + "/eliminar-prestamo";
        driver.get(eliminarPrestamoUrl);

        System.out.println("✓ Navegado a: " + eliminarPrestamoUrl);

        // Esperar a que la lista cargue
        esperarSegundos(3);

        /************** 2. Lógica de la prueba ***************/

        // Paso 2: Verificar que hay préstamos en la lista
        WebElement listaPrestamos = driver.findElement(By.cssSelector(".lista-prestamos"));
        Assert.assertTrue(listaPrestamos.isDisplayed(), "La lista de préstamos debe estar visible");
        System.out.println("✓ Lista de préstamos visible");

        // Contar cuántos préstamos hay antes de eliminar
        int cantidadInicial = driver.findElements(By.cssSelector(".card-prestamo")).size();
        System.out.println("✓ Cantidad de préstamos antes de eliminar: " + cantidadInicial);

        // Paso 3: Seleccionar el primer préstamo de la lista (hacer clic en la card)
        WebElement primerPrestamo = driver.findElement(By.cssSelector(".card-prestamo"));

        // Obtener el ID del préstamo antes de seleccionarlo
        String prestamoId = primerPrestamo.findElement(By.tagName("h3")).getText();
        System.out.println("✓ Préstamo a eliminar: " + prestamoId);

        // Hacer clic en la card para seleccionarlo
        primerPrestamo.click();
        System.out.println("✓ Préstamo seleccionado");

        esperarSegundos(2);

        // Paso 4: Verificar que aparece la sección de confirmación
        WebElement seccionConfirmacion = driver.findElement(By.cssSelector(".confirmacion-container"));
        Assert.assertTrue(seccionConfirmacion.isDisplayed(),
                "La sección de confirmación debe mostrarse");
        System.out.println("✓ Sección de confirmación visible");

        // Verificar que el título dice "Confirmar Eliminación"
        WebElement tituloConfirmacion = seccionConfirmacion.findElement(By.cssSelector(".subtitulo"));
        Assert.assertTrue(tituloConfirmacion.getText().contains("Confirmar Eliminación"),
                "El título debe indicar 'Confirmar Eliminación'");

        // Paso 5: Hacer clic en el botón "Confirmar Eliminación"
        WebElement botonConfirmar = driver.findElement(By.xpath("//button[contains(text(), 'Confirmar Eliminación')]"));
        botonConfirmar.click();
        System.out.println("✓ Clic en 'Confirmar Eliminación'");

        esperarSegundos(3);

        /************ 3. Verificación del resultado esperado - Assert ***************/

        // El mensaje de éxito aparece brevemente y luego se oculta porque
        // prestamoSeleccionado se vuelve null
        // Por lo tanto, verificamos directamente que el préstamo desapareció de la
        // lista

        System.out.println("✓ Esperando a que el préstamo se elimine de la lista...");

        // Esperar a que la confirmación desaparezca (señal de que la eliminación
        // finalizó)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".confirmacion-container")));

        System.out.println("✓ Sección de confirmación cerrada - eliminación completada");

        // Esperar un momento adicional para que la lista se actualice
        esperarSegundos(2);

        // Contar cuántos préstamos hay después de eliminar
        int cantidadFinal = driver.findElements(By.cssSelector(".card-prestamo")).size();
        System.out.println("✓ Cantidad de préstamos después de eliminar: " + cantidadFinal);

        // Verificar que la cantidad disminuyó en 1
        Assert.assertEquals(cantidadFinal, cantidadInicial - 1,
                "La cantidad de préstamos debe disminuir en 1 después de eliminar");

        System.out.println("✓ Préstamo eliminado exitosamente - verificado por desaparición de la lista");

        System.out.println("✅ TEST TC#14 PASSED: Préstamo eliminado exitosamente");
    }

    /************** Método auxiliar para esperar ***************/

    /**
     * Método helper para pausar la ejecución por un número de segundos.
     * Útil para dar tiempo a que los elementos de la UI se rendericen.
     * 
     * @param segundos Número de segundos a esperar
     */
    private void esperarSegundos(int segundos) {
        try {
            TimeUnit.SECONDS.sleep(segundos);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
