package com.nitish.gamershub.Pojo;

public class UserProfile {


    public ProfileData profileData;



    public UserProfile() {

    }

    public UserProfile(ProfileData profileData) {
        this.profileData = profileData;
    }

    public ProfileData getProfileData() {
        return profileData;
    }

    public void setProfileData(ProfileData profileData) {
        this.profileData = profileData;
    }


    public static class ProfileData {
        public String name ="";
        public String email="";
        public int gamePoints = 0;

        public String lastLogin="";



        public ProfileData() {

        }

        public ProfileData(String name, String email, int gamePoints, String lastLogin) {
            this.name = name;
            this.email = email;
            this.gamePoints = gamePoints;
            this.lastLogin = lastLogin;
        }

        public String getLastLogin() {
            return lastLogin;
        }

        public void setLastLogin(String lastLogin) {
            this.lastLogin = lastLogin;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getGamePoints() {
            return gamePoints;
        }

        public void setGamePoints(int gamePoints) {
            this.gamePoints = gamePoints;
        }
    }
}
