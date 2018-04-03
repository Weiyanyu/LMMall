package top.yeonon.lmmall.common;

/**
 * @Author yeonon
 * @date 2018/4/2 0002 20:49
 **/
public interface ServerConst {

    String SESSION_KEY_PREFIX = "SESSION_KEY_FOR_";
    /**
     * 保存用户信息的SESSION KEY
     */
    String SESSION_KEY_FOR_CURRENT = SESSION_KEY_PREFIX + "CURRENT_USER";


    enum UserFieldValidate {
        EMAIL("email"),
        PHONE("phone"),
        USERNAME("username");

        private String desc;

        UserFieldValidate(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * 用户角色枚举
     */
    enum Role {
        ADMIN(0, "ADMIN"),
        USER(1, "USER");

        private int code;
        private String desc;

        Role(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}
