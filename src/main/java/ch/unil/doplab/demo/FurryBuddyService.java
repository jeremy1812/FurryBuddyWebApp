import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")  // à la place de /api on va mettre le chemin où FurryBuddyService est deployé (exemple : http://localhost:8080)
public class FurryBuddyService extends Application {
}
