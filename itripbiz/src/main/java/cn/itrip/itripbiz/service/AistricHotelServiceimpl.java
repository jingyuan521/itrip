package cn.itrip.itripbiz.service;

import cn.itrip.beans.pojo.ItripAreaDic;
import cn.itrip.beans.pojo.ItripLabelDic;
import cn.itrip.beans.vo.ItripImageVO;
import cn.itrip.beans.vo.hotel.HotelVideoDescVO;
import cn.itrip.beans.vo.hotel.ItripSearchDetailsHotelVO;
import cn.itrip.beans.vo.hotel.ItripSearchFacilitiesHotelVO;
import cn.itrip.beans.vo.hotel.ItripSearchPolicyHotelVO;
import cn.itrip.dao.areadic.ItripAreaDicMapper;
import cn.itrip.dao.hotel.ItripHotelMapper;
import cn.itrip.dao.image.ItripImageMapper;
import cn.itrip.dao.labeldic.ItripLabelDicMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("AistrictHotelService")
public class AistricHotelServiceimpl implements AistrictHotelService{

    @Resource
    private ItripAreaDicMapper itripAreaDicMapper;
    @Resource
    private ItripImageMapper itripImageMapper;
    @Resource
    private ItripHotelMapper itripHotelMapper;
    @Resource
    private ItripLabelDicMapper itripLabelDicMapper;

    @Override
    public ItripAreaDic getTtripAreaDicById(Long id) throws Exception {
        return itripAreaDicMapper.getItripAreaDicById(id);
    }

    //查询热门商圈城市
    @Override
    public List<ItripAreaDic> getItripAreaDicListByMap(Map<String, Object> par) throws Exception {
        return itripAreaDicMapper.getItripAreaDicListByMap(par);
    }

    //查询酒店图片
    @Override
    public List<ItripImageVO> getItripImageMap(Map<String, Object> par) throws Exception {
        return itripImageMapper.getItripImageListByMap(par);
    }

    //根据酒店id查询酒店特色，商圈，酒店名称
    @Override
    public HotelVideoDescVO getVideoDescById(Long id) throws Exception {
        HotelVideoDescVO hotelVideoDescVO =  new HotelVideoDescVO();
        List<ItripAreaDic> itripAreaDics = new ArrayList<>();
        itripAreaDics = itripHotelMapper.getHotelAreaByHotelId(id);
        List<String> tempList1 = new ArrayList<>();
        for (ItripAreaDic itripAreaDic:itripAreaDics){
            tempList1.add(itripAreaDic.getName());
        }
        hotelVideoDescVO.setTradingAreaNameList(tempList1);
        List<ItripLabelDic> itripAreaDicList = new ArrayList<>();
        itripAreaDicList = itripHotelMapper.getHotelFeatureByHotelId(id);
        List<String> tempList2 = new ArrayList<>();
        for (ItripLabelDic itripLabelDic:itripAreaDicList){
            tempList2.add(itripLabelDic.getName());
        }
        hotelVideoDescVO.setHotelFeatureList(tempList2);
        hotelVideoDescVO.getHotelName(itripHotelMapper.getItripHotelById(id).getHotelName());
        return hotelVideoDescVO;
    }

    //根据id查询酒店设施
    @Override
    public ItripSearchFacilitiesHotelVO getItripHotelFacilById(Long id) throws Exception {
        return itripHotelMapper.getItripHotelFacilitiesById(id);
    }

    @Override
    public List<ItripSearchDetailsHotelVO> queryHotelDetails(Long id) throws Exception {
        List<ItripLabelDic> itripLabelDics = new ArrayList<>();
        itripLabelDics = itripHotelMapper.getHotelFeatureByHotelId(id);
        ItripSearchDetailsHotelVO vo = new ItripSearchDetailsHotelVO();
        List<ItripSearchDetailsHotelVO> list = new ArrayList<ItripSearchDetailsHotelVO>();
        vo.setName("酒店介绍");
        vo.setDescription(itripHotelMapper.getItripHotelById(id).getDetails());
        list.add(vo);
        for (ItripLabelDic itripLabelDic:itripLabelDics){
            ItripSearchDetailsHotelVO vo1 = new ItripSearchDetailsHotelVO();
            vo1.setName(itripLabelDic.getName());
            vo1.setDescription(itripLabelDic.getDescription());
        }
        return list;
    }

    //查询酒店特色列表
    @Override
    public List<ItripLabelDic> getItripLabelDicByMap(Map<String, Object> par) throws Exception {
        return itripLabelDicMapper.getItripLabelDicListByMap(par);
    }

    //查询酒店政策
    @Override
    public ItripSearchPolicyHotelVO queryHotelPolicy(Long id) throws Exception {
        return itripHotelMapper.queryHotelPolicy(id);
    }


}
