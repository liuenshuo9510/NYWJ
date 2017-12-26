package com.nanyue.app.nywj.okhttp.bean;


public class PersonalInfoBean {

    public userBaan getUser() {
        return user;
    }

    public void setUser(userBaan user) {
        this.user = user;
    }

    private userBaan user;


    public static class userBaan {
        private String userName;
        private String password;
        private String serialNo;
        private String nickname;
        private String head;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getSerialNo() {
            return serialNo;
        }

        public void setSerialNo(String serialNo) {
            this.serialNo = serialNo;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getHead() {
            return head;
        }

        public void setHead(String head) {
            this.head = head;
        }
    }
}
