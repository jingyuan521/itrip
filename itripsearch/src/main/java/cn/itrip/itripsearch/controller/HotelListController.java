package cn.itrip.itripsearch.controller;


import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.vo.hotel.SearchHotCityVO;
import cn.itrip.beans.vo.hotel.SearchHotelVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.Page;
import cn.itrip.itripsearch.beans.vo.ItripHotelVO;
import cn.itrip.itripsearch.service.SearchHotelService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/hotellist")
public class HotelListController {
    @Resource
    private SearchHotelService searchHotelService;

    @RequestMapping(value = "/searchHotelPage",produces = "application/json",method = RequestMethod.POST)
    @ResponseBody
    public Dto<Page<ItripHotelVO>> serchHotelPage(@RequestBody SearchHotelVO vo){

        System.out.println("进入查询酒店方法");
        Page page = null;
        System.out.println("=========="+vo.getDestination()+"=============="+vo.getFeatureIds());
        if (EmptyUtils.isEmpty(vo) || EmptyUtils.isEmpty(vo.getDestination())){
            return DtoUtil.returnFail("目的地不能为空","20002");
        }
        try {
            page = searchHotelService.searchHotelPage(vo,vo.getPageNo(),vo.getPageSize());
        } catch (Exception e) {
            e.printStackTrace();
            return  DtoUtil.returnFail("系统异常，获取失败","20001");
        }
        return  DtoUtil.returnDataSuccess(page);


    }

    //根据热门城市查询
    @RequestMapping(value = "/searchHotelByHotcity")
    @ResponseBody
    public Dto<Page<ItripHotelVO>> searchHotelListByHotcity(@RequestParam SearchHotCityVO vo) throws Exception {
        if (EmptyUtils.isEmpty(vo)|| EmptyUtils.isEmpty(vo.getCityId())){
            return DtoUtil.returnFail("城市id不能为空","20004");
        }
        Map<String,Object> param = new HashMap<>();
        param.put("cityid",vo.getCityId());
        List list = searchHotelService.searchHotelListByHotcity(vo.getCityId(),vo.getCount());
        return  DtoUtil.returnDataSuccess(list);
    }
}
