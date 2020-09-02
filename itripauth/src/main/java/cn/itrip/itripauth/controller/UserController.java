package cn.itrip.itripauth.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.userinfo.ItripUserVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.ErrorCode;
import cn.itrip.itripauth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

/**
 * 用户管理 Controller
 * 实现邮箱注册及手机注册
 */
@Controller
@RequestMapping("/api")
public class UserController {

    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userService;

    /**
     * 手机注册-获取短信
     * @param userCode 手机号
     * @return
     */
    @RequestMapping(value = "/getSMSPhone",method = RequestMethod.POST)
    @ResponseBody
    public Dto getSMSPhone(String userCode){
        System.out.println("进入手机注册方法");
        //判断手机号是否正确
        if (!validPhone(userCode))
            //手机号非法
            return DtoUtil.returnFail("请使用正确的手机号", ErrorCode.AUTH_ILLEGAL_USERCODE);
        //判断用户是否存在
        if (null == userService.findByUsername(userCode)){
            //不存在，生成验证码验证
            userService.getSMSPhone(userCode);
            return DtoUtil.returnSuccess();
        }else
            //存在
            return DtoUtil.returnFail("用户已存在",ErrorCode.AUTH_USER_ALREADY_EXISTS);
    }

    /**
     * 手机注册-注册
     */
    @RequestMapping(value = "/registerbyphone",method = RequestMethod.POST,produces = "application/json")
    @ResponseBody
    public  Dto registerbyphone(@RequestParam String userCode,
                                             @RequestParam String userPassword,
                                             @RequestParam String userName,
                                             @RequestParam String code){
        System.out.printf("===========jijijij-=============");
        try {
        //验证手机号码与短信码是否匹配
        if (userService.vailSMSPhone(userCode,code)) {
            //封装成pojo
            ItripUser itripUser=new ItripUser();
            itripUser.setUserCode(userCode);
            itripUser.setUserPassword(userPassword);
            itripUser.setUserName(userName);
            //注册类型
            itripUser.setUserType(1);
            //是否激活
            itripUser.setActivated(1);
            //用户注册
            userService.itripUserInsert(itripUser);
            return DtoUtil.returnSuccess();
        }else{
            return DtoUtil.returnFail("手机号与验证码不匹配",ErrorCode.AUTH_AUTHENTICATION_FAILED);
        }
        }catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_UNKNOWN);
        }
    }

    /**
     * 邮箱注册-获取验证码
     * @param userCode 邮箱号
     * @return
     */
    @RequestMapping(value = "getSMSMail",method = RequestMethod.POST,produces = "application/json")
    @ResponseBody
    public Dto getSMSMail(String userCode) {
        if (!validEmail(userCode))
            return DtoUtil.returnFail("请使用正确的邮箱地址注册",ErrorCode.AUTH_ILLEGAL_USERCODE);
        if (null == userService.findByUsername(userCode)){
            userService.getSMSMail(userCode);
            return DtoUtil.returnSuccess();
        }else
            return DtoUtil.returnFail("用户已存在",ErrorCode.AUTH_USER_ALREADY_EXISTS);
       /* //验证邮箱合法
        if (!validEmail(userCode)) ;
            return DtoUtil.returnFail("请使用正确的邮箱地址注册", ErrorCode.AUTH_ILLEGAL_USERCODE);
        if (null == userService.findByUsername(userCode)) {
            //不存在，生成验证码验证
            userService.getSMSMail(userCode);
            return DtoUtil.returnSuccess();
        } else
            //存在
            return DtoUtil.returnFail("用户已存在", ErrorCode.AUTH_USER_ALREADY_EXISTS);*/
    }

    /**
     * 邮箱注册
     */
    @RequestMapping(value = "/registerbymail",method = RequestMethod.POST,produces = "application/json")
    public @ResponseBody Dto registerbymail(@RequestParam String userCode,
                                            @RequestParam String userPassword,
                                            @RequestParam String userName,
                                            @RequestParam String code) {
        try {
            //验证邮箱与验证码是否匹配
            if (userService.vailSMSMail(userCode,code)) {
                //封装成pojo
                ItripUser itripUser=new ItripUser();
                itripUser.setUserCode(userCode);
                itripUser.setUserPassword(userPassword);
                itripUser.setUserName(userName);
                //注册类型
                itripUser.setUserType(0);
                //是否激活
                itripUser.setActivated(1);
                //用户注册
                userService.itripUserInsert(itripUser);
                return DtoUtil.returnSuccess();
            }else{
                return DtoUtil.returnFail("手机号与验证码不匹配",ErrorCode.AUTH_AUTHENTICATION_FAILED);
            }
        }catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_UNKNOWN);
        }
    }

    /**
     * 验证是否合法的手机号
     */
    private boolean validPhone(String phone) {
        String regex="^1[3578]{1}\\d{9}$";
        return Pattern.compile(regex).matcher(phone).find();
    }

    /**			 *
     * 合法E-mail地址：
     */
    private boolean validEmail(String email){
        String regex="^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$"  ;
        return Pattern.compile(regex).matcher(email).find();
    }

}
