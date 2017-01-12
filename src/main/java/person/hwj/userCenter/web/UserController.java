package person.hwj.userCenter.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import person.hwj.userCenter.common.RegisteredSystem;
import redis.clients.jedis.JedisCluster;

@Controller
public class UserController {
	@Autowired
	private JedisCluster jedisCluster;

	@RequestMapping(value = "/loginPage")
	public String login(HttpServletRequest request,String oldUrl,HttpServletResponse response
			,Model model) throws Exception {
		System.out.println("login-oldUrl=" + oldUrl);
		//传递 历史url 到页面
		model.addAttribute("oldUrl",oldUrl);
		//检查当前请求的浏览器是否登陆
		String sessionID=(String) request.getSession().getAttribute("userCenterSession");
		System.out.println("userCenter:session="+sessionID);
		if(sessionID!=null){
			//如果已经登录返回session(全局令牌)
			response.sendRedirect(oldUrl+"?globalToken="+sessionID);
			return null;
		}
		return "loginPage";
	}
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public void login(HttpServletRequest request, String userName, String password, String oldUrl,
			HttpServletResponse response) throws Exception {
		//查看数据库
		//用户中心只用来校验账号密码，提供一种登陆状态，至于每一个其他系统的权限和数据需要查询其对应的数据库
		if (userName.equals("admin") && password.equals("admin")) {
			String sessionID = UUID.randomUUID().toString();// 生成登录用户唯一标识
			//根据查询其他数据库需求，把相应的 公用的用户信息  转变为json字符串保存到redis
			//为了增加安全性，可以对sessionID进行加密
			//没有redis可以考虑存在内存中，如果放在内存中需要增加一个获取信息过程，如果直接在sendRedirect后面跟用户信息，会增加数据泄露风险
			//所以采用httpClient方式  校验令牌或者叫获取用户信息   可以增加安全性
			jedisCluster.setex(sessionID, 60 * 30, "userName=" + userName + "password=" + password);
			//存储全局令牌，用于判断浏览器是否登陆
			request.getSession().setAttribute("userCenterSession", sessionID);
			response.sendRedirect(oldUrl+"?globalToken="+sessionID);
		}else {
			response.sendRedirect("loginPage");
		}
	}

	@RequestMapping(value = "/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		String globalId = (String) request.getSession().getAttribute("userCenterSession");
		System.out.println("userCenterSession:"+globalId);
		List<String> sysIds=RegisteredSystem.registereSysInfo.get(globalId);
		for(String sysId:sysIds){
			jedisCluster.del(sysId);
		}
		jedisCluster.del(globalId);
		request.getSession().removeAttribute("userCenterSession");
		return "loginPage";
	}

	@RequestMapping(value = "/registered")
	public void registered(HttpServletRequest request, HttpServletResponse response) {
		/**
		 * 如果令牌存储在  用户中心 的内存中，本方法用于校验令牌是否有效、获取用户公共信息、注册子系统
		 * 如果令牌存储在 redis，本方法用于注册子系统
		 */
		String sysId = request.getParameter("sysId");
		String globalId = request.getParameter("globalId");
		List<String> sysIds=RegisteredSystem.registereSysInfo.get(globalId);
		if(sysIds==null){
			sysIds=new ArrayList<String>();
		}
		sysIds.add(sysId);
		RegisteredSystem.registereSysInfo.put(globalId, sysIds);
		try {
			response.getWriter().write("success");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/isLogin")
	public void isLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String sessionId = (String) request.getSession().getAttribute("userCenterSession");
		System.out.println("userCenterSession=" + sessionId);
		if (sessionId != null) {
			response.getWriter().write(sessionId);
		}
	}
}
