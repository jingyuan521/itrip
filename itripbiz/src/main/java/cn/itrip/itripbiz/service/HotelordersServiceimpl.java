package cn.itrip.itripbiz.service;

import cn.itrip.beans.pojo.*;
import cn.itrip.beans.vo.order.ItripPersonalOrderRoomVO;
import cn.itrip.beans.vo.store.StoreVO;
import cn.itrip.common.BigDecimalUtil;
import cn.itrip.common.Constants;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.Page;
import cn.itrip.dao.hotel.ItripHotelMapper;
import cn.itrip.dao.hotelorder.ItripHotelOrderMapper;
import cn.itrip.dao.hotelroom.ItripHotelRoomMapper;
import cn.itrip.dao.hoteltempstore.ItripHotelTempStoreMapper;
import cn.itrip.dao.orderlinkuser.ItripOrderLinkUserMapper;
import cn.itrip.dao.tradeends.ItripTradeEndsMapper;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.math.BigDecimal.ROUND_DOWN;

public class HotelordersServiceimpl implements HotelordersService{
    @Resource
    private ItripHotelTempStoreMapper itripHotelTempStoreMapper;
    @Resource
    private ItripHotelOrderMapper itripHotelOrderMapper;
    @Resource
    private ItripHotelRoomMapper itripHotelRoomMapper;
    @Resource
    private ItripTradeEndsMapper itripTradeEndsMapper;
    @Resource
    private ItripHotelMapper itripHotelMapper;
    @Resource
    private ItripOrderLinkUserMapper itripOrderLinkUserMapper;

    @Override
    public boolean validateRoomStore(Map<String, Object> param) throws Exception {
        Integer count = (Integer) param.get("count");
        itripHotelTempStoreMapper.flushStore(param);
        List<StoreVO> storeVOList = itripHotelTempStoreMapper.queryRoomStore(param);
        if(EmptyUtils.isEmpty(storeVOList)){
            return false;
        }
        for (StoreVO vo : storeVOList) {
            if (vo.getStore() < count) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItripHotelOrder getItripHotelOrderById(Long id) throws Exception {
        return itripHotelOrderMapper.getItripHotelOrderById(id);
    }

    @Override
    public ItripHotelRoom getItripHotelRoomById(Long id) throws Exception {
        return itripHotelRoomMapper.getItripHotelRoomById(id);
    }

    @Override
    public ItripPersonalOrderRoomVO getItripHotelOrderRoomInfoById(Long orderId) throws Exception {
        return itripHotelOrderMapper.getItripHotelOrderRoomInfoById(orderId);
    }

    @Override
    public Page<ItripHotelOrder> queryItripHotelOrderPageByMap(Map<String, Object> param, Integer pageNo, Integer pageSize) throws Exception {
        Integer total = itripHotelOrderMapper.getItripHotelOrderCountByMap(param);
        pageNo = EmptyUtils.isEmpty(pageNo) ? Constants.DEFAULT_PAGE_NO : pageNo;
        pageSize = EmptyUtils.isEmpty(pageSize) ? Constants.DEFAULT_PAGE_SIZE : pageSize;
        Page page = new Page(pageNo,pageSize,total);
        param.put("beginPos",page.getBeginPos());
        param.put("pageSize",page.getPageSize());
        List<ItripHotelOrder> itripHotelOrders = itripHotelOrderMapper.getItripHotelOrderListByMap(param);
        page.setRows(itripHotelOrders);
        return page;
    }

    @Override
    public List<ItripHotelOrder> getItripHotelOrderListByMap(Map<String, Object> param) throws Exception {
        return itripHotelOrderMapper.getItripHotelOrderListByMap(param);
    }

    @Override
    public Integer itriptxModifyItripTradeEnds(Map<String, Object> param) throws Exception {
        return itripTradeEndsMapper.updateItripTradeEnds(param);
    }

    @Override
    public List<ItripTradeEnds> getItripTradeEndsListByMap(Map<String, Object> param) throws Exception {
        return itripTradeEndsMapper.getItripTradeEndsListByMap(param);
    }

    @Override
    public boolean updateRoomStore(Map<String, Object> param) throws Exception {
        Integer isok= itripHotelTempStoreMapper.updateRoomStore(param);
        return isok == 0 ? false : true;
    }

    @Override
    public List<StoreVO> queryRoomStore(Map<String, Object> param) throws Exception {
        return itripHotelTempStoreMapper.queryRoomStore(param);
    }

    @Override
    public ItripHotel getItripHotelById(Long id) throws Exception {
        return itripHotelMapper.getItripHotelById(id);
    }

    @Override
    public BigDecimal getOrderPayAmount(int count, Long roomId) throws Exception {
        BigDecimal payAmount = null;
        BigDecimal roomPrice = itripHotelRoomMapper.getItripHotelRoomById(roomId).getRoomPrice();
        payAmount = BigDecimalUtil.OperationASMD(count, roomPrice,
                BigDecimalUtil.BigDecimalOprations.multiply,
                2, ROUND_DOWN);
        return payAmount;
    }

    @Override
    public Map<String, String> itriptxAddItripHotelOrder(ItripHotelOrder itripHotelOrder, List<ItripUserLinkUser> itripOrderLinkUserList) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        if (null != itripHotelOrder) {
            int flag=0;
            if (EmptyUtils.isNotEmpty(itripHotelOrder.getId())) {
                //删除联系人
                itripOrderLinkUserMapper.deleteItripOrderLinkUserByOrderId(itripHotelOrder.getId());
                itripHotelOrder.setModifyDate(new Date());
                flag=itripHotelOrderMapper.updateItripHotelOrder(itripHotelOrder);
            } else {
                itripHotelOrder.setCreationDate(new Date());
                flag=itripHotelOrderMapper.insertItripHotelOrder(itripHotelOrder);
            }
            if (flag > 0) {
                Long orderId = itripHotelOrder.getId();
                //添加订单之后还需要往订单与常用联系人关联表中添加记录
                if (orderId > 0) {
                    for (ItripUserLinkUser itripUserLinkUser : itripOrderLinkUserList) {
                        ItripOrderLinkUser itripOrderLinkUser = new ItripOrderLinkUser();
                        itripOrderLinkUser.setOrderId(orderId);
                        itripOrderLinkUser.setLinkUserId(itripUserLinkUser.getId());
                        itripOrderLinkUser.setLinkUserName(itripUserLinkUser.getLinkUserName());
                        itripOrderLinkUser.setCreationDate(new Date());
                        itripOrderLinkUser.setCreatedBy(itripHotelOrder.getCreatedBy());
                        itripOrderLinkUserMapper.insertItripOrderLinkUser(itripOrderLinkUser);
                    }
                }
                map.put("id", itripHotelOrder.getId().toString());
                map.put("orderNo", itripHotelOrder.getOrderNo());
                return map;
            }
        }
        return map;
    }


}
