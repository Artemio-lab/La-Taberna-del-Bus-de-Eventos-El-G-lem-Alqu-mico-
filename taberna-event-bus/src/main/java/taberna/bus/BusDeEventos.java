package taberna.bus;

/**
 * Abstracción del bus. Los componentes de la taberna dependen únicamente de
 * esta interfaz, nunca de otros componentes ni de la implementación concreta.
 */
public interface BusDeEventos {

    /** Registra un suscriptor para un tipo de evento (o para cualquiera de sus subtipos). */
    <E extends Evento> void suscribir(Class<E> tipoDeEvento, Suscriptor<? super E> suscriptor);

    /** Remueve un suscriptor. Devuelve true si estaba registrado. */
    <E extends Evento> boolean desuscribir(Class<E> tipoDeEvento, Suscriptor<? super E> suscriptor);

    /** Notifica el evento a todos los suscriptores registrados para su tipo. */
    void publicar(Evento evento);
}
