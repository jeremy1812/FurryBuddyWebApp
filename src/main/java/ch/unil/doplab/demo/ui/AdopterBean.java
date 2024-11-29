package ch.unil.doplab.demo.ui;

import ch.unil.doplab.demo.FurryBuddyService;
import ch.unil.furrybuddy.domain.Adopter;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.UUID;

@SessionScoped
@Named
public class AdopterBean implements Serializable {
    private static final long serialVersionUID = 1L;

    // Fields
    private UUID uuid;
    private String name;
    private String firstName;
    private String lastName;
    private String email;
    private String currentPassword;
    private String newPassword;
    private boolean changed;
    private String dialogMessage;
    private boolean buttonDisabled;

    @Inject
    private FurryBuddyService theService;

    // Load adopter data
    public void loadAdopterData() {
        if (uuid != null) {
            Adopter adopter = theService.getAdopter(uuid.toString());
            if (adopter != null) {
                this.firstName = adopter.getFirstName();
                this.lastName = adopter.getLastName();
                this.email = adopter.getEmail();
            }
        }
    }

    // Check if fields have changed
    public void checkIfChanged() {
        if (uuid != null) {
            Adopter adopter = theService.getAdopter(uuid.toString());
            if (adopter != null) {
                boolean nameChanged = !this.firstName.equals(adopter.getFirstName());
                boolean lastNameChanged = !this.lastName.equals(adopter.getLastName());
                boolean emailChanged = !this.email.equals(adopter.getEmail());
                this.changed = nameChanged || emailChanged;
                this.buttonDisabled = !this.changed;
            }
        }
    }

    // Update profile information
    public void updateProfile() {
        try {
            Adopter adopter = theService.getAdopter(uuid.toString());
            if (adopter != null) {
                adopter.setFirstName(this.firstName);
                adopter.setLastName(this.lastName);
                adopter.setEmail(this.email);
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
        this.changed = false;
        this.buttonDisabled = true;
    }

    // Change password
    public void changePassword() {
        try {
            Adopter adopter = theService.getAdopter(uuid.toString());
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
    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setUUID(UUID uuid) {
    }
}

