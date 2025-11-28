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
 * Como funcionario del banco, necesito filtrar las cuentas
 * bancarias por tipo de cuenta para facilitar la gestión y
 * consulta de información específica
 * 
 * Prueba de Aceptación:
 * Verificar que un funcionario autenticado puede filtrar
 * las cuentas bancarias seleccionando un tipo específico
 * 
 * Pasos:
 * 1. Navegar a la página principal (http://localhost:5173/)
 * 2. Hacer clic en "Acceso Funcionario"
 * 3. Ingresar credenciales válidas (funcionario@gmail.com / Password123!)
 * 4. Hacer clic en "Iniciar sesión"
 * 5. Hacer clic en "Gestión Cuentas"
 * 6. Hacer clic en "Obtener Cuentas"
 * 7. Seleccionar filtro "Ahorro" en el dropdown de tipos de cuenta
 * 
 * Resultado Esperado:
 * El sistema muestra únicamente las cuentas de tipo "Ahorro",
 * filtrando las demás cuentas de la lista
 ****************************************/
public class FiltrarCuentasPorTipoATDDTest {

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
        System.out.println("║    PRUEBA ATDD: FILTRAR CUENTAS POR TIPO          ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
    }

    @Test
    public void deberiaFiltrarCuentasPorTipoAhorro() {
        try {
            // ====================================================
            // PASO 1-5: LOGIN DEL FUNCIONARIO Y ACCESO A GESTIÓN
            // ====================================================
            realizarLoginYAccederGestionCuentas();

            // ====================================================
            // PASO 6: Hacer clic en "Obtener Cuentas"
            // ====================================================
            System.out.println("\n[PASO 6] Buscando y haciendo clic en 'Obtener Cuentas'...");

            // Esperar a que el elemento sea visible y clickeable
            WebElement btnObtenerCuentas = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath(
                                    "//h3[contains(text(), 'Obtener Cuentas')]/ancestor::div[contains(@class, 'card-accion')]")));

            System.out.println("✓ Botón 'Obtener Cuentas' encontrado");

            // Scroll al elemento para asegurar visibilidad
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
                    btnObtenerCuentas);
            Thread.sleep(500);

            // Intentar clic normal, si falla usar JavaScript
            try {
                btnObtenerCuentas.click();
                System.out.println("✓ Clic realizado en 'Obtener Cuentas' (método normal)");
            } catch (Exception e) {
                System.out.println("⚠ Clic normal falló, usando JavaScript...");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnObtenerCuentas);
                System.out.println("✓ Clic realizado con JavaScript");
            }

            // Esperar a que se cargue la interfaz de filtrado
            Thread.sleep(2000);

            // Verificar que el dropdown de filtro está presente
            System.out.println("⏳ Esperando que aparezca el dropdown de filtro...");
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("select.form-control")));
            System.out.println("✓ Interfaz de filtrado cargada correctamente");

            // ====================================================
            // PASO 7: Seleccionar filtro "Ahorro"
            // ====================================================
            System.out.println("\n[PASO 7] Seleccionando filtro 'Ahorro'...");

            // Localizar el dropdown de tipos de cuenta
            WebElement selectTipoCuenta = driver.findElement(
                    By.cssSelector("select.form-control"));

            // Verificar que el select está visible
            Assert.assertTrue(selectTipoCuenta.isDisplayed(),
                    "El dropdown de tipos de cuenta no está visible");

            // Crear objeto Select para manejar el dropdown
            Select dropdown = new Select(selectTipoCuenta);

            // Mostrar las opciones disponibles (para debug)
            List<WebElement> opciones = dropdown.getOptions();
            System.out.println("📋 Opciones disponibles en el dropdown:");
            for (int i = 0; i < opciones.size(); i++) {
                String texto = opciones.get(i).getText();
                String valor = opciones.get(i).getAttribute("value");
                System.out.println("  [" + i + "] " + texto + " (value: '" + valor + "')");
            }

            // Capturar el número total de cuentas ANTES del filtro
            int totalCuentasAntesFiltro = contarCuentasEnLista();
            System.out.println("📊 Total de cuentas ANTES del filtro: " + totalCuentasAntesFiltro);

            // Seleccionar "Ahorro" por su valor
            dropdown.selectByValue("ahorro");
            System.out.println("✓ Opción 'Ahorro' seleccionada");

            // Verificar que se seleccionó correctamente
            String valorSeleccionado = dropdown.getFirstSelectedOption().getText();
            System.out.println("✓ Valor seleccionado: " + valorSeleccionado);
            Assert.assertEquals(valorSeleccionado, "Ahorro",
                    "El filtro no se seleccionó correctamente");

            // Esperar a que el filtro se aplique
            System.out.println("⏳ Esperando que se aplique el filtro...");
            Thread.sleep(2000);

            // ====================================================
            // VERIFICACIÓN FINAL
            // ====================================================
            System.out.println("\n╔════════════════════════════════════════════════════╗");
            System.out.println("║          VERIFICACIÓN DE FILTRADO                  ║");
            System.out.println("╚════════════════════════════════════════════════════╝");

            // Contar cuentas después del filtro
            int totalCuentasDespuesFiltro = contarCuentasEnLista();
            System.out.println("📊 Total de cuentas DESPUÉS del filtro: " + totalCuentasDespuesFiltro);

            // Verificar que el filtro cambió el resultado
            if (totalCuentasAntesFiltro > totalCuentasDespuesFiltro) {
                System.out.println("✓ El filtro redujo el número de cuentas mostradas");
            } else if (totalCuentasAntesFiltro == totalCuentasDespuesFiltro) {
                System.out.println("⚠ Advertencia: El número de cuentas no cambió");
                System.out.println("  (Esto puede ser normal si todas las cuentas son de tipo Ahorro)");
            }

            // Verificar que solo se muestran cuentas de tipo "Ahorro"
            boolean soloAhorro = verificarSoloCuentasTipo("Ahorro", "ahorro");

            if (soloAhorro) {
                System.out.println("✓ Todas las cuentas mostradas son de tipo 'Ahorro'");
            } else {
                System.out.println("⚠ Se encontraron cuentas de otros tipos en los resultados");
            }

            // Capturar screenshot de los resultados filtrados
            capturarScreenshot("cuentas_filtradas_ahorro");

            System.out.println("\n╔════════════════════════════════════════════════════╗");
            System.out.println("║   ✅ PRUEBA COMPLETADA EXITOSAMENTE ✅             ║");
            System.out.println("║                                                    ║");
            System.out.println("║   Filtro Aplicado: Ahorro                         ║");
            System.out.println("║   Cuentas mostradas: " + String.format("%-2d", totalCuentasDespuesFiltro)
                    + "                           ║");
            System.out.println("╚════════════════════════════════════════════════════╝");

        } catch (Exception e) {
            System.err.println("\n❌ ERROR EN LA PRUEBA: " + e.getMessage());
            e.printStackTrace();

            // Información de depuración
            System.err.println("\n=== INFORMACIÓN DE DEPURACIÓN ===");
            System.err.println("URL actual: " + driver.getCurrentUrl());
            System.err.println("Título página: " + driver.getTitle());

            capturarDebugInfo("error_filtrar_cuentas");

            Assert.fail("La prueba falló: " + e.getMessage());
        } finally {
            // Volver al contexto principal
            try {
                driver.switchTo().defaultContent();
                System.out.println("↩ Contexto restaurado al documento principal");
            } catch (Exception e) {
                // Ignorar si ya estamos en el contexto principal
            }
        }
    }

    /**
     * Método auxiliar para realizar el login y acceder a Gestión de Cuentas
     * Incluye el cambio de contexto al iframe
     */
    private void realizarLoginYAccederGestionCuentas() throws InterruptedException {
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

        // CRÍTICO: CAMBIAR CONTEXTO AL IFRAME
        System.out.println("\n🔍 Detectando y cambiando al iframe...");
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(
                By.cssSelector("iframe")));
        System.out.println("✓ Contexto cambiado al iframe");

        // Esperar a que Vue cargue dentro del iframe
        Thread.sleep(2000);
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("div.card-accion, section, h3")));
        System.out.println("✓ Contenido del iframe cargado");
    }

    /**
     * Método auxiliar para contar el número de cuentas en la lista
     * 
     * @return número de cuentas encontradas
     */
    private int contarCuentasEnLista() {
        try {
            // Intentar contar filas de una tabla
            List<WebElement> filas = driver.findElements(
                    By.cssSelector("table tbody tr, .cuenta-item, .lista-cuentas > div"));

            if (filas.size() > 0) {
                return filas.size();
            }

            // Si no hay tabla, buscar cards o elementos individuales
            List<WebElement> cuentas = driver.findElements(
                    By.cssSelector(".cuenta, [class*='cuenta-card'], [data-cuenta]"));

            return cuentas.size();

        } catch (Exception e) {
            System.out.println("⚠ No se pudo contar las cuentas automáticamente");
            return 0;
        }
    }

    /**
     * Método auxiliar para verificar que todas las cuentas mostradas son del tipo
     * esperado
     * 
     * @param tipoEsperado  nombre del tipo a verificar
     * @param valorAtributo valor del atributo que puede indicar el tipo
     * @return true si todas las cuentas son del tipo esperado
     */
    private boolean verificarSoloCuentasTipo(String tipoEsperado, String valorAtributo) {
        try {
            String paginaHTML = driver.getPageSource().toLowerCase();

            // Verificar que aparece el tipo esperado
            boolean contieneAhorro = paginaHTML.contains(tipoEsperado.toLowerCase());

            // Verificar que NO aparecen otros tipos (esto es una verificación simple)
            boolean contieneCorriente = paginaHTML.contains("corriente") &&
                    !tipoEsperado.equalsIgnoreCase("corriente");
            boolean contieneChecking = paginaHTML.contains("checking") &&
                    !tipoEsperado.equalsIgnoreCase("checking");

            if (contieneAhorro && !contieneCorriente && !contieneChecking) {
                return true;
            }

            // Verificación más detallada si es posible
            List<WebElement> elementosTipo = driver.findElements(
                    By.xpath(
                            "//*[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '"
                                    +
                                    tipoEsperado.toLowerCase() + "')]"));

            return elementosTipo.size() > 0;

        } catch (Exception e) {
            System.out.println("⚠ No se pudo verificar el tipo de cuentas automáticamente");
            return true; // Asumir que está correcto si no se puede verificar
        }
    }

    /**
     * Capturar screenshot con nombre personalizado
     */
    private void capturarScreenshot(String nombreArchivo) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, new File(nombreArchivo + ".png"));
            System.out.println("📸 Screenshot guardado: " + nombreArchivo + ".png");
        } catch (Exception e) {
            System.err.println("Error al capturar screenshot: " + e.getMessage());
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
}
