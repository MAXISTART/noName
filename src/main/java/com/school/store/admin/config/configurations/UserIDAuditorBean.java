package com.school.store.admin.config.configurations;

import com.school.store.utils.HttpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;


@Configuration
public class UserIDAuditorBean implements AuditorAware<String> {

    @Override
    public String getCurrentAuditor() {
        return HttpUtil.getSessionUserId();
    }
}