package com.nanyue.app.nywj.okhttp.bean;

/**
 * Created by 87710 on 2017/12/13.
 */

public class LogInBean {
    /**
     * status : {"succeed":0,"error_code":0}
     * data : {"session":{"uid":"643ba4ffe9404081ad91fb44a39097e9","sid":"811804ABC6BCDE2C54422DD2709E022C"},"user":{"userName":"123","password":"202cb962ac59075b964b07152d234b70","serialNo":"862679037221411","nickname":"开发测试，请勿更改"}}
     */

    private StatusBean status;
    private DataBean data;

    public StatusBean getStatus() {
        return status;
    }

    public void setStatus(StatusBean status) {
        this.status = status;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class StatusBean {
        /**
         * succeed : 0
         * error_code : 0
         */

        private int succeed;
        private int error_code;
        private String error_desc;

        public String getError_desc() {
            return error_desc;
        }

        public void setError_desc(String error_desc) {
            this.error_desc = error_desc;
        }

        public int getSucceed() {
            return succeed;
        }

        public void setSucceed(int succeed) {
            this.succeed = succeed;
        }

        public int getError_code() {
            return error_code;
        }

        public void setError_code(int error_code) {
            this.error_code = error_code;
        }
    }

    public static class DataBean {
        /**
         * session : {"uid":"643ba4ffe9404081ad91fb44a39097e9","sid":"811804ABC6BCDE2C54422DD2709E022C"}
         * user : {"userName":"123","password":"202cb962ac59075b964b07152d234b70","serialNo":"862679037221411","nickname":"开发测试，请勿更改"}
         */

        private SessionBean session;
        private UserBean user;

        public SessionBean getSession() {
            return session;
        }

        public void setSession(SessionBean session) {
            this.session = session;
        }

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public static class SessionBean {
            /**
             * uid : 643ba4ffe9404081ad91fb44a39097e9
             * sid : 811804ABC6BCDE2C54422DD2709E022C
             */

            private String uid;
            private String sid;

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getSid() {
                return sid;
            }

            public void setSid(String sid) {
                this.sid = sid;
            }
        }

        public static class UserBean {
            /**
             * userName : 123
             * password : 202cb962ac59075b964b07152d234b70
             * serialNo : 862679037221411
             * nickname : 开发测试，请勿更改
             */

            private String userName;
            private String password;
            private String serialNo;
            private String nickname;

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
        }
    }
}
