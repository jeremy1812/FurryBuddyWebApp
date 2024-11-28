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
        this.status = "Submitted";
        this.requestDate = new Date();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Adoption request successfully submitted."));
        return "confirmation.xhtml?faces-redirect=true";
    }

    public String modifyRequest() {
        this.status = "Modified";
        this.requestDate = new Date();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Adoption request successfully modified."));
        return "confirmation.xhtml?faces-redirect=true";
    }

    public String cancelRequest() {
        this.status = "Cancelled";
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Adoption request successfully cancelled."));
        return "CancelAdoptionRequest.xhtml?faces-redirect=true";
    }

}


