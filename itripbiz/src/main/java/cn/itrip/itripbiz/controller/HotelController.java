package cn.itrip.itripbiz.controller;


import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripAreaDic;
import cn.itrip.beans.pojo.ItripLabelDic;
import cn.itrip.beans.vo.ItripAreaDicVO;
import cn.itrip.beans.vo.ItripImageVO;
import cn.itrip.beans.vo.ItripLabelDicVO;
import cn.itrip.beans.vo.hotel.HotelVideoDescVO;
import cn.itrip.beans.vo.hotel.ItripSearchDetailsHotelVO;
import cn.itrip.beans.vo.hotel.ItripSearchFacilitiesHotelVO;
import cn.itrip.beans.vo.hotel.ItripSearchPolicyHotelVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.EmptyUtils;
import cn.itrip.itripbiz.service.AistrictHotelService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/api/hotel")
public class HotelController {
    @Resource
    private AistrictHotelService aistrictHotelService;
    @RequestMapping(value = "/selectAddress")
    @ResponseBody
    public Dto<ItripAreaDicVO> queryHptCity(@RequestParam(required = true) Long cityId) throws Exception {
        System.out.println("进入查询热门城市方法");
        List<ItripAreaDicVO> itripAreaDicVOS = null;
        List<ItripAreaDic> itripAreaDics = null;
        if (EmptyUtils.isNotEmpty(cityId)){
            Map param = new HashMap();
            param.put("isTradingArea",1);
            param.put("parent",cityId);
            itripAreaDics = aistrictHotelService.getItripAreaDicListByMap(param);
            System.out.println("============"+itripAreaDics);
            if (EmptyUtils.isNotEmpty(itripAreaDics)){
                itripAreaDicVOS = new ArrayList<>();
                for (ItripAreaDic dic : itripAreaDics){
                    ItripAreaDicVO vo = new ItripAreaDicVO();
                    BeanUtils.copyProperties(dic,vo);
                    itripAreaDicVOS.add(vo);
                }
            }
        }else{
            DtoUtil.returnFail("城市id不能为空","10203");
        }
        return DtoUtil.returnDataSuccess(itripAreaDicVOS);
    }

    @RequestMapping(value = "/selectHotelImg")
    @ResponseBody
    public Dto<Object> getImgByTarget(@RequestParam(required = true) String targetId) {
        Dto<Object> dto = new Dto<>();
        if (targetId != null && targetId.equals("")) {
            List<ItripImageVO> itripAreaDicVOS = null;
            Map<String, Object> param = new HashMap<>();
            param.put("type", "0");
            param.put("targetId", targetId);
            try {
                itripAreaDicVOS = aistrictHotelService.getItripImageMap(param);
                dto = DtoUtil.returnSuccess("酒店图片获取成功", itripAreaDicVOS);
            } catch (Exception e) {
                e.printStackTrace();
                dto = DtoUtil.returnFail("酒店信息获取失败", "100212");
            }

        } else {
            dto = DtoUtil.returnFail("酒店id不能为空", "100213");
        }
        return dto;
    }


    //根据id查询酒店信息
    @RequestMapping(value = "/selectHtelInfo")
    @ResponseBody
    public Dto<Object> getVideoDescById(@RequestParam(required = true) String hotel){
        Dto<Object> dto = new Dto<>();
        if (hotel != null&&hotel.equals("")){
            HotelVideoDescVO hotelVideoDescVO = null;
            try {
                hotelVideoDescVO = aistrictHotelService.getVideoDescById(Long.valueOf(hotel));
                dto = DtoUtil.returnSuccess("获取酒店信息成功",hotelVideoDescVO);
            } catch (Exception e) {
                dto = DtoUtil.returnFail("获取酒店信息失败","100214");
                e.printStackTrace();
            }
        }else {
            dto = DtoUtil.returnFail("酒店id不能为空","100215");
        }
        return dto;
    }

    //根据id查询酒店设施
    @RequestMapping(value = "/queryHotelFac")
    @ResponseBody
    public Dto<ItripSearchFacilitiesHotelVO> queryHotelFac(@RequestParam(required = true) Long id){
        ItripSearchFacilitiesHotelVO itripSearchFacilitiesHotelVO =null;
        if (EmptyUtils.isNotEmpty(id)){
            try {
                itripSearchFacilitiesHotelVO = aistrictHotelService.getItripHotelFacilById(id);
                return DtoUtil.returnDataSuccess(itripSearchFacilitiesHotelVO.getFacilities());
            } catch (Exception e) {
                e.printStackTrace();
                return DtoUtil.returnFail("系统异常","10207");
            }
        }else {
            return DtoUtil.returnFail("酒店id不能为空","10206");
        }
    }

    //根据id查询酒店特色
    @RequestMapping(value = "/selectHotelDetails")
    @ResponseBody
    public Dto<ItripSearchFacilitiesHotelVO> queryHotelDetails(@RequestParam(required = true) Long id) {
        List<ItripSearchDetailsHotelVO> itripSearchDetailsHotelVOS = null;
        if (EmptyUtils.isNotEmpty(id)){
            try {
                itripSearchDetailsHotelVOS = aistrictHotelService.queryHotelDetails(id);
                return DtoUtil.returnDataSuccess(itripSearchDetailsHotelVOS);
            } catch (Exception e) {
                e.printStackTrace();
                return DtoUtil.returnFail("系统错误","10211");
            }

        }else{
            return DtoUtil.returnFail("酒店id不能为空","10210");
        }
    }

    //查询酒店特色列表
    @RequestMapping(value = "/selectHotelFeat")
    @ResponseBody
    public Dto<ItripLabelDicVO> queryHotelFeat(){
        List<ItripLabelDic> itripLabelDics = null;
        List<ItripLabelDicVO> itripLabelDicVOS = null;
        Map param = new HashMap();
        try {
            itripLabelDics = aistrictHotelService.getItripLabelDicByMap(param);
            if (EmptyUtils.isNotEmpty(itripLabelDics)){
                itripLabelDicVOS = new ArrayList<>();
                for (ItripLabelDic dic:itripLabelDics){
                    ItripLabelDicVO vo = new ItripLabelDicVO();
                    BeanUtils.copyProperties(dic,vo);
                    itripLabelDicVOS.add(vo);
                }
            }
        } catch (Exception e) {
            DtoUtil.returnFail("系统异常","10205");
            e.printStackTrace();
        }
        return DtoUtil.returnDataSuccess(itripLabelDicVOS);
    }

    //根据酒店id查询酒店政策
    @RequestMapping(value = "/selectHotelPolicy")
    @ResponseBody
    public Dto<ItripSearchFacilitiesHotelVO> queryHotelPolicy(@RequestParam(required = true) Long id) {
        ItripSearchPolicyHotelVO itripSearchPolicyHotelVO = null;
        if (EmptyUtils.isNotEmpty(id)){
            try {
                itripSearchPolicyHotelVO = aistrictHotelService.queryHotelPolicy(id);
                return DtoUtil.returnDataSuccess(itripSearchPolicyHotelVO.getHotelPolicy());
            } catch (Exception e) {
                e.printStackTrace();
                return DtoUtil.returnFail("系统异常","10209");
            }
        }else{
            return DtoUtil.returnFail("酒店id不能为空","10208");
        }
    }



}
