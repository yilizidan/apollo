package org.apollo.blog.controller.usernodemanage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apollo.blog.common.ApiResult;
import org.apollo.blog.entity.User;
import org.apollo.blog.models.node.SaveNodeFO;
import org.apollo.blog.service.NodeService;
import org.apollo.blog.service.UserService;
import org.apollo.blog.service.UserWeightsService;
import org.apollo.blog.util.BeanCopierUtils;
import org.apollo.blog.util.StringUtil;
import org.apollo.blog.entity.Node;
import org.apollo.blog.entity.Role;
import org.apollo.blog.entity.UserWeights;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户节点管理
 */
@RestController
@Slf4j
@RequestMapping("/api")
@Api(tags = "用户节点管理", description = "用户节点管理")
public class UserNodeManageController {

    @Resource
    private NodeService nodeService;
    @Resource
    private UserWeightsService userWeightsService;
    @Resource
    private UserService userService;

    @ApiOperation(value = "获取用户树数据")
    @PostMapping({"/nodemanage/personInit"})
    public ApiResult personInit() {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        List<User> list = userService.seachAllUser("id", "username", "nickname");
        List<UserWeights> tblManagerWeightsList = userWeightsService.seachUserInfo();
        Map<Long, Long> map = new HashMap<>();
        for (UserWeights t : tblManagerWeightsList) {
            map.put(t.getUserId(), t.getRoleId());
        }


        JSONArray jsonArray = new JSONArray();

        boolean isadminpro = false;
        boolean isadmin = false;
        List<Role> roleList = userWeightsService.findRoleByUserID(user.getId());
        if (StringUtil.equalsByLong(roleList.get(0).getId(), 1L)) {
            isadminpro = true;
        } else if (StringUtil.equalsByLong(roleList.get(0).getId(), 2L)) {
            isadmin = true;
        }

        for (User t : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", t.getId() + "");
            jsonObject.put("username", t.getUsername());
            jsonObject.put("nickname", t.getNickname());
            if (StringUtil.equalsByLong(user.getId(), 1L)) {
                jsonObject.put("edit", 0);
            } else {
                if (map.containsKey(t.getId())) {
                    //主管理员
                    if (StringUtil.equalsByLong(map.get(t.getId()), 1L)) {
                        jsonObject.put("edit", 1);
                    }
                    //一般管理员
                    else if (StringUtil.equalsByLong(map.get(t.getId()), 2L)) {
                        if (isadminpro) {
                            jsonObject.put("edit", 0);
                        } else {
                            jsonObject.put("edit", 1);
                        }
                    } else {
                        if (isadminpro || isadmin) {
                            jsonObject.put("edit", 0);
                        } else {
                            jsonObject.put("edit", 1);
                        }
                    }
                } else {
                    jsonObject.put("edit", 0);
                }
            }
            jsonArray.add(jsonObject);
        }
        map.clear();
        return ApiResult.sucess(jsonArray);
    }

    @ApiOperation(value = "获取节点详情数据")
    @PostMapping({"/nodelinkmanage/nodeLinkInit"})
    public ApiResult nodeLinkInit() {
        List<Node> list = nodeService.seachNodeList("nodeid", "pnodeid", "nodename", "nodetype", "descripte", "nodeurl", "nodeicon", "weights");
        return ApiResult.sucess(recData(list, "0"));
    }

    @ApiOperation(value = "获取节点简单数据")
    @PostMapping({"/nodelinkmanage/nodeOneLink"})
    public ApiResult nodeOneLink() {
        List<Node> list = nodeService.seachNodeList2("nodeid", "nodename", "nodeicon");
        JSONArray jsonArray = recData(list);
        return ApiResult.sucess(jsonArray);
    }

    @ApiOperation(value = "编辑节点")
    @PostMapping({"/nodelinkmanage/nodeLinkEdit"})
    public ApiResult<?> nodeLinkEdit(SaveNodeFO saveNodeFO) {
        if (!nodeService.seachSameNodeByName(saveNodeFO.getNodename(), saveNodeFO.getNodeid())) {
            return ApiResult.fail("节点名称重复!");
        }
        if (!nodeService.seachSameNodeByType(saveNodeFO.getNodetype(), saveNodeFO.getNodeid())) {
            return ApiResult.fail("节点编码重复!");
        }
        try {
            Node tblNode = new Node();
            if (StringUtil.equalsByLong(saveNodeFO.getNodeid(), 0L)) {
                BeanCopierUtils.copyProperties(saveNodeFO, tblNode);
                tblNode.setNodeid(null);
                tblNode.setIsdelete(1);
                tblNode.setPxh(nodeService.maxPxh(saveNodeFO.getPnodeid()));
                tblNode.setWeights(nodeService.maxWeights());
                nodeService.save(tblNode);
            } else {
                tblNode = nodeService.getById(saveNodeFO.getNodeid());
                BeanCopierUtils.copyProperties(saveNodeFO, tblNode);
                nodeService.updateById(tblNode);
            }
        } catch (NumberFormatException e) {
            log.error(e.getMessage(), e);
            return ApiResult.fail("设置节点失败!");
        }
        return ApiResult.SUCCESS;
    }

    @ApiOperation(value = "删除节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nodeid", value = "节点id"),
    })
    @PostMapping({"/nodelinkmanage/delnNodeLink"})
    public ApiResult nodeLinkEdit(long nodeid) {
        try {
            Node tblNode = nodeService.getById(nodeid);
            if (nodeService.hasChildren(tblNode.getNodeid())) {
                return ApiResult.fail("请先删除子节点!");
            }
            if (tblNode != null) {
                nodeService.removeById(nodeid);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ApiResult.fail("删除节点失败!");
        }
        return ApiResult.SUCCESS;
    }

    @ApiOperation(value = "获取用户节点权限数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userid", value = "用户id"),
    })
    @PostMapping({"/nodemanage/nodeInit"})
    public ApiResult nodeInit(long userid) {
        List<Node> list = nodeService.seachNodeList("nodeid", "pnodeid", "nodename", "nodetype", "descripte", "nodeicon", "weights");
        UserWeights userWeights = userWeightsService.getById(userid);
        long weights = 0;
        if (userWeights != null) {
            weights = userWeights.getNodeWeights();
        }
        BigInteger bigInteger = new BigInteger(String.valueOf(weights));
        JSONArray jsonArray = recData(list, "0", bigInteger);
        return ApiResult.sucess(jsonArray);
    }

    @ApiOperation(value = "保存用户节点权限数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userid", value = "用户id"),
            @ApiImplicitParam(name = "nodelist", value = "节点数据列表"),
    })
    @PostMapping({"/nodemanage/saveNode"})
    public ApiResult saveNode(long userid, String nodelist) {
        if (StringUtil.strIsEmpty(nodelist)) {
            return ApiResult.fail("缺失必要参数！");
        }
        JSONArray jsonArray = JSONArray.parseArray(nodelist);
        BigInteger bigInteger = getBigInteger(jsonArray);
        String value = bigInteger.toString();
        UserWeights userWeights = userWeightsService.getById(userid);
        if (userWeights == null) {
            userWeights = new UserWeights();
            userWeights.setUserId(userid);
            userWeights.setNodeWeights(Long.parseLong(value));
            userWeightsService.save(userWeights);
        } else {
            userWeights.setUserId(userid);
            userWeights.setNodeWeights(Long.parseLong(value));
            userWeightsService.updateById(userWeights);
        }
        return ApiResult.SUCCESS;
    }

    public JSONArray recData(List<Node> tblNodeList, String pid, BigInteger bigInteger) {
        JSONArray jsonArray = new JSONArray();
        for (Node t : tblNodeList) {
            if (pid.equals(t.getPnodeid().toString())) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("nodeid", t.getNodeid() + "");
                jsonObject.put("pnodeid", t.getPnodeid() + "");
                jsonObject.put("nodename", t.getNodename());
                jsonObject.put("nodetype", t.getNodetype());
                jsonObject.put("descripte", t.getDescripte());
                jsonObject.put("nodeicon", t.getNodeicon());
                jsonObject.put("weights", t.getWeights());

                JSONArray childAry = new JSONArray();
                if (!pid.equals(t.getNodeid().toString())) {
                    childAry = recData(tblNodeList, t.getNodeid().toString(), bigInteger);
                }
                if (bigInteger.testBit(t.getWeights())) {
                    jsonObject.put("check", "0");
                } else {
                    jsonObject.put("check", "1");
                }
                if (childAry != null && childAry.size() > 0) {
                    jsonObject.put("childlist", childAry);
                } else {
                    jsonObject.put("childlist", new JSONArray());
                }
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    private JSONArray recData(List<Node> tblNodeList, String pid) {
        JSONArray jsonArray = new JSONArray();
        for (Node t : tblNodeList) {
            if (pid.equals(t.getPnodeid().toString())) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("nodeid", t.getNodeid() + "");
                jsonObject.put("pnodeid", t.getPnodeid() + "");
                jsonObject.put("nodename", t.getNodename());
                jsonObject.put("nodetype", t.getNodetype());
                jsonObject.put("descripte", t.getDescripte());
                jsonObject.put("nodeicon", t.getNodeicon());
                jsonObject.put("weights", t.getWeights());
                jsonObject.put("nodeurl", t.getNodeurl());

                JSONArray childAry = new JSONArray();
                if (!pid.equals(t.getNodeid().toString())) {
                    childAry = recData(tblNodeList, t.getNodeid().toString());
                }
                if (childAry != null && childAry.size() > 0) {
                    jsonObject.put("childlist", childAry);
                } else {
                    jsonObject.put("childlist", new JSONArray());
                }
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    private JSONArray recData(List<Node> tblNodeList) {
        JSONArray jsonArray = new JSONArray();
        for (Node t : tblNodeList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nodeid", t.getNodeid() + "");
            jsonObject.put("nodename", t.getNodename());
            jsonObject.put("nodeicon", t.getNodeicon());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }


    public BigInteger getBigInteger(JSONArray jsonArray) {
        BigInteger bigInteger = new BigInteger("0");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            String check = object.getString("check");
            if (StringUtil.equals(check, "0")) {
                String weights = object.getString("weights");
                bigInteger = bigInteger.setBit(Integer.parseInt(weights));
            }
            JSONArray childlist = object.getJSONArray("childlist");
            if (childlist != null && childlist.size() > 0) {
                for (int j = 0; j < childlist.size(); j++) {
                    JSONObject object1 = childlist.getJSONObject(j);
                    String check1 = object1.getString("check");
                    if (StringUtil.equals(check1, "0")) {
                        String weights1 = object1.getString("weights");
                        bigInteger = bigInteger.setBit(Integer.parseInt(weights1));
                    }
                }
            }
        }
        return bigInteger;
    }
}