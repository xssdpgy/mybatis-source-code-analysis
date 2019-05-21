package com.zang.entity;

/**
 * 角色表
 *
 * @author xssdpgy
 * @version xssdpgy: Role.java,v1.0 2019/5/21 17:21 xssdpgy Exp $$
 * @since 1.0
 */
public class Role {

    private long id;
    private String roleName;
    private String note;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
