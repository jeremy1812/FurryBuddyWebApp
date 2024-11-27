package ch.unil.doplab.demo.ui;

import java.util.List;

public class AdoptionRequestBean {implements Serializable {
    private String uuid;
    private String name;
    private List<String> adoptionApplications;

    // Load data for the adopter
    public void loadAdopterData() {
        // Load the adopter's data using the UUID
        // Example: Query service or database for adopter's details and applications
    }

    // Getter and Setter for UUID
    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    // Getter and Setter for other fields as needed
}

}

// s'assurer que le bean contient une propriété pour la valeur de l'aire de texte :
@ManagedBean
@RequestScoped
public class Bean {
    private String comments;

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String submit() {
        // Logique lors de l'envoi
        System.out.println("Commentaires soumis : " + comments);
        return null; // ou une navigation
    }
}
