package ch.unil.doplab.demo.ui;

import ch.unil.doplab.demo.FurryBuddyService;
import ch.unil.furrybuddy.domain.PetOwner;
import ch.unil.furrybuddy.domain.Adopter;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.logging.Logger;

@Named
@SessionScoped
public class RegisterBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(RegisterBean.class.getName());

    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String role;
    private String petBiography;

    private boolean petOwnerSelected;
    private boolean adopterSelected;

    @Inject
    private FurryBuddyService theService;

    public RegisterBean() {
        reset();
    }

    // Resets all fields to default values
    public void reset() {
        firstName = null;
        lastName = null;
        email = null;
        username = null;
        password = null;
        role = null;
        petBiography = "Write your pet's biography here...";
        petOwnerSelected = false;
        adopterSelected = false;
    }

    /**
     * Handles the registration process.
     * Validates the role and attempts to register the user as a PetOwner or Adopter.
     */
    public String register() {
        // Invalidate any existing session
        LoginBean.invalidateSession();
        String hashedPassword = hashPassword(password); // Hash the password before storing
        String errorMessage = null;

        // Check the role and register the appropriate user type
        switch (role) {
            case "petowner":
                PetOwner petOwner = new PetOwner(firstName, lastName, email, username, hashedPassword, petBiography);
                try {
                    petOwner = theService.addPetOwner(petOwner);
                    log.info("PetOwner successfully registered: " + petOwner);
                    return "Login?faces-redirect=true"; // Redirect to login page after successful registration
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    log.warning("Failed to register new PetOwner " + username + ": " + errorMessage);
                }
                break;
            case "adopter":
                Adopter adopter = new Adopter(firstName, lastName, email, username, hashedPassword);
                try {
                    adopter = theService.addAdopter(adopter);
                    log.info("Adopter successfully registered: " + adopter);
                    return "Login?faces-redirect=true";
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    log.warning("Failed to register new Adopter " + username + ": " + errorMessage);
                }
                break;
            default:
                throw new IllegalStateException("Invalid role: " + role);
        }

        // If registration failed, add an error message to the context
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Registration failed. " + errorMessage, null));

        return "register?faces-redirect=true";
    }

    /**
     * Updates conditional rendering for biography field based on the selected role.
     */
    public void updateRoleSelection() {
        this.petOwnerSelected = "petowner".equals(role);
        this.adopterSelected = "adopter".equals(role);
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
        updateRoleSelection(); // Update the conditional fields whenever the role changes
    }

    public String getPetBiography() {
        return petBiography;
    }

    public void setPetBiography(String petBiography) {
        this.petBiography = petBiography;
    }

    public boolean isPetOwnerSelected() {
        return petOwnerSelected;
    }

    public boolean isAdopterSelected() {
        return adopterSelected;
    }

    // Hash password method (simple SHA-256 implementation)
    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
