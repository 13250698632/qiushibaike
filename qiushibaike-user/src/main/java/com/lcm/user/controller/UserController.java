package com.lcm.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lcm.api.entity.*;
import com.lcm.api.login.Login;
import com.lcm.api.request.Follow;
import com.lcm.api.request.MemberList;
import com.lcm.api.service.UmsFollowService;
import com.lcm.api.service.UmsFriendService;
import com.lcm.api.service.UmsMemberBindService;
import com.lcm.api.service.UmsMemberService;
import com.lcm.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.cert.ocsp.Req;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;

@RestController
@CrossOrigin
public class UserController {

    @Autowired
    private UmsMemberService umsMemberService;

    @Autowired
    private UmsMemberBindService umsMemberBindService;

    @Autowired
    private UmsFollowService umsFollowService;

    @Autowired
    private UmsFriendService umsFriendService;

    //根据ID查询用户
    @GetMapping("/user/member/{id}")
    public RequestMessage memberById(@PathVariable String id,String memberId){

        if(id==null){
            return RequestMessage.error().message("用户ID异常");
        }

        QueryWrapper<UmsMember> wrapper = new QueryWrapper();
        wrapper.eq("id",id);
        UmsMember member = umsMemberService.getOne(wrapper);
        if(member==null){
            return RequestMessage.error().message("用户不存在");
        }
        member.setPassword("true");

        //关注数
        QueryWrapper<UmsFollow> followQueryWrapper = new QueryWrapper<>();
        followQueryWrapper.eq("member_id",member.getId());
        List<UmsFollow> followList = umsFollowService.list(followQueryWrapper);

        //粉丝数
        QueryWrapper<UmsFollow> fans = new QueryWrapper<>();
        fans.eq("follow_id",member.getId());
        List<UmsFollow> fansList = umsFollowService.list(fans);
        if(StringUtils.isNotEmpty(memberId)){
            //当前查看的用户是否关注该用户
            QueryWrapper<UmsFollow> guanzhu = new QueryWrapper<>();
            guanzhu.eq("member_id",memberId);
            guanzhu.eq("follow_id",member.getId());
            UmsFollow one = umsFollowService.getOne(guanzhu);
            if(one!=null){
                member.setIsguanzhu(true);
            }else{
                member.setIsguanzhu(false);
            }
        }

        return RequestMessage.ok().data("member",member).data("followCount",followList.size()).data("fansCount",fansList.size());
    }

    //绑定手机
    @PostMapping("/user/bind/phone")
    public RequestMessage bindPhone(@RequestBody Login phoneLogin, HttpSession session){
        String code = (String)session.getAttribute(phoneLogin.getPhone());
        if(!phoneLogin.getCode().equals("123456")){
            return RequestMessage.error().message("验证码错误");
        }
        //判断手机是否已注册
        QueryWrapper<UmsMember> wrapper = new QueryWrapper();
        wrapper.eq("phone",phoneLogin.getPhone());
        UmsMember getMember = umsMemberService.getOne(wrapper);

        //查询第三方登录表
        QueryWrapper<UmsMemberBind> queryWrapper = new QueryWrapper();
        queryWrapper.eq("openid",phoneLogin.getOpenid());

        String token = "";
        Map<String,Object> userMap = new HashMap<>();
        if(getMember!=null){
            //查询第三方登录表
            UmsMemberBind memberBind = umsMemberBindService.getOne(queryWrapper);

            memberBind.setMemberId(getMember.getId());
            umsMemberBindService.updateById(memberBind);

            //用jwt制作token
            userMap.put("memberId",getMember.getId());
            userMap.put("nickname",getMember.getNickname());
            userMap.put("avatar",getMember.getAvatar());
            //按照设计的算法对参数进行加密后，生成token
            token = JwtUtil.encode("qiushibaike", userMap, "127.0.0.1");
            //将token存入redis一份

            userMap.put("token",token);
            return RequestMessage.ok().data(userMap);
        }
        //数据库没有，注册

        //查询第三方登录表
        UmsMemberBind memberBind = umsMemberBindService.getOne(queryWrapper);

        UmsMember umsMember = new UmsMember();
        umsMember.setMemberLevelId("1");
        umsMember.setAvatar(memberBind.getAvatar());
        umsMember.setPhone(phoneLogin.getPhone());
        umsMember.setNickname(memberBind.getNickname());
        umsMember.setGender(0);
        umsMember.setStatus(1);
        umsMember.setIntegration(0);
        umsMember.setSourceType(0);
        umsMember.setIsDeleted(0);
        umsMember.setTopicImg("1");
        umsMemberService.save(umsMember);

        memberBind.setMemberId(umsMember.getId());
        umsMemberBindService.updateById(memberBind);

        //返回token
        userMap.put("memberId",umsMember.getId());
        userMap.put("nickname",umsMember.getNickname());
        userMap.put("avatar",umsMember.getAvatar());
        token = JwtUtil.encode("qiushibaike", userMap, "127.0.0.1");
        userMap.put("token",token);
        return RequestMessage.ok().data(userMap);
    }

    //关注
    @PostMapping("/user/follow")
    public RequestMessage follow(@RequestBody Follow follow){

        if(follow.getMemberId() == null || follow.getFollowId() == null){
            return RequestMessage.error();
        }

        //判断是否关注过
        QueryWrapper<UmsFollow> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id",follow.getMemberId());
        wrapper.eq("follow_id",follow.getFollowId());
        UmsFollow followOne = umsFollowService.getOne(wrapper);
        if(followOne != null){
            return RequestMessage.error().message("已关注");
        }

        UmsFollow umsFollow = new UmsFollow();
        umsFollow.setFollowId(follow.getFollowId());
        umsFollow.setMemberId(follow.getMemberId());
        umsFollow.setIsDeleted(0);

        boolean save = umsFollowService.save(umsFollow);
        if(save){
            return RequestMessage.ok();
        }else {
            return RequestMessage.error();
        }
    }

    //取消关注
    @PostMapping("/user/off/follow")
    public RequestMessage offFollow(@RequestBody Follow follow){

        if(follow.getMemberId().equals("") || follow.getFollowId().equals("") || follow.getFollowId()==null || follow.getMemberId() ==null){
            return RequestMessage.error();
        }

        //取消关注
        QueryWrapper<UmsFollow> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id",follow.getMemberId());
        wrapper.eq("follow_id",follow.getFollowId());
        umsFollowService.remove(wrapper);

        return RequestMessage.ok();
    }

    //判断是否关注
    @GetMapping("/user/isguanzhu/{memberId}/{followId}")
    public Boolean isGuanzhu(@PathVariable String memberId,@PathVariable String followId){
        //判断是否关注过
        QueryWrapper<UmsFollow> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id",memberId);
        wrapper.eq("follow_id",followId);
        UmsFollow followOne = umsFollowService.getOne(wrapper);
        if(followOne != null){
            return true;
        }
        return false;
    }

    //获取用户关注列表,给发布调用
    @GetMapping("/user/get/follow/{memberId}")
    public RequestMessage getFollow(@PathVariable String memberId){
        QueryWrapper<UmsFollow> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id",memberId);
        List<UmsFollow> list = umsFollowService.list(wrapper);
        return RequestMessage.ok().data("list",list);
    }

    //获取用户关注,粉丝列表,type 0:我的关注 1:我的粉丝
    @GetMapping("/user/get/followFans/{memberId}/{current}/{limit}/{type}")
    public RequestMessage getFans(@PathVariable String memberId,@PathVariable Integer current,@PathVariable Integer limit,@PathVariable Integer type){
        QueryWrapper<UmsFollow> wrapper = new QueryWrapper<>();
        //我的关注
        if (type==0){
            wrapper.eq("member_id",memberId);
        }else {
            //我的粉丝
            wrapper.eq("follow_id",memberId);
        }
        List<UmsFollow> list = umsFollowService.list(wrapper);
        List<String> id = new ArrayList<>();
        for (UmsFollow umsFollow : list) {
            //我的关注
            if(type==0){
                id.add(umsFollow.getFollowId());
            }else{
                //我的粉丝
                id.add(umsFollow.getMemberId());
            }
        }

        Page<UmsMember> member = new Page<>(current, limit);
        QueryWrapper<UmsMember> memberQueryWrapper = new QueryWrapper<>();
        memberQueryWrapper.in("id",id);
        umsMemberService.page(member,memberQueryWrapper);

        long total = member.getTotal();//总条数
        List<UmsMember> records = member.getRecords();

        return RequestMessage.ok().data("total",total).data("list",records);
    }


    //修改用户信息
    @PostMapping("/user/update")
    public RequestMessage updateUser(@RequestBody UmsMember umsMember){

        UmsMember member = new UmsMember();
        member.setId(umsMember.getId());
        member.setNickname(umsMember.getNickname());
        member.setGender(umsMember.getGender());
        member.setAvatar(umsMember.getAvatar());
        if(umsMember.getBirthday()!=null){
            member.setBirthday(umsMember.getBirthday());
        }
        if(umsMember.getJob()!=null){
            member.setJob(umsMember.getJob());
        }
        if (umsMember.getAddress()!=null){
            member.setAddress(umsMember.getAddress());
        }
        if(umsMember.getEmotion()!=null){
            member.setEmotion(umsMember.getEmotion());
        }

        boolean update = umsMemberService.updateById(member);

        if(update){
            Map<String,Object> userMap = new HashMap<>();
            userMap.put("memberId",member.getId());
            userMap.put("nickname",member.getNickname());
            userMap.put("avatar",member.getAvatar());
            userMap.put("token",JwtUtil.encode("qiushibaike", userMap, "127.0.0.1"));
            return RequestMessage.ok().data(userMap);
        }else{
            return RequestMessage.error();
        }

    }

    //搜索用户
    @GetMapping("/user/search/{member}/{current}/{limit}")
    public RequestMessage searchMember(@PathVariable String member,@PathVariable Integer current,@PathVariable Integer limit){
        if(StringUtils.isEmpty(member)){
            return RequestMessage.error().message("搜索内容为空");
        }
        Page<UmsMember> page = new Page<>(current, limit);
        QueryWrapper<UmsMember> wrapper = new QueryWrapper<>();
        wrapper.eq("id",member).or().like("nickname",member);
        umsMemberService.page(page,wrapper);

        long total = page.getTotal();
        List<UmsMember> records = page.getRecords();

        return RequestMessage.ok().data("total",total).data("list",records);
    }

    //查询多个用户
    @PostMapping("/user/member/list")
    public RequestMessage memberList(@RequestBody MemberList memberList){

        if(memberList.getList().size()<1){
            return RequestMessage.error().message("用户组为空");
        }

        Collection<UmsMember> umsMembers = umsMemberService.listByIds(memberList.getList());

        return RequestMessage.ok().data("list",umsMembers);
    }

    //发送添加好友请求
    @GetMapping("/user/add/friend/{memberId}/{friendId}")
    public RequestMessage addFriend(@PathVariable String memberId,@PathVariable String friendId){

        if(StringUtils.isEmpty(memberId) || StringUtils.isEmpty(friendId)){
            return RequestMessage.error().message("ID为空");
        }

        //查询是否存在
        QueryWrapper<UmsFriend> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id",memberId);
        wrapper.eq("friend_id",friendId);
        UmsFriend friend = umsFriendService.getOne(wrapper);
        if(friend!=null){
            return RequestMessage.error().message("已经添加过");
        }
        UmsFriend umsFriend = new UmsFriend();
        umsFriend.setFriendId(friendId);
        umsFriend.setMemberId(memberId);
        umsFriend.setType(0);
        umsFriend.setIsDeleted(0);
        //添加我的好友
        boolean save = umsFriendService.save(umsFriend);
        if(save)
            return RequestMessage.ok();
        else
            return RequestMessage.error().message("保存错误");
    }

    //查询请求加我的用户
    @GetMapping("/user/get/friend/add/{friendId}/{current}/{limit}")
    public RequestMessage getMyFriendAdd(@PathVariable String friendId,@PathVariable Integer current,@PathVariable Integer limit){
        if(StringUtils.isEmpty(friendId)){
            return RequestMessage.error().message("用户为空");
        }
        Page<UmsFriend> page = new Page<>(current, limit);
        QueryWrapper<UmsFriend> wrapper = new QueryWrapper<>();
        wrapper.eq("friend_id",friendId);
        wrapper.orderByDesc("gmt_create");

        umsFriendService.page(page,wrapper);

        long total = page.getTotal();
        List<UmsFriend> records = page.getRecords();
        for (UmsFriend record : records) {
            QueryWrapper<UmsMember> wrapperMember = new QueryWrapper<>();
            wrapperMember.eq("id",record.getMemberId());
            record.setUmsMember(umsMemberService.getOne(wrapperMember));
        }
        return RequestMessage.ok().data("total",total).data("list",records);
    }

    //查询我的好友
    @GetMapping("/user/get/friend/{memberId}/{current}/{limit}")
    public RequestMessage getMyFriend(@PathVariable String memberId,@PathVariable Integer current,@PathVariable Integer limit){
        if(StringUtils.isEmpty(memberId)){
            return RequestMessage.error().message("用户为空");
        }
        Page<UmsFriend> page = new Page<>(current, limit);
        QueryWrapper<UmsFriend> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id",memberId);
        wrapper.orderByDesc("gmt_create");

        umsFriendService.page(page,wrapper);

        long total = page.getTotal();
        List<UmsFriend> records = page.getRecords();
        for (UmsFriend record : records) {
            QueryWrapper<UmsMember> wrapperMember = new QueryWrapper<>();
            wrapperMember.eq("id",record.getFriendId());
            record.setUmsMember(umsMemberService.getOne(wrapperMember));
        }
        return RequestMessage.ok().data("total",total).data("list",records);
    }

    //同意或拒绝添加好友
    @PostMapping("/user/friend/operating")
    public RequestMessage operatingFriend(@RequestBody UmsFriend umsFriend){

        boolean update = umsFriendService.updateById(umsFriend);
        if(umsFriend.getType() == 1){
            UmsFriend umsFriend1 = new UmsFriend();
            umsFriend1.setIsDeleted(0);
            umsFriend1.setType(1);
            umsFriend1.setMemberId(umsFriend.getFriendId());
            umsFriend1.setFriendId(umsFriend.getMemberId());
            umsFriendService.save(umsFriend1);
        }

        if(update)
            return RequestMessage.ok();
        else
            return RequestMessage.error().message("好友操作错误");
    }

}
