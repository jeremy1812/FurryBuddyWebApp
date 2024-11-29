package ch.unil.doplab.demo.ui;

import ch.unil.furrybuddy.domain.AdoptionRequest;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Named
@SessionScoped
public class AdoptionRequestBean implements Serializable {

    private String adopterName;
    private String petName;
    private String petType;
    private Date requestDate;
    private String status;
    private String requestMessage;
    private AdvertisementBean currentAdvertisement;


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

    public String getRequestMessage() { // Getter for requestMessage
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) { // Setter for requestMessage
        this.requestMessage = requestMessage;
    }
    public AdvertisementBean getCurrentAdvertisement() {
        return currentAdvertisement;
    }

    public void setCurrentAdvertisement(AdvertisementBean currentAdvertisement) {
        this.currentAdvertisement = currentAdvertisement;
    }

    // Méthodes métier
    public String submitRequest() {
        this.status = "Submitted";
        this.requestDate = new Date();

        // Mock sending request, log pet name
        System.out.println("Request sent for pet: " + getPetName());

        return "ConfirmationRequestSent.xhtml?faces-redirect=true";
    }

    public String cancelRequest() {
        this.status = "Cancelled";
        // Utilise le Flash Scope pour persister le message après redirection
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Adoption request successfully cancelled."));
        return "CancelAdoptionRequest.xhtml?faces-redirect=true";
    }

    public String updateRequestStatus() {
        this.status = "PENDING";// à revoir;
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Adoption request successfully cancelled."));
        return "CancelAdoptionRequest.xhtml?faces-redirect=true";
    }

}


