package top.yeonon.lmmall.service;

import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.User;

/**
 * @Author yeonon
 * @date 2018/4/2 0002 19:33
 **/
public interface IUserService {
    ServerResponse register(User user);
}
