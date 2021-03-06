package top.yeonon.web.interceptor;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import top.yeonon.common.ResponseCode;
import top.yeonon.common.ServerConst;
import top.yeonon.common.ServerResponse;
import top.yeonon.serivice.IUserService;
import top.yeonon.web.interceptor.authenticationAnnotation.Consumer;
import top.yeonon.security.token.TokenGenerator;
import top.yeonon.entity.User;
import top.yeonon.web.interceptor.authenticationAnnotation.Manager;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * @Author yeonon
 * @date 2018/4/11 0011 14:34
 **/
@Component
@Log
public class UserAuthenticationInterceptor implements HandlerInterceptor {

    private final static String CONSUMER = "CONSUMER";
    private final static String MANAGER = "MANAGER";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IUserService userService;

    @Autowired
    private TokenGenerator<String, DecodedJWT> jwtTokenGenerator;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 在进入URL Mapping对应的处理方法之前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            //默认是isPass,因为这个拦截器是全局拦截器，会拦截所有的URL，但是并不是所有的URL都需要验证用户或者管理员
        //所以不需要认证的默认就是通过的
        boolean isPass = true;
        //handler就是HandlerMethod，但是Spring传进来的是Object，需要转换才能调用API
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        //获取被拦截的方法（即Url Mapping对应的处理方法）
        Method method = handlerMethod.getMethod();

        //方便下面判断本次登录的是哪种类型，然后做出判断
        String permissionType = CONSUMER;
        if (method.getAnnotation(Consumer.class) != null) {
            isPass = isConsumer(request);
            permissionType = CONSUMER;
        }
        else if (method.getAnnotation(Manager.class) != null) {
            isPass = isManager(request);
            permissionType = MANAGER;
        }

        //如果isPass 是false,也就是没有通过验证，在本项目中就是没有登录如何没有相应的权限
        if (!isPass) {
            ServerResponse serverResponse = null;
            //根据PermissionType 的不同返回不同的内容
            if (permissionType.equals(CONSUMER)) {
                serverResponse = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户需要登录");
            }
            else {
                serverResponse = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "管理员需要登录");
            }
            //将内容写到response中返回给调用者
            response.setContentType("application/json");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write(objectMapper.writeValueAsString(serverResponse));
        }
        //最后如果是true，那么会继续走下面的流程（访问资源或者后面的拦截器，过滤器等）
        //如果是false,那么就会将response返回。
        return isPass;
    }

    private boolean isManager(HttpServletRequest request) {
        String token = request.getHeader(ServerConst.Token.LMMALL_LOGIN_TOKEN_NAME);
        DecodedJWT jwt = verfiy(token);
        if (jwt == null) {
            throw new JWTVerificationException("token过期或者错误");
        }
        String userId = jwt.getClaim(ServerConst.Token.TOKEN_PAYLOAD_NAME).asString();
        User user = userService.getUserInfo(Integer.valueOf(userId)).getData();
        ServerResponse checkResponse = userService.checkAdminRole(user);
        if (!checkResponse.isSuccess()) {
            return false;
        }
        return true;
    }

    private boolean isConsumer(HttpServletRequest request) {
        String token = request.getHeader(ServerConst.Token.LMMALL_LOGIN_TOKEN_NAME);
        DecodedJWT jwt = verfiy(token);
        if (jwt == null) {
            throw new JWTVerificationException("token过期或者错误");
        }
        return true;
    }


    private DecodedJWT verfiy(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        DecodedJWT jwt = null;
        try {
            jwt = jwtTokenGenerator.verifyToken(token);
        } catch (Exception e) {
            log.info("token过期或者解密失败");
            return null;
        }
        return jwt;
    }

    /**
     * 在完成URL Mapping对应的处理方法之后，视图渲染之前
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 在视图渲染之后
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
