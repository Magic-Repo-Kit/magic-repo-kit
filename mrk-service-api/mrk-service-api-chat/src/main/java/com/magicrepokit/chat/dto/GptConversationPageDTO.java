package com.magicrepokit.chat.dto;

import com.magicrepokit.mb.base.PageParam;
import lombok.Data;

@Data
public class GptConversationPageDTO extends PageParam {
    private Long userId;
}
