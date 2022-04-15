package io.arex.common.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author jmo
 * @since 2022/1/24
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponseType<T> implements Response {
    private ResponseStatusType responseStatusType;
    private T body;
}
