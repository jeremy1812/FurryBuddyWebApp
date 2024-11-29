package ch.unil.doplab.demo.ui;

import ch.unil.furrybuddy.domain.AdoptionRequest;
import ch.unil.doplab.demo.FurryBuddyService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Named
@SessionScoped
public class AdoptionRequestBean implements Serializable {
    private static final long serialVersionUID = 1L;

    // Champs pour les détails d'une demande
    private String adopterName;
    private String petName;
    private String petType;
    private Date requestDate;
    private String status;
    private String requestMessage;

    private List<AdoptionRequest> requests; // Liste des demandes d'adoption
    private AdoptionRequest selectedRequest; // Requête sélectionnée

    @Inject
    private FurryBuddyService service; // Service pour interagir avec le backend

    // Getters et Setters
    public List<AdoptionRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<AdoptionRequest> requests) {
        this.requests = requests;
    }

    public AdoptionRequest getSelectedRequest() {
        return selectedRequest;
    }

    public void setSelectedRequest(AdoptionRequest selectedRequest) {
        this.selectedRequest = selectedRequest;
    }

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

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    // Chargement des requêtes pour un adoptant
    public void loadRequestsForAdopter() {
        try {
            // Exemple : Charger les requêtes depuis le service
            this.requests = service.getRequestsForAdopter(adopterName);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to load requests."));
        }
    }

    // Annuler une demande
    public String cancelRequest(AdoptionRequest request) {
        try {
            request.setStatus(AdoptionRequest.Status.valueOf("Cancelled"));
            service.updateRequestStatus(request); // Exemple de mise à jour
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Request cancelled successfully."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to cancel request."));
        }
        return null; // Reste sur la même page
    }

    // Voir les détails d'une demande
    public String viewRequestDetails(AdoptionRequest request) {
        this.selectedRequest = request;
        return "ViewRequestDetails.xhtml?faces-redirect=true";
    }

    // Modifier une demande
    public String modifyRequest(AdoptionRequest request) {
        this.selectedRequest = request;
        return "ModifyRequest.xhtml?faces-redirect=true";
    }
}
