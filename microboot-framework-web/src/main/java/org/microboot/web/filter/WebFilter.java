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
 * 之所以将WebFilter和ConsumerFilter逻辑分开，是站在框架层面不应该写死重定向逻辑
 * 重定向逻辑应该是作为补充逻辑，可有可无，对于前后端分离的项目，就不应该由服务端进行页面重定向工作
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