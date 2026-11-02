package taberna.bus;

/**
 * Abstracción de un componente interesado en un tipo de evento.
 * Al ser interfaz funcional puede implementarse con una clase o con una lambda.
 *
 * @param <E> tipo de evento que atiende
 */
@FunctionalInterface
public interface Suscriptor<E extends Evento> {

    void alRecibir(E evento);
}
