package com.mmall.controller.common.interceptor;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisSharededPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;

/**
 * 拦截器
 * Created by 蒙卓明 on 2018/11/12
 */
@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {

    /**
     * 进入Controller之前进行拦截
     *
     * @param request  请求
     * @param response 响应
     * @param handler  handlerMethod对象
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
            Exception {
        log.info("preHandle");
        //强转
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        //获取请求的Controller类名
        String className = handlerMethod.getBean().getClass().getSimpleName();
        //获取请求的方法名
        String methodName = handlerMethod.getMethod().getName();

        //获取请求参数
        StringBuffer requestParameterBuffer = new StringBuffer();
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String value = Arrays.toString(entry.getValue());
            requestParameterBuffer.append(key).append("=").append(value).append(",");
        }

        log.info("拦截器拦截：className: {}, methodName: {}, requestParameters: {}", className, methodName,
                requestParameterBuffer.length() == 0 ? StringUtils.EMPTY :
                        requestParameterBuffer.substring(0, requestParameterBuffer.length() - 1));

        User user = null;
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isNotEmpty(loginToken)) {
            //根据登陆令牌属性值，从Redis缓存中获取User对象的JSON字符串
            String userJsonStr = RedisSharededPoolUtil.get(loginToken);
            //反序列化为User对象
            user = JsonUtil.string2Obj(userJsonStr, User.class);
        }

        if (user != null && user.getRole() == Const.Role.ROLE_ADMIN) {
            //是管理员登陆，则直接返回true
            return true;
        }

        //不是管理员登陆
        //重置response，否则会报getWriter() has already been called for this response的异常
        response.reset();
        //此时已不走Spring MVC的流程，类似于写Servlet的方式
        //设置编码
        response.setCharacterEncoding("UTF-8");
        //设置内容类型
        response.setContentType("application/json;charset=UTF-8");

        PrintWriter out = response.getWriter();
        if (user == null) {
            log.info("拦截器拦截，用户未登录");
            if (StringUtils.equals(className, "ProductManageController") && StringUtils.equals(methodName,
                    "uploadRichTextImg")) {
                //富文本上传的返回值类型不同，特殊处理
                Map<String, Object> resultMap = Maps.newHashMap();
                resultMap.put("success", false);
                resultMap.put("msg", "请先登录");
                out.print(JsonUtil.obj2String(resultMap));
            } else {
                out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("用户未登录")));
            }
        } else {
            log.info("拦截器拦截，用户不具有管理员权限");
            //
            if (StringUtils.equals(className, "ProductManageController") && StringUtils.equals(methodName,
                    "uploadRichTextImg")) {
                //富文本上传的返回值类型不同，特殊处理
                Map<String, Object> resultMap = Maps.newHashMap();
                resultMap.put("success", false);
                resultMap.put("msg", "当前用户无权限操作，需要管理员权限");
                out.print(JsonUtil.obj2String(resultMap));
            } else {
                out.print(JsonUtil.obj2String(
                        ServerResponse.createByErrorMessage("当前用户无权限操作，需要管理员权限")));
            }
        }

        //先清空流对象，再关闭流
        out.flush();
        out.close();

        return false;
    }

    /**
     * Controller执行完之后执行
     *
     * @param request 请求
     * @param response 响应
     * @param handler handlerMethod对象
     * @param modelAndView modelAndView对象
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

        log.info("postHandle");
    }

    /**
     * 响应之后执行
     *
     * @param request 请求
     * @param response 响应
     * @param handler handlerMethod对象
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {

        log.info("afterCompletion");
    }
}
