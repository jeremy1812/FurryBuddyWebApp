package ch.unil.furrybuddy.ui;

import ch.unil.furrybuddy.FurryBuddyService;
import ch.unil.furrybuddy.domain.Advertisement;
import ch.unil.furrybuddy.domain.AdoptionRequest;
import ch.unil.furrybuddy.domain.Location;
import ch.unil.furrybuddy.domain.PetOwner;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SessionScoped
@Named
public class PetOwnerBean extends PetOwner implements Serializable {
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
    private String town;
    private String postalCode;
    private String address;
    private String currentPassword;
    private String newPassword;
    private String dialogMessage;
    private boolean buttonDisabled;
    private boolean changed; // Indique si des modifications ont été apportées

    @Inject
    private FurryBuddyService theService;

    @Inject
    private LoginBean loginBean;

    public PetOwnerBean() {
        init();
    }

    public void init() {
        uuid = null;
    }

    // Méthode pour charger les données du propriétaire
    public void loadPetOwnerData() {
        if (uuid != null) {
            PetOwner petOwner = theService.getPetOwner(uuid);
            if (petOwner != null) {
                this.firstName = petOwner.getFirstName();
                this.lastName = petOwner.getLastName();
                this.email = petOwner.getEmail();
                this.town = petOwner.getLocation().getTown();
                this.postalCode = petOwner.getLocation().getPostalCode();
                this.address = petOwner.getLocation().getAddress();
            }
        }
    }

    // Charger les publicités postées
    public List<Advertisement> loadMyAdvertisements() {
        UUID userId = loginBean.getLoggedInUserId();
        if (uuid != null) {
            myAdvertisements = theService.getPetOwner(userId).getAdvertisements();
        }
        return Collections.emptyList();
    }

    // Charger les demandes reçues pour ses publicités
    public List<AdoptionRequest> loadReceivedRequests() {
        UUID userId = loginBean.getLoggedInUserId();
        if (userId != null) {
            List<AdoptionRequest> allRequests = theService.getAllAdoptionRequests();

            receivedRequests = allRequests.stream()
                    .filter(adoptionReq -> adoptionReq.getAdvertisement() != null &&
                            adoptionReq.getAdvertisement().getPetOwnerID() != null &&
                            adoptionReq.getAdvertisement().getPetOwnerID().equals(userId))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();

    }

    // Vérifie si des modifications ont été faites
    public void checkIfChanged() {
        if (uuid != null) {
            PetOwner petOwner = theService.getPetOwner(uuid);
            boolean firstNameChanged = !this.firstName.equals(petOwner.getFirstName());
            boolean lastNameChanged = !this.lastName.equals(petOwner.getLastName());
            boolean emailChanged = !this.email.equals(petOwner.getEmail());
            this.changed = firstNameChanged || lastNameChanged || emailChanged;
        }
    }

    // Met à jour les informations du profil
    public void updateProfile() {
        try {
            PetOwner petOwner = theService.getPetOwner(uuid);
            if (petOwner != null) {
                petOwner.setFirstName(this.firstName);
                petOwner.setLastName(this.lastName);
                petOwner.setEmail(this.email);
                petOwner.setLocation(new Location(this.town, this.postalCode, this.address));
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
            PetOwner petOwner = theService.getPetOwner(uuid);
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
            PetOwner petOwner = theService.getPetOwner(uuid);
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

    public String getDialogMessage() {
        return dialogMessage;
    }

    public void setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }

    public boolean isChanged() {
        return changed;
    }

    public boolean isButtonDisabled() {
        return buttonDisabled;
    }

    public void setButtonDisabled(boolean buttonDisabled) {
        this.buttonDisabled = buttonDisabled;
    }

    public String deleteAccount() {
        UUID petOwnerID = loginBean.getLoggedInUserId();
        if (petOwnerID == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "User ID not found. Please log in."));
            return null;
        }

        try {
            boolean success = theService.deletePetOwner(petOwnerID);
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
