package ch.unil.doplab.demo;

import ch.unil.furrybuddy.domain.PetOwner;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@ApplicationScoped
public class FurryBuddyService {
    private static final String BASE_URL = "http://localhost:8080/FurryBuddyService-1.0-SNAPSHOT/api/";
    private WebTarget petOwnerTarget;
    private WebTarget adopterTarget;
    private WebTarget advertisementTarget;
    private WebTarget adoptionRequestTarget;
    private WebTarget serviceTarget;

    @PostConstruct
    public void init() {
        System.out.println("FurryBuddyService created: " + this.hashCode());
        Client client = ClientBuilder.newClient();
        petOwnerTarget = client.target(BASE_URL).path("petOwners");
        adopterTarget = client.target(BASE_URL).path("adopters");
        advertisementTarget = client.target(BASE_URL).path("advertisements");
        adoptionRequestTarget = client.target(BASE_URL).path("adoptionRequests");
        serviceTarget = client.target(BASE_URL).path("service");
    }

    /*
     * Service operations
     */

    public void resetService() {
        String response = serviceTarget
                .path("reset")
                .request()
                .get(String.class);
    }

    /*
     * PetOwner operations
     */

    public PetOwner getPetOwner(String id) {
        var petowner = petOwnerTarget
                .path(id.toString())
                .request()
                .get(PetOwner.class);
        return petowner;
    }
}