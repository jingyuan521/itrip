package cn.itrip.itripbiz.controller;


import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.*;
import cn.itrip.beans.vo.order.*;
import cn.itrip.beans.vo.store.StoreVO;
import cn.itrip.common.*;
import cn.itrip.itripbiz.service.HotelordersService;
import com.alibaba.fastjson.JSONArray;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/lorder")
public class LoderController {
    @Resource
    private HotelordersService hotelordersService;
    @Resource
    private ValidationToken validationToken;
    @Resource
    private SystemConfig systemConfig;


    @RequestMapping("/validateroomstore")
    @ResponseBody
    public Dto<Map<String,Boolean>> validateRoomStore(@RequestBody ValidateRoomStoreVO validateRoomStoreVO, HttpServletRequest request){
        try {
            String tokenString = request.getHeader("token");
            ItripUser currentUser = validationToken.getCurrentUser(tokenString);
            if (EmptyUtils.isEmpty(currentUser)) {
                return DtoUtil.returnFail("token失效，请重登录", "100000");
            }
            if (EmptyUtils.isEmpty(validateRoomStoreVO.getHotelId())) {
                return DtoUtil.returnFail("hotelId不能为空", "100515");
            } else if (EmptyUtils.isEmpty(validateRoomStoreVO.getRoomId())) {
                return DtoUtil.returnFail("roomId不能为空", "100516");
            } else {
                Map param = new HashMap();
                param.put("startTime", validateRoomStoreVO.getCheckInDate());
                param.put("endTime", validateRoomStoreVO.getCheckOutDate());
                param.put("roomId", validateRoomStoreVO.getRoomId());
                param.put("hotelId", validateRoomStoreVO.getHotelId());
                param.put("count", validateRoomStoreVO.getCount());
                boolean flag = hotelordersService.validateRoomStore(param);
                Map<String, Boolean> map = new HashMap<String, Boolean>();
                map.put("flag", flag);
                return DtoUtil.returnSuccess("操作成功", map);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("系统异常", "100517");
        }

    }

    //根据订单id查看各人订单详情
    @RequestMapping(value = "/getpersonalorderinfo")
    @ResponseBody
    public Dto<Object> getPersonalOrderInfo(@RequestParam String orderId,HttpServletRequest request){
        Dto<Object> dto = null;
        String tokenString = request.getHeader("token");
        ItripUser currentUser = validationToken.getCurrentUser(tokenString);
        if (null != currentUser) {
            if (null == orderId || "".equals(orderId)) {
                return DtoUtil.returnFail("请传递参数：orderId", "100525");
            }
            try {
                ItripHotelOrder hotelOrder = hotelordersService.getItripHotelOrderById(Long.valueOf(orderId));
                if (null != hotelOrder) {
                    ItripPersonalHotelOrderVO itripPersonalHotelOrderVO = new ItripPersonalHotelOrderVO();
                    itripPersonalHotelOrderVO.setId(hotelOrder.getId());
                    itripPersonalHotelOrderVO.setBookType(hotelOrder.getBookType());
                    itripPersonalHotelOrderVO.setCreationDate(hotelOrder.getCreationDate());
                    itripPersonalHotelOrderVO.setOrderNo(hotelOrder.getOrderNo());
                    //查询预订房间的信息
                    ItripHotelRoom room = hotelordersService.getItripHotelRoomById(hotelOrder.getRoomId());
                    if (EmptyUtils.isNotEmpty(room)) {
                        itripPersonalHotelOrderVO.setRoomPayType(room.getPayType());
                    }
                    Integer orderStatus = hotelOrder.getOrderStatus();
                    itripPersonalHotelOrderVO.setOrderStatus(orderStatus);
                    //订单状态（0：待支付 1:已取消 2:支付成功 3:已消费 4:已点评）
                    //{"1":"订单提交","2":"订单支付","3":"支付成功","4":"入住","5":"订单点评","6":"订单完成"}
                    //{"1":"订单提交","2":"订单支付","3":"订单取消"}
                    if (orderStatus == 1) {
                        itripPersonalHotelOrderVO.setOrderProcess(JSONArray.parse(systemConfig.getOrderProcessCancel()));
                        itripPersonalHotelOrderVO.setProcessNode("3");
                    } else if (orderStatus == 0) {
                        itripPersonalHotelOrderVO.setOrderProcess(JSONArray.parse(systemConfig.getOrderProcessOK()));
                        itripPersonalHotelOrderVO.setProcessNode("2");//订单支付
                    } else if (orderStatus == 2) {
                        itripPersonalHotelOrderVO.setOrderProcess(JSONArray.parse(systemConfig.getOrderProcessOK()));
                        itripPersonalHotelOrderVO.setProcessNode("3");//支付成功（未出行）
                    } else if (orderStatus == 3) {
                        itripPersonalHotelOrderVO.setOrderProcess(JSONArray.parse(systemConfig.getOrderProcessOK()));
                        itripPersonalHotelOrderVO.setProcessNode("5");//订单点评
                    } else if (orderStatus == 4) {
                        itripPersonalHotelOrderVO.setOrderProcess(JSONArray.parse(systemConfig.getOrderProcessOK()));
                        itripPersonalHotelOrderVO.setProcessNode("6");//订单完成
                    } else {
                        itripPersonalHotelOrderVO.setOrderProcess(null);
                        itripPersonalHotelOrderVO.setProcessNode(null);
                    }
                    itripPersonalHotelOrderVO.setPayAmount(hotelOrder.getPayAmount());
                    itripPersonalHotelOrderVO.setPayType(hotelOrder.getPayType());
                    itripPersonalHotelOrderVO.setNoticePhone(hotelOrder.getNoticePhone());
                    dto = DtoUtil.returnSuccess("获取个人订单信息成功", itripPersonalHotelOrderVO);
                } else {
                    dto = DtoUtil.returnFail("没有相关订单信息", "100526");
                }
            } catch (Exception e) {
                e.printStackTrace();
                dto = DtoUtil.returnFail("获取个人订单信息错误", "100527");
            }
        } else {
            dto = DtoUtil.returnFail("token失效，请重登录", "100000");
        }
        return dto;
    }

    //根据订单id查询各人订单详情，房型相关信息
    @RequestMapping(value = "/getpersonalorderroominfo/{orderId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Dto<Object> getPersonalOrderRoomInfo(@ApiParam(required = true, name = "orderId", value = "订单ID")
                                                @RequestParam String orderId,
                                                HttpServletRequest request) {
        Dto<Object> dto = null;
        String tokenString = request.getHeader("token");
        ItripUser currentUser = validationToken.getCurrentUser(tokenString);
        if (null != currentUser) {
            if (null == orderId || "".equals(orderId)) {
                return DtoUtil.returnFail("请传递参数：orderId", "100529");
            }
            try {
                ItripPersonalOrderRoomVO vo = hotelordersService.getItripHotelOrderRoomInfoById(Long.valueOf(orderId));
                if (null != vo) {
                    dto = DtoUtil.returnSuccess("获取个人订单房型信息成功", vo);
                } else {
                    dto = DtoUtil.returnFail("没有相关订单房型信息", "100530");
                }
            } catch (Exception e) {
                e.printStackTrace();
                dto = DtoUtil.returnFail("获取个人订单房型信息错误", "100531");
            }
        } else {
            dto = DtoUtil.returnFail("token失效，请重登录", "100000");
        }
        return dto;
    }
    //查询个人订单列表，并分页显示
    @RequestMapping(value = "/getpersonalorderlist", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Dto<Object> getPersonalOrderList(@RequestBody ItripSearchOrderVO itripSearchOrderVO,
                                            HttpServletRequest request) {
        Integer orderType = itripSearchOrderVO.getOrderType();
        Integer orderStatus = itripSearchOrderVO.getOrderStatus();
        Dto<Object> dto = null;
        String tokenString = request.getHeader("token");
        ItripUser currentUser = validationToken.getCurrentUser(tokenString);
        if (null != currentUser) {
            if (orderType == null) {
                return DtoUtil.returnFail("请传递参数：orderType", "100501");
            }
            if (orderStatus == null) {
                return DtoUtil.returnFail("请传递参数：orderStatus", "100502");
            }

            Map<String, Object> param = new HashMap<>();
            param.put("orderType", orderType == -1 ? null : orderType);
            param.put("orderStatus", orderStatus == -1 ? null : orderStatus);
            param.put("userId", currentUser.getId());
            param.put("orderNo", itripSearchOrderVO.getOrderNo());
            param.put("linkUserName", itripSearchOrderVO.getLinkUserName());
            param.put("startDate", itripSearchOrderVO.getStartDate());
            param.put("endDate", itripSearchOrderVO.getEndDate());
            try {
                Page page = hotelordersService.queryItripHotelOrderPageByMap(param,
                        itripSearchOrderVO.getPageNo(),
                        itripSearchOrderVO.getPageSize());
                dto = DtoUtil.returnSuccess("获取个人订单列表成功", page);
            } catch (Exception e) {
                e.printStackTrace();
                dto = DtoUtil.returnFail("获取个人订单列表错误", "100503");
            }

        } else {
            dto = DtoUtil.returnFail("token失效，请重登录", "100000");
        }
        return dto;
    }
//扫描中间表，执行库存更新操作
@RequestMapping(value = "/scanTradeEnd", method = RequestMethod.GET, produces = "application/json")
@ResponseBody
public Dto<Object> scanTradeEnd() {
    Map param = new HashMap();
    List<ItripTradeEnds> tradeEndses = null;
    try {
        param.put("flag", 1);
        param.put("oldFlag", 0);
        hotelordersService.itriptxModifyItripTradeEnds(param);
        tradeEndses = hotelordersService.getItripTradeEndsListByMap(param);
        if (EmptyUtils.isNotEmpty(tradeEndses)) {
            for (ItripTradeEnds ends : tradeEndses) {
                Map<String, Object> orderParam = new HashMap<String, Object>();
                orderParam.put("orderNo", ends.getOrderNo());
                List<ItripHotelOrder> orderList = hotelordersService.getItripHotelOrderListByMap(orderParam);
                for (ItripHotelOrder order : orderList) {
                    Map<String, Object> roomStoreMap = new HashMap<String, Object>();
                    roomStoreMap.put("startTime", order.getCheckInDate());
                    roomStoreMap.put("endTime", order.getCheckOutDate());
                    roomStoreMap.put("count", order.getCount());
                    roomStoreMap.put("roomId", order.getRoomId());
                    hotelordersService.updateRoomStore(roomStoreMap);
                }
            }
            param.put("flag", 2);
            param.put("oldFlag", 1);
            hotelordersService.itriptxModifyItripTradeEnds(param);
            return DtoUtil.returnSuccess();
        }else{
            return DtoUtil.returnFail("100535", "没有查询到相应记录");
        }
    } catch (Exception e) {
        e.printStackTrace();
        return DtoUtil.returnFail("系统异常", "100536");
    }
}

     //生成订单前，获取订单信息
    public Dto<RoomStoreVO> getPreorderinfo(@RequestBody ValidateRoomStoreVO validateRoomStoreVO,HttpServletRequest request){
        String tokenString = request.getHeader("token");
        ItripUser currentUser = validationToken.getCurrentUser(tokenString);
        ItripHotel hotel = null;
        ItripHotelRoom room = null;
        RoomStoreVO roomStoreVO = null;
        try {
            if (EmptyUtils.isEmpty(currentUser)) {
                return DtoUtil.returnFail("token失效，请重登录", "100000");
            }
            if (EmptyUtils.isEmpty(validateRoomStoreVO.getHotelId())) {
                return DtoUtil.returnFail("hotelId不能为空", "100510");
            } else if (EmptyUtils.isEmpty(validateRoomStoreVO.getRoomId())) {
                return DtoUtil.returnFail("roomId不能为空", "100511");
            } else {
                roomStoreVO = new RoomStoreVO();
                hotel = hotelordersService.getItripHotelById(validateRoomStoreVO.getHotelId());
                room = hotelordersService.getItripHotelRoomById(validateRoomStoreVO.getRoomId());
                Map param = new HashMap();
                param.put("startTime", validateRoomStoreVO.getCheckInDate());
                param.put("endTime", validateRoomStoreVO.getCheckOutDate());
                param.put("roomId", validateRoomStoreVO.getRoomId());
                param.put("hotelId", validateRoomStoreVO.getHotelId());
                roomStoreVO.setCheckInDate(validateRoomStoreVO.getCheckInDate());
                roomStoreVO.setCheckOutDate(validateRoomStoreVO.getCheckOutDate());
                roomStoreVO.setHotelName(hotel.getHotelName());
                roomStoreVO.setRoomId(room.getId());
                roomStoreVO.setPrice(room.getRoomPrice());
                roomStoreVO.setHotelId(validateRoomStoreVO.getHotelId());
                List<StoreVO> storeVOList = hotelordersService.queryRoomStore(param);
                roomStoreVO.setCount(1);
                if (EmptyUtils.isNotEmpty(storeVOList)) {
                    roomStoreVO.setStore(storeVOList.get(0).getStore());
                } else {
                    return DtoUtil.returnFail("暂时无房", "100512");
                }
                return DtoUtil.returnSuccess("获取成功", roomStoreVO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("系统异常", "100513");
        }
    }
    //支付成功过后查询订单信息
    @RequestMapping(value = "/queryOrderinfo")
    @ResponseBody
    public Dto<Map<String,Boolean>> querySuccessOrderInfo(@RequestParam Long id,HttpServletRequest request){
        String tokenString = request.getHeader("token");
        ItripUser currentUser = validationToken.getCurrentUser(tokenString);
        if (EmptyUtils.isEmpty(currentUser)) {
            return DtoUtil.returnFail("token失效，请重登录", "100000");
        }
        if (EmptyUtils.isEmpty(id)) {
            return DtoUtil.returnFail("id不能为空", "100519");
        }
        try {
            ItripHotelOrder order = hotelordersService.getItripHotelOrderById(id);
            if (EmptyUtils.isEmpty(order)) {
                return DtoUtil.returnFail("没有查询到相应订单", "100519");
            }
            ItripHotelRoom room = hotelordersService.getItripHotelRoomById(order.getRoomId());
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("id", order.getId());
            resultMap.put("orderNo", order.getOrderNo());
            resultMap.put("payType", order.getPayType());
            resultMap.put("payAmount", order.getPayAmount());
            resultMap.put("hotelName", order.getHotelName());
            resultMap.put("roomTitle", room.getRoomTitle());
            return DtoUtil.returnSuccess("获取数据成功", resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取数据失败", "100520");
        }
    }

    //生成订单
    @RequestMapping(value = "/addhotelorder")
    public Dto<Object> addHotelOrder(@RequestBody ItripAddHotelOrderVO itripAddHotelOrderVO,HttpServletRequest request){
        Dto<Object> dto = new Dto<Object>();
        String tokenString = request.getHeader("token");
        ItripUser currentUser = validationToken.getCurrentUser(tokenString);
        Map<String, Object> validateStoreMap = new HashMap<String, Object>();
        validateStoreMap.put("startTime", itripAddHotelOrderVO.getCheckInDate());
        validateStoreMap.put("endTime", itripAddHotelOrderVO.getCheckOutDate());
        validateStoreMap.put("hotelId", itripAddHotelOrderVO.getHotelId());
        validateStoreMap.put("roomId", itripAddHotelOrderVO.getRoomId());
        validateStoreMap.put("count", itripAddHotelOrderVO.getCount());
        List<ItripUserLinkUser> linkUserList = itripAddHotelOrderVO.getLinkUser();
        if(EmptyUtils.isEmpty(currentUser)){
            return DtoUtil.returnFail("token失效，请重登录", "100000");
        }
        try {
            //判断库存是否充足
            Boolean flag = hotelordersService.validateRoomStore(validateStoreMap);
            if (flag && null != itripAddHotelOrderVO) {
                //计算订单的预定天数
                Integer days = DateUtil.getBetweenDates(
                        itripAddHotelOrderVO.getCheckInDate(), itripAddHotelOrderVO.getCheckOutDate()
                ).size()-1;
                if(days<=0){
                    return DtoUtil.returnFail("退房日期必须大于入住日期", "100505");
                }
                ItripHotelOrder itripHotelOrder = new ItripHotelOrder();
                itripHotelOrder.setId(itripAddHotelOrderVO.getId());
                itripHotelOrder.setUserId(currentUser.getId());
                itripHotelOrder.setOrderType(itripAddHotelOrderVO.getOrderType());
                itripHotelOrder.setHotelId(itripAddHotelOrderVO.getHotelId());
                itripHotelOrder.setHotelName(itripAddHotelOrderVO.getHotelName());
                itripHotelOrder.setRoomId(itripAddHotelOrderVO.getRoomId());
                itripHotelOrder.setCount(itripAddHotelOrderVO.getCount());
                itripHotelOrder.setCheckInDate(itripAddHotelOrderVO.getCheckInDate());
                itripHotelOrder.setCheckOutDate(itripAddHotelOrderVO.getCheckOutDate());
                itripHotelOrder.setNoticePhone(itripAddHotelOrderVO.getNoticePhone());
                itripHotelOrder.setNoticeEmail(itripAddHotelOrderVO.getNoticeEmail());
                itripHotelOrder.setSpecialRequirement(itripAddHotelOrderVO.getSpecialRequirement());
                itripHotelOrder.setIsNeedInvoice(itripAddHotelOrderVO.getIsNeedInvoice());
                itripHotelOrder.setInvoiceHead(itripAddHotelOrderVO.getInvoiceHead());
                itripHotelOrder.setInvoiceType(itripAddHotelOrderVO.getInvoiceType());
                itripHotelOrder.setCreatedBy(currentUser.getId());
                StringBuilder linkUserName = new StringBuilder();
                int size = linkUserList.size();
                for (int i = 0; i < size; i++) {
                    if (i != size - 1) {
                        linkUserName.append(linkUserList.get(i).getLinkUserName() + ",");
                    } else {
                        linkUserName.append(linkUserList.get(i).getLinkUserName());
                    }
                }
                itripHotelOrder.setLinkUserName(linkUserName.toString());
                itripHotelOrder.setBookingDays(days);
                if (tokenString.startsWith("token:PC")) {
                    itripHotelOrder.setBookType(0);
                } else if (tokenString.startsWith("token:MOBILE")) {
                    itripHotelOrder.setBookType(1);
                } else {
                    itripHotelOrder.setBookType(2);
                }
                //支付之前生成的订单的初始状态为未支付
                itripHotelOrder.setOrderStatus(0);
                try {
                    //生成订单号：机器码 +日期+（MD5）（商品IDs+毫秒数+1000000的随机数）
                    StringBuilder md5String = new StringBuilder();
                    md5String.append(itripHotelOrder.getHotelId());
                    md5String.append(itripHotelOrder.getRoomId());
                    md5String.append(System.currentTimeMillis());
                    md5String.append(Math.random() * 1000000);
                    String md5 = MD5.getMd5(md5String.toString(), 6);

                    //生成订单编号
                    StringBuilder orderNo = new StringBuilder();
                    orderNo.append(systemConfig.getMachineCode());
                    orderNo.append(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
                    orderNo.append(md5);
                    itripHotelOrder.setOrderNo(orderNo.toString());
                    //计算订单的总金额
                    itripHotelOrder.setPayAmount(hotelordersService.getOrderPayAmount(days * itripAddHotelOrderVO.getCount(), itripAddHotelOrderVO.getRoomId()));

                    Map<String, String> map = hotelordersService.itriptxAddItripHotelOrder(itripHotelOrder, linkUserList);
                    DtoUtil.returnSuccess();
                    dto = DtoUtil.returnSuccess("生成订单成功", map);
                } catch (Exception e) {
                    e.printStackTrace();
                    dto = DtoUtil.returnFail("生成订单失败", "100505");
                }
            } else if (flag && null == itripAddHotelOrderVO) {
                dto = DtoUtil.returnFail("不能提交空，请填写订单信息", "100506");
            } else {
                dto = DtoUtil.returnFail("库存不足", "100507");
            }
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("系统异常", "100508");
        }

    }

}
