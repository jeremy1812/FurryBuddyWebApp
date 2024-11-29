package ch.unil.doplab.demo.ui;

import ch.unil.doplab.demo.FurryBuddyService;
import ch.unil.furrybuddy.domain.Advertisement;
import ch.unil.furrybuddy.domain.AdoptionRequest;
import ch.unil.furrybuddy.domain.PetOwner;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@SessionScoped
@Named
public class PetOwnerBean implements Serializable {
    private static final long serialVersionUID = 1L;

    // Champs existants
    private UUID uuid;
    private String name;
    private List<Advertisement> myAdvertisements;
    private List<AdoptionRequest> receivedRequests;

    // Champs ajoutés pour le profil
    private String firstName;
    private String lastName;
    private String email;
    private String currentPassword;
    private String newPassword;
    private String dialogMessage;
    private boolean changed; // Indique si des modifications ont été apportées

    @Inject
    private FurryBuddyService theService;

    // Méthode pour charger les données du propriétaire
    public void loadPetOwnerData() {
        if (uuid != null) {
            PetOwner petOwner = theService.getPetOwner(uuid.toString());
            if (petOwner != null) {
                this.firstName = petOwner.getFirstName();
                this.lastName = petOwner.getLastName();
                this.email = petOwner.getEmail();
            }
        }
    }

    // Charger les publicités postées
    public void loadMyAdvertisements() {
        if (uuid != null) {
            myAdvertisements = theService.getAdvertisementsByPetOwner(uuid);
        }
    }

    // Charger les demandes reçues pour ses publicités
    public void loadReceivedRequests() {
        if (uuid != null) {
            receivedRequests = theService.getRequestsForAdvertisements(uuid);
        }
    }

    // Vérifie si des modifications ont été faites
    public void checkIfChanged() {
        if (uuid != null) {
            PetOwner petOwner = theService.getPetOwner(uuid.toString());
            boolean firstNameChanged = !this.firstName.equals(petOwner.getFirstName());
            boolean lastNameChanged = !this.lastName.equals(petOwner.getLastName());
            boolean emailChanged = !this.email.equals(petOwner.getEmail());
            this.changed = firstNameChanged || lastNameChanged || emailChanged;
        }
    }

    // Met à jour les informations du profil
    public void updateProfile() {
        try {
            PetOwner petOwner = theService.getPetOwner(uuid.toString());
            if (petOwner != null) {
                petOwner.setFirstName(this.firstName);
                petOwner.setLastName(this.lastName);
                petOwner.setEmail(this.email);
                theService.setPetOwner(petOwner); // Sauvegarde des modifications
                this.changed = false;
                this.dialogMessage = "Profile updated successfully.";
            }
        } catch (Exception e) {
            this.dialogMessage = "Error updating profile: " + e.getMessage();
        }
    }

    // Annule les modifications et recharge les données depuis le service
    public void undoChanges() {
        if (uuid != null) {
            PetOwner petOwner = theService.getPetOwner(uuid.toString());
            if (petOwner != null) {
                this.firstName = petOwner.getFirstName();
                this.lastName = petOwner.getLastName();
                this.email = petOwner.getEmail();
                this.changed = false;
            }
        }
    }

    // Change le mot de passe du propriétaire
    public void changePassword() {
        try {
            PetOwner petOwner = theService.getPetOwner(uuid.toString());
            if (petOwner != null && petOwner.getPassword().equals(this.currentPassword)) {
                petOwner.setPassword(this.newPassword); // Remplacez par un hash sécurisé si nécessaire
                theService.setPetOwner(petOwner);
                this.dialogMessage = "Password changed successfully.";
                resetPasswordChange();
            } else {
                this.dialogMessage = "Current password is incorrect.";
            }
        } catch (Exception e) {
            this.dialogMessage = "Error changing password: " + e.getMessage();
        }
    }

    // Réinitialise les champs du formulaire de changement de mot de passe
    public void resetPasswordChange() {
        this.currentPassword = null;
        this.newPassword = null;
    }

    // Getters et Setters pour tous les champs existants et ajoutés
    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Advertisement> getMyAdvertisements() {
        return myAdvertisements;
    }

    public void setMyAdvertisements(List<Advertisement> myAdvertisements) {
        this.myAdvertisements = myAdvertisements;
    }

    public List<AdoptionRequest> getReceivedRequests() {
        return receivedRequests;
    }

    public void setReceivedRequests(List<AdoptionRequest> receivedRequests) {
        this.receivedRequests = receivedRequests;
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

    public String getDialogMessage() {
        return dialogMessage;
    }

    public void setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }

    public boolean isChanged() {
        return changed;
    }
}
