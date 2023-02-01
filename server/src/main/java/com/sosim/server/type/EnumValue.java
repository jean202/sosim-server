package com.sosim.server.type;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class EnumValue {

    public String code;
    public String value;

    private EnumValue(EnumModel enumModel) {
        this.code = enumModel.getCode();
        this.value = enumModel.getValue();
    }

    public static EnumValue of(EnumModel enumModel) {
        if (enumModel == null) {
            return null;
        }
        return new EnumValue(enumModel);
    }
}
