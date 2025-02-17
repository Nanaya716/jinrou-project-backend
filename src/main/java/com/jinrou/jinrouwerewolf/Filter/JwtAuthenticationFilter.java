//package com.jinrou.jinrouwerewolf.Filter;
//
///**
// * @Author: nanaya
// * @Date: 2024/12/21/2:10
// * @Email: qiyewuyin@gmail.com\714991699@qq.com
// * @QQ: 714991699
// * @Description:
// */
//import com.jinrou.jinrouwerewolf.util.JwtUtil;
//import io.jsonwebtoken.Claims;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import java.io.IOException;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtUtil jwtUtil;  // 处理 Token 的服务
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String header = request.getHeader("Authorization");
//
//        // 如果没有 "Authorization" 头或它不是以 "Bearer " 开头，则跳过 Token 验证
//        if (header == null || !header.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);  // 继续执行其他过滤器
//            return;
//        }
//
//        String token = header.substring(7);  // 去掉 "Bearer " 前缀
//        Claims claims = jwtUtil.getTokenBody(token);  // 解析 JWT Token
//
//        if (claims != null) {
//            String userId = claims.getSubject();  // 从 Token 中提取用户名
//
//            // 将用户名存储到 HttpServletRequest 中，供后续请求使用
//            request.setAttribute("userId", userId);
//        }
//
//        // 继续执行后续的过滤器或请求处理
//        filterChain.doFilter(request, response);
//    }
//}
