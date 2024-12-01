package ch.unil.doplab.demo.ui;

import ch.unil.doplab.demo.FurryBuddyService;
import ch.unil.furrybuddy.domain.Adopter;
import ch.unil.furrybuddy.domain.PetOwner;
import ch.unil.furrybuddy.domain.Location;
import ch.unil.furrybuddy.domain.User.Role;
import ch.unil.furrybuddy.domain.User;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.logging.Logger;


@Named
@ViewScoped
public class RegisterBean implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(RegisterBean.class.getName());

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private User.Role role;
    private String selectedRole;
    private String locationTown; // Assuming location requires a city
    private String locationPostalCode; //New field for location input
    private String locationAddress;

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
        password = null;
        role = null;
        locationTown = null;
        locationPostalCode = null; //Reset location PostalCode
        locationAddress = null;
        petOwnerSelected = false;
        adopterSelected = false;
        selectedRole = null;
    }

    /**
     * Handles the registration process.
     * Validates the role and attempts to register the user as a PetOwner or Adopter.
     */
    //TODO no location is set in register fields
    Location location = new Location(locationTown, locationPostalCode, locationAddress);
    public String register() {
        // Invalidate any existing session
        LoginBean.invalidateSession(); // Clear session before registering
        String hashedPassword = hashPassword(password); // Hash the password before storing
        String errorMessage = null;

        // Check the role and register the appropriate user type
        switch (selectedRole) {
            case "PetOwner":
                PetOwner petOwner = new PetOwner(email, password, firstName, lastName, location, Role.PET_OWNER);
                try {
                    petOwner = theService.addPetOwner(petOwner);
                    log.info("PetOwner successfully registered: " + petOwner);
                    return "Login?faces-redirect=true"; // Redirect to login page after successful registration
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    log.warning("Failed to register new PetOwner " + email + ": " + errorMessage);
                }
                break;
            case "Adopter":
                Adopter adopter = new Adopter(email, password, firstName, lastName, location, Role.ADOPTER);
                try {
                    adopter = theService.addAdopter(adopter); //Call service to add Adopter
                    log.info("Adopter successfully registered: " + adopter);
                    return "Login?faces-redirect=true";
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    log.warning("Failed to register new Adopter " + email + ": " + errorMessage);
                }
                break;
            default:
                throw new IllegalStateException("Invalid role: " + selectedRole);
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
    public User.Role getPetOwnerRole() {
        return Role.PET_OWNER;
    }

    public User.Role getAdopterRole() {
        return Role.ADOPTER;
    }

    public void updateRoleSelection() {
        this.petOwnerSelected = Role.PET_OWNER.equals(role);
        this.adopterSelected = Role.ADOPTER.equals(role);
        log.info("Role updated: " + role);
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
        updateRoleSelection(); // Update the conditional fields whenever the role changes
    }

    public String getSelectedRole(){ return selectedRole;}

    public void setSelectedRole(String selectedRole){ this.selectedRole = selectedRole;}
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
