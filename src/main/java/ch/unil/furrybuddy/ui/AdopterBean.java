package ch.unil.furrybuddy.ui;

import ch.unil.furrybuddy.FurryBuddyService;
import ch.unil.furrybuddy.domain.Adopter;
import ch.unil.furrybuddy.domain.AdoptionRequest;
import ch.unil.furrybuddy.domain.Location;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@SessionScoped
@Named
public class AdopterBean extends Adopter implements Serializable {
    private static final long serialVersionUID = 1L;

    // Fields
    private Adopter theAdopter;
    private UUID uuid;
    private String firstName;
    private String lastName;
    private String email;
    private String town;
    private String postalCode;
    private String address;
    private String currentPassword;
    private String newPassword;
    private boolean changed;
    private String dialogMessage;
    private boolean buttonDisabled;
    private List<AdoptionRequest> requests;

    @Inject
    private FurryBuddyService theService;

    @Inject
    private LoginBean loginBean;

    public AdopterBean() {
        init();
    }

    public void init() {
        uuid = null;
        theAdopter = null;
        firstName = null;
        lastName = null;
        email = null;
        currentPassword = null;
        newPassword = null;
        changed = false;
        dialogMessage = null;
        buttonDisabled = false;
    }

    // Load adopter data
    public void loadAdopterData() {
        if (uuid != null) {
            Adopter adopter = theService.getAdopter(uuid);
            if (adopter != null) {
                this.firstName = adopter.getFirstName();
                this.lastName = adopter.getLastName();
                this.email = adopter.getEmail();
                this.requests = adopter.getAdoptionRequests();
                this.town = adopter.getLocation().getTown();
                this.postalCode = adopter.getLocation().getPostalCode();
                this.address = adopter.getLocation().getAddress();
            }
        }
    }

    public List<AdoptionRequest> loadMyRequests() {
        UUID userId = loginBean.getLoggedInUserId();
        if (uuid != null) {
            requests = theService.getAdopter(userId).getAdoptionRequests();
        }
        return Collections.emptyList();
    }

    // Check if fields have changed
    public void checkIfChanged() {
        if (uuid != null) {
            Adopter adopter = theService.getAdopter(uuid);
            if (adopter != null) {
                boolean nameChanged = !this.firstName.equals(adopter.getFirstName());
                boolean lastNameChanged = !this.lastName.equals(adopter.getLastName());
                boolean emailChanged = !this.email.equals(adopter.getEmail());
                this.changed = nameChanged || emailChanged || lastNameChanged;
//                this.buttonDisabled = !this.changed;
            }
        }
    }

    // Update profile information
    public void updateProfile() {
        try {
            Adopter adopter = theService.getAdopter(uuid);
            if (this.getUUID() != null) {
                adopter.setFirstName(this.firstName);
                adopter.setLastName(this.lastName);
                adopter.setEmail(this.email);
                adopter.setLocation(new Location(this.town, this.postalCode, this.address));
                theService.setAdopter(adopter); // Save changes
                this.changed = false;
                this.dialogMessage = "Profile updated successfully.";
            }
        } catch (Exception e) {
            this.dialogMessage = "Error updating profile: " + e.getMessage();
        }
    }

    // Undo changes and reload data
    public void undoChanges() {
        loadAdopterData();
        this.replaceWith(theAdopter);
        this.changed = false;
    }

    // Change password
    public void changePassword() {
        try {
            Adopter adopter = theService.getAdopter(uuid);
            if (adopter != null && adopter.getPassword().equals(this.currentPassword)) {
                adopter.setPassword(this.newPassword); // Replace with secure hashing if needed
                theService.setAdopter(adopter);
                this.dialogMessage = "Password changed successfully.";
                resetPasswordChange();
            } else {
                this.dialogMessage = "Current password is incorrect.";
            }
        } catch (Exception e) {
            this.dialogMessage = "Error changing password: " + e.getMessage();
        }
    }

    // Reset password form fields
    public void resetPasswordChange() {
        this.currentPassword = null;
        this.newPassword = null;
    }

    // Getters and Setters
    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

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
    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public String getDialogMessage() {
        return dialogMessage;
    }

    public void setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }

    public boolean isButtonDisabled() {
        return buttonDisabled;
    }

    public void setButtonDisabled(boolean buttonDisabled) {
        this.buttonDisabled = buttonDisabled;
    }

    public List<AdoptionRequest> getRequests() {return requests;}

    public void setRequests(List<AdoptionRequest> requests) {this.requests = requests;}

    public String deleteAccount() {
        UUID adopterID = loginBean.getLoggedInUserId();
        if (adopterID == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "User ID not found. Please log in."));
            return null;
        }

        try {
            boolean success = theService.deleteAdopter(adopterID);
            if (success) {
                // Log out the user and redirect to the login page
                loginBean.logout();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Account Deleted", "Your account has been deleted."));
                return "Login?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete account."));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred: " + e.getMessage()));
        }

        return null; // Stay on the same page if deletion fails
    }
}

