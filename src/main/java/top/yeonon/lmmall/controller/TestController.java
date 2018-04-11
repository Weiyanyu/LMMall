package top.yeonon.lmmall.controller;

import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.yeonon.lmmall.common.ServerConst;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.entity.User;
import top.yeonon.lmmall.service.ISessionService;
import top.yeonon.lmmall.service.impl.SessionService;
import top.yeonon.lmmall.token.TokenGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

/**
 * @Author yeonon
 * @date 2018/4/11 0011 13:55
 **/
@RestController
@RequestMapping("test")
@Log
public class TestController {

    @Autowired
    private ISessionService sessionService;

    @Autowired
    private TokenGenerator<String> jwtTokenGenerator;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @GetMapping
    public ServerResponse testException() {
        log.info("集群1");
        int i = 0;
        int j = 5;
        int res = j / i;
        return ServerResponse.createBySuccess("ok");
    }

    @PostMapping
    public ServerResponse testLogin(HttpServletResponse response, String username, String password) throws Exception {
        ServerResponse<User> serverResponse = sessionService.login(username, password);
        if (serverResponse.isSuccess()) {
            String token = jwtTokenGenerator.generate(username);
            redisTemplate.opsForValue().set(token, serverResponse.getData(), 30, TimeUnit.MINUTES);
            response.setHeader("lmmall_login_token", token);
        }
        return serverResponse;
    }

    @DeleteMapping
    public ServerResponse logout(HttpServletRequest request) throws Exception {
        String token = request.getHeader("lmmall_login_token");
        if (StringUtils.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("请在请求头中携带Token");
        }
        if (!jwtTokenGenerator.verifyToken(token)) {
            return ServerResponse.createByErrorMessage("token过期或者错误!");
        }
        redisTemplate.delete(token);
        return ServerResponse.createBySuccessMessage("登出成功");
    }

    @GetMapping("tokenContent")
    public ServerResponse getTokenContent(HttpServletRequest request) throws Exception {
        String token = request.getHeader("lmmall_login_token");
        if (StringUtils.isEmpty(token)) {
            return ServerResponse.createByErrorMessage("请在请求头中携带token");
        }
        if (!jwtTokenGenerator.verifyToken(token)) {
            return ServerResponse.createByErrorMessage("token过期或者错误!");
        }
        User user = (User) redisTemplate.opsForValue().get(token);
        return ServerResponse.createBySuccess(user);
    }
}
