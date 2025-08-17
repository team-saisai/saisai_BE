package com.saisai.domain.auth.dto.response;

import com.saisai.domain.auth.constant.ProviderType;

public record WithdrawRes(
    ProviderType provider
) {

}
