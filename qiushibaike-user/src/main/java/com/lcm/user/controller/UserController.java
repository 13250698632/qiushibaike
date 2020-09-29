package com.lcm.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lcm.api.entity.RequestMessage;
import com.lcm.api.entity.UmsMember;
import com.lcm.api.entity.UmsMemberBind;
import com.lcm.api.login.Login;
import com.lcm.api.service.UmsMemberBindService;
import com.lcm.api.service.UmsMemberService;
import com.lcm.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class UserController {

    @Autowired
    private UmsMemberService umsMemberService;

    @Autowired
    private UmsMemberBindService umsMemberBindService;

    //根据ID查询用户
    @GetMapping("/user/member/{id}")
    public RequestMessage memberById(@PathVariable String id){

        if(id==null){
            return RequestMessage.error().message("用户ID异常");
        }

        QueryWrapper<UmsMember> wrapper = new QueryWrapper();
        wrapper.eq("id",id);
        UmsMember member = umsMemberService.getOne(wrapper);
        member.setPassword("true");
        return RequestMessage.ok().data("member",member);
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

}
