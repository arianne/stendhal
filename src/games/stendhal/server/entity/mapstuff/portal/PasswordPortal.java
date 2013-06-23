package games.stendhal.server.entity.mapstuff.portal;

import games.stendhal.server.entity.RPEntity;

public class PasswordPortal extends Portal {
    
    private String password;
    
    private String rejected;
    
    public PasswordPortal(String password) {
        this.password = password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public boolean isCorrect(String password) {
        return password.equals(this.password);
    }
    
    public void setRejectedMessage(final String message) {
        this.rejected = message;
    }
    
    public String getRejectedMessage() {
        return this.rejected;
    }
    
    /**
     * Password portals are not "used"
     */
    public void sayPassword(final RPEntity entity, final String password) {
        if (password.equals(this.password)) {
            onUsed(entity);
        }
    }
}
