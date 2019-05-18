package org.apollo.blog.controller.usernodemanage;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apollo.blog.common.ApiResult;
import org.apollo.blog.entity.User;
import org.apollo.blog.models.personal.RoleListVO;
import org.apollo.blog.models.role.RoleInfoListVO;
import org.apollo.blog.models.role.RoleInitVO;
import org.apollo.blog.models.role.SaveRoleFO;
import org.apollo.blog.service.NodeService;
import org.apollo.blog.service.RoleService;
import org.apollo.blog.service.UserWeightsService;
import org.apollo.blog.util.BeanCopierUtils;
import org.apollo.blog.util.StringUtil;
import org.apollo.blog.entity.Node;
import org.apollo.blog.entity.Role;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api")
@Api(tags = "用户角色管理类", description = "用户角色管理类")
public class UserRoleManageController {

    @Resource
    private RoleService roleService;
    @Resource
    private NodeService nodeService;
    @Resource
    private UserNodeManageController userNodeManageController;
    @Resource
    private UserWeightsService userWeightsService;

    @ApiOperation(value = "获取节点树")
    @PostMapping({"/rolemanage/roleInit"})
    public ApiResult<RoleInitVO> roleInit() {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        long l = System.currentTimeMillis();
        List<Role> list = roleService.selectRoleAll();
        System.out.println("測試 " + (System.currentTimeMillis() - l) + "ms");
        List<RoleInfoListVO> roleInfoListVOS = list.stream().map(e -> {
            RoleInfoListVO roleInfoListVO = new RoleInfoListVO();
            BeanCopierUtils.copyProperties(e, roleInfoListVO);
            //超级管理员有所有权限
            if (StringUtil.equalsByLong(user.getId(), 1L)) {
                roleInfoListVO.setEdit(0);
            } else {
                roleInfoListVO.setEdit(e.getEdit());
            }
            if (StringUtil.equalsByLong(e.getId(), 1L) ||
                    StringUtil.equalsByLong(e.getId(), 2L) ||
                    StringUtil.equalsByLong(e.getId(), 3L)) {
                roleInfoListVO.setDel(1);
            } else {
                roleInfoListVO.setDel(0);
            }
            return roleInfoListVO;
        }).collect(Collectors.toList());
        List<Role> roleList = userWeightsService.findRoleByUserID(user.getId());
        List<RoleListVO> qxData = roleList.stream().map(e -> {
            RoleListVO roleListVO = new RoleListVO();
            BeanCopierUtils.copyProperties(e, roleListVO);
            return roleListVO;
        }).collect(Collectors.toList());
        return ApiResult.sucess(RoleInitVO.builder().roleData(roleInfoListVOS).qxData(qxData).build());
    }

    @ApiOperation(value = "角色编辑")
    @PostMapping({"/rolemanage/roleEdit"})
    public ApiResult roleEdit(SaveRoleFO saveRoleFO) {
        roleService.insertRole(saveRoleFO);
        return ApiResult.sucess("添加成功！");
    }

    @ApiOperation(value = "删除角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleid", value = "角色id"),
    })
    @PostMapping({"/rolemanage/delRole"})
    public ApiResult delRole(@Param("roleid") long roleid) {
        if (roleid == 0) {
            return ApiResult.fail("参数不完整！");
        }
        try {
            roleService.deleteRole(roleid);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ApiResult.fail("删除失败！");
        }
        return ApiResult.sucess("删除成功！");
    }

    @ApiOperation(value = "获取单个角色信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleid", value = "角色id"),
    })
    @PostMapping({"/rolemanage/nodeInfoById"})
    public ApiResult nodeInit(@Param("roleid") long roleid) {
        Role role = roleService.getById(roleid);
        long weights = 0;
        if (role != null) {
            weights = role.getWeights();
        }
        List<Node> list = nodeService.seachNodeList("nodeid", "pnodeid", "nodename", "nodetype", "descripte", "nodeicon", "weights");
        BigInteger bigInteger = new BigInteger(String.valueOf(weights));
        JSONArray jsonArray = userNodeManageController.recData(list, "0", bigInteger);
        return ApiResult.sucess(jsonArray);
    }

    @ApiOperation(value = "保存角色节点权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleid", value = "角色id"),
            @ApiImplicitParam(name = "nodelist", value = "节点数据列表"),
    })
    @PostMapping({"/rolemanage/saveNodeQx"})
    public ApiResult saveNode(long roleid, String nodelist) {
        if (StringUtil.strIsEmpty(nodelist)) {
            return ApiResult.fail("缺失必要参数！");
        }
        JSONArray jsonArray = JSONArray.parseArray(nodelist);
        BigInteger bigInteger = userNodeManageController.getBigInteger(jsonArray);
        String value = bigInteger.toString();
        Role tblRole = roleService.getById(roleid);
        if (tblRole == null) {
        } else {
            tblRole.setId(roleid);
            tblRole.setWeights(Long.parseLong(value));
            roleService.updateById(tblRole);
        }
        return ApiResult.SUCCESS;
    }
}