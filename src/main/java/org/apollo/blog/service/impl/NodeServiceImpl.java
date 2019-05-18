package org.apollo.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apollo.blog.entity.Node;
import org.apollo.blog.mapper.NodeMapper;
import org.apollo.blog.service.NodeService;
import org.apollo.blog.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class NodeServiceImpl extends ServiceImpl<NodeMapper, Node> implements NodeService {

    @Override
    public List<Node> seachNodeList(String... columns) {
        QueryWrapper<Node> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(columns);
        queryWrapper.orderByAsc("pxh");
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<Node> seachNodeList2(String... columns) {
        QueryWrapper<Node> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(columns);
        queryWrapper.eq("pnodeid", "0");
        queryWrapper.ne("nodeid", "0");
        queryWrapper.orderByAsc("pxh");
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public boolean seachSameNodeByName(String value, long nodeid) {
        QueryWrapper<Node> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("nodename", value);
        if (!StringUtil.equalsByLong(nodeid, 0L)) {
            queryWrapper.notIn("nodeid", nodeid);
        }
        List<Node> list = baseMapper.selectList(queryWrapper);
        if (list.size() > 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean seachSameNodeByType(String value, long nodeid) {
        QueryWrapper<Node> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("nodetype", value);
        if (!StringUtil.equalsByLong(nodeid, 0L)) {
            queryWrapper.notIn("nodeid", nodeid);
        }
        List<Node> list = baseMapper.selectList(queryWrapper);
        if (list.size() > 0) {
            return false;
        }
        return true;
    }

    @Override
    public Integer maxPxh(long pid) {
        Integer integer = 0;
        QueryWrapper<Node> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Node::getPnodeid, pid);
        queryWrapper.select("pxh");
        queryWrapper.orderByDesc("pxh");
        Page<Node> page = new Page<Node>(0, 1);
        IPage<Node> tblNodePage = baseMapper.selectPage(page, queryWrapper);
        if (tblNodePage.getRecords().size() > 0) {
            List<Node> list = tblNodePage.getRecords();
            integer = list.get(0).getPxh() + 1;
        }
        return integer;
    }

    @Override
    public Integer maxWeights() {
        Integer integer = 0;
        QueryWrapper<Node> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("weights");
        queryWrapper.orderByDesc("weights");
        Page<Node> page = new Page<Node>(0, 1);
        IPage<Node> tblNodePage = baseMapper.selectPage(page, queryWrapper);
        if (tblNodePage.getSize() > 0) {
            List<Node> list = tblNodePage.getRecords();
            integer = list.get(0).getWeights() + 1;
        }
        return integer;
    }

    @Override
    public boolean hasChildren(long pid) {
        QueryWrapper<Node> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Node::getPnodeid, pid);
        List<Node> list = baseMapper.selectList(queryWrapper);
        if (list.size() > 0) {
            return true;
        }
        return false;
    }
}
