package cn.itrip.itriptrade.service;

import cn.itrip.beans.pojo.ItripHotelOrder;
import cn.itrip.beans.pojo.ItripTradeEnds;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.SystemConfig;
import cn.itrip.dao.hotelorder.ItripHotelOrderMapper;
import cn.itrip.dao.tradeends.ItripTradeEndsMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class OrderServiceimpl implements OrderService{
    @Resource
    private ItripHotelOrderMapper itripHotelOrderMapper;
    @Resource
    private ItripTradeEndsMapper itripTradeEndsMapper;
    @Resource
    private SystemConfig systemConfig;
    @Override
    public ItripHotelOrder LoadItripHotelOrder(String orderNo) throws Exception {
        Map<String,Object> param = new HashMap<>();
        param.put("orderNo",orderNo);
        List<ItripHotelOrder> orders = itripHotelOrderMapper.getItripHotelOrderListByMap(param);
        if (orders.size()==1){
            return orders.get(0);
        }else{
            return null;
        }

    }

    @Override
    public boolean processed(String orderNo) throws Exception {

        ItripHotelOrder itripHotelOrder = this.LoadItripHotelOrder(orderNo);
        return itripHotelOrder.getOrderStatus().equals(2)&&!EmptyUtils.isEmpty(itripHotelOrder.getTradeNo());
    }

    @Override
    public void paySuccess(String order, int payType, String tradeNo) throws Exception {
        ItripHotelOrder itripHotelOrder = new ItripHotelOrder();
        itripHotelOrder.setOrderStatus(2);
        itripHotelOrder.setPayType(payType);
        itripHotelOrder.setTradeNo(tradeNo);
        itripHotelOrderMapper.updateItripHotelOrder(itripHotelOrder);
        //增加订单的后续待处理记录
        ItripTradeEnds itripTradeEnds = new ItripTradeEnds();
        itripTradeEnds.setId(itripHotelOrder.getId());
        itripTradeEnds.setOrderNo(itripHotelOrder.getOrderNo());
        itripTradeEndsMapper.insertItripTradeEnds(itripTradeEnds);
    }
    @Override
    public void payFailed(String orderNo, int payType, String tradeNo) throws Exception {
        ItripHotelOrder itripHotelOrder = new ItripHotelOrder();
        itripHotelOrder.setOrderStatus(1);
        itripHotelOrder.setPayType(payType);
        itripHotelOrder.setTradeNo(tradeNo);
        itripHotelOrderMapper.updateItripHotelOrder(itripHotelOrder);


    }


}
