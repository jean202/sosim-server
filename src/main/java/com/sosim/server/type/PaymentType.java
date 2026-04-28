package com.sosim.server.type;

import com.sosim.server.common.util.EnumUtils;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum PaymentType {

    NON_PAYMENT("미납", "non"),
    CONFIRMING("확인중/확인필요", "con"),
    FULL_PAYMENT("완납", "full");

    private String desc;
    private String param;

    private static final Map<Object, PaymentType> map = EnumUtils.getMap(PaymentType.class);

    public static final PaymentType getType(String value) {
        if (value == null) {
            return map.get(NON_PAYMENT.name());
        }
        switch (value) {
            case "non":
            case "미납":
                return map.get(NON_PAYMENT.name());
            case "con":
            case "확인중":
            case "확인필요":
            case "확인중/확인필요":
                return map.get(CONFIRMING.name());
            case "full":
            case "완납":
                return map.get(FULL_PAYMENT.name());
            default:
                return map.get(NON_PAYMENT.name());
        }
    }
}
