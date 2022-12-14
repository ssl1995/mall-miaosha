package com.changgou.pay.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.pay.service.PayService;
import com.github.wxpay.sdk.WXPayUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {


    @Autowired
    private PayService payService;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/nativePay")
    public Map nativePay(){
        String username = "test";
        return payService.nativePay(username);
    }

    @GetMapping("/queryOrder")
    public Map queryOrder(@RequestParam("orderId") String orderId){
        return payService.queryOrder(orderId);
    }


    @GetMapping("/closeOrder")
    public Result closeOrder(@RequestParam("orderId")String orderId){
        payService.closeOrder(orderId);
        return new Result(true, StatusCode.OK, "关闭订单成功");
    }


    /**
     * 微信支付回调的接口
     * @param request
     * @return
     */
    @PostMapping("/notify")
    public String payNofity(HttpServletRequest request){
       logger.info("微信支付回调了。。。。。。");
        //TODO 支付回调内部处理逻辑

        //1.获取微信回调通知的XML字符串
        try {
            byte[] bytes = IOUtils.toByteArray(request.getInputStream());
            String notifyXml = new String(bytes, "UTF-8");
            logger.info("微信回调的XML数据：" + notifyXml);


            //2.获取到微信订单ID和商户订单ID
            Map<String, String> notifyMap = WXPayUtil.xmlToMap(notifyXml);
            String transactionId = notifyMap.get("transaction_id"); //微信支付的订单ID
            String outTradeNo = notifyMap.get("out_trade_no");//商户订单号（畅购订单号）

            logger.info("回调的重要数据 => transactionId:{},outTradeNo:{} ",transactionId,outTradeNo );

            //3.将微信订单ID和商户订单ID存入MQ
            Map<String,String> payOrderMap = new HashMap<>();
            payOrderMap.put("transactionId", transactionId);
            payOrderMap.put("outTradeNo", outTradeNo);
            String payOrderJson = JSON.toJSONString(payOrderMap);
            kafkaTemplate.send("pay_order", payOrderJson);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //按照微信支付官方要求返回规定结果给微信
       Map<String,String> respMap = new HashMap<>();
       respMap.put("return_code", "SUCCESS");
       respMap.put("return_msg", "OK");
        try {
            return WXPayUtil.mapToXml(respMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "回调完毕";
    }
}
