# La Taberna del Bus de Eventos — "El Gólem Alquímico"

Sistema de una taberna universitaria rediseñado con una **arquitectura basada en eventos**:
ningún componente conoce a otro, todos se comunican únicamente a través de un **Event Bus**.

## Requisitos
- Java **17 o superior** (usa `record`)
- IntelliJ IDEA (o Maven / `javac`)

## Cómo ejecutar

### Opción A: IntelliJ IDEA
1. `File > Open...` y selecciona la carpeta `taberna-event-bus` (IntelliJ detecta el `pom.xml`).
2. Configura el SDK en `File > Project Structure > Project` (Java 17+).
3. Abre `src/main/java/taberna/Main.java` y pulsa el botón verde ▶ junto a `main`.

### Opción B: línea de comandos
```bash
mkdir out
javac -encoding UTF-8 -d out $(find src -name "*.java")     # Linux/Mac
java -cp out taberna.Main
```
En Windows PowerShell:
```powershell
mkdir out
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out taberna.Main
```
La demo dura ~17 segundos. Los colores usan códigos ANSI (funcionan en la consola de IntelliJ).

## Estructura
```
src/main/java/taberna
├── Main.java                  Compone el sistema y muestra las 4 fases de la demo
├── bus/                       Evento, Suscriptor<E>, BusDeEventos (interfaz), EventBus
├── eventos/                   PedidoRealizadoEvent, BebidaServidaEvent,
│                              ComidaPreparadaEvent, BandaTocandoEvent  (records inmutables)
├── dominio/Menu.java          Qué es bebida (Barra) y qué es comida (Cocina)
├── componentes/               SistemaDePedidos, Barra, Cocina, Banda, SistemaDeSonido, PanelLED
│                              + extensiones: SistemaDeHumo, LucesEscenicas, RegistroDeAuditoria
└── util/Consola.java          Salida coloreada, con tiempo y atómica por línea
docs/                          Diagramas PlantUML (.puml y .png): clases, secuencia y componentes
```

## Diseño
- **EventBus**: `suscribir(tipo, suscriptor)`, `desuscribir(tipo, suscriptor)` y `publicar(evento)`.
  Es thread-safe, entrega de forma asíncrona (un suscriptor lento no bloquea a nadie), aísla las
  excepciones de los suscriptores y permite suscribirse a `Evento.class` para recibir **todo**.
- **Eventos inmutables**: `record` con validación y copia defensiva de la lista de items.
- **Desacoplamiento total**: los componentes solo reciben un `BusDeEventos` (interfaz). Ninguno
  importa a otro componente. Solo `Main` conoce todas las piezas para conectarlas.
- **Extensibilidad (Open/Closed)**: `SistemaDeHumo`, `LucesEscenicas` y `RegistroDeAuditoria` se
  agregan en `Main` sin modificar `Banda`, `EventBus` ni ninguna otra clase existente.

## Qué muestra la demo
1. **Fase 1**: pedido de la Mesa 5 (cerveza y hamburguesa → bebida a los 2 s, comida a los 5 s),
   un segundo pedido concurrente y la banda tocando cada 5 s; reaccionan sonido y panel LED.
2. **Fase 2**: se agregan los 3 nuevos suscriptores; el humo se activa con
   "Through the Fire and Flames".
3. **Fase 3**: `SistemaDeSonido` se desuscribe y la siguiente canción ya no lo activa.
4. **Fase 4**: cierre ordenado y resumen de auditoría.

## Diagramas
Los `.puml` de `docs/` se pueden regenerar con PlantUML (`java -jar plantuml.jar docs/*.puml`).
