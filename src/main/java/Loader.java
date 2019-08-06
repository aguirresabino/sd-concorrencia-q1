import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class Loader {

    private static final Logger log = Logger.getLogger(Loader.class.getName());

    private static AtomicInteger atomicInteger;
    private static final int MAX = 1000;

    public static void main(String args[]) {

        final Repository repository = new Repository();
        final ArrayBlockingQueue<Entidade> updatQueue = new ArrayBlockingQueue(3);
        final ArrayBlockingQueue<Entidade> deleteQueue = new ArrayBlockingQueue(3);

        final Instancia ultimaInstancia = repository.buscarUltimaInstancia();
        final Instancia instanciaAtual;

        if (ultimaInstancia == null)
            instanciaAtual = new Instancia(1, 1, MAX);
        else
            instanciaAtual = new Instancia(ultimaInstancia.getId() + 1,
                    ultimaInstancia.getMaxId() + 1, ultimaInstancia.getMaxId() + MAX);

        repository.salvarInstancia(instanciaAtual);
        atomicInteger = new AtomicInteger(instanciaAtual.getMinId());

        Runnable inserir = () -> {
            try {
                Entidade entidade = new Entidade(atomicInteger.getAndIncrement());
                updatQueue.put(entidade);
                repository.salvarEntidade(entidade);
                log.info("[INSERIR] " + entidade);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Runnable editar = () -> {
            try {
                Entidade e = updatQueue.take();
                e.setEditado(true);
                deleteQueue.put(e);
                repository.atualizarEntidade(e.getId());
                log.info("[EDITAR] " + e);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        };

        Runnable deletar = () -> {
            try {
                Entidade e = deleteQueue.take();
                e.setExcluido(true);
                repository.excluirEntidade(e.getId());
                log.info("[DELETAR] " + e);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        };

        final long tempoInicial = System.currentTimeMillis();
        for (int i = instanciaAtual.getMinId(); i <= instanciaAtual.getMaxId(); i++) {
            new Thread(inserir).run();
            new Thread(editar).run();
            new Thread(deletar).run();
        }
        final long tempoFinal = System.currentTimeMillis();

        final long tempoTotalEmMilissegundo = tempoFinal - tempoInicial;
        final long minutos = (tempoTotalEmMilissegundo / 1000) / 60;
        final long segundos = (tempoTotalEmMilissegundo / 1000) % 60;

        log.info(String.format("[TEMPO DE EXECUCÃO] %d MINUTOS E %d SEGUNDOS", minutos, segundos));
    }
}
