package com.lcm.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lcm.api.entity.UmsFriend;
import com.lcm.api.entity.UmsMember;
import com.lcm.api.service.UmsFriendService;
import com.lcm.api.service.UmsMemberService;
import com.lcm.user.mapper.UmsFriendMapper;
import com.lcm.user.mapper.UmsMemberMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-09-28
 */
@Service
public class UmsFriendServiceImpl extends ServiceImpl<UmsFriendMapper, UmsFriend> implements UmsFriendService {

}
