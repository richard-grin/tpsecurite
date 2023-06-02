package fr.grin.tpsecurite.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.LoginToContinue;
import jakarta.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;

/**
 * Configuration pour la sécurité
 * @author grin
 */
@ApplicationScoped
@DatabaseIdentityStoreDefinition(
  dataSourceLookup = "jdbc/tpsecurite",
  callerQuery = "select mot_de_passe from login where login=?",
  groupsQuery = "select groupe from login_groupe where login=?"
)
@CustomFormAuthenticationMechanismDefinition(
  loginToContinue = @LoginToContinue(
    loginPage = "/login/login.xhtml",
    errorPage = ""
  )
)
public class Config {
  
}
