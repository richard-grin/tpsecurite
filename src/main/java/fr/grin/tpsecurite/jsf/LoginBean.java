package fr.grin.tpsecurite.jsf;

import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.Flash;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationStatus;
import static jakarta.security.enterprise.AuthenticationStatus.SEND_CONTINUE;
import static jakarta.security.enterprise.AuthenticationStatus.SEND_FAILURE;
import jakarta.security.enterprise.SecurityContext;
import static jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters.withParams;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.Password;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.IOException;

/**
 * Backing bean pour la page de login avec un formulaire personnalisé (custom).
 *
 * @author grin
 */
@Named(value = "loginBean")
@RequestScoped
public class LoginBean {

  @Inject
  private SecurityContext securityContext;
  @Inject
  private FacesContext facesContext;
  @Inject
  private ExternalContext externalContext;
  @Inject
  private HttpServletRequest httpServletRequest;
  @Inject
  private HttpSession session;
  
  @Inject
  private Flash flash;
  @Inject // Injecte la valeur du paramètre new
  @ManagedProperty("#{param.new}")
  private boolean isNew;


  private String nom;
  @NotNull
  @Size(min = 4, message = "Au moins 4 caractères")
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

  public void login() throws IOException {
    switch (continueAuthentication()) {
      case SEND_CONTINUE ->
        facesContext.responseComplete();
      case SEND_FAILURE ->
        addMessage("Nom / mot de passe incorrects", FacesMessage.SEVERITY_ERROR);
      case SUCCESS -> {
        addMessage("Login réussi", FacesMessage.SEVERITY_INFO);
        flash.setKeepMessages(true);
        externalContext.redirect(
                externalContext.getRequestContextPath() + "/index.xhtml");
      }
      case NOT_DONE -> {
        // n’arrivera jamais
      }
    }
  }

  private AuthenticationStatus continueAuthentication() {
    Credential credential = new UsernamePasswordCredential(
            nom, new Password(motDePasse));
    return securityContext.authenticate(
            httpServletRequest,
            (HttpServletResponse) externalContext.getResponse(),
            withParams().newAuthentication(isNew).credential(credential));
  }

  /**
   * Ajoute un message à afficher dans la page de login.
   *
   * @param message texte du message
   * @param severity sévérité du message
   */
  private void addMessage(String message, FacesMessage.Severity severity) {
    facesContext.addMessage(null, new FacesMessage(severity, message, null));
  }

  /**
   * Sortir de la session.
   *
   * @return
   */
  public String logout() {
    try {
//      HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext.getRequest();
      httpServletRequest.logout();
      session.invalidate();
    } catch (ServletException ex) {
      System.err.println("Erreur pendant logout : " + ex);
    }
    return null; // reste sur la page index.xhtml
  }

}
