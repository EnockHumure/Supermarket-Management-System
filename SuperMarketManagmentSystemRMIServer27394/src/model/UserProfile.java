package model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "user_profile")
public class UserProfile implements Serializable {
    
    public static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long profileId;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    private String address;
    
    @Column(name = "profile_image_path")
    private String profileImagePath;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public UserProfile() {
    }

    public UserProfile(Long profileId, String phoneNumber, String address, String profileImagePath, User user) {
        this.profileId = profileId;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.profileImagePath = profileImagePath;
        this.user = user;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
