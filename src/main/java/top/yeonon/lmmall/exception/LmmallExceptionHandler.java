package top.yeonon.lmmall.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.yeonon.lmmall.common.ServerResponse;
import top.yeonon.lmmall.security.validate.ValidateCode;

import java.util.Map;

/**
 * @Author yeonon
 * @date 2018/4/11 0011 13:52
 **/
@RestControllerAdvice
public class LmmallExceptionHandler {

    @ExceptionHandler(ValidateCodeException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ServerResponse processValidateCodeException(ValidateCodeException e) {
        e.printStackTrace();
        return ServerResponse.createByErrorMessage(e.toString());
    }

    @ExceptionHandler(JWTVerificationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ServerResponse processJWTVerificationException(JWTVerificationException e) {
        e.printStackTrace();
        return ServerResponse.createByErrorMessage(e.toString());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ServerResponse processRuntimeException(RuntimeException e) {
        e.printStackTrace();
        return ServerResponse.createByErrorMessage(e.toString());
    }
}
