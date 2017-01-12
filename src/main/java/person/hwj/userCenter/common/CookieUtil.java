package person.hwj.userCenter.common;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hwj on 2016/11/19.
 */
public class CookieUtil {
    public static void saveCookie(HttpServletResponse response, String name, String value, int lifeTime)
    {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(lifeTime);
        saveCookie(response, cookie);
    }

    public static void saveCookie(HttpServletResponse response, Cookie cookie)
    {
        response.addCookie(cookie);
    }

    public static void saveCookie(HttpServletResponse response, String name, String value)
    {
        saveCookie(response, name, value, 86400);
    }

    public static Cookie getCookie(HttpServletRequest request, String cookieName)
    {
        if (request.getCookies() == null) {
            return null;
        }
        Cookie[] arrayOfCookie;
        int j = (arrayOfCookie = request.getCookies()).length;
        for (int i = 0; i < j; i++)
        {
            Cookie c = arrayOfCookie[i];
            if (c.getName().equals(cookieName)) {
                return c;
            }
        }
        return null;
    }

    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String value)
    {
        Cookie c = getCookie(request, cookieName);
        if (c != null)
        {
            c.setValue(value);
            saveCookie(response, c);
        }
    }

    public static void delCookie(HttpServletRequest request, HttpServletResponse response, String cookieName)
    {
        Cookie c = getCookie(request, cookieName);
        if (c != null)
        {
            c.setValue(null);
            c.setMaxAge(0);
            saveCookie(response, c);
        }
    }
}
