package com.aurora.handler;

import com.aurora.model.dto.ResourceRoleDTO;
import com.aurora.mapper.RoleMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
public class FilterInvocationSecurityMetadataSourceImpl implements FilterInvocationSecurityMetadataSource {


    @Autowired
    private RoleMapper roleMapper;

    private static List<ResourceRoleDTO> resourceRoleList;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @PostConstruct
    private void loadResourceRoleList() {
        resourceRoleList = roleMapper.listResourceRoles();
    }

    public void clearDataSource() {
        resourceRoleList = null;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        if (CollectionUtils.isEmpty(resourceRoleList)) {
            this.loadResourceRoleList();
        }
        FilterInvocation fi = (FilterInvocation) object;
        return getAttributes(fi.getRequest());
    }

    public Collection<ConfigAttribute> getAttributes(HttpServletRequest request) {
        if (CollectionUtils.isEmpty(resourceRoleList)) {
            this.loadResourceRoleList();
        }
        String method = request.getMethod();
        String url = request.getRequestURI();
        for (ResourceRoleDTO resourceRoleDTO : resourceRoleList) {
            if (antPathMatcher.match(resourceRoleDTO.getUrl(), url) && Objects.equals(resourceRoleDTO.getRequestMethod(), method)) {
                List<String> roleList = resourceRoleDTO.getRoleList();
                if (CollectionUtils.isEmpty(roleList)) {
                    return SecurityConfig.createList("disable");
                }
                return SecurityConfig.createList(roleList.toArray(new String[]{}));
            }
        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
