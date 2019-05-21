package com.zang.mapper;

import com.zang.entity.Role;

public interface RoleMapper {

    public Role getRole(Long id);
    public Role findRole(String roleName);
    public int insertRole(Role role);
    public int updateRole(Role role);
    public int deleteRole(Long id);

}
