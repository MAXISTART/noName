package com.school.store.wechat.controller;


import com.school.store.wechat.utils.Sha1Encoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;

@RestController
@RequestMapping(value = "/wechat")
public class AuthorizeController {

    /**
     *  处理微信的基本认证配置
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    @GetMapping(value = "/baseAuthorzie")
    public String wxAuthorize(@RequestParam String signature,
                              @RequestParam String timestamp,
                              @RequestParam String nonce,
                              @RequestParam String echostr){


        String token = "wxSpring";
        ArrayList<String> list=new ArrayList<String>();
        list.add(nonce);
        list.add(timestamp);
        list.add(token);

        Collections.sort(list);

        String hashcode = Sha1Encoder.encode(list.get(0)+list.get(1)+list.get(2));
        if(hashcode.equals(signature)){
            return echostr;
        }

        return "";

    }





}
