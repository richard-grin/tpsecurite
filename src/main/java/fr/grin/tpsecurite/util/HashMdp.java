package fr.grin.tpsecurite.util;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.util.HashMap;
import java.util.Map;

/**
 * Pour générer un mot de passe haché.
 * Avec l'initialisation actuelle, le mot de passe haché est de longueur 159.
 */
@ApplicationScoped
public class HashMdp {

  @Inject
  private Pbkdf2PasswordHash passwordHash;

  @PostConstruct
  public void init() {
    Map<String, String> parameters = new HashMap<>();
    parameters.put("Pbkdf2PasswordHash.Iterations", "3072");
    parameters.put("Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA512");
    parameters.put("Pbkdf2PasswordHash.SaltSizeBytes", "64");
    passwordHash.initialize(parameters);
  }

  /**
   * Retourne le mot de passe crypté.
   * @param mdp le mot de passe non crypté.
   * @return le mot de passe crypté.
   */  
  public String generate(String mdp) {
    return passwordHash.generate(mdp.toCharArray());
  }
  
}
