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
        public int gameCoins = 0;

        public String lastLogin="";
        public String lastOpened="";


        public ProfileData() {

        }

        public ProfileData(String name, String email, int gameCoins, String lastLogin, String lastOpened) {
            this.name = name;
            this.email = email;
            this.gameCoins = gameCoins;
            this.lastLogin = lastLogin;
            this.lastOpened = lastOpened;
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

        public int getGameCoins() {
            return gameCoins;
        }

        public void setGameCoins(int gameCoins) {
            this.gameCoins = gameCoins;
        }

        public String getLastLogin() {
            return lastLogin;
        }

        public void setLastLogin(String lastLogin) {
            this.lastLogin = lastLogin;
        }

        public String getLastOpened() {
            return lastOpened;
        }

        public void setLastOpened(String lastOpened) {
            this.lastOpened = lastOpened;
        }
    }
}
