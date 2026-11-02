package taberna;

import taberna.bus.EventBus;
import taberna.componentes.Banda;
import taberna.componentes.Barra;
import taberna.componentes.Cocina;
import taberna.componentes.LucesEscenicas;
import taberna.componentes.PanelLED;
import taberna.componentes.RegistroDeAuditoria;
import taberna.componentes.SistemaDeHumo;
import taberna.componentes.SistemaDePedidos;
import taberna.componentes.SistemaDeSonido;
import taberna.util.Consola;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * La Taberna del Bus de Eventos - "El Gólem Alquímico".
 *
 * <p>Este es el ÚNICO lugar que conoce todas las piezas: crea el bus, crea los componentes
 * y los conecta. Ningún componente conoce a otro; solo conocen el bus (por su interfaz).
 */
public final class Main {

    public static void main(String[] args) throws InterruptedException {
        // Para que acentos y símbolos (¡ ♪) se vean bien en la consola de IntelliJ.
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));

        ScheduledExecutorService reloj = Executors.newScheduledThreadPool(4);
        EventBus bus = new EventBus();

        Consola.titulo("FASE 1: La taberna abre sus puertas");

        PanelLED panel = new PanelLED(bus);
        SistemaDeSonido sonido = new SistemaDeSonido(bus);
        Barra barra = new Barra(bus, reloj);
        Cocina cocina = new Cocina(bus, reloj);
        SistemaDePedidos pedidos = new SistemaDePedidos(bus);
        Banda banda = new Banda(bus, reloj, "Los Seguidores de Dijkstra", List.of(
                new Banda.Cancion("El Algoritmo del Amor", 180),
                new Banda.Cancion("Through the Fire and Flames", 442),
                new Banda.Cancion("Segmentation Fault Blues", 205)));

        panel.iniciar();
        sonido.iniciar();
        barra.iniciar();
        cocina.iniciar();

        banda.iniciar(1, 5); // canciones en t = 1, 6, 11, 16 s
        pedidos.realizarPedido(5, "cerveza", "hamburguesa"); // flujo del ejemplo del PDF
        pausa(1500);
        pedidos.realizarPedido(2, "vino", "pizza", "refresco"); // pedido concurrente

        pausa(2000);
        Consola.titulo("FASE 2: Extensibilidad (nuevos suscriptores, cero cambios en código existente)");
        SistemaDeHumo humo = new SistemaDeHumo(bus);
        LucesEscenicas luces = new LucesEscenicas(bus);
        RegistroDeAuditoria auditoria = new RegistroDeAuditoria(bus);
        humo.iniciar();
        luces.iniciar();
        auditoria.iniciar();
        Consola.imprimir(Consola.NEGRITA, "MAIN", "Agregados: SistemaDeHumo, LucesEscenicas y RegistroDeAuditoria");

        pausa(3500);
        pedidos.realizarPedido(3, "hidromiel", "alitas");

        pausa(5000);
        Consola.titulo("FASE 3: Desuscripción dinámica (SistemaDeSonido deja de escuchar)");
        sonido.detener();
        Consola.imprimir(Consola.NEGRITA, "MAIN", "SistemaDeSonido desuscrito: la siguiente canción ya no lo activa");

        pausa(5000);
        Consola.titulo("FASE 4: Cierre de la taberna");
        banda.detener();
        reloj.shutdown();
        reloj.awaitTermination(15, TimeUnit.SECONDS); // espera pedidos que aún se están preparando

        bus.close(); // entrega los eventos pendientes y cierra

        Consola.imprimir(Consola.NEGRITA, "AUDITORIA", "Eventos que pasaron por el bus (desde la Fase 2): " + auditoria.resumen());
    }

    private static void pausa(long milisegundos) throws InterruptedException {
        Thread.sleep(milisegundos);
    }
}
