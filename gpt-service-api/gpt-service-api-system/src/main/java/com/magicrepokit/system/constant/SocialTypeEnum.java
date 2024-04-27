package com.magicrepokit.system.constant;

import cn.hutool.core.util.ArrayUtil;
import com.magicrepokit.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum SocialTypeEnum implements IntArrayValuable {
    GIHUB(10,"GITHUB"),

    GOOGLE(20,"GOOGLE"),

    GITEE(30, "GITEE");

    ;

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(SocialTypeEnum::getType).toArray();

    /**
     * 类型
     */
    private final Integer type;
    /**
     * 类型的标识
     */
    private final String source;

    @Override
    public int[] array() {
        return ARRAYS;
    }

    public static SocialTypeEnum valueOfType(Integer type) {
        return ArrayUtil.firstMatch(o -> o.getType().equals(type), values());
    }
}
