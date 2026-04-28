package com.sosim.server.event.dto.info;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.ObjectUtils;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ListInfo<E> {

    private Long totalCount;
    private List<E> list;

    public static <E> ListInfo<E> from() {
        return from(Lists.newArrayList());
    }

    public static <E> ListInfo<E> from(List<E> list) {
        if (ObjectUtils.isEmpty(list)) {
            return from(0, list);
        }
        return from(list.size(), list);
    }

    public static <E> ListInfo<E> from(Long totalCount, List<E> list) {
        ListInfo<E> info = new ListInfo<>();
        info.setList(ObjectUtils.isEmpty(list) ? Lists.newArrayList() : list);
        info.setTotalCount(totalCount);
        return info;
    }

    public static <E> ListInfo<E> from(Integer totalCount, List<E> list) {
        return from(totalCount.longValue(), list);
    }
}
