package fr.grin.tpsecurite.jsf;

import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import static jakarta.security.auth.message.AuthStatus.SEND_CONTINUE;
import static jakarta.security.auth.message.AuthStatus.SEND_FAILURE;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import static jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters.withParams;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.Password;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Backing bean pour la page de login avec un formulaire personnalisé (custom).
 *
 * @author grin
 */
@Named(value = "loginBean")
@RequestScoped
public class LoginBean {

  /**
   * Creates a new instance of LoginBean
   */
  public LoginBean() {
  }

  @Inject
  private SecurityContext securityContext;
  @Inject
  private FacesContext facesContext;
  @Inject
  private ExternalContext externalContext;

  private String nom;
  private String motDePasse;

  public String getMotDePasse() {
    return motDePasse;
  }

  public void setMotDePasse(String motDePasse) {
    this.motDePasse = motDePasse;
  }

  public String getNom() {
    return nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  public void login() {
    Credential credential
            = new UsernamePasswordCredential(nom, new Password(motDePasse));
    AuthenticationStatus status = securityContext.authenticate(
            (HttpServletRequest) externalContext.getRequest(),
            (HttpServletResponse) externalContext.getResponse(),
            withParams().credential(credential));
    if (status.equals(SEND_CONTINUE)) {
      facesContext.responseComplete();
    } else if (status.equals(SEND_FAILURE)) {
      addError(facesContext, "Nom / mot de passe incorrects");
    }
  }

  /**
   * Ajoute une erreur à afficher dans la page de login.
   *
   * @param facesContext
   * @param authentication_failed
   */
  private void addError(FacesContext facesContext,
          String message) {
    facesContext.addMessage(
            null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    message,
                    null));
  }

}
