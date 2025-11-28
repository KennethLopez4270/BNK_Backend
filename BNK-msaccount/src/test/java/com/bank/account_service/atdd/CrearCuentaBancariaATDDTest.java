package com.bank.account_service.atdd;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.PrintWriter;

/****************************************
 * Historia de Usuario:
 * Como funcionario del banco, necesito crear una nueva cuenta
 * bancaria para un cliente, especificando el tipo de cuenta y
 * el saldo inicial
 * 
 * Prueba de Aceptación:
 * Verificar que un funcionario autenticado puede crear una
 * cuenta bancaria completando todos los datos requeridos
 * 
 * Pasos:
 * 1. Navegar a la página principal (http://localhost:5173/)
 * 2. Hacer clic en "Acceso Funcionario"
 * 3. Ingresar credenciales válidas (funcionario@gmail.com / Password123!)
 * 4. Hacer clic en "Iniciar sesión"
 * 5. Hacer clic en "Gestión Cuentas"
 * 6. Hacer clic en "Crear Cuenta"
 * 7. Llenar ID Cliente: 2000
 * 8. Llenar Número de Cuenta: 1003-1003
 * 9. Seleccionar Tipo de Cuenta: Corriente
 * 10. Ingresar Saldo Inicial: 8000
 * 11. Hacer clic en "Guardar Cuenta"
 * 12. Confirmar con "Aceptar" que se guardó exitosamente
 * 
 * Resultado Esperado:
 * La cuenta bancaria se crea exitosamente y se almacena en
 * PostgreSQL, mostrando mensaje de confirmación
 ****************************************/
public class CrearCuentaBancariaATDDTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeTest
    public void setup() {
        // Configura automáticamente el driver de Chrome
        WebDriverManager.chromedriver().setup();

        // Configurar opciones de Chrome
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        // options.addArguments("--headless"); // Descomenta para ejecutar sin ventana

        driver = new ChromeDriver(options);

        // Configurar espera explícita (máximo 15 segundos)
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║    PRUEBA ATDD: CREAR CUENTA BANCARIA             ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
    }

    private void realizarLoginComoFuncionario() throws InterruptedException {
        // PASO 1: Navegar a la página principal
        System.out.println("\n[PASO 1] Navegando a http://localhost:5173/");
        driver.get("http://localhost:5173/");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h3")));
        System.out.println("✓ Página principal cargada");

        // PASO 2: Hacer clic en "Acceso Funcionario"
        System.out.println("\n[PASO 2] Haciendo clic en 'Acceso Funcionario'");
        WebElement btnAccesoFuncionario = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//h3[contains(text(), 'Acceso Funcionario')]")));
        btnAccesoFuncionario.click();
        System.out.println("✓ Clic realizado en 'Acceso Funcionario'");
        Thread.sleep(1000);

        // PASO 3: Llenar credenciales
        System.out.println("\n[PASO 3] Ingresando credenciales");
        WebElement campoEmail = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("input[type='email'][placeholder='Correo electrónico']")));
        campoEmail.clear();
        campoEmail.sendKeys("funcionario@gmail.com");
        System.out.println("✓ Email: funcionario@gmail.com");

        WebElement campoPassword = driver.findElement(
                By.cssSelector("input[type='password'][placeholder='Contraseña']"));
        campoPassword.clear();
        campoPassword.sendKeys("Password123!");
        System.out.println("✓ Contraseña: ********");

        // PASO 4: Iniciar sesión
        System.out.println("\n[PASO 4] Haciendo clic en 'Iniciar sesión'");
        WebElement btnIniciarSesion = driver.findElement(
                By.cssSelector("button[type='submit'].btn-accion"));
        btnIniciarSesion.click();
        System.out.println("✓ Clic realizado en 'Iniciar sesión'");
        Thread.sleep(2000);

        // PASO 5: Ir a Gestión Cuentas
        System.out.println("\n[PASO 5] Haciendo clic en 'Gestión Cuentas'");
        WebElement btnGestionCuentas = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath(
                                "//button[contains(@class, 'nav-btn')]/i[@class='bi bi-arrow-left-right']/parent::button")));
        btnGestionCuentas.click();
        System.out.println("✓ Acceso a Gestión Cuentas");
        Thread.sleep(2000);

        // ====================================================
        // CRÍTICO: CAMBIAR CONTEXTO AL IFRAME
        // ====================================================
        System.out.println("\n🔍 Detectando iframe...");

        // Esperar a que el iframe esté presente
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(
                By.cssSelector("iframe")));

        System.out.println("✓ Contexto cambiado al iframe");

        // Esperar a que Vue cargue dentro del iframe
        Thread.sleep(2000);

        // Verificar que estamos dentro del iframe
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("div.card-accion, section, h3")));

        System.out.println("✓ Contenido del iframe cargado");
    }

    // PASO 6 SIMPLIFICADO - Ya estamos en el contexto correcto
    @Test
    public void deberiaCrearCuentaBancariaExitosamente() {
        try {
            // PASO 1-5: LOGIN DEL FUNCIONARIO
            realizarLoginComoFuncionario();

            // ====================================================
            // PASO 6: Hacer clic en "Crear Cuenta" (Ya estamos en el iframe)
            // ====================================================
            System.out.println("\n[PASO 6] Buscando botón 'Crear Cuenta'...");

            // Ahora los selectores funcionarán porque estamos en el iframe
            WebElement btnCrearCuenta = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath(
                                    "//h3[contains(text(), 'Crear Cuenta')]/ancestor::div[contains(@class, 'card-accion')]")));

            System.out.println("✓ Botón 'Crear Cuenta' encontrado");

            // Scroll y clic
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
                    btnCrearCuenta);
            Thread.sleep(500);

            btnCrearCuenta.click();
            System.out.println("✓ Clic realizado en 'Crear Cuenta'");
            Thread.sleep(2000);

            // ====================================================
            // PASO 7-11: Continúa igual (ya estamos en el iframe)
            // ====================================================
            System.out.println("\n[PASO 7] Ingresando ID Cliente: 2000");
            WebElement campoIdCliente = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector("input[type='number'][placeholder*='1001']")));
            campoIdCliente.clear();
            campoIdCliente.sendKeys("2000");
            System.out.println("✓ ID Cliente ingresado: 2000");

            System.out.println("\n[PASO 8] Ingresando Número de Cuenta: 1003-1003");
            WebElement campoNumeroCuenta = driver.findElement(
                    By.cssSelector("input[type='text'][placeholder*='1001-0001']"));
            campoNumeroCuenta.clear();
            campoNumeroCuenta.sendKeys("1003-1003");
            System.out.println("✓ Número de Cuenta ingresado: 1003-1003");

            System.out.println("\n[PASO 9] Seleccionando Tipo de Cuenta: Corriente");
            WebElement selectTipoCuenta = driver.findElement(
                    By.cssSelector("select.form-control"));
            Select dropdown = new Select(selectTipoCuenta);
            dropdown.selectByValue("corriente");
            System.out.println("✓ Tipo de Cuenta seleccionado: Corriente");

            System.out.println("\n[PASO 10] Ingresando Saldo Inicial: 8000");
            WebElement campoSaldoInicial = driver.findElement(
                    By.cssSelector("input[type='number'][min='0'][step='0.01']"));
            campoSaldoInicial.clear();
            campoSaldoInicial.sendKeys("8000");
            System.out.println("✓ Saldo Inicial ingresado: 8000.00");

            Thread.sleep(500);

            System.out.println("\n[PASO 11] Haciendo clic en 'Guardar Cuenta'");
            WebElement btnGuardarCuenta = driver.findElement(
                    By.cssSelector("button[type='submit'].btn-accion"));
            btnGuardarCuenta.click();
            System.out.println("✓ Clic realizado en 'Guardar Cuenta'");
            Thread.sleep(3000);

            System.out.println("\n[PASO 12] Confirmando mensaje de éxito");
            WebElement btnAceptar = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//button[contains(@class, 'btn-accion') and contains(text(), 'Aceptar')]")));
            btnAceptar.click();
            System.out.println("✓ Clic realizado en 'Aceptar'");
            Thread.sleep(1500);

            // VERIFICACIÓN FINAL
            System.out.println("\n╔════════════════════════════════════════════════════╗");
            System.out.println("║   ✅ PRUEBA COMPLETADA EXITOSAMENTE ✅             ║");
            System.out.println("╚════════════════════════════════════════════════════╝");

        } catch (Exception e) {
            System.err.println("\n❌ ERROR: " + e.getMessage());
            e.printStackTrace();

            // Debug: mostrar en qué contexto estamos
            System.err.println("Contexto actual: " + driver.getWindowHandle());
            capturarDebugInfo("error_test");

            Assert.fail("La prueba falló: " + e.getMessage());
        } finally {
            // IMPORTANTE: Volver al contexto principal si es necesario
            try {
                driver.switchTo().defaultContent();
                System.out.println("↩ Contexto restaurado al documento principal");
            } catch (Exception e) {
                // Ignorar si ya estamos en el contexto principal
            }
        }
    }

    @AfterTest
    public void teardown() {
        // Esperar antes de cerrar para ver el resultado
        try {
            System.out.println("\n⏳ Esperando 3 segundos antes de cerrar...");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Cerrar el navegador
        if (driver != null) {
            System.out.println("🔒 Cerrando navegador");
            driver.quit();
        }
    }

    /**
     * Método de debugging para capturar pantalla y HTML
     */
    private void capturarDebugInfo(String nombreArchivo) {
        try {
            // Capturar screenshot
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, new File("debug_" + nombreArchivo + ".png"));
            System.out.println("📸 Screenshot guardado: debug_" + nombreArchivo + ".png");

            // Guardar HTML completo
            String html = driver.getPageSource();
            PrintWriter writer = new PrintWriter("debug_" + nombreArchivo + ".html", "UTF-8");
            writer.println(html);
            writer.close();
            System.out.println("📄 HTML guardado: debug_" + nombreArchivo + ".html");

        } catch (Exception e) {
            System.err.println("Error al capturar debug: " + e.getMessage());
        }
    }

}