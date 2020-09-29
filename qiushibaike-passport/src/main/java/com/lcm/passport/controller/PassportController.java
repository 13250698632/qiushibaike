package com.lcm.passport.controller;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lcm.api.entity.RequestMessage;
import com.lcm.api.entity.UmsMember;
import com.lcm.api.entity.UmsMemberBind;
import com.lcm.api.login.Login;
import com.lcm.api.service.UmsMemberBindService;
import com.lcm.api.service.UmsMemberService;
import com.lcm.util.JwtUtil;
import com.lcm.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


@RestController
@CrossOrigin
public class PassportController {

    @Autowired
    private UmsMemberService umsMemberService;

    @Autowired
    private UmsMemberBindService umsMemberBindService;

    @PostMapping("/passport/code/{phone}")
    public RequestMessage code(@PathVariable String phone, HttpSession session){

        //判断是否已获取验证码
        String getCode = (String)session.getAttribute(phone);
        if(getCode!=null){
            return RequestMessage.error().message("无须重复获取验证码");
        }
        //判断手机号是否正确
        if(!ReUtil.isMatch("1[34578]\\d{9}", phone)) {
            return RequestMessage.error().message("手机格式错误");
        }
//        String code = SMSInterface.getCode(phone);
        String code="123456";
        session.setMaxInactiveInterval(300);
        session.setAttribute(phone,code);
        return RequestMessage.ok().data("code",code);
    }

    @PostMapping("/passport/login/phone")
    public RequestMessage phoneLogin(@RequestBody Login phoneLogin, HttpSession session){
        String code = (String)session.getAttribute(phoneLogin.getPhone());
        if(!phoneLogin.getCode().equals(code)){
            return RequestMessage.error().message("验证码错误");
        }
        //判断是否已注册
        QueryWrapper<UmsMember> wrapper = new QueryWrapper();
        wrapper.eq("phone",phoneLogin.getPhone());
        UmsMember getMember = umsMemberService.getOne(wrapper);

        String token = "";
        Map<String,Object> userMap = new HashMap<>();
        if(getMember!=null){
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
        UmsMember umsMember = new UmsMember();
        umsMember.setMemberLevelId("1");
        umsMember.setAvatar("https://luchaoming.oss-cn-shenzhen.aliyuncs.com/default.jpg");
        umsMember.setPhone(phoneLogin.getPhone());
        umsMember.setNickname("糗友"+ IdUtil.simpleUUID().substring(26));
        umsMember.setGender(0);
        umsMember.setStatus(1);
        umsMember.setIntegration(0);
        umsMember.setSourceType(0);
        umsMember.setIsDeleted(0);
        umsMember.setTopicImg("1");
        umsMemberService.save(umsMember);
        //返回token
        userMap.put("memberId",umsMember.getId());
        userMap.put("nickname",umsMember.getNickname());
        userMap.put("avatar",getMember.getAvatar());
        token = JwtUtil.encode("qiushibaike", userMap, "127.0.0.1");
        userMap.put("token",token);
        return RequestMessage.ok().data(userMap);
    }

    //校验token真假
    @PostMapping("/passport/verify")
    public RequestMessage verify(@RequestBody String token,String currentIp){

        //通过jwt校验token真假
        Map<String,String> map = new HashMap<>();

        Map<String, Object> decode = JwtUtil.decode(token, "qiushibaike", "127.0.0.1");
        if(decode != null) {
            if(((String) decode.get("memberId")).equals("0")){
                return RequestMessage.error().code(20002);
            }
            map.put("memberId", (String) decode.get("memberId"));
            map.put("nickname", (String) decode.get("nickname"));
            map.put("avatar",(String) decode.get("avatar"));
        }else{
            return RequestMessage.error().message("token异常");
        }

        return RequestMessage.ok().data(decode);
    }

    //第三方登录
    @PostMapping("/passport/tologin")
    public RequestMessage tologin(@RequestBody UmsMemberBind umsMemberBind){

        //判断是否已存在\
        QueryWrapper<UmsMemberBind> wrapper = new QueryWrapper<>();
        wrapper.eq("openid",umsMemberBind.getOpenid());
        UmsMemberBind memberBind = umsMemberBindService.getOne(wrapper);

        Map<String,Object> userMap = new HashMap<>();
        String token = "";
        if(memberBind!=null){
            if(memberBind.getMemberId().equals("0")){
                userMap.put("memberId", "0");
                userMap.put("nickname", memberBind.getNickname());
                userMap.put("avatar",memberBind.getAvatar());
                token = JwtUtil.encode("qiushibaike", userMap, "127.0.0.1");
                userMap.put("token",token);
                userMap.put("openid",memberBind.getOpenid());
                return RequestMessage.ok().data(userMap);
            }
            QueryWrapper<UmsMember> wrapper1 = new QueryWrapper();
            wrapper1.eq("id",memberBind.getMemberId());
            UmsMember member = umsMemberService.getOne(wrapper1);

            userMap.put("memberId", member.getId());
            userMap.put("nickname", member.getNickname());
            userMap.put("avatar",member.getAvatar());
            //jwt设置token
            token = JwtUtil.encode("qiushibaike", userMap, "127.0.0.1");
            userMap.put("token",token);
            return RequestMessage.ok().data(userMap);
        }

        //创建新用户
        umsMemberBind.setMemberId("0");
        umsMemberBind.setIsDeleted(0);
        umsMemberBindService.save(umsMemberBind);
        userMap.put("memberId", "0");
        userMap.put("nickname", umsMemberBind.getNickname());
        userMap.put("avatar",umsMemberBind.getAvatar());
        token = JwtUtil.encode("qiushibaike", userMap, "127.0.0.1");
        userMap.put("token",token);
        userMap.put("openid",umsMemberBind.getOpenid());
        return RequestMessage.ok().data(userMap);
    }

    //账号密码登录
    @PostMapping("/passport/login")
    public RequestMessage login(@RequestBody Login login){

        QueryWrapper<UmsMember> wrapper = new QueryWrapper<>();
        wrapper.eq("username",login.getUsername()).or().eq("email",login.getUsername()).or().eq("phone",login.getUsername());
        String md5 = MD5Utils.md5(login.getPassword());
        wrapper.eq("password",md5);

        UmsMember member = umsMemberService.getOne(wrapper);
        if(member==null){
            return RequestMessage.error();
        }

        Map<String,Object> userMap = new HashMap<>();
        String token = "";
        userMap.put("memberId",member.getId());
        userMap.put("nickname",member.getNickname());
        userMap.put("avatar",member.getAvatar());
        token = JwtUtil.encode("qiushibaike", userMap, "127.0.0.1");
        userMap.put("token",token);
        return RequestMessage.ok().data(userMap);
    }
}
