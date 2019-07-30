import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Loader {

    private static Repository repository;
    private static ArrayBlockingQueue<Entidade> updatQueue;
    private static ArrayBlockingQueue<Entidade> deleteQueue;
    private static AtomicInteger atomicInteger;
    private static final int MAX = 10;

    public static void main(String args[]) {

        repository = new Repository();

        Instancia ultimaInstancia = repository.buscarUltimaInstancia();
        Instancia instanciaAtual = new Instancia();

        if(ultimaInstancia.getId() == 0) {
            instanciaAtual.setId(1);
            instanciaAtual.setMinId(1);
            instanciaAtual.setMaxId(MAX);
            repository.persistirInstancia(instanciaAtual);
        } else {
            instanciaAtual.setId(ultimaInstancia.getId() + 1);
            instanciaAtual.setMinId(ultimaInstancia.getMaxId() + 1);
            instanciaAtual.setMaxId(ultimaInstancia.getMaxId() + MAX);
            repository.persistirInstancia(instanciaAtual);
        }

        updatQueue = new ArrayBlockingQueue<Entidade>(3);
        deleteQueue = new ArrayBlockingQueue<Entidade>(3);
        atomicInteger = new AtomicInteger(instanciaAtual.getMinId());

        Long tempo = System.currentTimeMillis();

        int contador = instanciaAtual.getMinId();

        Runnable inserir = () -> {
            try {
                Entidade entidade = new Entidade(atomicInteger.getAndIncrement());
                repository.inserir(entidade);
                updatQueue.put(entidade);
                System.out.println("INSERIR " + entidade +  "\n");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Runnable editar = () -> {
            try {
                Entidade e = updatQueue.take();
                e.setEditado(true);
                repository.atualizar(e.getId());
                System.out.println("EDITAR " + e +  "\n");
                deleteQueue.put(e);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        };

        Runnable deletar = () -> {
            try {
                Entidade e = deleteQueue.take();
                e.setExcluido(true);
                repository.excluir(e.getId());
                System.out.println("DELETAR " + e +  "\n");
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        };

        final long tempoInicial = System.currentTimeMillis();
        while(contador <= instanciaAtual.getMaxId()) {
            contador++;
            new Thread(inserir).run();
            new Thread(editar).run();
            new Thread(deletar).run();
        }
        final long tempoFinal = System.currentTimeMillis();

        final long tempoTotalEmMilissegundo = tempoFinal - tempoInicial;
        final long minutos = (tempoTotalEmMilissegundo / 1000) / 60;
        final long segundos = (tempoTotalEmMilissegundo / 1000) % 60;

        System.out.printf("TEMPO DE EXECUCÃO: %d MINUTOS E %d SEGUNDOS", minutos, segundos);
    }
}
