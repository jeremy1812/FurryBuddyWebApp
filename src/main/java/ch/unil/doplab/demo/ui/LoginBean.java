package ch.unil.doplab.demo.ui;

import ch.unil.doplab.demo.FurryBuddyService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.UUID;

@SessionScoped
@Named
public class LoginBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;
    private String role; // petowner or adopter

    @Inject
    FurryBuddyService furryBuddyService; // Service for authentication and data fetching

    @Inject
    PetOwnerBean petOwnerBean; // Bean to handle pet owners' data

    @Inject
    AdopterBean adopterBean; // Bean to handle adopters' data

    public LoginBean() {
        reset();
    }

    public void reset() {
        email = null;
        password = null;
        role = null;
    }

     public String login() {
        var uuid = furryBuddyService.authenticate(email, password, role); // Authenticate user
        var session = getSession(true);
        if (uuid != null) {
            session.setAttribute("uuid", uuid);
            session.setAttribute("email", email);
            session.setAttribute("role", role);
            switch (role) {
                case "petOwner":
                    petOwnerBean.setUUID(uuid);
                    petOwnerBean.loadPetOwnerData(); // Load data specific to the pet owner
                    return "GetAllAdvertisements?faces-redirect=true"; // Redirect to pet owner dashboard
                case "adopter":
                    adopterBean.setUUID(uuid);
                    adopterBean.loadAdopterData(); // Load data specific to the adopter
                    return "GetAllAdvertisements?faces-redirect=true"; // Redirect to adopter dashboard
                default:
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                    "Invalid role specified", null));
                    reset();
                    return "Login";
            }
        }
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Invalid login credentials", null));
        reset();
        return "Login";
    }

    public String logout() {
        invalidateSession();
        reset();
        return "Login?faces-redirect=true";
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static HttpSession getSession(boolean create) {
        var facesContext = FacesContext.getCurrentInstance();
        if (facesContext == null) {
            return null;
        }
        var externalContext = facesContext.getExternalContext();
        if (externalContext == null) {
            return null;
        }
        return (HttpSession) externalContext.getSession(create);
    }

    public static void invalidateSession() {
        var session = getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
