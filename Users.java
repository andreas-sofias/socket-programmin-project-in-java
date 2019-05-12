import java.io.Serializable;

public class Users implements Serializable {

    //For message exchange they should have the same serializable number
    private static final long serialVersionUID = 578515438738407941L;

    private String ip;
    private String username;
    private int password;
    private int energy;
    private int energy_needed;

    public Users(String ip, String username, int password, int energy, int energy_needed){

        this.ip = ip;
        this.username = username;
        this.password = password;
        this.energy = energy;
        this.energy_needed = energy_needed;

    }

    public String getIp() {
        return ip;
    }

    public String getUsername() {
        return username;
    }

    public int getPassword() {
        return password;
    }

    public void setEnergy(int energy){
        this.energy = energy;
    }

    public int getEnergy() {
        return energy;
    }

    public void increaseEnergy(int amount){
        this.energy += amount;
    }
    public void decreaseEnergy(int amount){
        this.energy -= amount;
    }

    public int getEnergy_needed(){ return this.energy_needed; }

}
