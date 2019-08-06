import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Repository {

    private static final Logger log = Logger.getLogger(Repository.class.getName());
    private Connection con;

    public Repository() {
        try {
            this.con = ConnectionFactory.getConnectionPostgres();
        } catch (SQLException e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }
    }

    public void salvarEntidade(Entidade entidade) {
        try {
            String sql = "INSERT INTO entidade (id, editado, excluido) VALUES (?,?,?)";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, entidade.getId());
            statement.setBoolean(2, false);
            statement.setBoolean(3, false);
            statement.executeUpdate();
        } catch (SQLException e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }
    }

    public void atualizarEntidade(int id) {
        try {
            String sql = "UPDATE entidade SET editado = ? WHERE id = ?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setBoolean(1, true);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }
    }

    public void excluirEntidade(int id) {
        try {
            String sql = "UPDATE entidade SET excluido = ? WHERE id = ?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setBoolean(1, true);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }
    }

    public void salvarInstancia(Instancia instancia) {
        try {
            String sql = "INSERT INTO instancia (id, minId, maxId) VALUES (?,?,?)";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, instancia.getId());
            statement.setInt(2, instancia.getMinId());
            statement.setInt(3, instancia.getMaxId());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }
    }

    public Instancia buscarUltimaInstancia() {
        Instancia instancia = null;
        try {
            String sql = "SELECT id, minId, maxId FROM instancia ORDER BY id DESC LIMIT 1";
            PreparedStatement statement = con.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            if(rs.next())
                instancia = new Instancia(rs.getInt("id"), rs.getInt("minId"), rs.getInt("maxId"));
        } catch (SQLException e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }
        return instancia;
    }
}
