package ch.unil.doplab.demo.ui;

import ch.unil.furrybuddy.domain.AdoptionRequest;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class AdoptionRequestBean implements Serializable {

    private String adopterName;
    private String petName;
    private String petType;
    private Date requestDate;
    private String status;

    // Getters et Setters pour toutes les propriétés
    public String getAdopterName() {
        return adopterName;
    }

    public void setAdopterName(String adopterName) {
        this.adopterName = adopterName;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetType() {
        return petType;
    }

    public void setPetType(String petType) {
        this.petType = petType;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Méthodes métier
    public String submitRequest() {
        // Logique pour soumettre une demande
        this.status = "Submitted";
        this.requestDate = new Date();
        System.out.println("Adoption request submitted for pet: " + petName);
        return "confirmation.xhtml?faces-redirect=true"; // Redirige vers la page de confirmation
    }

    public String cancelRequest() {
        // Logique pour annuler une demande
        this.status = "Cancelled";
        System.out.println("Adoption request cancelled for pet: " + petName);
        return "CancelAdoptionRequest.xhtml?faces-redirect=true";
    }
}


