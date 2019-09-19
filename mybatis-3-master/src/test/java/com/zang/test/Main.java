package com.zang.test;

import com.zang.entity.Role;
import com.zang.mapper.RoleMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * 测试类，辅助断点调试
 * @author xssdpgy
 * @version xssdpgy: Main.java,v1.0 2019/5/21 17:49 xssdpgy Exp $$
 * @since 1.0
 */
public class Main {
    public static void main(String[] args) {
        String resource="com/zang/resource/mybatis-config.xml";
        InputStream inputStream=null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = null;
        try {
            sqlSession=sqlSessionFactory.openSession();
            RoleMapper roleMapper=sqlSession.getMapper(RoleMapper.class);
            Role role=roleMapper.getRole(1L);
//            role.setNote("二次备注");
//            roleMapper.updateRole(role);
            System.out.println(role.getId()+":"+role.getRoleName()+":"+role.getNote());
//            sqlSession.commit();

        } catch (Exception e) {
            sqlSession.rollback();
            e.printStackTrace();
        }finally {
            sqlSession.close();
        }
    }
}
