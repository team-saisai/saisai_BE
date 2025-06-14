package com.saisai.domain.reward.entity;

public enum RewardDeliveryStatus {
    PENDING_REQUEST,    // 리워드 신청됨 (배송 대기)
    PREPARING_SHIPMENT, // 배송 준비 중
    SHIPPED,            // 배송 중
    DELIVERED,          // 배송 완료
}
