package org.microboot.web.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.microboot.core.utils.*;
import org.microboot.web.utils.RequestUtils;
import org.springframework.http.HttpHeaders;
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

    private final String ALL = "*";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        this.setResponse(request, response);
        try {
            filterChain.doFilter(request, response);
        } catch (Throwable e) {
            LoggerUtils.error(logger, e);
            String errorMsg = ErrorUtils.getErrorMsg(e);
            /*
                MediaType.APPLICATION_JSON_UTF8_VALUE 过期，spring 的类这个常量标记为过时，
                官方说法是主流浏览器如谷歌符合正常规范，不需要设置字符编码了
                注意：这个设置不要放在setResponse，不然会导致页面都变成字符串形式展示
             */
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            try (ServletOutputStream out = response.getOutputStream()) {
                String json = ConvertUtils.object2Json(ResultUtils.error(errorMsg));
                out.write(json.getBytes(StandardCharsets.UTF_8));
                out.flush();
            }
        } finally {
            ThreadLocalUtils.remove();
        }
    }

    private void setResponse(HttpServletRequest request, HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        //用来指定允许跨域请求的URL，* 则允许任何域的请求。
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, RequestUtils.getParameter(request, "Origin"));
        /*
            表示是否允许发送Cookie，默认情况下，Cookie不包括在CORS请求之中
            需要注意的是，如果要发送Cookie，Access-Control-Allow-Origin就不能设为星号，必须指定明确的、与请求网页一致的域名。
            同时，Cookie依然遵循同源政策，只有用服务器域名设置的Cookie才会上传，其他域名的Cookie并不会上传，
            且（跨源）原网页代码中的document.cookie也无法读取服务器域名下的Cookie。
         */
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        //用来指定本次预检请求的有效期，单位为秒，在此期间不用发出另一条预检请求。
        response.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "36000");
        //用来列出浏览器的CORS请求会用到哪些HTTP方法, * 则允许全部方法, 可指定,如:POST, GET, PUT, DELETE, OPTIONS, HEAD, PATCH
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, ALL);
        /*
            CORS请求时，XMLHttpRequest对象的getResponseHeader()方法只能拿到6个基本字段：
            Cache-Control、Content-Language、Content-Type、Expires、Last-Modified、Pragma。
            如果想拿到其他字段，就必须在Access-Control-Expose-Headers里面指定
         */
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, ALL);
    }
}