package org.apollo.blog.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apollo.blog.entity.Node;
import org.apollo.blog.entity.Role;
import org.apollo.blog.entity.UserWeights;
import org.apollo.blog.mapper.UserWeightsMapper;
import org.apollo.blog.service.NodeService;
import org.apollo.blog.service.RoleService;
import org.apollo.blog.service.UserWeightsService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 人员节点表 服务实现类
 */
@Slf4j
@Service
@Transactional
public class UserWeightsServiceImpl extends ServiceImpl<UserWeightsMapper, UserWeights> implements UserWeightsService {

    @Resource
    private RoleService roleService;
    @Resource
    private NodeService nodeService;

    @Override
    @Cacheable(value = "nodetree", unless = "#result==null")
    public JSONArray getPersonalNodeTree(long id) {
        UserWeights userWeights = baseMapper.selectById(id);
        if (userWeights == null) {
            return new JSONArray();
        }
        List<Node> nodeList = nodeService.seachNodeList("nodeid", "pnodeid", "nodename", "nodeurl", "nodeurl", "nodeicon", "weights");
        BigInteger bigInteger = new BigInteger(String.valueOf(userWeights.getNodeWeights()));
        List<Node> node = new ArrayList<>();
        for (Node t : nodeList) {
            if (bigInteger.testBit(t.getWeights())) {
                node.add(t);
            }
        }
        return recData(node, "0");
    }

    private JSONArray recData(List<Node> nodeList, String pid) {
        JSONArray jsonArray = new JSONArray();
        for (Node t : nodeList) {
            if (pid.equals(t.getPnodeid().toString())) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("nodeid", t.getNodeid());
                jsonObject.put("pnodeid", t.getPnodeid());
                jsonObject.put("nodename", t.getNodename());
                jsonObject.put("nodeurl", t.getNodeurl());
                jsonObject.put("nodeicon", t.getNodeicon());

                JSONArray childAry = new JSONArray();
                if (!pid.equals(t.getNodeid().toString())) {
                    childAry = recData(nodeList, t.getNodeid().toString());
                }
                if (childAry != null && childAry.size() > 0) {
                    jsonObject.put("childlist", childAry);
                    jsonObject.put("child", "0");
                } else {
                    jsonObject.put("childlist", new JSONArray());
                    jsonObject.put("child", "1");
                }
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    @Override
    public List<Role> findRoleByUserID(long id) {
        UserWeights userWeights = baseMapper.selectById(id);
        if (userWeights == null) {
            return new ArrayList<>();
        }
        Role role = roleService.selectRoleById(userWeights.getRoleId());
        List<Role> list = new ArrayList<>();
        list.add(role);
        return list;
    }

    @Override
    @Cacheable(value = "nodeuserlist", unless = "#result==null")
    public List<UserWeights> seachUserInfo() {
        return baseMapper.selectList(new QueryWrapper<>());
    }
}
