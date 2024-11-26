package ch.unil.doplab.demo.ui;

import ch.unil.doplab.demo.FurryBuddyService;
import ch.unil.doplab.demo.ui.AdopterBean;
import ch.unil.doplab.demo.ui.PetOwnerBean;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;

public class LoginBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
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
        username = null;
        password = null;
        role = null;
    }

    public String login() {
        var uuid = furryBuddyService.authenticate(username, password, role); // Authenticate user
        var session = getSession(true);
        if (uuid != null) {
            session.setAttribute("uuid", uuid);
            session.setAttribute("username", username);
            session.setAttribute("role", role);
            switch (role.toLowerCase()) {
                case "petowner":
                    petOwnerBean.setUUID(uuid);
                    petOwnerBean.loadPetOwnerData(); // Load data specific to the pet owner
                    return "PetOwnerDashboard?faces-redirect=true"; // Redirect to pet owner dashboard
                case "adopter":
                    adopterBean.setUUID(uuid);
                    adopterBean.loadAdopterData(); // Load data specific to the adopter
                    return "AdopterDashboard?faces-redirect=true"; // Redirect to adopter dashboard
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
