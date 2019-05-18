package org.apollo.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("test")
//代替get、set
@Data
@Builder
//全参构造器
@AllArgsConstructor
//无参构造器
@NoArgsConstructor
public class Test {

    @TableId
    private Long id;

    private String name;

    private Integer uAge;

}
