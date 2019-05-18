package org.apollo.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.apollo.blog.entity.Node;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 */
public interface NodeService extends IService<Node> {

    List<Node> seachNodeList(String... columns);

    List<Node> seachNodeList2(String... columns);

    boolean seachSameNodeByName(String value,long nodeid);

    boolean seachSameNodeByType(String value,long nodeid);

    Integer maxPxh(long pid);

    Integer maxWeights();

    boolean hasChildren(long pid);
}
