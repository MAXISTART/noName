package com.school.store.admin.girl.controller;


import com.school.store.asyn.AsynRes;
import com.school.store.admin.boy.entity.Boy;
import com.school.store.admin.boy.service.BoyService;
import com.school.store.admin.girl.entity.Girl;
import com.school.store.admin.girl.service.GirlService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class GirlController {

/*    @Autowired
    private GirlService girlService;

    @Autowired
    private BoyService boyService;

    @RequestMapping("value")
    public AsynRes test(){
        AsynRes res = new AsynRes();
        Map<String,Object> map = new HashMap<String, Object>();

        List<Boy> boys = boyService.findAll();
        List<Boy> boys_copy = new ArrayList<>();
        boys.forEach(B -> {

            List<Girl> girls = girlService.findByBoyId(B.getId());
            //B.setGirls(girls);
            if(girls.size() > 0){
                Boy boy_copy = new Boy();
                BeanUtils.copyProperties(girls.get(0).getBoy(),boy_copy);
                boy_copy.setGirls(girls);
                boys_copy.add(boy_copy);
            }
        });


        List<Girl> ls3 = girlService.findByBoyId("4028fbdf6128a838016128a83d5f0000");
        Boy boy = boyService.findById("4028fbdf6128a838016128a83d5f0000");
        boy.setGirls(ls3);
        map.put("zhiyuan", boy);


*//*        List<Girl> ls3 = girlService.findByBoyId("4028fbdf6128a838016128a83d5f0000");
        Boy boy = new Boy();
        BeanUtils.copyProperties(ls3.get(0).getBoy(),boy);
        boy.setGirls(ls3);*//*

*//*        map.put("boys",boys_copy);*//*
        res.setSucceed(map);
        return res;
    }*/
}
