package com.hotel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.entity.SysRole;
import com.hotel.entity.SysRoleMenu;
import com.hotel.mapper.SysMenuMapper;
import com.hotel.mapper.SysRoleMapper;
import com.hotel.mapper.SysRoleMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 角色服务
 */
@Service
@RequiredArgsConstructor
public class SysRoleService extends ServiceImpl<SysRoleMapper, SysRole> {

    private final SysRoleMenuMapper roleMenuMapper;
    private final SysMenuMapper menuMapper;

    /**
     * 分页查询角色
     */
    public IPage<SysRole> page(Page<SysRole> page, String roleName, Integer status) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(roleName), SysRole::getRoleName, roleName)
               .eq(status != null, SysRole::getStatus, status)
               .orderByAsc(SysRole::getSort);
        return page(page, wrapper);
    }

    /**
     * 查询用户角色列表
     */
    public List<SysRole> getRolesByUserId(Long userId) {
        return baseMapper.selectRolesByUserId(userId);
    }

    /**
     * 查询角色菜单ID列表
     */
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        return menuMapper.selectMenuIdsByRoleId(roleId);
    }

    /**
     * 创建角色
     */
    @Transactional(rollbackFor = Exception.class)
    public void createRole(SysRole role, List<Long> menuIds) {
        save(role);
        saveRoleMenus(role.getId(), menuIds);
    }

    /**
     * 更新角色
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(SysRole role, List<Long> menuIds) {
        updateById(role);
        roleMenuMapper.deleteByRoleId(role.getId());
        saveRoleMenus(role.getId(), menuIds);
    }

    /**
     * 保存角色菜单关联
     */
    private void saveRoleMenus(Long roleId, List<Long> menuIds) {
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenuMapper.insert(roleMenu);
            }
        }
    }
}
