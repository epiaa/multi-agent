package com.multiagent.common.entity;



public class UserHolder {
    private static final ThreadLocal<UserPO> userHolder = new ThreadLocal<UserPO>();

    public static void setUser(UserPO user) {
        userHolder.set(user);
    }

    public static UserPO getUser() {
        return userHolder.get();
    }

    public static void removeUser() {
        userHolder.remove();
    }
}
