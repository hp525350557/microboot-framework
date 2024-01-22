package org.microboot.web.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.microboot.core.utils.*;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author 胡鹏
 * <p>
 *
 * microboot框架构建了默认的WebFilter，针对的是前后端分离的模式，因此抛异常时默认返回的也是Json格式的数据
 * 但是也可以支持模板引擎（如：JSP，Freemarker，Thymeleaf等）的形式
 * 定义SpringMVC的模板引擎，Controller返回ModelAndView对象并指定页面的路径即可
 * 同时在web应用中自定义一个Filter类，在catch中捕捉异常，并且最后执行response.sendRedirect(errorUrl)即可
 * 注意：自定义的Filter在构建时，一定要指定Order值大于1
 * 不同情况的执行顺序如下：
 * 1、请求 → WebFilter → 自定义Filter → Controller（Json数据）→ 自定义Filter → WebFilter
 * 2、请求 → WebFilter → 自定义Filter → Controller（Json数据）→ 自定义Filter（报错，重定向到error页面）→║ WebFilter
 * 3、请求 → WebFilter → 自定义Filter → Controller（ModelAndView）→║ 自定义Filter → WebFilter
 * 4、请求 → WebFilter → 自定义Filter → Controller（ModelAndView）→║ 自定义Filter（报错，重定向到error页面）→║ WebFilter
 * 5、请求 → WebFilter → Controller（Json返回值） → WebFilter
 * 6、请求 → WebFilter → Controller（Json返回值） → WebFilter
 * 一开始在写框架时，是连着后台管理系统一起写的，那时候没有用前后端分离的模式，WebFilter抛异常时默认是重定向到一个error页面
 * 后来随着框架被其他项目使用，并使用了前后端分离的模式，才意识到不应该由服务端写死做页面重定向工作
 */
public class WebFilter extends OncePerRequestFilter {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Throwable e) {
            LoggerUtils.error(logger, ErrorUtils.getCause(e));
            String errorMsg = ErrorUtils.getErrorMsg(e);
            /*
                MediaType.APPLICATION_JSON_UTF8_VALUE 过期，spring 的类这个常量标记为过时，
                官方说法是主流浏览器如谷歌符合正常规范，不需要设置字符编码了
                注意：这个设置不要放在setResponse，不然会导致页面都变成字符串形式展示
             */
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            try (ServletOutputStream out = response.getOutputStream()) {
                String json = ConvertUtils.object2Json(ResultUtils.error(errorMsg));
                out.write(json.getBytes(StandardCharsets.UTF_8));
                out.flush();
            }
        } finally {
            ThreadLocalUtils.remove();
        }
    }
}