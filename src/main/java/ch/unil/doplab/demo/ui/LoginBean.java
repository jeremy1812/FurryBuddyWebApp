package ch.unil.doplab.demo.ui;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named
@RequestScoped
public class LoginBean {
    private String username;
    private String password;
    private boolean loggedIn = false;

    // Hardcoded credentials for demo purposes
    private final String validUsername = "admin";
    private final String validPassword = "admin123";
    
    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }


    // Login method
    public String login() {
        if (validUsername.equals(username) && validPassword.equals(password)) {
            loggedIn = true;
            return "home?faces-redirect=true"; // Redirect to the home page if login is successful
        } else {
            loggedIn = false;
            return null; // Stay on the login page
        }
    }
    // Logout method to clear session
    public String logout() {
        loggedIn = false;
        return "login?faces-redirect=true"; // Redirect back to login page
    }
}