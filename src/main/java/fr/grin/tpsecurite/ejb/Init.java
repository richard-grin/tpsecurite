package fr.grin.tpsecurite.ejb;

import fr.grin.tpsecurite.util.HashMdp;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

/**
 * Créé les tables des utilisateurs et les remplit, au démarrage de
 * l'application.
 *
 * @author grin
 */
@Startup
@Singleton
public class Init {

  @Resource(lookup = "jdbc/tpsecurite")
  private DataSource dataSource;

  // Pour coder le mot de passe
  @Inject
  private HashMdp passwordHash;

  @PostConstruct
  public void init() {
    try (Connection c = dataSource.getConnection()) {
      // Si la table des logins n'existe pas déjà, créer les tables
      if (!existe(c, "LOGIN")) { // Attention, la casse compte !!!
        System.out.println("Création des tables");
        execute(c, "CREATE TABLE login (login VARCHAR(20) PRIMARY KEY, mot_de_passe VARCHAR(160))");
        execute(c, "CREATE TABLE login_groupe (login VARCHAR(20) references login, groupe VARCHAR(20))");
      } else {
        System.out.println("Tables existent déjà");
      }

      // Si la table LOGIN n'est pas vide, ne rien faire
      if (vide(c, "login")) {
        System.out.println("============ Table login vide ; initialisation des données dans ls tables");
        // La table LOGIN est vide
        // Le mot de passe haché :
        String hashMdp = passwordHash.generate("toto");
        // Administrateur
        execute(c, "INSERT INTO login (LOGIN, MOT_DE_PASSE) VALUES('admin', '"
                + hashMdp + "')");
        execute(c, "INSERT INTO login_groupe(login, groupe) VALUES('admin', 'admin')");
        execute(c, "INSERT INTO login_groupe(login, groupe) VALUES('admin', 'utilisateur')");
        // Simple utilisateur enregistré
        hashMdp = passwordHash.generate("toto");
        execute(c, "INSERT INTO login (LOGIN, MOT_DE_PASSE) VALUES('toto', '"
                + hashMdp + "')");
        execute(c, "INSERT INTO login_groupe(login, groupe) VALUES('toto', 'utilisateur')");
      }
    } catch (SQLException e) {
      // Une méthode annotée avec @PostConstruct ne peut lancer d'exception contrôlée.
      e.printStackTrace();
    }
  }

  private void execute(Connection c, String query) {
    try (PreparedStatement stmt = c.prepareStatement(query)) {
      stmt.executeUpdate();
    } catch (SQLException e) {
      // Pour les logs du serveur d'application
      e.printStackTrace();
    }
  }

  /**
   * Teste si une table existe déjà.
   *
   * @param connection
   * @param nomTable nom de la table ; attention la casse compte !
   * @return true ssi la table existe.
   * @throws SQLException
   */
  private static boolean existe(Connection connection, String nomTable)
          throws SQLException {
    boolean existe;
    DatabaseMetaData dmd = connection.getMetaData();
    try (ResultSet tables
            = dmd.getTables(connection.getCatalog(),
                    null, nomTable, null)) {
      existe = tables.next();
    }
    return existe;
  }

  /**
   *
   * @return true ssi la table LOGIN est vide.
   */
  private boolean vide(Connection c, String nomTable) throws SQLException {
    Statement stmt = c.createStatement();
    ResultSet rset = stmt.executeQuery("select count(1) from " + nomTable);
    rset.next();
    int nb = rset.getInt(1);
    return nb == 0;
  }

}
