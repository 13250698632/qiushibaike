package com.lcm.api.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

import java.io.Serializable;
import java.util.List;

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
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="PpsComment对象", description="")
public class PpsComment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    @ApiModelProperty(value = "用户ID")
    private String memberId;

    @ApiModelProperty(value = "发布ID")
    private String releaseId;

    @ApiModelProperty(value = "回复评论父级ID")
    private String replyCommentFuUserId;

    @ApiModelProperty(value = "回复ID")
    private String replyCommentId;

    @ApiModelProperty(value = "回复用户ID")
    private String replyCommentUserId;

    @ApiModelProperty(value = "评论内容")
    private String text;

    @ApiModelProperty(value = "一级评论，二级评论")
    private Integer commentLevel;

    @ApiModelProperty(value = "是否置顶")
    private Integer topStatus;

    @ApiModelProperty(value = "点赞数")
    private Integer likeCount;

    @ApiModelProperty(value = "逻辑删除 1（true）已删除， 0（false）未删除")
    @TableLogic
    private Integer isDeleted;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    @TableField(exist = false)
    private UmsMember umsMember;

    @TableField(exist = false)
    private UmsMember umsMember2;

    @TableField(exist = false)
    private List<PpsComment> twoCommentList;

    public UmsMember getUmsMember2() {
        return umsMember2;
    }

    public void setUmsMember2(UmsMember umsMember2) {
        this.umsMember2 = umsMember2;
    }

    public String getReplyCommentId() {
        return replyCommentId;
    }

    public void setReplyCommentId(String replyCommentId) {
        this.replyCommentId = replyCommentId;
    }

    public String getReplyCommentFuUserId() {
        return replyCommentFuUserId;
    }

    public void setReplyCommentFuUserId(String replyCommentFuUserId) {
        this.replyCommentFuUserId = replyCommentFuUserId;
    }

    public List<PpsComment> getTwoCommentList() {
        return twoCommentList;
    }

    public void setTwoCommentList(List<PpsComment> twoCommentList) {
        this.twoCommentList = twoCommentList;
    }

    public UmsMember getUmsMember() {
        return umsMember;
    }

    public void setUmsMember(UmsMember umsMember) {
        this.umsMember = umsMember;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getReplyCommentUserId() {
        return replyCommentUserId;
    }

    public void setReplyCommentUserId(String replyCommentUserId) {
        this.replyCommentUserId = replyCommentUserId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getCommentLevel() {
        return commentLevel;
    }

    public void setCommentLevel(Integer commentLevel) {
        this.commentLevel = commentLevel;
    }

    public Integer getTopStatus() {
        return topStatus;
    }

    public void setTopStatus(Integer topStatus) {
        this.topStatus = topStatus;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }
}
