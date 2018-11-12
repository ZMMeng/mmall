package com.mmall.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
//import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 蒙卓明 on 2018/11/12
 */
@Slf4j
//@Component与@Repository、@Service注解一样，都表示当前类为Spring的Bean
//@Repository用于Dao层，@Service用于服务层，@Component用于其它层
@Component
public class ExceptionResolver implements HandlerExceptionResolver {

    /**
     * Spring MVC处理全局异常的方法，当发生异常时，对异常进行包装后，返回给前端
     *
     * @param request 请求
     * @param response 响应
     * @param o 对象？
     * @param e 异常
     * @return 必须是ModelAndView对象，可以指定相关属性，类似于Map集合
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                                         Object o, Exception e) {
        log.error("{} Exception", request.getRequestURI(), e);

        //当使用Jackson 2时，用MappingJackson2JsonView，当前Jackson版本为1.19
        ModelAndView modelAndView = new ModelAndView(new MappingJackson2JsonView());

        //填充ModelAndView对象，返回与ServerResponse类似的JSON字符串
        modelAndView.addObject("status", ResponseCode.ERROR.getCode());
        modelAndView.addObject("msg", "接口异常，详情请查看服务端日志的异常信息");
        modelAndView.addObject("data", e.toString());

        return modelAndView;
    }
}
