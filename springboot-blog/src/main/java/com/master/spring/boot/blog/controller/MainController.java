package com.master.spring.boot.blog.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.master.spring.boot.blog.domain.Authority;
import com.master.spring.boot.blog.domain.User;
import com.master.spring.boot.blog.service.AuthorityService;
import com.master.spring.boot.blog.service.UserService;
import com.master.spring.boot.blog.util.Constants;

/**
 * 主页控制器
 * 
 * @author zhu
 */
@Controller
public class MainController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthorityService authorityService;
	
	@GetMapping("/")
	public String root() {
		return "redirect:/index";
	}
	
	@GetMapping("/index")
	public String index() {
		return "redirect:/blogs";
	}

	@GetMapping("/logout")
	public String logout() {
		return "redirect:/blogs";
	}
	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/login-error")
	public String loginError(Model model) {
		model.addAttribute("loginError", true);
		model.addAttribute("errorMsg", "登陆失败，用户名或密码错误！");
		return "login";
	}
	
	@GetMapping("/register")
	public String register() {
		return "register";
	}
	@PostMapping("/register")
	public String register(User user) {
		List<Authority> authorities = new ArrayList<>();
		authorities.add(authorityService.findById(Constants.ROLE_USER_AUTHORITY_ID));
		user.setAuthorities(authorities);
		
		userService.register(user);
		return "redirect:/login";
	}
	
	
	
	@GetMapping("/search")
	public String search() {
		return "search";
	}
}
