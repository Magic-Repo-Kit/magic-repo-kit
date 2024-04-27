package com.gpt.chat.dto.gpt;

import com.gpt.mb.base.PageParam;
import lombok.Data;

@Data
public class GptConversationPageDTO extends PageParam {
    private Long userId;
}
