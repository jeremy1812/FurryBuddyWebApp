package ch.unil.furrybuddy.ui;

import ch.unil.furrybuddy.FurryBuddyService;
import ch.unil.furrybuddy.domain.User;
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
    private String selectedRole; // petowner or adopter
    private User.Role role;

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
        selectedRole = null;
        role = null;
    }

     public String login() {
        var uuid = furryBuddyService.authenticate(email, password, selectedRole); // Authenticate user
        var session = getSession(true);
        if (uuid != null) {
            session.setAttribute("uuid", uuid);
            session.setAttribute("email", email);
            session.setAttribute("role", selectedRole);
            switch (selectedRole) {
                case "petOwner":
                    this.role = User.Role.PET_OWNER;
                    petOwnerBean.setUUID(uuid);
                    petOwnerBean.loadPetOwnerData(); // Load data specific to the pet owner
                    return "MyAdvertisement?faces-redirect=true"; // Redirect to pet owner dashboard
                case "adopter":
                    this.role = User.Role.ADOPTER;
                    adopterBean.setUUID(uuid);
                    adopterBean.loadAdopterData(); // Load data specific to the adopter
                    return "MyRequest?faces-redirect=true"; // Redirect to adopter dashboard
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

    public String getSelectedRole() {
        return selectedRole;
    }

    public void setSelectedRole(String selectedRole) {
        this.selectedRole = selectedRole;
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

    public User.Role getRole() {
        return role;
    }
    public void setRole(User.Role role) {
        this.role = role;
    }

    public boolean isPetOwner() {
        return User.Role.PET_OWNER.equals(role);
    }

    public boolean isAdopter() {
        return User.Role.ADOPTER.equals(role);
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

    public UUID getLoggedInUserId() {
        if (role == null){
            return null;
        }

        HttpSession session = LoginBean.getSession(false); // Get current session
        if (session != null) {
            return (UUID) session.getAttribute("uuid"); // Assumes the UUID is stored in the session
        }
        return null;
    }

    public static void invalidateSession() {
        var session = getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
