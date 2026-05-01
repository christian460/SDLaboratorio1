# Laboratorio de Sockets en Java

## Descripción general

Este laboratorio desarrolla una aplicación **cliente-servidor** en Java usando **sockets TCP**.  
El servidor escucha en un puerto (por ejemplo, `5000`) y múltiples clientes se conectan para recibir mensajes enviados por el servidor.

## Tecnologías utilizadas

- **Java**
- **Sockets TCP** (`java.net`)
- **Hilos (Threads)** para atención concurrente de clientes (si aplica)

## Estructura del proyecto

- `Servidor.java`: gestiona conexiones entrantes y envía mensajes a cada cliente.
- `Cliente.java`: se conecta al servidor y recibe el mensaje enviado.

## Funcionamiento

1. El servidor inicia y queda escuchando en un puerto (ejemplo: `5000`).
2. Los clientes se conectan al servidor.
3. El servidor atiende un número limitado de clientes.
4. Cada cliente recibe un saludo con formato: **"Hola cliente N"**.

## Instrucciones de ejecución

### Compilar

```bash
javac Servidor.java
javac Cliente.java
```

### Ejecutar servidor

```bash
java Servidor
```

### Ejecutar cliente (en otra consola)

```bash
java Cliente
```

## Resultados esperados

- El servidor muestra en consola mensajes de conexión de clientes.
- Los clientes reciben el saludo enviado por el servidor.
- Al alcanzar el número definido de clientes, el servidor finaliza.

## Aprendizajes

- Comprensión del modelo cliente-servidor.
- Uso práctico de sockets en Java.
- Manejo básico de conexiones de red en una arquitectura distribuida.
