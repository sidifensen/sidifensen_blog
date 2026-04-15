package com.sidifensen.utils;

import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Re-Reading Advisor (Re2)
 * <p>
 * 通过让模型重新阅读问题来提高推理能力。原理出自论文 arXiv:2309.06275。
 * <p>
 * 核心思想：将用户输入改写为 "{Input_Query}\nRead the question again: {Input_Query}"
 * 强制模型在生成答案前重新审视问题，减少误解，提高复杂推理的准确率。
 * <p>
 * 注意：输入长度翻倍，API 调用成本也会翻倍，请根据场景权衡使用。
 */
public class ReReadingAdvisor implements BaseAdvisor {

    private static final String RE2_INPUT_QUERY_KEY = "re2_input_query";

    private static final String DEFAULT_RE2_ADVISE_TEMPLATE = """
            {re2_input_query}
            Read the question again: {re2_input_query}
            """;

    @Override
    public AdvisedRequest before(AdvisedRequest advisedRequest) {
        // 将原始查询存入参数，以便在模板中使用
        Map<String, Object> advisedUserParams = new HashMap<>(advisedRequest.userParams());
        advisedUserParams.put(RE2_INPUT_QUERY_KEY, advisedRequest.userText());

        // 使用 Re2 模板构建新请求
        return AdvisedRequest.from(advisedRequest)
                .userText(DEFAULT_RE2_ADVISE_TEMPLATE)
                .userParams(advisedUserParams)
                .build();
    }

    @Override
    public AdvisedResponse after(AdvisedResponse advisedResponse) {
        // 不修改响应，直接返回
        return advisedResponse;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        // 设置为高优先级，确保在 MessageChatMemoryAdvisor 之前执行
        return 0;
    }
}
