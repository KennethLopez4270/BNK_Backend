package com.bank.account_service.atdd;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

/****************************************
 * Historia de Usuario:
 *   Como funcionario del banco, necesito acceder al módulo de 
 *   Gestión de Cuentas para administrar las cuentas bancarias
 * 
 * Prueba de Aceptación:
 *   Verificar que un funcionario autenticado puede acceder 
 *   exitosamente a la pestaña de Gestión de Cuentas
 * 
 * Pasos:
 *   1. Navegar a la página principal (http://localhost:5173/)
 *   2. Hacer clic en "Acceso Funcionario"
 *   3. Ingresar credenciales válidas:
 *      - Email: funcionario@gmail.com
 *      - Password: Password123!
 *   4. Hacer clic en "Iniciar sesión"
 *   5. Hacer clic en el botón "Gestión Cuentas"
 *   6. Verificar que se carga la página de Gestión de Cuentas
 * 
 * Resultado Esperado:
 *   El funcionario accede exitosamente a la sección de 
 *   Gestión de Cuentas y puede ver la interfaz correspondiente
 ****************************************/
public class AccesoGestionCuentasATDDTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeTest
    public void setup() {
        // Configura automáticamente el driver de Chrome
        WebDriverManager.chromedriver().setup();
        
        // Configurar opciones de Chrome (opcional)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        // options.addArguments("--headless"); // Descomenta para ejecutar sin ventana
        
        driver = new ChromeDriver(options);
        
        // Configurar espera explícita (máximo 10 segundos)
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        
        System.out.println("=== INICIO DE LA PRUEBA ATDD ===");
    }

    @Test
    public void deberiaAccederAGestionDeCuentasComoFuncionario() {
        try {
            // ====================================================
            // PASO 1: Navegar a la página principal
            // ====================================================
            System.out.println("Paso 1: Navegando a http://localhost:5173/");
            driver.get("http://localhost:5173/");
            
            // Esperar a que la página cargue
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h3")));
            
            // Verificar que estamos en la página correcta
            String urlActual = driver.getCurrentUrl();
            Assert.assertTrue(urlActual.contains("localhost:5173"), 
                "No se cargó la página principal correctamente");
            System.out.println("✓ Página principal cargada correctamente");

            // ====================================================
            // PASO 2: Hacer clic en "Acceso Funcionario"
            // ====================================================
            System.out.println("\nPaso 2: Haciendo clic en 'Acceso Funcionario'");
            
            // Buscar el h3 que contiene "Acceso Funcionario"
            WebElement btnAccesoFuncionario = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//h3[contains(text(), 'Acceso Funcionario')]")
                )
            );
            
            btnAccesoFuncionario.click();
            System.out.println("✓ Clic realizado en 'Acceso Funcionario'");
            
            // Esperar a que cargue el formulario de login
            Thread.sleep(3000); // Espera para animaciones de Vue

            // ====================================================
            // PASO 3: Llenar el formulario de login
            // ====================================================
            System.out.println("\nPaso 3: Llenando formulario de autenticación");
            
            // Esperar a que aparezca el campo de email
            WebElement campoEmail = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("input[type='email'][placeholder='Correo electrónico']")
                )
            );
            
            // Limpiar y llenar el campo de email
            campoEmail.clear();
            campoEmail.sendKeys("funcionario@gmail.com");
            System.out.println("✓ Email ingresado: funcionario@gmail.com");
            
            // Buscar y llenar el campo de contraseña
            WebElement campoPassword = driver.findElement(
                By.cssSelector("input[type='password'][placeholder='Contraseña']")
            );
            
            campoPassword.clear();
            campoPassword.sendKeys("Password123!");
            System.out.println("✓ Contraseña ingresada: ********");

            // ====================================================
            // PASO 4: Hacer clic en "Iniciar sesión"
            // ====================================================
            System.out.println("\nPaso 4: Haciendo clic en 'Iniciar sesión'");
            
            WebElement btnIniciarSesion = driver.findElement(
                By.cssSelector("button[type='submit'].btn-accion")
            );
            
            btnIniciarSesion.click();
            System.out.println("✓ Clic realizado en 'Iniciar sesión'");
            
            // Esperar a que cargue el dashboard
            Thread.sleep(2000); // Espera para autenticación y redirección

            // ====================================================
            // PASO 5: Hacer clic en "Gestión Cuentas"
            // ====================================================
            System.out.println("\nPaso 5: Haciendo clic en 'Gestión Cuentas'");
            
            // Esperar a que aparezca el botón de Gestión Cuentas
            WebElement btnGestionCuentas = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(@class, 'nav-btn')]/i[@class='bi bi-arrow-left-right']/parent::button")
                )
            );
            
            btnGestionCuentas.click();
            System.out.println("✓ Clic realizado en 'Gestión Cuentas'");
            
            // Esperar a que cargue la página de gestión
            Thread.sleep(1500);

            // ====================================================
            // VERIFICACIÓN FINAL
            // ====================================================
            System.out.println("\n=== VERIFICACIÓN FINAL ===");
            
            // Verificar que estamos en la página de Gestión de Cuentas
            // Opción 1: Verificar URL
            String urlFinal = driver.getCurrentUrl();
            System.out.println("URL actual: " + urlFinal);
            
            // Opción 2: Verificar que existe algún elemento característico de la página
            // (ajusta el selector según tu página real)
            boolean paginaCargada = driver.findElements(
                By.xpath("//h1[contains(text(), 'Gestión')] | //h2[contains(text(), 'Cuentas')] | //div[contains(@class, 'gestion-cuentas')]")
            ).size() > 0;
            
            Assert.assertTrue(paginaCargada, 
                "No se pudo verificar que se cargó la página de Gestión de Cuentas");
            
            System.out.println("✓ Se accedió exitosamente a Gestión de Cuentas");
            System.out.println("\n=== PRUEBA COMPLETADA EXITOSAMENTE ===");
            
            // Capturar screenshot de éxito (opcional)
            // File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            // FileUtils.copyFile(screenshot, new File("test-success.png"));

        } catch (Exception e) {
            System.err.println("❌ ERROR EN LA PRUEBA: " + e.getMessage());
            e.printStackTrace();
            Assert.fail("La prueba falló: " + e.getMessage());
        }
    }

    @AfterTest
    public void teardown() {
        // Esperar un momento antes de cerrar (para ver el resultado)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Cerrar el navegador
        if (driver != null) {
            System.out.println("\n=== Cerrando navegador ===");
            driver.quit();
        }
    }
}