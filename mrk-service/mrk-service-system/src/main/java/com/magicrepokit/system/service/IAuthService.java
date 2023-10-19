package com.magicrepokit.system.service;

import com.magicrepokit.system.entity.dto.LoginDTO;
import com.magicrepokit.system.entity.vo.AuthTokenVO;

import javax.validation.Valid;

/**
 * 用户认证登录接口
 *
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
public interface IAuthService {

    /**
     * 登录
     *
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    AuthTokenVO login(@Valid LoginDTO loginDTO);
}
