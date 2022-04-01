package io.cstool.comply22.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
public class ChangeRef {

    private Long id;

    public static ChangeRef of(Change change) {
        if (change == null)
            return null;
        return new ChangeRef(change.getId());
    }

    public static Set<ChangeRef> of(Set<Change> nextRelatedChange) {
        return nextRelatedChange.stream()
                .map(ChangeRef::of)
                .collect(Collectors.toSet());
    }
}
