package com.lcm.api.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author testjava
 * @since 2020-09-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="PpsRelease对象", description="")
public class PpsRelease implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "用户id")
    private String memberId;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "封面")
    private String titleImg;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "类型:0：图文,1：视频")
    private Integer type;

    @ApiModelProperty(value = "地址")
    private String city;

    @ApiModelProperty(value = "阿里云视频ID")
    private String videoId;

    @ApiModelProperty(value = "播放次数")
    private Integer playCount;

    @ApiModelProperty(value = "视频时长")
    private String duration;

    @ApiModelProperty(value = "分享次数")
    private Integer shareCount;

    @ApiModelProperty(value = "评论次数")
    private Integer commentCount;

    @ApiModelProperty(value = "点赞次数")
    private Integer likeCount;

    @ApiModelProperty(value = "是否开启评论功能:0禁用 1:启动")
    private Integer isComment;

    @ApiModelProperty(value = "逻辑删除 1（true）已删除， 0（false）未删除")
    @TableLogic
    private Integer isDeleted;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;


}
