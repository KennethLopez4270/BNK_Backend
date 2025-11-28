# Instrucciones para Ejecutar Pruebas ATDD

## 🚀 Pre-requisitos

### 1. Instalar Dependencias del Frontend
```bash
cd "e:\Trabajos Ronald 2\Trabajos UCB S8\Proyecto Gestion de Calidad\BNK_Frontend\frt1-clientes"
npm install
```

### 2. Insertar Datos de Prueba en la Base de Datos

Conectarse a PostgreSQL y ejecutar:
```sql
-- Ver archivo: src/test/resources/datos-prueba-atdd.sql
```

O desde la línea de comandos:
```bash
psql -U postgres -d bnk_clients_db -f "e:\Trabajos Ronald 2\Trabajos UCB S8\Proyecto Gestion de Calidad\BNK_Backend\BNK-msclient\src\test\resources\datos-prueba-atdd.sql"
```

### 3. Iniciar el Backend (Puerto 8081)
```bash
cd "e:\Trabajos Ronald 2\Trabajos UCB S8\Proyecto Gestion de Calidad\BNK_Backend\BNK-msclient"
./mvnw.cmd spring-boot:run
```

Verificar que esté corriendo:
- http://localhost:8081/api/clients

### 4. Iniciar el Frontend (Puerto 5173)
```bash
cd "e:\Trabajos Ronald 2\Trabajos UCB S8\Proyecto Gestion de Calidad\BNK_Frontend\frt1-clientes"
npm run dev
```

Verificar que esté corriendo:
- http://localhost:5173

---

## ▶️ Ejecutar las Pruebas ATDD

### Opción 1: Ejecutar TODAS las pruebas ATDD
```bash
cd "e:\Trabajos Ronald 2\Trabajos UCB S8\Proyecto Gestion de Calidad\BNK_Backend\BNK-msclient"
./mvnw.cmd test -Dtest="com.bank.client_service.atdd.*"
```

### Opción 2: Ejecutar pruebas individuales

**Prueba #1: Registro de Cliente (TC 4)**
```bash
./mvnw.cmd test -Dtest=RegistrarClienteATDDTest
```

**Prueba #2: Actualización de Cliente (TC 9)**
```bash
./mvnw.cmd test -Dtest=ActualizarClienteATDDTest
```

**Prueba #3: Eliminación de Cliente (TC 11)**
```bash
./mvnw.cmd test -Dtest=EliminarClienteATDDTest
```

---

## 📊 Resultados Esperados

Al ejecutar todas las pruebas ATDD, deberías ver:

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.bank.client_service.atdd.RegistrarClienteATDDTest
[INFO] ✓ Mensaje de éxito mostrado: Cliente creado exitosamente
[INFO] ✓ Cliente registrado encontrado en la tabla
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Running com.bank.client_service.atdd.ActualizarClienteATDDTest
[INFO] ✓ Datos del cliente cargados correctamente
[INFO] ✓ Mensaje de éxito mostrado: Cliente actualizado exitosamente
[INFO] ✓ Datos actualizados verificados en la tabla
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Running com.bank.client_service.atdd.EliminarClienteATDDTest
[INFO] ✓ Alert de confirmación mostrado
[INFO] ✓ Cliente eliminado exitosamente
[INFO] ✓ Cliente eliminado correctamente - NO aparece en la tabla
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] BUILD SUCCESS
```

---

## 🔧 Troubleshooting

### Error: "Connection refused" al backend
**Solución:** Asegúrate de que el backend esté corriendo en puerto 8081
```bash
netstat -ano | findstr :8081
```

### Error: "No se puede acceder a http://localhost:5173"
**Solución:** El frontend debe estar corriendo con Vite
```bash
# Verificar que el proceso esté activo
netstat -ano | findstr :5173
```

### Error: "Element not found" en Selenium
**Solución:** Aumentar el tiempo de espera en las pruebas o verificar que los elementos existan en el frontend

### Error: "Alert not present"
**Solución:** Aumentar el sleep antes de manejar el alert
```java
TimeUnit.SECONDS.sleep(1);
```

---

## 🧹 Limpiar Datos de Prueba (Opcional)

Después de ejecutar las pruebas, puedes limpiar los datos:

```sql
-- Eliminar clientes de prueba pre-cargados
DELETE FROM clients WHERE document_number IN ('98765432', '11223344', '55667788');

-- Eliminar cliente creado por la prueba de registro
DELETE FROM clients WHERE document_number = '12345678';
```

---

## 📝 Notas Importantes

1. **Orden de ejecución**: Las pruebas pueden ejecutarse en cualquier orden, son independientes.

2. **Cliente eliminado**: La prueba #3 eliminará el cliente con documento 11223344. Si quieres ejecutarla múltiples veces, deberás volver a insertar ese cliente.

3. **Cliente creado**: La prueba #1 creará un cliente con documento 12345678. Si quieres ejecutarla múltiples veces, primero elimina ese cliente de la BD.

4. **ChromeDriver**: WebDriverManager descargará automáticamente el driver correcto. Asegúrate de tener Google Chrome instalado.

5. **Visibilidad**: Las pruebas abrirán ventanas de Chrome. Puedes ver el navegador ejecutando los pasos automáticamente.
