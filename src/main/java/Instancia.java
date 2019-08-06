import java.io.Serializable;
import java.util.Objects;

public class Instancia implements Serializable {

    private int id;
    private int minId;
    private int maxId;

    public Instancia(int id, int minId, int maxId) {
        this.id = id;
        this.minId = minId;
        this.maxId = maxId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMinId() {
        return minId;
    }

    public void setMinId(int minId) {
        this.minId = minId;
    }

    public int getMaxId() {
        return maxId;
    }

    public void setMaxId(int maxId) {
        this.maxId = maxId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instancia instancia = (Instancia) o;
        return id == instancia.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Instancia{" +
                "id=" + id +
                '}';
    }
}
